/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.aggregation;

/**
 * An incrementally maintainable aggregation over a multiset of values, used by
 * {@link de.monticore.rete.nodes.AggregatorNode}.
 * <p>
 * Accumulators may be mutable; {@link #update} may return the given
 * accumulator after modifying it.
 *
 * @param <V> type of the aggregated values
 * @param <A> type of the accumulator
 * @param <R> type of the aggregation result
 */
public interface AggregationOperator<V, A, R> {

  /**
   * @return the accumulator of an empty multiset
   */
  A createNeutral();

  /**
   * @return the accumulator after inserting ({@code isInsertion == true}) or
   * removing one occurrence of {@code value}
   */
  A update(A accumulator, V value, boolean isInsertion);

  /**
   * @return the aggregation result of a non-empty multiset; must not be {@code null}
   */
  R getAggregate(A accumulator);

  /**
   * @param valueType the type of the aggregated values
   * @return the type of the aggregation results
   */
  Class<?> getResultType(Class<?> valueType);
}
