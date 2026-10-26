/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExistenceNodeTest extends AbstractNodeTest {

  /**
   * [name, value]
   */
  protected TestSource left;

  /**
   * [name]
   */
  protected TestSource right;

  @BeforeEach
  public void setUp() {
    left = source(String.class, Integer.class);
    right = source(String.class);
  }

  @Test
  public void testSemiJoin() {
    Indexer leftIndexer = net.indexer(left, 0);
    Indexer rightIndexer = net.indexer(right, 0);
    ExistenceNode semi = net.semiJoin(leftIndexer, rightIndexer);
    RecordingReceiver recorder = record(semi);
    assertFalse(semi.isNegative());
    assertSame(leftIndexer, semi.getLeft());
    assertSame(rightIndexer, semi.getRight());
    assertEquals(left.getColumnTypes(), semi.getColumnTypes());

    left.insert("a", 1);
    left.insert("b", 1);
    assertEquals(List.of(), recorder.take());
    right.insert("a");
    assertEquals(List.of("+[a, 1]"), recorder.take());
    left.insert("a", 2);
    assertEquals(List.of("+[a, 2]"), recorder.take());

    // only changes of the existence of a key matter
    right.insert("a");
    right.delete("a");
    assertEquals(List.of(), recorder.take());

    left.delete("a", 1);
    assertEquals(List.of("-[a, 1]"), recorder.take());
    right.delete("a");
    assertEquals(List.of("-[a, 2]"), recorder.take());
    recorder.assertConsistent();
  }

  @Test
  public void testAntiJoin() {
    ExistenceNode anti = net.antiJoin(net.indexer(left, 0), net.indexer(right, 0));
    RecordingReceiver recorder = record(anti);
    assertTrue(anti.isNegative());

    left.insert("a", 1);
    assertEquals(List.of("+[a, 1]"), recorder.take());
    right.insert("a");
    assertEquals(List.of("-[a, 1]"), recorder.take());
    right.insert("a");
    left.insert("a", 2);
    right.delete("a");
    assertEquals(List.of(), recorder.take());

    right.delete("a");
    assertEquals(unordered(List.of("+[a, 1]", "+[a, 2]")), unordered(recorder.take()));
    left.delete("a", 2);
    assertEquals(List.of("-[a, 2]"), recorder.take());
    recorder.assertConsistent();
  }

  @Test
  public void testLeftMultiplicitiesArePropagated() {
    ExistenceNode semi = net.semiJoin(net.indexer(left, 0), net.indexer(right, 0));
    RecordingReceiver recorder = record(semi);
    left.insert("a", 1);
    left.insert("a", 1);
    right.insert("a");
    assertEquals(List.of("+[a, 1]", "+[a, 1]"), recorder.take());
    right.delete("a");
    assertEquals(List.of("-[a, 1]", "-[a, 1]"), recorder.take());
  }

  @Test
  public void testInitializedFromPopulatedIndexers() {
    left.insert("a", 1);
    left.insert("a", 1);
    left.insert("b", 1);
    right.insert("a");
    right.insert("a");
    ExistenceNode semi = net.semiJoin(net.indexer(left, 0), net.indexer(right, 0));
    ExistenceNode anti = net.antiJoin(net.indexer(left, 0), net.indexer(right, 0));
    assertEquals(tuples(Tuple.of("a", 1), Tuple.of("a", 1)), contents(semi));
    assertEquals(tuples(Tuple.of("b", 1)), contents(anti));
  }

  @Test
  public void testInvalidConfigurationsAreRejected() {
    Indexer byName = net.indexer(left, 0);
    assertThrows(IllegalArgumentException.class, () -> net.semiJoin(byName, byName),
        "identical indexers");
    assertThrows(IllegalArgumentException.class,
        () -> net.antiJoin(net.indexer(left, 0, 1), net.indexer(right, 0)), "key sizes");
    assertThrows(IllegalArgumentException.class,
        () -> net.antiJoin(net.indexer(left, 1), net.indexer(right, 0)), "Integer vs. String");
  }

  @Test
  public void testDispose() {
    Indexer leftIndexer = net.indexer(left, 0);
    Indexer rightIndexer = net.indexer(right, 0);
    net.semiJoin(leftIndexer, rightIndexer).dispose();
    assertTrue(leftIndexer.getListeners().isEmpty());
    assertTrue(rightIndexer.getListeners().isEmpty());
  }
}
