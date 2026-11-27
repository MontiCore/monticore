/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete;

import de.monticore.ast.ASTNode;
import de.monticore.rete.aggregation.AggregationOperator;
import de.monticore.rete.aggregation.Aggregators;
import de.monticore.rete.nodes.AggregatorNode;
import de.monticore.rete.nodes.Direction;
import de.monticore.rete.nodes.DistinctNode;
import de.monticore.rete.nodes.EvalNode;
import de.monticore.rete.nodes.ExistenceNode;
import de.monticore.rete.nodes.FeatureInputNode;
import de.monticore.rete.nodes.FilterNode;
import de.monticore.rete.nodes.Indexer;
import de.monticore.rete.nodes.JoinNode;
import de.monticore.rete.nodes.LeftOuterJoinNode;
import de.monticore.rete.nodes.ProductionNode;
import de.monticore.rete.nodes.ProjectionNode;
import de.monticore.rete.nodes.Receiver;
import de.monticore.rete.nodes.TransitiveClosureNode;
import de.monticore.rete.nodes.TupleSource;
import de.monticore.rete.nodes.TypeInputNode;
import de.monticore.rete.nodes.UnionNode;
import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An incremental, Rete-based pattern matching network over MontiCore ASTs.
 *
 * <h2>Construction</h2>
 * The network is built manually from nodes created via the factory methods
 * of this class (or the node constructors). Input nodes ({@link #typeInput},
 * {@link #featureInput}) are canonical per type/feature and have to be
 * created before the first change notification. All other nodes may also be
 * added later; they initialize themselves from the current contents of their
 * parents.
 *
 * <h2>Change notifications</h2>
 * Model changes are reported via {@link #nodeAdded}, {@link #nodeRemoved},
 * {@link #featureValueAdded} and {@link #featureValueRemoved}. Reporting the
 * removal of an unknown fact or adding an AST node twice raises an
 * {@link IllegalStateException}. Several notifications can be grouped by
 * {@link #transaction(Runnable)}.
 *
 * <h2>Propagation</h2>
 * Each notification is propagated synchronously, i.e. all matches are up to
 * date when the notification method returns. Internally, deltas are passed
 * between nodes via a FIFO queue, while the listeners of an {@link Indexer}
 * are notified directly after the indexer's memory has been updated. This
 * guarantees that every join sees a memory state consistent with the deltas
 * it has already processed.
 *
 * <h2>Match events</h2>
 * {@link MatchListener}s are notified at the end of the outermost
 * transaction, when the whole network is consistent. Changes cancelling each
 * other out within a transaction are not reported.
 *
 * <h2>Threading</h2>
 * A network is not thread-safe and must be confined to a single thread.
 * Different networks are independent of each other.
 */
public class ReteNetwork {

  protected final Map<Class<?>, TypeInputNode<?>> typeInputs = new LinkedHashMap<>();

  protected final Map<Feature<?, ?>, FeatureInputNode<?, ?>> featureInputs =
      new IdentityHashMap<>();

  /**
   * concrete AST class -> all type inputs the instances of that class belong to
   */
  protected final Map<Class<?>, List<TypeInputNode<?>>> typeDispatch = new HashMap<>();

  protected final ArrayDeque<PendingDelta> propagationQueue = new ArrayDeque<>();

  protected final Set<ProductionNode> productionsWithPendingEvents = new LinkedHashSet<>();

  protected boolean factsReceived;

  protected boolean propagating;

  protected boolean deliveringEvents;

  protected int transactionDepth;

  protected record PendingDelta(Receiver receiver, Direction direction, Tuple tuple, int count) {
  }

  // ---------------------------------------------------------------------------
  // Input nodes
  // ---------------------------------------------------------------------------

  /**
   * @return the input node of the given AST type, created if necessary
   * @throws IllegalStateException if the node has to be created after facts
   *                               have already been reported
   */
  @SuppressWarnings("unchecked")
  public <T extends ASTNode> TypeInputNode<T> typeInput(Class<T> type) {
    TypeInputNode<?> input = typeInputs.get(type);
    return input != null ? (TypeInputNode<T>) input : new TypeInputNode<>(this, type);
  }

  /**
   * @return the input node of the given feature, created if necessary
   * @throws IllegalStateException if the node has to be created after facts
   *                               have already been reported
   */
  @SuppressWarnings("unchecked")
  public <O extends ASTNode, V> FeatureInputNode<O, V> featureInput(Feature<O, V> feature) {
    FeatureInputNode<?, ?> input = featureInputs.get(feature);
    return input != null ? (FeatureInputNode<O, V>) input : new FeatureInputNode<>(this, feature);
  }

  public Collection<TypeInputNode<?>> getTypeInputs() {
    return Collections.unmodifiableCollection(typeInputs.values());
  }

  public Collection<FeatureInputNode<?, ?>> getFeatureInputs() {
    return Collections.unmodifiableCollection(featureInputs.values());
  }

  /**
   * Registers a newly created type input node. Called by its constructor.
   *
   * @throws IllegalStateException if facts have already been reported or
   *                               there already is an input node for the type
   */
  public void registerTypeInput(TypeInputNode<?> input) {
    checkInputCreationAllowed();
    if (typeInputs.putIfAbsent(input.getType(), input) != null) {
      throw new IllegalStateException("There already is an input node for " + input.getType());
    }
    typeDispatch.clear();
  }

  /**
   * Registers a newly created feature input node. Called by its constructor.
   *
   * @throws IllegalStateException if facts have already been reported or
   *                               there already is an input node for the feature
   */
  public void registerFeatureInput(FeatureInputNode<?, ?> input) {
    checkInputCreationAllowed();
    if (featureInputs.putIfAbsent(input.getFeature(), input) != null) {
      throw new IllegalStateException("There already is an input node for " + input.getFeature());
    }
  }

  /**
   * Unregisters a disposed type input node, so that AST nodes are no longer
   * dispatched to it. Called by the node only.
   */
  public void unregisterTypeInput(TypeInputNode<?> input) {
    typeInputs.remove(input.getType(), input);
    typeDispatch.clear();
  }

  /**
   * Unregisters a disposed feature input node, so that values of its feature
   * are ignored afterwards. Called by the node only.
   */
  public void unregisterFeatureInput(FeatureInputNode<?, ?> input) {
    featureInputs.remove(input.getFeature(), input);
  }

  /**
   * @throws IllegalStateException if facts have already been reported, as a
   *                               new input node could not be initialized
   */
  protected void checkInputCreationAllowed() {
    if (factsReceived) {
      throw new IllegalStateException("Input nodes must be created before the first change "
          + "notification, as facts reported earlier are not retained");
    }
  }

  // ---------------------------------------------------------------------------
  // Factory methods for inner nodes
  // ---------------------------------------------------------------------------

  /**
   * Creates a new indexer (existing indexers are not reused).
   *
   * @param keyColumns the columns of {@code source} forming the index key
   */
  public Indexer indexer(TupleSource source, int... keyColumns) {
    return indexer(source, TupleMask.of(source.getArity(), keyColumns));
  }

  /**
   * Creates a new indexer (existing indexers are not reused).
   *
   * @param keyMask selects the columns of {@code source} forming the index key
   */
  public Indexer indexer(TupleSource source, TupleMask keyMask) {
    return new Indexer(this, source, keyMask);
  }

  /**
   * Joins on equal keys. The result consists of the left columns followed by
   * the non-key columns of the right side.
   */
  public JoinNode join(Indexer left, Indexer right) {
    return new JoinNode(this, left, right);
  }

  /**
   * Joins on equal keys. The result consists of the left columns followed by
   * the right columns selected by {@code rightOutputMask}.
   */
  public JoinNode join(Indexer left, Indexer right, TupleMask rightOutputMask) {
    return new JoinNode(this, left, right, rightOutputMask);
  }

  /**
   * Left outer join on equal keys (optional pattern elements). The result
   * consists of the left columns followed by the non-key columns of the right
   * side, which are {@link Tuple#ABSENT} for left tuples without partner.
   *
   * @see LeftOuterJoinNode
   */
  public LeftOuterJoinNode leftOuterJoin(Indexer left, Indexer right) {
    return new LeftOuterJoinNode(this, left, right);
  }

  /**
   * Left outer join on equal keys for which the condition holds. The result
   * consists of the left columns followed by the non-key columns of the right
   * side, which are {@link Tuple#ABSENT} for left tuples without partner.
   *
   * @param condition a deterministic predicate only depending on tuple
   *                  values, evaluated for the joined tuple (left columns
   *                  followed by the non-key columns of the right side)
   * @see LeftOuterJoinNode
   */
  public LeftOuterJoinNode leftOuterJoin(Indexer left, Indexer right,
      Predicate<Tuple> condition) {
    return new LeftOuterJoinNode(this, left, right, right.getMask().complement(), condition);
  }

  /**
   * Left outer join on equal keys for which the condition holds. The result
   * consists of the left columns followed by the right columns selected by
   * {@code rightOutputMask}, which are {@link Tuple#ABSENT} for left tuples
   * without partner.
   *
   * @param condition a deterministic predicate only depending on tuple
   *                  values, evaluated for the joined tuple
   * @see LeftOuterJoinNode
   */
  public LeftOuterJoinNode leftOuterJoin(Indexer left, Indexer right, TupleMask rightOutputMask,
      Predicate<Tuple> condition) {
    return new LeftOuterJoinNode(this, left, right, rightOutputMask, condition);
  }

  /**
   * @return the left tuples for which a right tuple with equal key exists
   */
  public ExistenceNode semiJoin(Indexer left, Indexer right) {
    return new ExistenceNode(this, left, right, false);
  }

  /**
   * @param condition a deterministic predicate only depending on tuple
   *                  values, evaluated for the joined tuple (left columns
   *                  followed by the non-key columns of the right side)
   * @return the left tuples for which a right tuple with equal key exists for
   * which the condition holds
   * @see ExistenceNode
   */
  public ExistenceNode semiJoin(Indexer left, Indexer right, Predicate<Tuple> condition) {
    return new ExistenceNode(this, left, right, false, condition);
  }

  /**
   * @return the left tuples for which no right tuple with equal key exists
   * (negative application condition)
   */
  public ExistenceNode antiJoin(Indexer left, Indexer right) {
    return new ExistenceNode(this, left, right, true);
  }

  /**
   * Anti join with a condition spanning both sides, e.g. the injectivity
   * between the objects of a negative application condition and the objects
   * of the positive pattern.
   *
   * @param condition a deterministic predicate only depending on tuple
   *                  values, evaluated for the joined tuple (left columns
   *                  followed by the non-key columns of the right side)
   * @return the left tuples for which no right tuple with equal key exists
   * for which the condition holds (negative application condition)
   * @see ExistenceNode
   */
  public ExistenceNode antiJoin(Indexer left, Indexer right, Predicate<Tuple> condition) {
    return new ExistenceNode(this, left, right, true, condition);
  }

  /**
   * @param predicate a deterministic predicate only depending on tuple values
   * @return a node keeping the tuples of {@code source} satisfying the predicate
   */
  public FilterNode filter(TupleSource source, Predicate<Tuple> predicate) {
    return new FilterNode(this, source, predicate);
  }

  /**
   * As in joins, {@link Tuple#ABSENT} equals no value, not even
   * {@code ABSENT}, so tuples with {@code ABSENT} in one of the columns are
   * dropped.
   *
   * @return a filter keeping the tuples whose given columns hold equal values
   */
  public FilterNode equality(TupleSource source, int column, int... otherColumns) {
    return filter(source, t -> {
      if (t.isAbsent(column)) {
        return false;
      }
      for (int other : otherColumns) {
        if (t.isAbsent(other) || !t.get(column).equals(t.get(other))) {
          return false;
        }
      }
      return true;
    });
  }

  /**
   * As {@link Tuple#ABSENT} equals no value, not even {@code ABSENT}, tuples
   * with {@code ABSENT} in one of the columns are kept. For injectivity, this
   * means that an unmatched optional element does not conflict with any other
   * element.
   *
   * @return a filter keeping the tuples whose given columns hold different values
   */
  public FilterNode inequality(TupleSource source, int column, int otherColumn) {
    return filter(source, t -> t.isAbsent(column) || t.isAbsent(otherColumn)
        || !t.get(column).equals(t.get(otherColumn)));
  }

  /**
   * @param function   a deterministic function only depending on tuple
   *                   values; {@code null} results drop the tuple
   * @param resultType the type of the computed values
   * @return a node extending each tuple of {@code source} by the computed value
   */
  public EvalNode eval(TupleSource source, Function<Tuple, ?> function, Class<?> resultType) {
    return new EvalNode(this, source, function, resultType);
  }

  /**
   * @param columns the columns of {@code source} to keep, in output order
   * @return a node projecting the tuples of {@code source} (keeping multiplicities)
   */
  public ProjectionNode project(TupleSource source, int... columns) {
    return project(source, TupleMask.of(source.getArity(), columns));
  }

  /**
   * @param mask selects the columns of {@code source} to keep
   * @return a node projecting the tuples of {@code source} (keeping multiplicities)
   */
  public ProjectionNode project(TupleSource source, TupleMask mask) {
    return new ProjectionNode(this, source, mask);
  }

  /**
   * @return a node containing each tuple of {@code source} exactly once
   */
  public DistinctNode distinct(TupleSource source) {
    return new DistinctNode(this, source);
  }

  /**
   * @param sources nodes of equal arity
   * @return a node containing the multiset union of all sources
   */
  public UnionNode union(TupleSource... sources) {
    return new UnionNode(this, Arrays.asList(sources));
  }

  /**
   * Counts the tuples of each group: the result consists of the group columns
   * followed by the count.
   */
  public AggregatorNode count(TupleSource source, int... groupColumns) {
    return new AggregatorNode(this, source, TupleMask.of(source.getArity(), groupColumns),
        AggregatorNode.WHOLE_TUPLE, Aggregators.count());
  }

  /**
   * Aggregates one column per group: the result consists of the group
   * columns followed by the aggregation result.
   */
  public AggregatorNode aggregate(TupleSource source, TupleMask groupMask, int aggregatedColumn,
      AggregationOperator<?, ?, ?> operator) {
    return new AggregatorNode(this, source, groupMask, aggregatedColumn, operator);
  }

  /**
   * @param source a binary relation {@code [from, to]}
   * @return a node containing all pairs connected via one or more edges of {@code source}
   */
  public TransitiveClosureNode transitiveClosure(TupleSource source) {
    return new TransitiveClosureNode(this, source);
  }

  /**
   * @param source         a node whose columns are all AST types
   * @param parameterNames one unique name per column of {@code source}
   * @return a production providing the matches of {@code source}
   */
  public ProductionNode production(TupleSource source, String... parameterNames) {
    return new ProductionNode(this, source, List.of(parameterNames));
  }

  // ---------------------------------------------------------------------------
  // Change notifications
  // ---------------------------------------------------------------------------

  /**
   * @return whether some type input is interested in the given AST node
   */
  public boolean isRelevant(ASTNode node) {
    return !dispatch(node).isEmpty();
  }

  /**
   * @return whether there is an input node for the given feature
   */
  public boolean isRelevant(Feature<?, ?> feature) {
    return featureInputs.containsKey(feature);
  }

  /**
   * Reports that an AST node has been added to the model. The node is added
   * to all type inputs whose type it is an instance of.
   *
   * @throws IllegalStateException if the node has already been added
   */
  public void nodeAdded(ASTNode node) {
    Objects.requireNonNull(node);
    List<TypeInputNode<?>> inputs = dispatch(node);
    for (TypeInputNode<?> input : inputs) {
      if (input.contains(node)) {
        throw new IllegalStateException("AST node " + node + " was already added");
      }
    }
    notifyChange(() -> inputs.forEach(input -> input.insert(node)));
  }

  /**
   * Reports that an AST node has been removed from the model. The values of
   * its features have to be removed separately.
   *
   * @throws IllegalStateException if the node is not known
   */
  public void nodeRemoved(ASTNode node) {
    Objects.requireNonNull(node);
    List<TypeInputNode<?>> inputs = dispatch(node);
    for (TypeInputNode<?> input : inputs) {
      if (!input.contains(node)) {
        throw new IllegalStateException("AST node " + node + " is not known");
      }
    }
    notifyChange(() -> inputs.forEach(input -> input.delete(node)));
  }

  /**
   * Reports that {@code value} has been added to the given feature of
   * {@code owner}. Values of features without input node are ignored.
   *
   * @throws ClassCastException if owner or value do not match the feature's
   *                            types (only possible with raw types)
   */
  public <O extends ASTNode, V> void featureValueAdded(Feature<O, V> feature, O owner, V value) {
    FeatureInputNode<O, V> input = checkFeatureFact(feature, owner, value);
    notifyChange(() -> {
      if (input != null) {
        input.insert(owner, value);
      }
    });
  }

  /**
   * Reports that {@code value} has been removed from the given feature of
   * {@code owner}. Values of features without input node are ignored.
   *
   * @throws IllegalStateException if the value is not known
   */
  public <O extends ASTNode, V> void featureValueRemoved(Feature<O, V> feature, O owner,
      V value) {
    FeatureInputNode<O, V> input = checkFeatureFact(feature, owner, value);
    if (input != null && input.count(owner, value) == 0) {
      throw new IllegalStateException("Value " + value + " of feature " + feature + " of "
          + owner + " is not known");
    }
    notifyChange(() -> {
      if (input != null) {
        input.delete(owner, value);
      }
    });
  }

  /**
   * Reports that the value of a single-valued feature changed from
   * {@code oldValue} to {@code newValue}, within a single transaction.
   */
  public <O extends ASTNode, V> void featureValueChanged(Feature<O, V> feature, O owner,
      V oldValue, V newValue) {
    transaction(() -> {
      featureValueRemoved(feature, owner, oldValue);
      featureValueAdded(feature, owner, newValue);
    });
  }

  /**
   * Validates a reported feature fact at runtime (guarding against raw types).
   *
   * @return the input node of the feature, or {@code null} if the feature is
   * not relevant
   * @throws NullPointerException if owner or value are {@code null}
   * @throws ClassCastException   if owner or value do not match the feature's types
   */
  @SuppressWarnings("unchecked")
  protected <O extends ASTNode, V> FeatureInputNode<O, V> checkFeatureFact(Feature<O, V> feature,
      O owner, V value) {
    if (!feature.getOwnerType().isInstance(Objects.requireNonNull(owner))) {
      throw new ClassCastException(owner + " is not an instance of the owner type of " + feature);
    }
    if (!feature.getValueType().isInstance(Objects.requireNonNull(value))) {
      throw new ClassCastException(value + " is not an instance of the value type of " + feature);
    }
    return (FeatureInputNode<O, V>) featureInputs.get(feature);
  }

  /**
   * Determines the type inputs an AST node belongs to, i.e. those whose type
   * is a supertype of the node's class. The result is cached per concrete
   * class; the cache is cleared whenever type inputs are (un)registered.
   */
  protected List<TypeInputNode<?>> dispatch(ASTNode node) {
    return typeDispatch.computeIfAbsent(node.getClass(), cls -> {
      List<TypeInputNode<?>> result = new ArrayList<>();
      for (TypeInputNode<?> input : typeInputs.values()) {
        if (input.getType().isAssignableFrom(cls)) {
          result.add(input);
        }
      }
      return result;
    });
  }

  /**
   * Executes the given changes as one transaction: match events are
   * delivered after the transaction, and changes cancelling each other out
   * are not reported. Transactions may be nested; events are delivered at
   * the end of the outermost one.
   * <p>
   * If {@code changes} throws an exception, no events are delivered; the
   * pending events are delivered at the end of the next transaction.
   *
   * @throws IllegalStateException if called during propagation (e.g. from a
   *                               filter or eval function)
   */
  public void transaction(Runnable changes) {
    if (propagating) {
      throw new IllegalStateException("Transactions cannot be started during propagation");
    }
    transactionDepth++;
    boolean completed = false;
    try {
      changes.run();
      completed = true;
    } finally {
      transactionDepth--;
    }
    if (completed && transactionDepth == 0) {
      deliverEvents();
    }
  }

  public boolean isInTransaction() {
    return transactionDepth > 0;
  }

  /**
   * Executes a change of input nodes within a transaction and propagates it
   * through the whole network before returning. If the propagation fails,
   * the remaining queued deltas are discarded and the network has to be
   * considered inconsistent.
   *
   * @param inputChange inserts or deletes facts of input nodes
   */
  protected void notifyChange(Runnable inputChange) {
    transaction(() -> {
      factsReceived = true;
      propagating = true;
      try {
        inputChange.run();
        drainQueue();
      } finally {
        propagationQueue.clear();
        propagating = false;
      }
    });
  }

  // ---------------------------------------------------------------------------
  // Propagation (used by nodes)
  // ---------------------------------------------------------------------------

  /**
   * Schedules the delivery of a delta to a receiver. Called by nodes only.
   */
  public void enqueue(Receiver receiver, Direction direction, Tuple tuple, int count) {
    propagationQueue.add(new PendingDelta(receiver, direction, tuple, count));
  }

  /**
   * Delivers queued deltas in FIFO order until the queue is empty. Deltas
   * produced while delivering are appended to the queue.
   */
  protected void drainQueue() {
    PendingDelta delta;
    while ((delta = propagationQueue.poll()) != null) {
      for (int i = 0; i < delta.count(); i++) {
        delta.receiver().update(delta.direction(), delta.tuple());
      }
    }
  }

  public boolean isPropagating() {
    return propagating;
  }

  /**
   * @throws IllegalStateException if the network is not propagating a change
   */
  public void checkPropagating() {
    if (!propagating) {
      throw new IllegalStateException("Input nodes must only be modified via the change "
          + "notification methods of the network");
    }
  }

  /**
   * @throws IllegalStateException if the network is propagating a change
   */
  public void checkStructuralChangeAllowed() {
    if (propagating) {
      throw new IllegalStateException("The network must not be modified during propagation");
    }
  }

  // ---------------------------------------------------------------------------
  // Match events (used by production nodes)
  // ---------------------------------------------------------------------------

  /**
   * Registers a production with undelivered match events. Called by
   * productions only.
   */
  public void registerPendingEvents(ProductionNode production) {
    productionsWithPendingEvents.add(production);
  }

  /**
   * Delivers the pending events of all productions. Events caused by
   * listeners (via further notifications) are delivered in subsequent rounds
   * of the same loop instead of recursively.
   */
  protected void deliverEvents() {
    if (deliveringEvents) {
      // events caused by listeners are delivered by the running loop
      return;
    }
    deliveringEvents = true;
    try {
      while (!productionsWithPendingEvents.isEmpty()) {
        List<ProductionNode> productions = new ArrayList<>(productionsWithPendingEvents);
        productionsWithPendingEvents.clear();
        for (ProductionNode production : productions) {
          production.deliverPendingEvents();
        }
      }
    } finally {
      deliveringEvents = false;
    }
  }
}
