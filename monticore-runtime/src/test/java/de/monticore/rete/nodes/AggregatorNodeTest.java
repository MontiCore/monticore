/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.aggregation.AggregationOperator;
import de.monticore.rete.aggregation.Aggregators;
import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Disposing and rejecting parents of another network are inherited from
 * {@link AbstractSingleParentNode} and tested in {@link FilterNodeTest}.
 */
public class AggregatorNodeTest extends AbstractNodeTest {

  /**
   * [group, value]
   */
  protected TestSource source;

  @BeforeEach
  public void setUp() {
    source = source(String.class, Integer.class);
  }

  /**
   * Counting per group: the output consists of the group columns followed by
   * the count. A changed result is passed on as deletion of the old result
   * tuple followed by insertion of the new one; a group becoming empty deletes
   * its result tuple and has no result anymore.
   */
  @Test
  public void testCountPerGroup() {
    AggregatorNode count = net.count(source, 0);
    RecordingReceiver recorder = record(count);
    assertEquals(List.of(String.class, Long.class), count.getColumnTypes());
    assertEquals(TupleMask.of(2, 0), count.getGroupMask());
    assertEquals(AggregatorNode.WHOLE_TUPLE, count.getAggregatedColumn());
    assertEquals("count", count.getOperator().toString());

    source.insert("g", 1);
    assertEquals(List.of("+[g, 1]"), recorder.take());
    source.insert("g", 2);
    source.insert("h", 1);
    assertEquals(List.of("-[g, 1]", "+[g, 2]", "+[h, 1]"), recorder.take());
    assertEquals(2L, count.getResult(Tuple.of("g")));

    source.delete("g", 1);
    assertEquals(List.of("-[g, 2]", "+[g, 1]"), recorder.take());
    source.delete("g", 2);
    assertEquals(List.of("-[g, 1]"), recorder.take());
    assertNull(count.getResult(Tuple.of("g")));
    assertEquals(tuples(Tuple.of("h", 1L)), contents(count));
    recorder.assertConsistent();
  }

  /**
   * A change not affecting the result of its group (adding 0 to a sum)
   * produces no delta.
   */
  @Test
  public void testUnchangedResultIsNotPropagated() {
    AggregatorNode sum = net.aggregate(source, TupleMask.of(2, 0), 1, Aggregators.sumLong());
    RecordingReceiver recorder = record(sum);
    source.insert("g", 1);
    source.insert("g", 0);
    source.delete("g", 0);
    assertEquals(List.of("+[g, 1]"), recorder.take());
  }

  /**
   * Aggregating a column without group columns yields a single result tuple;
   * the result type of {@code min} is the type of the aggregated column.
   */
  @Test
  public void testAggregatedColumnWithoutGroups() {
    AggregatorNode min = net.aggregate(source, TupleMask.empty(2), 1, Aggregators.min());
    RecordingReceiver recorder = record(min);
    assertEquals(List.of(Integer.class), min.getColumnTypes());

    source.insert("g", 3);
    source.insert("h", 1);
    assertEquals(List.of("+[3]", "-[3]", "+[1]"), recorder.take());
    assertEquals(1, min.getResult(Tuple.EMPTY));
    source.delete("h", 1);
    assertEquals(List.of("-[1]", "+[3]"), recorder.take());
    recorder.assertConsistent();
  }

  /**
   * Each occurrence of a tuple counts, also for repeated occurrences.
   */
  @Test
  public void testMultiplicitiesAreCounted() {
    AggregatorNode count = net.count(source);
    source.insert("g", 1);
    source.insert("g", 1);
    assertEquals(2L, count.getResult(Tuple.EMPTY));
  }

  /**
   * An aggregator created on a populated source computes the results of the
   * existing tuples and updates them on later changes.
   */
  @Test
  public void testInitializedFromPopulatedSource() {
    source.insert("g", 1);
    source.insert("g", 2);
    AggregatorNode count = net.count(source, 0);
    RecordingReceiver recorder = record(count);
    assertEquals(tuples(Tuple.of("g", 2L)), contents(count));
    source.delete("g", 1);
    assertEquals(List.of("-[g, 2]", "+[g, 1]"), recorder.take());
  }

  /**
   * A group mask for another arity and an aggregated column out of range are
   * rejected.
   */
  @Test
  public void testInvalidConfigurationsAreRejected() {
    AggregationOperator<?, ?, ?> count = Aggregators.count();
    assertThrows(IllegalArgumentException.class,
        () -> net.aggregate(source, TupleMask.of(3, 0), 1, count), "group mask");
    assertThrows(IllegalArgumentException.class,
        () -> net.aggregate(source, TupleMask.of(2, 0), 2, count), "column too large");
    assertThrows(IllegalArgumentException.class,
        () -> net.aggregate(source, TupleMask.of(2, 0), -2, count), "negative column");
  }

  /**
   * Group columns may be optional: the result tuples keep their optionality
   * (the result column is not optional), and tuples with {@link Tuple#ABSENT}
   * in a group column form a group of their own. Whole tuples may be
   * aggregated as well, even if they contain {@code ABSENT}.
   */
  @Test
  public void testOptionalGroupColumns() {
    TestSource optional = source(List.of(true, false), String.class, Integer.class);
    AggregatorNode count = net.count(optional, 0);
    assertEquals(List.of(true, false), count.getColumnOptionality());

    optional.insert(Tuple.ABSENT, 1);
    optional.insert(Tuple.ABSENT, 2);
    optional.insert("g", 1);
    assertEquals(tuples(Tuple.of(Tuple.ABSENT, 2L), Tuple.of("g", 1L)), contents(count));
    assertEquals(2L, count.getResult(Tuple.of(Tuple.ABSENT)));
  }

  /**
   * The aggregated column must not be optional, as the operators cannot
   * aggregate {@link Tuple#ABSENT}.
   */
  @Test
  public void testOptionalAggregatedColumnIsRejected() {
    TestSource optional = source(List.of(false, true), String.class, Integer.class);
    assertThrows(IllegalArgumentException.class,
        () -> net.aggregate(optional, TupleMask.of(2, 0), 1, Aggregators.sumLong()));
    net.aggregate(optional, TupleMask.of(2, 0), AggregatorNode.WHOLE_TUPLE, Aggregators.count());
  }

  /**
   * The deletion of a tuple of an empty group is reported as an inconsistent
   * network.
   */
  @Test
  public void testDeletionFromUnknownGroupIsDetected() {
    net.count(source, 0);
    assertThrows(IllegalStateException.class, () -> source.delete("g", 1));
  }

  /**
   * An operator returning {@code null} as result violates the contract of
   * {@link AggregationOperator} and causes a {@link NullPointerException}.
   */
  @Test
  public void testNullResultIsRejected() {
    AggregationOperator<Object, Object, Object> nullResult = new AggregationOperator<>() {
      @Override
      public Object createNeutral() {
        return "";
      }

      @Override
      public Object update(Object accumulator, Object value, boolean isInsertion) {
        return accumulator;
      }

      @Override
      public Object getAggregate(Object accumulator) {
        return null;
      }

      @Override
      public Class<?> getResultType(Class<?> valueType) {
        return Object.class;
      }
    };
    net.aggregate(source, TupleMask.empty(2), 1, nullResult);
    assertThrows(NullPointerException.class, () -> source.insert("g", 1));
  }
}
