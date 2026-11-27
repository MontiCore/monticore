/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link ExistenceNode} as semi join and anti join: left tuples are
 * passed on depending on the existence of right tuples with the same key.
 */
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

  /**
   * A semi join passes on the left tuples having a right partner with the same
   * key. A right change only matters if it changes the existence of the key
   * (first insertion, last deletion); then all left tuples of the key are
   * inserted or deleted. The output columns are those of the left side.
   */
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

  /**
   * The output columns of semi and anti joins that are not part of the key
   * keep the optionality of the left columns; {@link Tuple#ABSENT} in these
   * columns is passed on like other values.
   */
  @Test
  public void testOptionalColumns() {
    TestSource optionalLeft = source(List.of(false, true), String.class, Integer.class);
    Indexer leftIndexer = net.indexer(optionalLeft, 0);
    ExistenceNode semi = net.semiJoin(leftIndexer, net.indexer(right, 0));
    ExistenceNode anti = net.antiJoin(leftIndexer, net.indexer(right, 0));
    assertEquals(List.of(false, true), semi.getColumnOptionality());
    assertEquals(List.of(false, true), anti.getColumnOptionality());

    optionalLeft.insert("a", Tuple.ABSENT);
    assertEquals(tuples(Tuple.of("a", Tuple.ABSENT)), contents(anti));
    right.insert("a");
    assertEquals(tuples(Tuple.of("a", Tuple.ABSENT)), contents(semi));
  }

  /**
   * Keys with {@link Tuple#ABSENT} have no partners, not even right tuples
   * with {@code ABSENT} in their key: such left tuples are never passed on by
   * a semi join and always by an anti join, and such right tuples have no
   * effect. Hence, the key columns of a semi join are not optional, while
   * those of an anti join keep their optionality. Nodes created on populated
   * indexers agree.
   */
  @Test
  public void testKeysWithAbsentHaveNoPartners() {
    TestSource optionalLeft = source(List.of(true, false), String.class, Integer.class);
    TestSource optionalRight = source(List.of(true), String.class);
    Indexer leftIndexer = net.indexer(optionalLeft, 0);
    Indexer rightIndexer = net.indexer(optionalRight, 0);
    ExistenceNode semi = net.semiJoin(leftIndexer, rightIndexer);
    ExistenceNode anti = net.antiJoin(leftIndexer, rightIndexer);
    RecordingReceiver semiRecorder = record(semi);
    RecordingReceiver antiRecorder = record(anti);
    assertEquals(List.of(false, false), semi.getColumnOptionality());
    assertEquals(List.of(true, false), anti.getColumnOptionality());

    optionalLeft.insert(Tuple.ABSENT, 1);
    assertEquals(List.of(), semiRecorder.take());
    assertEquals(List.of("+[⊥, 1]"), antiRecorder.take());

    // right tuples with ABSENT in their key change nothing
    optionalRight.insert(Tuple.ABSENT);
    optionalLeft.insert(Tuple.ABSENT, 2);
    assertEquals(List.of(), semiRecorder.take());
    assertEquals(List.of("+[⊥, 2]"), antiRecorder.take());
    assertEquals(tuples(Tuple.of(Tuple.ABSENT, 1), Tuple.of(Tuple.ABSENT, 2)), contents(anti));
    assertTrue(contents(semi).isEmpty());
    assertTrue(contents(net.semiJoin(leftIndexer, rightIndexer)).isEmpty());
    assertEquals(contents(anti), contents(net.antiJoin(leftIndexer, rightIndexer)));

    optionalRight.delete(Tuple.ABSENT);
    assertEquals(List.of(), semiRecorder.take());
    assertEquals(List.of(), antiRecorder.take());
    semiRecorder.assertConsistent();
    antiRecorder.assertConsistent();
  }

  /**
   * An anti join passes on the left tuples without right partner: the first
   * right tuple of a key deletes the left tuples of that key, the deletion of
   * the last one inserts them again.
   */
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

  /**
   * With a condition, only right tuples with the same key satisfying the
   * condition for {@code l ++ r[without key]} are partners, so the existence
   * is decided per left tuple: here, a partner must have another value than
   * the left tuple (as for injectivity). A right change only affects the left
   * tuples for which it is the first or last partner. Without condition, the
   * node reports no condition.
   */
  @Test
  public void testAntiJoinWithCondition() {
    TestSource values = source(String.class, Integer.class);   // [name, value]
    // [name, value, other value]: value != other value
    ExistenceNode anti = net.antiJoin(net.indexer(left, 0), net.indexer(values, 0),
        t -> !t.get(1).equals(t.get(2)));
    RecordingReceiver recorder = record(anti);
    assertTrue(anti.isNegative());
    assertTrue(anti.getCondition().isPresent());
    assertTrue(net.antiJoin(net.indexer(left, 0), net.indexer(right, 0)).getCondition().isEmpty());
    assertEquals(left.getColumnTypes(), anti.getColumnTypes());

    left.insert("a", 1);
    assertEquals(List.of("+[a, 1]"), recorder.take());
    values.insert("a", 1);   // violates the condition
    assertEquals(List.of(), recorder.take());
    values.insert("a", 2);
    assertEquals(List.of("-[a, 1]"), recorder.take());
    values.insert("a", 3);   // a second partner
    values.delete("a", 2);
    assertEquals(List.of(), recorder.take());

    // [a, 2] has the partners [a, 1] and [a, 3]
    left.insert("a", 2);
    assertEquals(List.of(), recorder.take());
    values.delete("a", 3);   // the last partner of [a, 1], but not of [a, 2]
    assertEquals(List.of("+[a, 1]"), recorder.take());
    values.delete("a", 1);
    assertEquals(List.of("+[a, 2]"), recorder.take());
    left.delete("a", 1);
    assertEquals(List.of("-[a, 1]"), recorder.take());
    assertEquals(tuples(Tuple.of("a", 2)), contents(anti));
    recorder.assertConsistent();
  }

  /**
   * A semi join with condition passes on the left tuples having a partner
   * satisfying the condition, here a right value greater than the left one.
   */
  @Test
  public void testSemiJoinWithCondition() {
    TestSource limits = source(String.class, Integer.class);   // [name, limit]
    // [name, value, limit]: value < limit
    ExistenceNode semi = net.semiJoin(net.indexer(left, 0), net.indexer(limits, 0),
        t -> t.get(1, Integer.class) < t.get(2, Integer.class));
    RecordingReceiver recorder = record(semi);
    assertFalse(semi.isNegative());
    assertTrue(semi.getCondition().isPresent());

    left.insert("a", 5);
    limits.insert("a", 3);
    assertEquals(List.of(), recorder.take());
    limits.insert("a", 10);
    assertEquals(List.of("+[a, 5]"), recorder.take());
    left.insert("a", 1);
    assertEquals(List.of("+[a, 1]"), recorder.take());
    left.insert("a", 20);
    assertEquals(List.of(), recorder.take());

    limits.delete("a", 10);
    assertEquals(List.of("-[a, 5]"), recorder.take());
    assertEquals(tuples(Tuple.of("a", 1)), contents(semi));
    recorder.assertConsistent();
  }

  /**
   * With a condition, the partners are counted with their multiplicities: a
   * left tuple loses its last partner only when all occurrences of all its
   * partners are deleted. Left tuples are passed on as often as they occur.
   */
  @Test
  public void testMultiplicitiesWithCondition() {
    TestSource values = source(String.class, Integer.class);   // [name, value]
    ExistenceNode anti = net.antiJoin(net.indexer(left, 0), net.indexer(values, 0),
        t -> !t.get(1).equals(t.get(2)));
    RecordingReceiver recorder = record(anti);
    left.insert("a", 1);
    left.insert("a", 1);
    assertEquals(List.of("+[a, 1]", "+[a, 1]"), recorder.take());
    values.insert("a", 2);
    assertEquals(List.of("-[a, 1]", "-[a, 1]"), recorder.take());
    values.insert("a", 2);
    values.delete("a", 2);
    assertEquals(List.of(), recorder.take());
    values.delete("a", 2);
    assertEquals(List.of("+[a, 1]", "+[a, 1]"), recorder.take());
    recorder.assertConsistent();
  }

  /**
   * Keys with {@link Tuple#ABSENT} have no partners also with a condition
   * that would hold; {@code ABSENT} in the other columns is read as
   * {@code null} by the condition.
   */
  @Test
  public void testConditionWithAbsentValues() {
    TestSource optionalLeft = source(List.of(true, true), String.class, Integer.class);
    TestSource values = source(String.class, Integer.class);   // [name, value]
    // [name, value?, other value]: a partner needs a present left value
    ExistenceNode anti = net.antiJoin(net.indexer(optionalLeft, 0), net.indexer(values, 0),
        t -> t.get(1) != null);
    RecordingReceiver recorder = record(anti);
    assertEquals(List.of(true, true), anti.getColumnOptionality());

    optionalLeft.insert(Tuple.ABSENT, 1);
    optionalLeft.insert("a", Tuple.ABSENT);
    optionalLeft.insert("a", 1);
    assertEquals(unordered(List.of("+[⊥, 1]", "+[a, ⊥]", "+[a, 1]")),
        unordered(recorder.take()));
    values.insert("a", 2);
    assertEquals(List.of("-[a, 1]"), recorder.take());
    assertEquals(tuples(Tuple.of(Tuple.ABSENT, 1), Tuple.of("a", Tuple.ABSENT)), contents(anti));
    recorder.assertConsistent();
  }

  /**
   * With two indexers of the same source, a tuple may be its own partner: the
   * changes of a tuple arrive as left and as right change one after the
   * other, and the tuple is passed on resp. withdrawn once. Here, an edge
   * {@code [x, y]} is passed on by the semi join if an edge from {@code x}
   * exists at all (incl. itself), and by the anti join if no other edge from
   * {@code x} to another target exists.
   */
  @Test
  public void testIndexersOfSameSourceWithCondition() {
    TestSource edges = source(String.class, String.class);   // [x, y]
    ExistenceNode semi = net.semiJoin(net.indexer(edges, 0), net.indexer(edges, 0), t -> true);
    // [x, y, y2]: y != y2
    ExistenceNode anti = net.antiJoin(net.indexer(edges, 0), net.indexer(edges, 0),
        t -> !t.get(1).equals(t.get(2)));
    RecordingReceiver semiRecorder = record(semi);
    RecordingReceiver antiRecorder = record(anti);

    edges.insert("a", "b");
    assertEquals(List.of("+[a, b]"), semiRecorder.take());
    assertEquals(List.of("+[a, b]"), antiRecorder.take());
    edges.insert("a", "c");
    assertEquals(List.of("+[a, c]"), semiRecorder.take());
    assertEquals(List.of("-[a, b]"), antiRecorder.take());

    edges.delete("a", "b");
    assertEquals(List.of("-[a, b]"), semiRecorder.take());
    assertEquals(List.of("+[a, c]"), antiRecorder.take());
    edges.delete("a", "c");
    assertEquals(List.of("-[a, c]"), semiRecorder.take());
    assertEquals(List.of("-[a, c]"), antiRecorder.take());
    semiRecorder.assertConsistent();
    antiRecorder.assertConsistent();
  }

  /**
   * When the existence of a key changes, each left tuple is passed on as often
   * as it occurs.
   */
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

  /**
   * Semi and anti joins created on populated indexers provide the left tuples
   * with resp. without partner, with their multiplicities.
   */
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

  /**
   * Semi and anti joins with condition created on populated indexers count
   * the existing partners: later changes are passed on as for nodes created
   * before the tuples.
   */
  @Test
  public void testInitializedFromPopulatedIndexersWithCondition() {
    TestSource values = source(String.class, Integer.class);   // [name, value]
    left.insert("a", 1);
    left.insert("a", 2);
    values.insert("a", 1);
    ExistenceNode semi = net.semiJoin(net.indexer(left, 0), net.indexer(values, 0),
        t -> !t.get(1).equals(t.get(2)));
    ExistenceNode anti = net.antiJoin(net.indexer(left, 0), net.indexer(values, 0),
        t -> !t.get(1).equals(t.get(2)));
    assertEquals(tuples(Tuple.of("a", 2)), contents(semi));
    assertEquals(tuples(Tuple.of("a", 1)), contents(anti));

    RecordingReceiver semiRecorder = record(semi);
    RecordingReceiver antiRecorder = record(anti);
    values.delete("a", 1);
    assertEquals(List.of("-[a, 2]"), semiRecorder.take());
    assertEquals(List.of("+[a, 2]"), antiRecorder.take());
    semiRecorder.assertConsistent();
    antiRecorder.assertConsistent();
  }

  /**
   * Identical indexers on both sides, keys of different size and key columns
   * whose types can never hold equal values are rejected.
   */
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

  /**
   * Disposing the node unregisters it from both indexers.
   */
  @Test
  public void testDispose() {
    Indexer leftIndexer = net.indexer(left, 0);
    Indexer rightIndexer = net.indexer(right, 0);
    net.semiJoin(leftIndexer, rightIndexer).dispose();
    assertTrue(leftIndexer.getListeners().isEmpty());
    assertTrue(rightIndexer.getListeners().isEmpty());
  }
}
