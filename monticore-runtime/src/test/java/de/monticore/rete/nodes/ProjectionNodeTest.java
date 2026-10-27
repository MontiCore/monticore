/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import de.monticore.rete.tuple.TupleMask;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Disposing and rejecting parents of another network are inherited from
 * {@link AbstractSingleParentNode} and tested in {@link FilterNodeTest}.
 */
public class ProjectionNodeTest extends AbstractNodeTest {

  /**
   * The mask selects, reorders and repeats columns; the column types are
   * transformed accordingly, and each delta is passed on projected.
   */
  @Test
  public void testSelectsAndReordersColumns() {
    TestSource source = source(String.class, Integer.class, Boolean.class);
    ProjectionNode projection = net.project(source, 2, 0, 0);
    RecordingReceiver recorder = record(projection);
    assertEquals(List.of(Boolean.class, String.class, String.class), projection.getColumnTypes());
    assertEquals(TupleMask.of(3, 2, 0, 0), projection.getMask());

    source.insert("a", 1, true);
    source.delete("a", 1, true);
    assertEquals(List.of("+[true, a, a]", "-[true, a, a]"), recorder.take());
  }

  /**
   * Different tuples with equal projections are not merged: each insertion
   * and deletion is passed on, so the projection contains duplicates.
   */
  @Test
  public void testKeepsMultiplicities() {
    TestSource source = source(String.class, Integer.class);
    source.insert("a", 1);
    ProjectionNode projection = net.project(source, 0);
    RecordingReceiver recorder = record(projection);

    source.insert("a", 2);
    assertEquals(List.of("+[a]"), recorder.take());
    assertEquals(tuples(Tuple.of("a"), Tuple.of("a")), contents(projection));

    source.delete("a", 1);
    assertEquals(List.of("-[a]"), recorder.take());
    recorder.assertConsistent();
  }

  /**
   * A projection to no column yields one empty tuple per source tuple.
   */
  @Test
  public void testEmptyProjection() {
    TestSource source = source(String.class);
    ProjectionNode projection = net.project(source, TupleMask.empty(1));
    source.insert("a");
    source.insert("b");
    assertEquals(0, projection.getArity());
    assertEquals(tuples(Tuple.EMPTY, Tuple.EMPTY), contents(projection));
  }

  /**
   * A mask for another arity and a column index out of range are rejected.
   */
  @Test
  public void testMaskMustFitArity() {
    TestSource source = source(String.class, Integer.class);
    assertThrows(IllegalArgumentException.class,
        () -> net.project(source, TupleMask.of(3, 0)));
    assertThrows(IllegalArgumentException.class, () -> net.project(source, 2));
  }
}
