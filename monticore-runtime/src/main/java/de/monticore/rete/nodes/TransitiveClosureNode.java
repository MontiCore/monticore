/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Transitive closure of a binary relation: contains {@code [x, y]} iff
 * {@code y} is reachable from {@code x} via one or more edges of the parent
 * (e.g. the ancestor relation derived from the parent-child relation of an
 * AST). The parent may contain cycles; the result has set semantics.
 * <p>
 * Insertions are handled by connecting all predecessors of the edge's source
 * with all successors of its target. Deletions re-check the reachability of
 * all potentially affected pairs (DRed-like over-deletion and re-derivation),
 * which costs a graph traversal per affected source node.
 */
public class TransitiveClosureNode extends AbstractSingleParentNode {

  protected final Multiset<Tuple> edges = HashMultiset.create();

  /**
   * x -> direct successors of x (distinct edges)
   */
  protected final Map<Object, Set<Object>> successors = new HashMap<>();

  /**
   * x -> all y reachable from x
   */
  protected final Map<Object, Set<Object>> reachableFrom = new HashMap<>();

  /**
   * y -> all x from which y is reachable
   */
  protected final Map<Object, Set<Object>> reachingTo = new HashMap<>();

  /**
   * Creates the node, computes the closure of the current contents of
   * {@code parent} and attaches it to {@code parent}.
   *
   * @param parent a binary relation {@code [source, target]}
   * @throws IllegalArgumentException if {@code parent} is not binary or has
   *                                  optional columns
   */
  public TransitiveClosureNode(ReteNetwork network, TupleSource parent) {
    super(network, parent, computeColumnTypes(parent), ColumnTypes.noOptionalColumns(2));
    for (Multiset.Entry<Tuple> entry : pull(parent).entrySet()) {
      for (int i = 0; i < entry.getCount(); i++) {
        update(Direction.INSERT, entry.getElement());
      }
    }
    attach();
  }

  /**
   * Both output columns may contain sources as well as targets of edges, so
   * both have the common supertype of the parent's column types. Edges must
   * not contain {@link Tuple#ABSENT}, so the parent's columns must not be
   * optional.
   *
   * @throws IllegalArgumentException if {@code parent} is not binary or has
   *                                  optional columns
   */
  protected static List<Class<?>> computeColumnTypes(TupleSource parent) {
    if (parent.getArity() != 2) {
      throw new IllegalArgumentException("Transitive closure requires a binary relation, got "
          + parent);
    }
    ColumnTypes.checkNotOptional(parent, 0, "the source column of the edges");
    ColumnTypes.checkNotOptional(parent, 1, "the target column of the edges");
    Class<?> type = ColumnTypes.commonSupertype(parent.getColumnTypes().get(0),
        parent.getColumnTypes().get(1));
    return List.of(type, type);
  }

  /**
   * Counts the occurrences of the edge; the reachability relation is only
   * updated if the edge appears for the first time or disappears completely.
   */
  @Override
  public void update(Direction direction, Tuple tuple) {
    if (direction == Direction.INSERT) {
      if (edges.add(tuple, 1) == 0) {
        insertEdge(tuple.get(0), tuple.get(1));
      }
    } else {
      int previous = edges.remove(tuple, 1);
      if (previous == 0) {
        throw inconsistency(this, tuple);
      }
      if (previous == 1) {
        deleteEdge(tuple.get(0), tuple.get(1));
      }
    }
  }

  /**
   * Adds a new distinct edge: every node reaching {@code source} (and
   * {@code source} itself) now reaches every node reachable from
   * {@code target} (and {@code target} itself). Newly reachable pairs are
   * inserted.
   */
  protected void insertEdge(Object source, Object target) {
    successors.computeIfAbsent(source, k -> new HashSet<>()).add(target);
    if (isReachable(source, target)) {
      // every path using the new edge already exists
      return;
    }
    List<Object> sources = withAll(source, reachingTo.get(source));
    List<Object> targets = withAll(target, reachableFrom.get(target));
    for (Object x : sources) {
      for (Object y : targets) {
        if (addReachability(x, y)) {
          propagate(Direction.INSERT, Tuple.of(x, y));
        }
      }
    }
  }

