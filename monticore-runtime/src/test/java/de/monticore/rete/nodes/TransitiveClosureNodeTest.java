/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Disposing and rejecting parents of another network are inherited from
 * {@link AbstractSingleParentNode} and tested in {@link FilterNodeTest}.
 */
public class TransitiveClosureNodeTest extends AbstractNodeTest {

  /**
   * [from, to]
   */
  protected TestSource edges;

  @BeforeEach
  public void setUp() {
    edges = source(String.class, String.class);
  }

  /**
   * In a chain a, b, c, each new edge adds the pairs it connects, and deleting
   * an edge deletes the pairs that depended on it. The queries
   * {@code isReachable} and {@code getReachableFrom} reflect the pairs.
   */
  @Test
  public void testChain() {
    TransitiveClosureNode closure = net.transitiveClosure(edges);
    RecordingReceiver recorder = record(closure);
    assertEquals(List.of(String.class, String.class), closure.getColumnTypes());

    edges.insert("a", "b");
    assertEquals(List.of("+[a, b]"), recorder.take());
    edges.insert("b", "c");
    assertEquals(unordered(List.of("+[b, c]", "+[a, c]")), unordered(recorder.take()));
    assertTrue(closure.isReachable("a", "c"));
    assertFalse(closure.isReachable("c", "a"));
    assertFalse(closure.isReachable("x", "a"));
    assertEquals(Set.of("b", "c"), closure.getReachableFrom("a"));
    assertEquals(Set.of(), closure.getReachableFrom("c"));

    edges.delete("a", "b");
    assertEquals(unordered(List.of("-[a, b]", "-[a, c]")), unordered(recorder.take()));
    recorder.assertConsistent();
  }

  /**
   * In a diamond, deleting one path keeps the pair connected by the other one;
   * only deleting both paths deletes it.
   */
  @Test
  public void testAlternativePathKeepsPair() {
    TransitiveClosureNode closure = net.transitiveClosure(edges);
    edges.insert("a", "b");
    edges.insert("b", "d");
    edges.insert("a", "c");
    edges.insert("c", "d");
    RecordingReceiver recorder = record(closure);

    edges.delete("a", "b");
    assertEquals(List.of("-[a, b]"), recorder.take());
    assertTrue(closure.isReachable("a", "d"));
    edges.delete("c", "d");
    assertEquals(unordered(List.of("-[c, d]", "-[a, d]")), unordered(recorder.take()));
    recorder.assertConsistent();
  }

  /**
   * In a cycle, every node reaches itself. Breaking the cycle deletes these
   * pairs and the pairs only connected via the deleted edge.
   */
  @Test
  public void testCycle() {
    TransitiveClosureNode closure = net.transitiveClosure(edges);
    RecordingReceiver recorder = record(closure);
    edges.insert("a", "b");
    edges.insert("b", "a");
    assertEquals(tuples(Tuple.of("a", "b"), Tuple.of("b", "a"), Tuple.of("a", "a"),
        Tuple.of("b", "b")), contents(closure));

    edges.delete("b", "a");
    assertEquals(tuples(Tuple.of("a", "b")), contents(closure));
    recorder.assertConsistent();
  }

  /**
   * An edge reported twice is a single edge for the closure, which only
   * changes when the edge appears for the first time or disappears completely.
   */
  @Test
  public void testParallelEdgesAreCounted() {
    TransitiveClosureNode closure = net.transitiveClosure(edges);
    RecordingReceiver recorder = record(closure);
    edges.insert("a", "b");
    edges.insert("a", "b");
    assertEquals(List.of("+[a, b]"), recorder.take());
    edges.delete("a", "b");
    assertEquals(List.of(), recorder.take());
    edges.delete("a", "b");
    assertEquals(List.of("-[a, b]"), recorder.take());
  }

  /**
   * A closure created on a populated source contains all reachable pairs.
   */
  @Test
  public void testInitializedFromPopulatedSource() {
    edges.insert("a", "b");
    edges.insert("b", "c");
    TransitiveClosureNode closure = net.transitiveClosure(edges);
    assertEquals(tuples(Tuple.of("a", "b"), Tuple.of("b", "c"), Tuple.of("a", "c")),
        contents(closure));
  }

  /**
   * Both output columns may contain sources and targets of edges, so both have
   * the common supertype of the input columns.
   */
  @Test
  public void testColumnTypesAreCommonSupertype() {
    TestSource mixed = source(String.class, CharSequence.class);
    assertEquals(List.of(CharSequence.class, CharSequence.class),
        net.transitiveClosure(mixed).getColumnTypes());
  }

  /**
   * A source that is no binary relation is rejected.
   */
  @Test
  public void testOnlyBinaryRelationsAreAccepted() {
    assertThrows(IllegalArgumentException.class,
        () -> net.transitiveClosure(source(String.class, String.class, String.class)));
  }

  /**
   * The deletion of an unknown edge is reported as an inconsistent network.
   */
  @Test
  public void testDeletionOfUnknownEdgeIsDetected() {
    net.transitiveClosure(edges);
    assertThrows(IllegalStateException.class, () -> edges.delete("a", "b"));
  }
}
