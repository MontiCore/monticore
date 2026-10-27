/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link JoinNode}: the deltas produced for changes on both sides,
 * multiplicities, output columns and the special case of a self join.
 */
public class JoinNodeTest extends AbstractNodeTest {

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
   * A change on either side is combined with all tuples of the other side
   * having the same key; the output consists of the left columns followed by
   * the non-key columns of the right side. Changes without partner produce no
   * delta.
   */
  @Test
  public void testJoinsOnEqualKeys() {
    Indexer leftIndexer = net.indexer(left, 0);
    Indexer rightIndexer = net.indexer(right, 0);
    JoinNode join = net.join(leftIndexer, rightIndexer);
    RecordingReceiver recorder = record(join);
    assertSame(leftIndexer, join.getLeft());
    assertSame(rightIndexer, join.getRight());
    assertEquals(TupleMask.of(2, 1), join.getRightOutputMask());
    assertEquals(List.of(String.class, Integer.class, Boolean.class), join.getColumnTypes());

    left.insert("a", 1);
    assertEquals(List.of(), recorder.take());
    right.insert("a", true);
    right.insert("b", false);
    assertEquals(List.of("+[a, 1, true]"), recorder.take());
    left.insert("a", 2);
    assertEquals(List.of("+[a, 2, true]"), recorder.take());

    right.delete("a", true);
    assertEquals(unordered(List.of("-[a, 1, true]", "-[a, 2, true]")),
        unordered(recorder.take()));
    recorder.assertConsistent();
  }

  /**
   * A pair occurs as often as the product of the multiplicities of its left
   * and right tuple: a change is passed on once per occurrence of its partner.
   */
  @Test
  public void testMultiplicitiesAreMultiplied() {
    JoinNode join = net.join(net.indexer(left, 0), net.indexer(right, 0));
    RecordingReceiver recorder = record(join);
    left.insert("a", 1);
    left.insert("a", 1);
    right.insert("a", true);
    assertEquals(List.of("+[a, 1, true]", "+[a, 1, true]"), recorder.take());
    right.insert("a", true);
    assertEquals(4, contents(join).count(Tuple.of("a", 1, true)));
    recorder.assertConsistent();
  }

  /**
   * A join created on populated indexers provides all pairs with the products
   * of their multiplicities.
   */
  @Test
  public void testInitializedFromPopulatedIndexers() {
    left.insert("a", 1);
    left.insert("a", 1);
    right.insert("a", true);
    right.insert("a", false);
    JoinNode join = net.join(net.indexer(left, 0), net.indexer(right, 0));
    assertEquals(tuples(Tuple.of("a", 1, true), Tuple.of("a", 1, true),
        Tuple.of("a", 1, false), Tuple.of("a", 1, false)), contents(join));
  }

  /**
   * An explicit output mask selects the right columns appended to the left
   * tuple, here including the right key column.
   */
  @Test
  public void testRightOutputMask() {
    JoinNode join = net.join(net.indexer(left, 0), net.indexer(right, 0), TupleMask.identity(2));
    assertEquals(List.of(String.class, Integer.class, String.class, Boolean.class),
        join.getColumnTypes());
    left.insert("a", 1);
    right.insert("a", true);
    assertEquals(tuples(Tuple.of("a", 1, "a", true)), contents(join));
  }

  /**
   * Joined key values are instances of both key column types, so the output
   * type of the key columns is the more specific one.
   */
  @Test
  public void testKeyColumnsAreNarrowed() {
    TestSource general = source(CharSequence.class);
    TestSource specific = source(String.class);
    JoinNode join = net.join(net.indexer(general, 0), net.indexer(specific, 0),
        TupleMask.identity(1));
    assertEquals(List.of(String.class, String.class), join.getColumnTypes());
  }

  /**
   * Keys of different size, key columns whose types can never hold equal
   * values and an output mask for another arity are rejected.
   */
  @Test
  public void testInvalidConfigurationsAreRejected() {
    Indexer byName = net.indexer(left, 0);
    assertThrows(IllegalArgumentException.class,
        () -> net.join(byName, net.indexer(right, 0, 1)), "keys of different size");
    assertThrows(IllegalArgumentException.class,
        () -> net.join(net.indexer(left, 1), net.indexer(right, 1)), "Integer vs. Boolean");
    assertThrows(IllegalArgumentException.class,
        () -> net.join(byName, net.indexer(right, 0), TupleMask.of(3, 0)), "output mask");
  }

  /**
   * In a self join on a shared indexer, the join is notified only once per
   * change but has to process it as left and as right tuple. Each pair must
   * nevertheless be produced exactly as often as its multiplicity, also for
   * repeated occurrences of a tuple and for deletions.
   */
  @Test
  public void testSelfJoin() {
    TestSource members = source(String.class, String.class);   // [member, group]
    Indexer byGroup = net.indexer(members, 1);
    JoinNode pairs = net.join(byGroup, byGroup);               // [x, group, y]
    RecordingReceiver recorder = record(pairs);
    assertEquals(List.of(pairs), byGroup.getListeners());

    members.insert("a", "g");
    assertEquals(List.of("+[a, g, a]"), recorder.take());
    members.insert("b", "g");
    assertEquals(unordered(List.of("+[a, g, b]", "+[b, g, a]", "+[b, g, b]")),
        unordered(recorder.take()));

    // a second occurrence of b: 2 x 2 instead of 1 x 1 pairs [b, g, b]
    members.insert("b", "g");
    assertEquals(unordered(List.of("+[a, g, b]", "+[b, g, a]", "+[b, g, b]", "+[b, g, b]",
        "+[b, g, b]")), unordered(recorder.take()));
    assertEquals(4, contents(pairs).count(Tuple.of("b", "g", "b")));
    recorder.assertConsistent();

    members.delete("a", "g");
    assertEquals(unordered(List.of("-[a, g, a]", "-[a, g, b]", "-[b, g, a]",
        "-[a, g, b]", "-[b, g, a]")), unordered(recorder.take()));
    members.delete("b", "g");
    members.delete("b", "g");
    recorder.assertConsistent();
    assertTrue(contents(pairs).isEmpty());
  }

  /**
   * Disposing a join unregisters it from both indexers, which can then be
   * disposed themselves.
   */
  @Test
  public void testDispose() {
    Indexer leftIndexer = net.indexer(left, 0);
    Indexer rightIndexer = net.indexer(right, 0);
    JoinNode join = net.join(leftIndexer, rightIndexer);
    join.dispose();
    assertTrue(leftIndexer.getListeners().isEmpty());
    assertTrue(rightIndexer.getListeners().isEmpty());
    leftIndexer.dispose();
    rightIndexer.dispose();
  }
}
