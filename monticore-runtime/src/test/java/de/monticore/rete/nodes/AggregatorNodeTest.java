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
   * A changed result replaces the old result tuple; an empty group deletes it.
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

  @Test
  public void testUnchangedResultIsNotPropagated() {
    AggregatorNode sum = net.aggregate(source, TupleMask.of(2, 0), 1, Aggregators.sumLong());
    RecordingReceiver recorder = record(sum);
    source.insert("g", 1);
    source.insert("g", 0);
    source.delete("g", 0);
    assertEquals(List.of("+[g, 1]"), recorder.take());
  }

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

  @Test
  public void testMultiplicitiesAreCounted() {
    AggregatorNode count = net.count(source);
    source.insert("g", 1);
    source.insert("g", 1);
    assertEquals(2L, count.getResult(Tuple.EMPTY));
  }

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

  @Test
  public void testDeletionFromUnknownGroupIsDetected() {
    net.count(source, 0);
    assertThrows(IllegalStateException.class, () -> source.delete("g", 1));
  }

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
