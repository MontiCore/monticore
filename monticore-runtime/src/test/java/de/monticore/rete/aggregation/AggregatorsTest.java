/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.aggregation;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class AggregatorsTest {

  /**
   * Applies the insertions and removals to a neutral accumulator.
   *
   * @param changes values to insert, or to remove if preceded by {@code "-"}
   * @return the aggregation result
   */
  @SuppressWarnings("unchecked")
  protected static <V, A, R> R aggregate(AggregationOperator<V, A, R> operator, Object... changes) {
    A accumulator = operator.createNeutral();
    boolean insert = true;
    for (Object change : changes) {
      if ("-".equals(change)) {
        insert = false;
        continue;
      }
      accumulator = operator.update(accumulator, (V) change, insert);
      insert = true;
    }
    return operator.getAggregate(accumulator);
  }

  @Test
  public void testCount() {
    AggregationOperator<Object, Long, Long> count = Aggregators.count();
    assertEquals(0L, count.getAggregate(count.createNeutral()));
    assertEquals(3L, aggregate(count, "a", "a", 1));
    assertEquals(1L, aggregate(count, "a", "b", "-", "a"));
    assertSame(Long.class, count.getResultType(String.class));
    assertEquals("count", count.toString());
  }

  @Test
  public void testSumLong() {
    AggregationOperator<Number, Long, Long> sum = Aggregators.sumLong();
    assertEquals(0L, sum.getAggregate(sum.createNeutral()));
    assertEquals(6L, aggregate(sum, 1, 2L, (short) 3));
    assertEquals(-2L, aggregate(sum, 1, -3, 4, "-", 4));
    assertSame(Long.class, sum.getResultType(Integer.class));
    assertEquals("sumLong", sum.toString());
  }

  @Test
  public void testSumDouble() {
    AggregationOperator<Number, Double, Double> sum = Aggregators.sumDouble();
    assertEquals(0.0, sum.getAggregate(sum.createNeutral()));
    assertEquals(4.0, aggregate(sum, 1.5, 2.5f, 1, "-", 1), 1e-9);
    assertSame(Double.class, sum.getResultType(Integer.class));
    assertEquals("sumDouble", sum.toString());
  }

  @Test
  public void testMin() {
    AggregationOperator<Integer, ?, Integer> min = Aggregators.min();
    assertEquals(1, aggregate(min, 3, 1, 2));
    assertEquals(2, aggregate(min, 3, 1, 2, "-", 1));
    // a value inserted twice remains until both occurrences are removed
    assertEquals(1, aggregate(min, 1, 1, 3, "-", 1));
    assertEquals(3, aggregate(min, 1, 1, 3, "-", 1, "-", 1));
    assertSame(Integer.class, min.getResultType(Integer.class));
    assertEquals("min", min.toString());
  }

  @Test
  public void testMax() {
    AggregationOperator<String, ?, String> max = Aggregators.max();
    assertEquals("c", aggregate(max, "a", "c", "b"));
    assertEquals("b", aggregate(max, "a", "c", "b", "-", "c"));
    assertEquals("max", max.toString());
  }

  @Test
  public void testComparator() {
    AggregationOperator<String, ?, String> shortest =
        Aggregators.min(Comparator.comparingInt(String::length));
    AggregationOperator<String, ?, String> longest =
        Aggregators.max(Comparator.comparingInt(String::length));
    assertEquals("b", aggregate(shortest, "ccc", "b", "dd"));
    assertEquals("ccc", aggregate(longest, "ccc", "b", "dd"));
    // values equal w.r.t. the comparator are treated as the same value
    assertEquals("a", aggregate(shortest, "a", "b", "dd", "-", "b"),
        "the first of the equal values is kept, both occurrences are counted");
    assertEquals("dd", aggregate(shortest, "a", "b", "dd", "-", "b", "-", "a"));
  }

  @Test
  public void testRemovalOfUnknownValueIsIgnored() {
    AggregationOperator<Integer, ?, Integer> min = Aggregators.min();
    assertEquals(1, aggregate(min, 1, "-", 5));
  }
}
