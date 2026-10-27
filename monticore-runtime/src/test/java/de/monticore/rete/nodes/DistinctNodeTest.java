/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Disposing and rejecting parents of another network are inherited from
 * {@link AbstractSingleParentNode} and tested in {@link FilterNodeTest}.
 */
public class DistinctNodeTest extends AbstractNodeTest {

  /**
   * Only the first insertion and the last deletion of a tuple are passed on;
   * further occurrences only change the internal count.
   */
  @Test
  public void testPropagatesOnlyFirstAndLastOccurrence() {
    TestSource source = source(String.class);
    DistinctNode distinct = net.distinct(source);
    RecordingReceiver recorder = record(distinct);
    assertEquals(source.getColumnTypes(), distinct.getColumnTypes());

    source.insert("a");
    source.insert("a");
    source.insert("b");
    assertEquals(List.of("+[a]", "+[b]"), recorder.take());

    source.delete("a");
    assertEquals(List.of(), recorder.take());
    source.delete("a");
    assertEquals(List.of("-[a]"), recorder.take());
    recorder.assertConsistent();
  }

  /**
   * A distinct node created on a populated source contains each tuple once and
   * knows the counts of the existing occurrences: deleting the first of two
   * occurrences is not passed on, deleting the second one is.
   */
  @Test
  public void testInitializedFromPopulatedSource() {
    TestSource source = source(String.class);
    source.insert("a");
    source.insert("a");
    DistinctNode distinct = net.distinct(source);
    assertEquals(tuples(Tuple.of("a")), contents(distinct));
    RecordingReceiver recorder = record(distinct);

    // the counts of the initial contents are known
    source.delete("a");
    assertEquals(List.of(), recorder.take());
    source.delete("a");
    assertEquals(List.of("-[a]"), recorder.take());
  }

  /**
   * The deletion of a tuple the node does not contain is reported as an
   * inconsistent network.
   */
  @Test
  public void testDeletionOfUnknownTupleIsDetected() {
    TestSource source = source(String.class);
    net.distinct(source);
    IllegalStateException e = assertThrows(IllegalStateException.class, () -> source.delete("a"));
    assertTrue(e.getMessage().startsWith("Inconsistent Rete network"), e.getMessage());
  }
}
