/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

  @Test
  public void testRightOutputMask() {
    JoinNode join = net.join(net.indexer(left, 0), net.indexer(right, 0), TupleMask.identity(2));
    assertEquals(List.of(String.class, Integer.class, String.class, Boolean.class),
        join.getColumnTypes());
    left.insert("a", 1);
    right.insert("a", true);
    assertEquals(tuples(Tuple.of("a", 1, "a", true)), contents(join));
  }

  @Test
  public void testKeyColumnsAreNarrowed() {
    TestSource general = source(CharSequence.class);
    TestSource specific = source(String.class);
    JoinNode join = net.join(net.indexer(general, 0), net.indexer(specific, 0),
        TupleMask.identity(1));
    assertEquals(List.of(String.class, String.class), join.getColumnTypes());
  }

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
   * In a self join on a shared indexer, each pair must be produced exactly
   * once, although both sides are notified about the same change.
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
