/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.aggregation;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Standard {@link AggregationOperator}s.
 */
public final class Aggregators {

  private Aggregators() {
  }

  /**
   * Counts the aggregated values (with multiplicities).
   */
  public static AggregationOperator<Object, Long, Long> count() {
    return new AggregationOperator<>() {
      @Override
      public Long createNeutral() {
        return 0L;
      }

      @Override
      public Long update(Long accumulator, Object value, boolean isInsertion) {
        return isInsertion ? accumulator + 1 : accumulator - 1;
      }

      @Override
      public Long getAggregate(Long accumulator) {
        return accumulator;
      }

      @Override
      public Class<?> getResultType(Class<?> valueType) {
        return Long.class;
      }
    };
  }

  /**
   * Sums up integral values.
   */
  public static AggregationOperator<Number, Long, Long> sumLong() {
    return new AggregationOperator<>() {
      @Override
      public Long createNeutral() {
        return 0L;
      }

      @Override
      public Long update(Long accumulator, Number value, boolean isInsertion) {
        return isInsertion ? accumulator + value.longValue() : accumulator - value.longValue();
      }

      @Override
      public Long getAggregate(Long accumulator) {
        return accumulator;
      }

      @Override
      public Class<?> getResultType(Class<?> valueType) {
        return Long.class;
      }
    };
  }

  /**
   * Sums up floating point values. Note that removals may introduce rounding
   * errors.
   */
  public static AggregationOperator<Number, Double, Double> sumDouble() {
    return new AggregationOperator<>() {
      @Override
      public Double createNeutral() {
        return 0.0;
      }

      @Override
      public Double update(Double accumulator, Number value, boolean isInsertion) {
        return isInsertion ? accumulator + value.doubleValue()
            : accumulator - value.doubleValue();
      }

      @Override
      public Double getAggregate(Double accumulator) {
        return accumulator;
      }

      @Override
      public Class<?> getResultType(Class<?> valueType) {
        return Double.class;
      }
    };
  }

  /**
   * Determines the minimum of the aggregated values w.r.t. their natural order.
   */
  public static <V extends Comparable<? super V>> AggregationOperator<V, TreeMap<V, Integer>, V> min() {
    return min(Comparator.naturalOrder());
  }

  /**
   * Determines the maximum of the aggregated values w.r.t. their natural order.
   */
  public static <V extends Comparable<? super V>> AggregationOperator<V, TreeMap<V, Integer>, V> max() {
    return max(Comparator.naturalOrder());
  }

  /**
   * Determines the minimum of the aggregated values w.r.t. the given
   * comparator. Values considered equal by the comparator are treated as
   * the same value.
   */
  public static <V> AggregationOperator<V, TreeMap<V, Integer>, V> min(Comparator<? super V> comparator) {
    return new Extremum<>(comparator, true);
  }

  /**
   * Determines the maximum of the aggregated values w.r.t. the given
   * comparator. Values considered equal by the comparator are treated as
   * the same value.
   */
  public static <V> AggregationOperator<V, TreeMap<V, Integer>, V> max(Comparator<? super V> comparator) {
    return new Extremum<>(comparator, false);
  }

  /**
   * Minimum or maximum, maintained via a sorted multiset of all values.
   */
  protected static class Extremum<V> implements AggregationOperator<V, TreeMap<V, Integer>, V> {

    protected final Comparator<? super V> comparator;

    protected final boolean minimum;

    protected Extremum(Comparator<? super V> comparator, boolean minimum) {
      this.comparator = comparator;
      this.minimum = minimum;
    }

    @Override
    public TreeMap<V, Integer> createNeutral() {
      return new TreeMap<>(comparator);
    }

    /**
     * Increments or decrements the multiplicity of {@code value} in the
     * sorted multiset, removing values whose multiplicity drops to 0.
     */
    @Override
    public TreeMap<V, Integer> update(TreeMap<V, Integer> accumulator, V value,
        boolean isInsertion) {
      if (isInsertion) {
        accumulator.merge(value, 1, Integer::sum);
      } else {
        accumulator.computeIfPresent(value, (v, count) -> count == 1 ? null : count - 1);
      }
      return accumulator;
    }

    /**
     * @return the first (minimum) or last (maximum) value of the sorted multiset
     */
    @Override
    public V getAggregate(TreeMap<V, Integer> accumulator) {
      return minimum ? accumulator.firstKey() : accumulator.lastKey();
    }

    @Override
    public Class<?> getResultType(Class<?> valueType) {
      return valueType;
    }
  }
}
