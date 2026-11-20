/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.monticore.ast.ASTNode;
import de.monticore.rete.Match;
import de.monticore.rete.MatchListener;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Terminal node of a pattern: holds the matches of the pattern and provides
 * the query API and match notifications.
 * <p>
 * The matches have set semantics, i.e. a tuple contained several times in
 * the parent is a single match. All parameters must be AST nodes; this is
 * checked against the column types of the parent on construction.
 * <p>
 * Parameters of optional columns of the parent are
 * {@linkplain #isOptionalParameter optional}: they are bound to
 * {@link Tuple#ABSENT} in matches in which the optional pattern element is
 * absent (see {@link Match#isPresent}).
 * <p>
 * Queries accept partial bindings: one value per parameter, where
 * {@code null} denotes an unbound parameter and {@link Tuple#ABSENT} an
 * absent optional parameter (which no match of a non-optional parameter
 * has). Indexes for partial bindings are created on first use and maintained
 * afterwards.
 */
public class ProductionNode extends ReteNode implements Receiver {

  protected final TupleSource parent;

  protected final List<String> parameterNames;

  protected final Map<String, Integer> parameterIndices = new HashMap<>();

  protected final Multiset<Tuple> memory = HashMultiset.create();

  protected final Map<TupleMask, Map<Tuple, Set<Tuple>>> indexes = new HashMap<>();

  protected final List<MatchListener> listeners = new ArrayList<>();

  /**
   * tuple -> whether the tuple was a match at the start of the current transaction
   */
  protected Map<Tuple, Boolean> pendingChanges = new LinkedHashMap<>();

  /**
   * Creates the production, initializes it from the current contents of
   * {@code parent} (without reporting events) and attaches it to {@code parent}.
   *
   * @param parameterNames one unique name per column of {@code parent}
   * @throws IllegalArgumentException if the number of names does not match
   *                                  the arity, a name is duplicated or a
   *                                  column type is not an AST type
   */
  public ProductionNode(ReteNetwork network, TupleSource parent, List<String> parameterNames) {
    super(network);
    checkSameNetwork(parent);
    if (parameterNames.size() != parent.getArity()) {
      throw new IllegalArgumentException(parameterNames.size() + " parameter names for arity "
          + parent.getArity() + " of " + parent);
    }
    for (int i = 0; i < parameterNames.size(); i++) {
      Class<?> type = parent.getColumnTypes().get(i);
      if (!ASTNode.class.isAssignableFrom(type)) {
        throw new IllegalArgumentException("Parameter " + parameterNames.get(i)
            + " has type " + type.getName() + ", but all parameters must be AST nodes");
      }
      if (parameterIndices.put(parameterNames.get(i), i) != null) {
        throw new IllegalArgumentException("Duplicate parameter " + parameterNames.get(i));
      }
    }
    this.parent = parent;
    this.parameterNames = List.copyOf(parameterNames);
    parent.pullInto(memory);
    parent.attachChild(this);
  }

  public TupleSource getParent() {
    return parent;
  }

  public List<String> getParameterNames() {
    return parameterNames;
  }

  public int getArity() {
    return parameterNames.size();
  }

  /**
   * @return the position of the parameter with the given name
   * @throws IllegalArgumentException if there is no parameter of that name
   */
  public int getParameterIndex(String parameterName) {
    Integer index = parameterIndices.get(parameterName);
    if (index == null) {
      throw new IllegalArgumentException("Unknown parameter " + parameterName
          + ", expected one of " + parameterNames);
    }
    return index;
  }

  /**
   * @return whether the parameter at the given position is optional, i.e.
   * may be bound to {@link Tuple#ABSENT}
   */
  public boolean isOptionalParameter(int index) {
    return parent.isOptional(index);
  }

  /**
   * @return whether the parameter of the given name is optional, i.e. may be
   * bound to {@link Tuple#ABSENT}
   * @throws IllegalArgumentException if there is no parameter of that name
   */
  public boolean isOptionalParameter(String parameterName) {
    return isOptionalParameter(getParameterIndex(parameterName));
  }

  // ---------------------------------------------------------------------------
  // Rete protocol
  // ---------------------------------------------------------------------------

  /**
   * Counts the occurrences of the tuple. If it becomes or stops being a
   * match, the query indexes are updated and, if listeners are registered,
   * the change is recorded for delivery at the end of the transaction
   * (together with the state at the first change, to detect changes that
   * cancel out).
   */
  @Override
  public void update(Direction direction, Tuple tuple) {
    boolean before = memory.contains(tuple);
    if (direction == Direction.INSERT) {
      memory.add(tuple);
    } else if (!memory.remove(tuple)) {
      throw inconsistency(this, tuple);
    }
    boolean after = memory.contains(tuple);
    if (before == after) {
      return;
    }
    for (Map.Entry<TupleMask, Map<Tuple, Set<Tuple>>> index : indexes.entrySet()) {
      Tuple key = index.getKey().transform(tuple);
      if (after) {
        index.getValue().computeIfAbsent(key, k -> new HashSet<>()).add(tuple);
      } else {
        Set<Tuple> tuples = index.getValue().get(key);
        tuples.remove(tuple);
        if (tuples.isEmpty()) {
          index.getValue().remove(key);
        }
      }
    }
    if (!listeners.isEmpty()) {
      if (pendingChanges.isEmpty()) {
        network.registerPendingEvents(this);
      }
      pendingChanges.putIfAbsent(tuple, before);
    }
  }

  /**
   * Delivers the match events collected since the last delivery. Called by the
   * network at the end of the outermost transaction.
   * <p>
   * A tuple is reported only if its current match state differs from its
   * state before the first recorded change. Changes caused by listeners
   * during delivery are recorded anew and delivered in a later round.
   */
  public void deliverPendingEvents() {
    Map<Tuple, Boolean> changes = pendingChanges;
    pendingChanges = new LinkedHashMap<>();
    List<MatchListener> currentListeners = new ArrayList<>(listeners);
    for (Map.Entry<Tuple, Boolean> change : changes.entrySet()) {
      boolean before = change.getValue();
      boolean now = memory.contains(change.getKey());
      if (before == now) {
        continue;
      }
      Match match = new Match(this, change.getKey());
      for (MatchListener listener : currentListeners) {
        if (now) {
          listener.matchAppeared(match);
        } else {
          listener.matchDisappeared(match);
        }
      }
    }
  }

  // ---------------------------------------------------------------------------
  // Listeners
  // ---------------------------------------------------------------------------

  /**
   * Registers a listener notified about future match changes.
   *
   * @throws IllegalStateException if the network is propagating a change
   */
  public void addMatchListener(MatchListener listener) {
    addMatchListener(listener, false);
  }

  /**
   * Registers a listener notified about future match changes.
   *
   * @param fireForExistingMatches whether {@link MatchListener#matchAppeared}
   *                               is called immediately for all current matches
   * @throws IllegalStateException if the network is propagating a change
   */
  public void addMatchListener(MatchListener listener, boolean fireForExistingMatches) {
    network.checkStructuralChangeAllowed();
    listeners.add(listener);
    if (fireForExistingMatches) {
      for (Tuple tuple : new ArrayList<>(memory.elementSet())) {
        listener.matchAppeared(new Match(this, tuple));
      }
    }
  }

  public void removeMatchListener(MatchListener listener) {
    listeners.remove(listener);
  }

  // ---------------------------------------------------------------------------
  // Queries
  // ---------------------------------------------------------------------------

  /**
   * @param partialBinding one value per parameter, {@code null} for unbound
   *                       parameters and {@link Tuple#ABSENT} for absent
   *                       optional parameters; no values for querying all
   *                       matches
   * @return all matches compatible with the partial binding
   */
  public List<Match> getAllMatches(Object... partialBinding) {
    List<Match> result = new ArrayList<>();
    forEachMatch(result::add, partialBinding);
    return result;
  }

  /**
   * @param partialBinding see {@link #getAllMatches(Object...)}
   * @return some match compatible with the partial binding, if any
   */
  public Optional<Match> getOneArbitraryMatch(Object... partialBinding) {
    return matchingTuples(partialBinding).stream().findAny().map(t -> new Match(this, t));
  }

  /**
   * @param partialBinding see {@link #getAllMatches(Object...)}
   * @return whether there is a match compatible with the partial binding
   */
  public boolean hasMatch(Object... partialBinding) {
    return !matchingTuples(partialBinding).isEmpty();
  }

  /**
   * @param partialBinding see {@link #getAllMatches(Object...)}
   * @return the number of matches compatible with the partial binding
   */
  public int countMatches(Object... partialBinding) {
    return matchingTuples(partialBinding).size();
  }

  /**
   * Performs the action for each match compatible with the partial binding.
   * The matches are copied beforehand, so the action may report changes to
   * the network.
   *
   * @param partialBinding see {@link #getAllMatches(Object...)}
   */
  public void forEachMatch(Consumer<Match> action, Object... partialBinding) {
    for (Tuple tuple : new ArrayList<>(matchingTuples(partialBinding))) {
      action.accept(new Match(this, tuple));
    }
  }

  /**
   * @return all matches as tuples (unmodifiable view)
   */
  public Set<Tuple> getMatchTuples() {
    return Collections.unmodifiableSet(memory.elementSet());
  }

  /**
   * Looks up the tuples compatible with a partial binding: no or no bound
   * values yield all tuples, a full binding is a containment check, and
   * other bindings use an index over the bound positions.
   *
   * @return the compatible tuples (a view; must not be modified)
   * @throws IllegalArgumentException if the number of values is neither 0
   *                                  nor the arity
   */
  protected Set<Tuple> matchingTuples(Object... partialBinding) {
    if (partialBinding.length == 0) {
      return memory.elementSet();
    }
    if (partialBinding.length != getArity()) {
      throw new IllegalArgumentException("Expected " + getArity() + " values (null for "
          + "unbound parameters), got " + partialBinding.length);
    }
    List<Integer> bound = new ArrayList<>();
    List<Object> key = new ArrayList<>();
    for (int i = 0; i < partialBinding.length; i++) {
      if (partialBinding[i] != null) {
        bound.add(i);
        key.add(partialBinding[i]);
      }
    }
    if (bound.isEmpty()) {
      return memory.elementSet();
    }
    if (bound.size() == getArity()) {
      Tuple tuple = Tuple.of(partialBinding);
      return memory.contains(tuple) ? Set.of(tuple) : Set.of();
    }
    TupleMask mask = TupleMask.of(getArity(), bound.stream().mapToInt(i -> i).toArray());
    Set<Tuple> tuples = getOrCreateIndex(mask).get(Tuple.of(key.toArray()));
    return tuples == null ? Set.of() : Collections.unmodifiableSet(tuples);
  }

  /**
   * Returns the index for the given bound positions, building it from the
   * current matches on first use. Indexes are maintained by
   * {@link #update(Direction, Tuple)} afterwards.
   *
   * @param mask selects the bound positions
   * @return key (values of the bound positions) -> matching tuples
   */
  protected Map<Tuple, Set<Tuple>> getOrCreateIndex(TupleMask mask) {
    Map<Tuple, Set<Tuple>> index = indexes.get(mask);
    if (index == null) {
      index = new HashMap<>();
      for (Tuple tuple : memory.elementSet()) {
        index.computeIfAbsent(mask.transform(tuple), k -> new HashSet<>()).add(tuple);
      }
      indexes.put(mask, index);
    }
    return index;
  }

  // ---------------------------------------------------------------------------

  @Override
  protected void checkDisposable() {
    // productions have no children
  }

  /**
   * Detaches this production from its parent and drops all listeners and
   * undelivered events.
   */
  @Override
  protected void detachFromParents() {
    parent.detachChild(this);
    listeners.clear();
    pendingChanges.clear();
  }

  @Override
  public String toString() {
    return "ProductionNode" + parameterNames;
  }
}