  /**
   * Removes a distinct edge. Only pairs {@code (x, y)} with {@code x}
   * reaching {@code source} and {@code y} reachable from {@code target} may
   * have depended on the edge; for each such {@code x}, reachability is
   * recomputed by a graph traversal and pairs no longer reachable are deleted.
   */
  protected void deleteEdge(Object source, Object target) {
    Set<Object> succ = successors.get(source);
    succ.remove(target);
    if (succ.isEmpty()) {
      successors.remove(source);
    }
    // all pairs whose derivation may have used the edge
    List<Object> sources = withAll(source, reachingTo.get(source));
    List<Object> targets = withAll(target, reachableFrom.get(target));
    for (Object x : sources) {
      Set<Object> stillReachable = computeReachable(x);
      for (Object y : targets) {
        if (!stillReachable.contains(y) && removeReachability(x, y)) {
          propagate(Direction.DELETE, Tuple.of(x, y));
        }
      }
    }
  }

  /**
   * @return a new list containing {@code first} followed by the elements of
   * {@code others} (which may be {@code null}); copying allows modifying the
   * reachability sets while iterating
   */
  protected static List<Object> withAll(Object first, Set<Object> others) {
    List<Object> result = new ArrayList<>(others == null ? 1 : others.size() + 1);
    result.add(first);
    if (others != null) {
      result.addAll(others);
    }
    return result;
  }

  /**
   * Computes the nodes reachable from {@code start} via one or more edges by
   * a breadth-first traversal of the current edges.
   *
   * @return the reachable nodes; contains {@code start} only if it lies on a cycle
   */
  protected Set<Object> computeReachable(Object start) {
    Set<Object> visited = new HashSet<>();
    Deque<Object> queue = new ArrayDeque<>();
    queue.add(start);
    while (!queue.isEmpty()) {
      Set<Object> next = successors.get(queue.poll());
      if (next != null) {
        for (Object y : next) {
          if (visited.add(y)) {
            queue.add(y);
          }
        }
      }
    }
    return visited;
  }

  /**
   * @return whether {@code target} is reachable from {@code source} via one
   * or more edges
   */
  public boolean isReachable(Object source, Object target) {
    Set<Object> reachable = reachableFrom.get(source);
    return reachable != null && reachable.contains(target);
  }

  /**
   * @return all nodes reachable from {@code source} (unmodifiable)
   */
  public Set<Object> getReachableFrom(Object source) {
    Set<Object> reachable = reachableFrom.get(source);
    return reachable == null ? Collections.emptySet() : Collections.unmodifiableSet(reachable);
  }

  /**
   * Records that {@code y} is reachable from {@code x} in both directions of
   * the reachability index.
   *
   * @return whether the pair was not recorded before
   */
  protected boolean addReachability(Object x, Object y) {
    if (reachableFrom.computeIfAbsent(x, k -> new HashSet<>()).add(y)) {
      reachingTo.computeIfAbsent(y, k -> new HashSet<>()).add(x);
      return true;
    }
    return false;
  }

  /**
   * Removes the pair {@code (x, y)} from both directions of the reachability
   * index, dropping empty sets.
   *
   * @return whether the pair was recorded before
   */
  protected boolean removeReachability(Object x, Object y) {
    Set<Object> forward = reachableFrom.get(x);
    if (forward == null || !forward.remove(y)) {
      return false;
    }
    if (forward.isEmpty()) {
      reachableFrom.remove(x);
    }
    Set<Object> backward = reachingTo.get(y);
    backward.remove(x);
    if (backward.isEmpty()) {
      reachingTo.remove(y);
    }
    return true;
  }

  /**
   * Adds each reachable pair {@code [x, y]} exactly once.
   */
  @Override
  public void pullInto(Multiset<Tuple> collector) {
    for (Map.Entry<Object, Set<Object>> entry : reachableFrom.entrySet()) {
      for (Object y : entry.getValue()) {
        collector.add(Tuple.of(entry.getKey(), y));
      }
    }
  }
}
