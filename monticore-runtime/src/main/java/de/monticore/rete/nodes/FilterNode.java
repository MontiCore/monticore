/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.Multiset;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;

import java.util.function.Predicate;

/**
 * Contains the tuples of the parent satisfying a predicate (check
 * constraint).
 * <p>
 * The predicate must be deterministic and must only depend on the values of
 * the tuple. In particular, it must not read mutable state of AST nodes, as
 * changes of that state are not tracked by the network; use
 * {@link de.monticore.rete.Feature}s for such state instead. Values of
 * optional columns may be {@link Tuple#ABSENT}, which the getters of the
 * tuple return as {@code null}. The output columns (incl. their optionality)
 * are those of the parent.
 */
public class FilterNode extends AbstractSingleParentNode {

  protected final Predicate<Tuple> predicate;

  /**
   * Creates the filter and attaches it to {@code parent}. A filter keeps no
   * memory, so no initialization is required.
   *
   * @param predicate a deterministic predicate over the parent's tuples
   */
  public FilterNode(ReteNetwork network, TupleSource parent, Predicate<Tuple> predicate) {
    super(network, parent, parent.getColumnTypes(), parent.getColumnOptionality());
    this.predicate = predicate;
    attach();
  }

  /**
   * Passes the delta on if the tuple satisfies the predicate.
   */
  @Override
  public void update(Direction direction, Tuple tuple) {
    if (predicate.test(tuple)) {
      propagate(direction, tuple);
    }
  }

  /**
   * Adds the parent's tuples satisfying the predicate.
   */
  @Override
  public void pullInto(Multiset<Tuple> collector) {
    for (Multiset.Entry<Tuple> entry : pull(parent).entrySet()) {
      if (predicate.test(entry.getElement())) {
        collector.add(entry.getElement(), entry.getCount());
      }
    }
  }
}
