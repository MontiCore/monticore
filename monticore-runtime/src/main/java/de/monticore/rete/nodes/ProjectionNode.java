/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.Multiset;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;

/**
 * Projects the tuples of the parent by a {@link TupleMask}. Multiplicities
 * are preserved, i.e. tuples that become equal by the projection are counted
 * several times; use a {@link DistinctNode} to remove duplicates.
 */
public class ProjectionNode extends AbstractSingleParentNode {

  protected final TupleMask mask;

  /**
   * Creates the projection and attaches it to {@code parent}. A projection
   * keeps no memory, so no initialization is required.
   *
   * @param mask selects the output columns of the parent's tuples
   * @throws IllegalArgumentException if the mask does not fit the arity of {@code parent}
   */
  public ProjectionNode(ReteNetwork network, TupleSource parent, TupleMask mask) {
    super(network, parent, checkMask(parent, mask).transform(parent.getColumnTypes()));
    this.mask = mask;
    attach();
  }

  /**
   * @return {@code mask}
   * @throws IllegalArgumentException if the mask does not fit the arity of {@code parent}
   */
  protected static TupleMask checkMask(TupleSource parent, TupleMask mask) {
    if (mask.getSourceArity() != parent.getArity()) {
      throw new IllegalArgumentException("Mask " + mask + " does not fit arity "
          + parent.getArity() + " of " + parent);
    }
    return mask;
  }

  public TupleMask getMask() {
    return mask;
  }

  /**
   * Passes the delta on with the projected tuple.
   */
  @Override
  public void update(Direction direction, Tuple tuple) {
    propagate(direction, mask.transform(tuple));
  }

  /**
   * Adds the projections of the parent's tuples, keeping multiplicities.
   */
  @Override
  public void pullInto(Multiset<Tuple> collector) {
    for (Multiset.Entry<Tuple> entry : pull(parent).entrySet()) {
      collector.add(mask.transform(entry.getElement()), entry.getCount());
    }
  }
}
