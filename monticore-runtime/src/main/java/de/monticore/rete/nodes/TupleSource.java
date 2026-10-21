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
   * @return for each column, a type all values of that column are instances of
   */
  List<Class<?>> getColumnTypes();

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
