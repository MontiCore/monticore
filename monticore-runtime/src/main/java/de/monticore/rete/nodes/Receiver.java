/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;

/**
 * A node receiving deltas from a {@link TupleSource}.
 * <p>
 * Each call represents a single occurrence of the tuple; multiplicities are
 * transmitted as repeated calls.
 */
public interface Receiver {

  /**
   * Processes a single delta of a parent node. Implementations update their
   * state (if any) and propagate the resulting deltas to their own children.
   *
   * @param direction whether the tuple was inserted into or deleted from the parent
   * @param tuple     the inserted or deleted tuple
   * @throws IllegalStateException if a deletion of an unknown tuple is
   *                               detected, i.e. the network is inconsistent
   */
  void update(Direction direction, Tuple tuple);
}
