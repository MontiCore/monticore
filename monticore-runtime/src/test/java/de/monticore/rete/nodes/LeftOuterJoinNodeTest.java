/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link LeftOuterJoinNode}: left tuples are kept, joined with
 * their partners or padded with {@link Tuple#ABSENT} if they have none.
 */
public class LeftOuterJoinNodeTest extends AbstractNodeTest {

  /**
   * [name, value]
   */
  protected TestSource left;

  /**
   * [name, flag]
   */
  protected TestSource right;

  @BeforeEach
  public void setUp() {
    left = source(String.class, Integer.class);
    right = source(String.class, Boolean.class);
  }

  /**
   * A left tuple without partner is padded with {@code ABSENT}. The first
   * partner replaces the padded tuple, further partners add pairs, and the
   * deletion of the last partner restores the padded tuple. The output
   * consists of the left columns followed by the non-key columns of the right
   * side, which are optional.
   */
  @Test
  public void testKeepsLeftTuplesAndPadsMissingPartners() {
    Indexer leftIndexer = net.indexer(left, 0);
    Indexer rightIndexer = net.indexer(right, 0);
    LeftOuterJoinNode outer = net.leftOuterJoin(leftIndexer, rightIndexer);
    RecordingReceiver recorder = record(outer);
    assertSame(leftIndexer, outer.getLeft());
    assertSame(rightIndexer, outer.getRight());
    assertEquals(TupleMask.of(2, 1), outer.getRightOutputMask());
    assertEquals(List.of(String.class, Integer.class, Boolean.class), outer.getColumnTypes());
    assertEquals(List.of(false, false, true), outer.getColumnOptionality());

    left.insert("a", 1);
    assertEquals(List.of("+[a, 1, ⊥]"), recorder.take());
    right.insert("a", true);
    assertEquals(List.of("-[a, 1, ⊥]", "+[a, 1, true]"), recorder.take());
    right.insert("a", false);
    right.insert("b", false);
    assertEquals(List.of("+[a, 1, false]"), recorder.take());
    left.insert("a", 2);
    assertEquals(unordered(List.of("+[a, 2, true]", "+[a, 2, false]")),
        unordered(recorder.take()));

    right.delete("a", true);
    assertEquals(unordered(List.of("-[a, 1, true]", "-[a, 2, true]")),
        unordered(recorder.take()));
    right.delete("a", false);
    assertEquals(unordered(List.of("-[a, 1, false]", "+[a, 1, ⊥]", "-[a, 2, false]",
        "+[a, 2, ⊥]")), unordered(recorder.take()));
    left.delete("a", 2);
    assertEquals(List.of("-[a, 2, ⊥]"), recorder.take());
    assertEquals(tuples(Tuple.of("a", 1, Tuple.ABSENT)), contents(outer));
    recorder.assertConsistent();
  }

  /**
   * Only right tuples satisfying the condition are partners. A left tuple
   * whose candidates all violate the condition is padded, which filtering the
   * output of the join could not achieve.
   */
  @Test
  public void testConditionDecidesAboutPartners() {
    TestSource limits = source(String.class, Integer.class);   // [name, limit]
    // [name, value, limit]: value < limit
    LeftOuterJoinNode outer = net.leftOuterJoin(net.indexer(left, 0), net.indexer(limits, 0),
        t -> t.get(1, Integer.class) < t.get(2, Integer.class));
    RecordingReceiver recorder = record(outer);

    left.insert("a", 5);
    limits.insert("a", 3);
    assertEquals(List.of("+[a, 5, ⊥]"), recorder.take());
    limits.insert("a", 10);
    assertEquals(List.of("-[a, 5, ⊥]", "+[a, 5, 10]"), recorder.take());
    left.insert("a", 1);
    assertEquals(unordered(List.of("+[a, 1, 3]", "+[a, 1, 10]")), unordered(recorder.take()));

    limits.delete("a", 10);
    assertEquals(unordered(List.of("-[a, 5, 10]", "+[a, 5, ⊥]", "-[a, 1, 10]")),
        unordered(recorder.take()));
    assertEquals(tuples(Tuple.of("a", 5, Tuple.ABSENT), Tuple.of("a", 1, 3)), contents(outer));
    recorder.assertConsistent();
  }

  /**
   * Pairs occur as often as the product of the multiplicities of their left
   * and right tuple, padded tuples as often as their left tuple.
   */
  @Test
  public void testMultiplicities() {
    LeftOuterJoinNode outer = net.leftOuterJoin(net.indexer(left, 0), net.indexer(right, 0));
    RecordingReceiver recorder = record(outer);
    left.insert("a", 1);
    left.insert("a", 1);
    assertEquals(List.of("+[a, 1, ⊥]", "+[a, 1, ⊥]"), recorder.take());
    right.insert("a", true);
    assertEquals(List.of("-[a, 1, ⊥]", "-[a, 1, ⊥]", "+[a, 1, true]", "+[a, 1, true]"),
        recorder.take());
    right.insert("a", true);
    assertEquals(List.of("+[a, 1, true]", "+[a, 1, true]"), recorder.take());
    assertEquals(4, contents(outer).count(Tuple.of("a", 1, true)));

    right.delete("a", true);
    assertEquals(List.of("-[a, 1, true]", "-[a, 1, true]"), recorder.take());
    right.delete("a", true);
    assertEquals(List.of("-[a, 1, true]", "-[a, 1, true]", "+[a, 1, ⊥]", "+[a, 1, ⊥]"),
        recorder.take());
    left.delete("a", 1);
    assertEquals(List.of("-[a, 1, ⊥]"), recorder.take());
    recorder.assertConsistent();
  }

  /**
   * A left outer join created on populated indexers provides the pairs and
   * padded tuples, and its partner counts are initialized, so that later
   * changes are handled correctly.
   */
  @Test
  public void testInitializedFromPopulatedIndexers() {
    left.insert("a", 1);
    left.insert("b", 2);
    right.insert("a", true);
    right.insert("a", false);
    LeftOuterJoinNode outer = net.leftOuterJoin(net.indexer(left, 0), net.indexer(right, 0));
    RecordingReceiver recorder = record(outer);
    assertEquals(tuples(Tuple.of("a", 1, true), Tuple.of("a", 1, false),
        Tuple.of("b", 2, Tuple.ABSENT)), contents(outer));

    right.delete("a", true);
    assertEquals(List.of("-[a, 1, true]"), recorder.take());
    right.delete("a", false);
    assertEquals(List.of("-[a, 1, false]", "+[a, 1, ⊥]"), recorder.take());
    right.insert("b", true);
    assertEquals(List.of("-[b, 2, ⊥]", "+[b, 2, true]"), recorder.take());
    recorder.assertConsistent();
  }

  /**
   * An explicit output mask selects the right columns, all of which are
   * padded. Only the selected right key columns are narrowed to the more
   * specific key type, as left tuples are passed on unchanged.
   */
  @Test
  public void testRightOutputMaskAndColumnTypes() {
    LeftOuterJoinNode outer = net.leftOuterJoin(net.indexer(left, 0), net.indexer(right, 0),
        TupleMask.identity(2), t -> true);
    assertEquals(List.of(String.class, Integer.class, String.class, Boolean.class),
        outer.getColumnTypes());
    assertEquals(List.of(false, false, true, true), outer.getColumnOptionality());
    left.insert("a", 1);
    left.insert("b", 2);
    right.insert("a", true);
    assertEquals(tuples(Tuple.of("a", 1, "a", true),
        Tuple.of("b", 2, Tuple.ABSENT, Tuple.ABSENT)), contents(outer));

    TestSource general = source(CharSequence.class);
    TestSource specific = source(String.class);
    LeftOuterJoinNode narrowed = net.leftOuterJoin(net.indexer(general, 0),
        net.indexer(specific, 0), TupleMask.identity(1), t -> true);
    assertEquals(List.of(CharSequence.class, String.class), narrowed.getColumnTypes());
  }

  /**
   * The left columns keep their optionality. Left tuples with
   * {@link Tuple#ABSENT} in their key have no partner and are padded; right
   * tuples with {@code ABSENT} in their key have no effect. {@code ABSENT} in
   * other right columns is passed on like other values; the condition reads
   * it as {@code null}.
   */
  @Test
  public void testOptionalColumns() {
    TestSource optionalLeft = source(List.of(true, false), String.class, Integer.class);
    TestSource optionalRight = source(List.of(true, true), String.class, Boolean.class);
    LeftOuterJoinNode outer = net.leftOuterJoin(net.indexer(optionalLeft, 0),
        net.indexer(optionalRight, 0), t -> t.get(2) == null || t.get(2, Boolean.class));
    RecordingReceiver recorder = record(outer);
    assertEquals(List.of(true, false, true), outer.getColumnOptionality());

    optionalLeft.insert(Tuple.ABSENT, 1);
    assertEquals(List.of("+[⊥, 1, ⊥]"), recorder.take());
    optionalRight.insert(Tuple.ABSENT, true);
    assertEquals(List.of(), recorder.take());

    optionalLeft.insert("a", 2);
    optionalRight.insert("a", false);   // violates the condition
    assertEquals(List.of("+[a, 2, ⊥]"), recorder.take());
    // a partner with ABSENT in its non-key column yields the same tuple as padding
    optionalRight.insert("a", Tuple.ABSENT);
    assertEquals(List.of("-[a, 2, ⊥]", "+[a, 2, ⊥]"), recorder.take());

    optionalRight.delete(Tuple.ABSENT, true);
    assertEquals(List.of(), recorder.take());
    assertEquals(tuples(Tuple.of(Tuple.ABSENT, 1, Tuple.ABSENT), Tuple.of("a", 2, Tuple.ABSENT)),
        contents(outer));
    recorder.assertConsistent();
  }

  /**
   * With two indexers of the same source, the changes of a tuple arrive as
   * left and as right change one after the other; a tuple may be its own
   * partner. Here, each edge {@code [x, y]} is joined with the optional
   * successor edges {@code [y, z]}.
   */
  @Test
  public void testIndexersOfSameSource() {
    TestSource edges = source(String.class, String.class);
    LeftOuterJoinNode successors = net.leftOuterJoin(net.indexer(edges, 1),
        net.indexer(edges, 0));                                 // [x, y, z]
    RecordingReceiver recorder = record(successors);

    edges.insert("a", "b");
    edges.insert("b", "c");
    assertEquals(tuples(Tuple.of("a", "b", "c"), Tuple.of("b", "c", Tuple.ABSENT)),
        contents(successors));
    edges.insert("c", "c");
    assertEquals(tuples(Tuple.of("a", "b", "c"), Tuple.of("b", "c", "c"),
        Tuple.of("c", "c", "c")), contents(successors));
    recorder.assertConsistent();

    edges.delete("c", "c");
    edges.delete("a", "b");
    assertEquals(tuples(Tuple.of("b", "c", Tuple.ABSENT)), contents(successors));
    recorder.assertConsistent();
  }

  /**
   * A self outer join on a single indexer, keys of different size, key columns
   * whose types can never hold equal values and an output mask for another
   * arity are rejected.
   */
  @Test
  public void testInvalidConfigurationsAreRejected() {
    Indexer byName = net.indexer(left, 0);
    assertThrows(IllegalArgumentException.class, () -> net.leftOuterJoin(byName, byName),
        "self outer join");
    assertThrows(IllegalArgumentException.class,
        () -> net.leftOuterJoin(byName, net.indexer(right, 0, 1)), "keys of different size");
    assertThrows(IllegalArgumentException.class,
        () -> net.leftOuterJoin(net.indexer(left, 1), net.indexer(right, 1)),
        "Integer vs. Boolean");
    assertThrows(IllegalArgumentException.class,
        () -> net.leftOuterJoin(byName, net.indexer(right, 0), TupleMask.of(3, 0), t -> true),
        "output mask");
  }

  /**
   * Disposing the node unregisters it from both indexers, which can then be
   * disposed themselves.
   */
  @Test
  public void testDispose() {
    Indexer leftIndexer = net.indexer(left, 0);
    Indexer rightIndexer = net.indexer(right, 0);
    net.leftOuterJoin(leftIndexer, rightIndexer).dispose();
    assertTrue(leftIndexer.getListeners().isEmpty());
    assertTrue(rightIndexer.getListeners().isEmpty());
    leftIndexer.dispose();
    rightIndexer.dispose();
  }
}
