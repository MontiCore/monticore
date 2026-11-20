/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Also covers the behavior of {@link AbstractSingleParentNode} shared by all
 * nodes with a single parent: disposing and rejecting parents of another
 * network, and the column metadata of {@link AbstractSourceNode} (see
 * {@link AbstractNodeTest}).
 */
public class FilterNodeTest extends AbstractNodeTest {

  /**
   * The optional columns of the parent stay optional; the predicate receives
   * {@code null} for {@link Tuple#ABSENT}, and tuples containing it are passed
   * on like other tuples.
   */
  @Test
  public void testOptionalColumns() {
    TestSource source = source(List.of(false, true), String.class, String.class);
    FilterNode filter = net.filter(source, t -> t.get(1) == null);
    RecordingReceiver recorder = record(filter);
    assertEquals(List.of(false, true), filter.getColumnOptionality());
    assertTrue(filter.isOptional(1));
    assertTrue(filter.hasOptionalColumns());

    source.insert("a", Tuple.ABSENT);
    source.insert("b", "x");
    source.delete("a", Tuple.ABSENT);
    assertEquals(List.of("+[a, ⊥]", "-[a, ⊥]"), recorder.take());
    recorder.assertConsistent();
  }

  /**
   * Column metadata as implemented by {@link AbstractSourceNode}: without
   * explicit optionality, no column is optional; the optionality has to fit
   * the column types; the string representation lists the column types and
   * marks optional columns.
   */
  @Test
  public void testColumnMetadata() {
    TestSource plain = source(String.class, Integer.class);
    assertEquals(List.of(false, false), plain.getColumnOptionality());
    assertFalse(plain.hasOptionalColumns());
    assertEquals("TestSource[String, Integer]", plain.toString());

    TestSource optional = source(List.of(false, true), String.class, Integer.class);
    assertEquals("TestSource[String, Integer?]", optional.toString());

    assertThrows(IllegalArgumentException.class,
        () -> source(List.of(false), String.class, Integer.class));
  }

  /**
   * Insertions and deletions of tuples satisfying the predicate are passed on
   * unchanged and in order, including repeated occurrences; deltas of other
   * tuples are dropped. The output columns are those of the parent.
   */
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

  /**
   * A filter created on a populated source provides the satisfying tuples of
   * the source with their multiplicities.
   */
  @Test
  public void testPullIntoKeepsMultiplicities() {
    TestSource source = source(String.class, Integer.class);
    source.insert("a", 1);
    source.insert("a", 1);
    source.insert("b", -1);
    FilterNode filter = net.filter(source, t -> t.get(1, Integer.class) > 0);
    assertEquals(tuples(Tuple.of("a", 1), Tuple.of("a", 1)), contents(filter));
  }

  /**
   * {@code equality} keeps the tuples whose given columns are all equal, for
   * two as well as for more columns.
   */
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

  /**
   * {@code inequality} keeps the tuples whose two given columns differ.
   */
  @Test
  public void testInequality() {
    TestSource source = source(String.class, String.class);
    FilterNode inequality = net.inequality(source, 0, 1);
    source.insert("a", "a");
    source.insert("a", "b");
    assertEquals(tuples(Tuple.of("a", "b")), contents(inequality));
  }

  /**
   * A predicate creating a node during propagation is rejected with an
   * {@link IllegalStateException}.
   */
  @Test
  public void testPredicateMustNotModifyNetwork() {
    TestSource source = source(String.class);
    net.filter(source, t -> {
      net.filter(source, x -> true);
      return true;
    });
    assertThrows(IllegalStateException.class, () -> source.insert("a"));
  }

  /**
   * Disposing as implemented by {@link AbstractSingleParentNode}: a node with
   * children cannot be disposed; a disposed node is detached from its parent,
   * disposing it again has no effect, and no node can be attached to it.
   */
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

  /**
   * A node whose parent belongs to another network is rejected (implemented
   * by {@link AbstractSingleParentNode}).
   */
  @Test
  public void testParentOfOtherNetworkIsRejected() {
    TestSource foreign = new TestSource(new TestNetwork(), String.class);
    assertThrows(IllegalArgumentException.class, () -> net.filter(foreign, t -> true));
  }
}
