/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.Multiset;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;

import java.util.List;

/**
 * A node producing a multiset of tuples of fixed arity. Changes of the
 * multiset are propagated as deltas to all attached {@link Receiver}s.
 */
public interface TupleSource {

  ReteNetwork getNetwork();

  int getArity();

  /**
   * @return for each column, a type all values of that column are instances
   * of (except {@link Tuple#ABSENT} in optional columns)
   */
  List<Class<?>> getColumnTypes();

  /**
   * @return for each column, whether it is optional, i.e. may contain
   * {@link Tuple#ABSENT} instead of a value of its type
   */
  List<Boolean> getColumnOptionality();

  /**
   * @return whether the given column may contain {@link Tuple#ABSENT}
   */
  default boolean isOptional(int column) {
    return getColumnOptionality().get(column);
  }

  /**
   * @return whether any column may contain {@link Tuple#ABSENT}
   */
  default boolean hasOptionalColumns() {
    return getColumnOptionality().contains(true);
  }

  /**
   * Attaches a child receiving all future deltas of this node. The child is
   * responsible for initializing itself from the current contents (see
   * {@link #pullInto(Multiset)}) before attaching.
   *
   * @throws IllegalStateException if the network is propagating a change or
   *                               this node is disposed
   */
  void attachChild(Receiver child);

  /**
   * Detaches a child, which then no longer receives deltas of this node.
   *
   * @throws IllegalStateException if the network is propagating a change
   */
  void detachChild(Receiver child);

  List<Receiver> getChildren();

  /**
   * Adds the current contents of this node (with multiplicities) to the given
   * collector. Used to initialize nodes attached to an already populated
   * network.
   */
  void pullInto(Multiset<Tuple> collector);
}
