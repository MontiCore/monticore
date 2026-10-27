/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link Indexer}: the memory grouping the tuples of a source by
 * key, and the notification of its listeners.
 */
public class IndexerTest extends AbstractNodeTest {

  /**
   * The tuples are grouped by the values of the key columns, with
   * multiplicities; unknown keys yield no tuples, and keys whose last tuple is
   * deleted disappear.
   */
  @Test
  public void testGroupsTuplesByKey() {
    TestSource source = source(String.class, Integer.class);
    Indexer indexer = net.indexer(source, 0);
    assertSame(source, indexer.getSource());
    assertEquals(TupleMask.of(2, 0), indexer.getMask());
    assertEquals(List.of(String.class), indexer.getKeyTypes());

    source.insert("a", 1);
    source.insert("a", 2);
    source.insert("a", 2);
    source.insert("b", 1);
    assertEquals(Set.of(Tuple.of("a"), Tuple.of("b")), indexer.getKeys());
    assertEquals(3, indexer.count(Tuple.of("a")));
    assertEquals(tuples(Tuple.of("a", 1), Tuple.of("a", 2), Tuple.of("a", 2)),
        indexer.get(Tuple.of("a")));
    assertEquals(0, indexer.count(Tuple.of("c")));
    assertTrue(indexer.get(Tuple.of("c")).isEmpty());

    // keys without tuples are removed
    source.delete("b", 1);
    assertEquals(Set.of(Tuple.of("a")), indexer.getKeys());
  }

  /**
   * A key may consist of several columns in any order; an empty key puts all
   * tuples under the empty tuple.
   */
  @Test
  public void testCompositeAndEmptyKeys() {
    TestSource source = source(String.class, Integer.class, Boolean.class);
    Indexer composite = net.indexer(source, 2, 0);
    Indexer empty = net.indexer(source);
    source.insert("a", 1, true);
    source.insert("a", 2, true);
    source.insert("b", 1, true);
    assertEquals(List.of(Boolean.class, String.class), composite.getKeyTypes());
    assertEquals(2, composite.count(Tuple.of(true, "a")));
    assertEquals(Set.of(Tuple.EMPTY), empty.getKeys());
    assertEquals(3, empty.count(Tuple.EMPTY));
  }

  /**
   * An indexer created on a populated source contains its tuples with their
   * multiplicities and keeps track of later deletions.
   */
  @Test
  public void testInitializedFromPopulatedSource() {
    TestSource source = source(String.class, Integer.class);
    source.insert("a", 1);
    source.insert("a", 1);
    Indexer indexer = net.indexer(source, 0);
    assertEquals(2, indexer.count(Tuple.of("a")));
    source.delete("a", 1);
    assertEquals(1, indexer.count(Tuple.of("a")));
  }

  /**
   * Listeners are notified about each change in order, with the key of the
   * tuple, and already see the updated memory (the count includes the
   * change). Removed listeners are no longer notified.
   */
  @Test
  public void testListenersAreNotifiedAfterTheUpdate() {
    TestSource source = source(String.class, Integer.class);
    Indexer indexer = net.indexer(source, 0);
    RecordingIndexerListener listener = new RecordingIndexerListener();
    indexer.addListener(listener);
    assertEquals(List.of(listener), indexer.getListeners());

    source.insert("a", 1);
    source.insert("a", 2);
    source.delete("a", 1);
    assertEquals(List.of("+[a, 1] key [a] count 1", "+[a, 2] key [a] count 2",
        "-[a, 1] key [a] count 1"), listener.take());

    indexer.removeListener(listener);
    source.insert("b", 1);
    assertEquals(List.of(), listener.take());
  }

  /**
   * The tuples, keys and listeners returned by the indexer cannot be modified.
   */
  @Test
  public void testContentsAreUnmodifiable() {
    TestSource source = source(String.class);
    Indexer indexer = net.indexer(source, 0);
    source.insert("a");
    assertThrows(UnsupportedOperationException.class,
        () -> indexer.get(Tuple.of("a")).add(Tuple.of("b")));
    assertThrows(UnsupportedOperationException.class, () -> indexer.getKeys().clear());
    assertThrows(UnsupportedOperationException.class,
        () -> indexer.getListeners().add(new RecordingIndexerListener()));
  }

  /**
   * The deletion of an unknown tuple is reported as an inconsistent network,
   * both for an unknown key and for an unknown tuple of a known key.
   */
  @Test
  public void testDeletionOfUnknownTupleIsDetected() {
    TestSource source = source(String.class, Integer.class);
    net.indexer(source, 0);
    source.insert("a", 1);
    // unknown key and unknown tuple with known key
    assertThrows(IllegalStateException.class, () -> source.delete("b", 1));
    assertThrows(IllegalStateException.class, () -> source.delete("a", 2));
  }

  /**
   * A key mask for another arity and a source of another network are rejected.
   */
  @Test
  public void testMaskMustFitArity() {
    TestSource source = source(String.class, Integer.class);
    assertThrows(IllegalArgumentException.class,
        () -> net.indexer(source, TupleMask.of(3, 0)));
    TestSource foreign = new TestSource(new TestNetwork(), String.class);
    assertThrows(IllegalArgumentException.class, () -> net.indexer(foreign, 0));
  }

  /**
   * An indexer with listeners cannot be disposed; a disposed indexer is
   * detached from its source and accepts no new listeners.
   */
  @Test
  public void testDispose() {
    TestSource source = source(String.class);
    Indexer indexer = net.indexer(source, 0);
    RecordingIndexerListener listener = new RecordingIndexerListener();
    indexer.addListener(listener);
    assertThrows(IllegalStateException.class, indexer::dispose, "indexer with listeners");

    indexer.removeListener(listener);
    indexer.dispose();
    assertTrue(source.getChildren().isEmpty());
    assertThrows(IllegalStateException.class, () -> indexer.addListener(listener));
  }
}
