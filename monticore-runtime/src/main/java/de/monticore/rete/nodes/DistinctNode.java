/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;

/**
 * Removes duplicates: contains each tuple of the parent exactly once. Only
 * the transitions of a tuple's multiplicity between 0 and 1 are propagated.
 */
public class DistinctNode extends AbstractSingleParentNode {

  protected final Multiset<Tuple> memory = HashMultiset.create();

  /**
   * Creates the node, initializes its memory from the current contents of
   * {@code parent} and attaches it to {@code parent}.
   */
  public DistinctNode(ReteNetwork network, TupleSource parent) {
    super(network, parent, parent.getColumnTypes(), parent.getColumnOptionality());
    parent.pullInto(memory);
    attach();
  }

  /**
   * Counts the occurrences of the tuple and passes the delta on only if the
   * tuple appears for the first time or disappears completely.
   */
  @Override
  public void update(Direction direction, Tuple tuple) {
    if (direction == Direction.INSERT) {
      if (memory.add(tuple, 1) == 0) {
        propagate(direction, tuple);
      }
    } else {
      int previous = memory.remove(tuple, 1);
      if (previous == 0) {
        throw inconsistency(this, tuple);
      }
      if (previous == 1) {
        propagate(direction, tuple);
      }
    }
  }

  /**
   * Adds each distinct tuple of the parent exactly once.
   */
  @Override
  public void pullInto(Multiset<Tuple> collector) {
    collector.addAll(memory.elementSet());
  }
}
