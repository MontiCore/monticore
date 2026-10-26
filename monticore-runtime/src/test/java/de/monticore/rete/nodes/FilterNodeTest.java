/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Also covers the behavior of {@link AbstractSingleParentNode} shared by all
 * nodes with a single parent: disposing and rejecting parents of another
 * network (see {@link AbstractNodeTest}).
 */
public class FilterNodeTest extends AbstractNodeTest {

  @Test
  public void testPassesOnlyMatchingDeltas() {
    TestSource source = source(String.class, Integer.class);
    FilterNode filter = net.filter(source, t -> t.get(1, Integer.class) > 0);
    RecordingReceiver recorder = record(filter);
    assertEquals(source.getColumnTypes(), filter.getColumnTypes());
    assertSame(source, filter.getParent());

    source.insert("a", 1);
    source.insert("b", -1);
    source.insert("a", 1);
    assertEquals(List.of("+[a, 1]", "+[a, 1]"), recorder.take());

    source.delete("b", -1);
    source.delete("a", 1);
    assertEquals(List.of("-[a, 1]"), recorder.take());
    recorder.assertConsistent();
  }

  @Test
  public void testPullIntoKeepsMultiplicities() {
    TestSource source = source(String.class, Integer.class);
    source.insert("a", 1);
    source.insert("a", 1);
    source.insert("b", -1);
    FilterNode filter = net.filter(source, t -> t.get(1, Integer.class) > 0);
    assertEquals(tuples(Tuple.of("a", 1), Tuple.of("a", 1)), contents(filter));
  }

  @Test
  public void testEquality() {
    TestSource source = source(String.class, String.class, String.class);
    FilterNode twoColumns = net.equality(source, 0, 1);
    FilterNode threeColumns = net.equality(source, 0, 1, 2);
    source.insert("a", "a", "b");
    source.insert("a", "a", "a");
    source.insert("a", "b", "a");
    assertEquals(tuples(Tuple.of("a", "a", "b"), Tuple.of("a", "a", "a")), contents(twoColumns));
    assertEquals(tuples(Tuple.of("a", "a", "a")), contents(threeColumns));
  }

  @Test
  public void testInequality() {
    TestSource source = source(String.class, String.class);
    FilterNode inequality = net.inequality(source, 0, 1);
    source.insert("a", "a");
    source.insert("a", "b");
    assertEquals(tuples(Tuple.of("a", "b")), contents(inequality));
  }

  @Test
  public void testPredicateMustNotModifyNetwork() {
    TestSource source = source(String.class);
    net.filter(source, t -> {
      net.filter(source, x -> true);
      return true;
    });
    assertThrows(IllegalStateException.class, () -> source.insert("a"));
  }

  @Test
  public void testDispose() {
    TestSource source = source(String.class);
    FilterNode filter = net.filter(source, t -> true);
    FilterNode child = net.filter(filter, t -> true);
    assertThrows(IllegalStateException.class, filter::dispose, "node with children");

    child.dispose();
    filter.dispose();
    assertTrue(filter.isDisposed());
    assertTrue(source.getChildren().isEmpty());
    filter.dispose();  // disposing twice has no effect
    assertThrows(IllegalStateException.class, () -> net.filter(filter, t -> true),
        "attaching to a disposed node");
  }

  @Test
  public void testParentOfOtherNetworkIsRejected() {
    TestSource foreign = new TestSource(new TestNetwork(), String.class);
    assertThrows(IllegalArgumentException.class, () -> net.filter(foreign, t -> true));
  }
}
