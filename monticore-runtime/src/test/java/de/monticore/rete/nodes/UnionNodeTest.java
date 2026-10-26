/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UnionNodeTest extends AbstractNodeTest {

  @Test
  public void testPassesDeltasOfAllParents() {
    TestSource left = source(String.class);
    TestSource right = source(String.class);
    UnionNode union = net.union(left, right);
    RecordingReceiver recorder = record(union);
    assertEquals(List.of(left, right), union.getParents());

    left.insert("a");
    right.insert("a");
    right.insert("b");
    left.delete("a");
    assertEquals(List.of("+[a]", "+[a]", "+[b]", "-[a]"), recorder.take());
    assertEquals(tuples(Tuple.of("a"), Tuple.of("b")), contents(union));
    recorder.assertConsistent();
  }

  @Test
  public void testColumnTypesAreCommonSupertypes() {
    TestSource strings = source(String.class, Integer.class);
    TestSource numbers = source(Integer.class, Long.class);
    UnionNode union = net.union(strings, numbers);
    assertEquals(List.of(Serializable.class, Number.class), union.getColumnTypes());
  }

  @Test
  public void testParentContainedTwiceContributesTwice() {
    TestSource source = source(String.class);
    source.insert("a");
    UnionNode union = net.union(source, source);
    RecordingReceiver recorder = record(union);
    assertEquals(tuples(Tuple.of("a"), Tuple.of("a")), contents(union));

    source.insert("b");
    assertEquals(List.of("+[b]", "+[b]"), recorder.take());

    recorder.assertConsistent();
    union.detachChild(recorder);
    union.dispose();
    assertTrue(source.getChildren().isEmpty());
  }

  @Test
  public void testInvalidParentsAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> new UnionNode(net, List.of()));
    assertThrows(IllegalArgumentException.class,
        () -> net.union(source(String.class), source(String.class, String.class)));
    TestSource foreign = new TestSource(new TestNetwork(), String.class);
    assertThrows(IllegalArgumentException.class, () -> net.union(source(String.class), foreign));
  }
}
