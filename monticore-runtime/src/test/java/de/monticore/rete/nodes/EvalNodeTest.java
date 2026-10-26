/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Disposing and rejecting parents of another network are inherited from
 * {@link AbstractSingleParentNode} and tested in {@link FilterNodeTest}.
 */
public class EvalNodeTest extends AbstractNodeTest {

  @Test
  public void testAppendsComputedValue() {
    TestSource source = source(String.class);
    EvalNode eval = net.eval(source, t -> t.get(0, String.class).length(), Integer.class);
    RecordingReceiver recorder = record(eval);
    assertEquals(List.of(String.class, Integer.class), eval.getColumnTypes());

    source.insert("abc");
    source.insert("abc");
    source.delete("abc");
    assertEquals(List.of("+[abc, 3]", "+[abc, 3]", "-[abc, 3]"), recorder.take());
    recorder.assertConsistent();
  }

  @Test
  public void testPrimitiveResultTypeIsWrapped() {
    TestSource source = source(String.class);
    EvalNode eval = net.eval(source, t -> t.get(0, String.class).length(), int.class);
    assertEquals(List.of(String.class, Integer.class), eval.getColumnTypes());
    source.insert("ab");
    assertEquals(tuples(Tuple.of("ab", 2)), contents(eval));
  }

  @Test
  public void testNullResultDropsTuple() {
    TestSource source = source(String.class);
    source.insert("");
    EvalNode eval = net.eval(source, t -> t.get(0, String.class).isEmpty() ? null : "x",
        String.class);
    RecordingReceiver recorder = record(eval);
    assertTrue(contents(eval).isEmpty());

    source.insert("");
    source.insert("a");
    source.delete("");
    assertEquals(List.of("+[a, x]"), recorder.take());
  }

  @Test
  public void testResultOfWrongTypeIsRejected() {
    TestSource source = source(String.class);
    net.eval(source, t -> "not a number", Integer.class);
    assertThrows(ClassCastException.class, () -> source.insert("a"));
  }

  /**
   * Eval nodes have no memory: the function runs for every delta and every
   * pull of the contents.
   */
  @Test
  public void testFunctionIsEvaluatedPerDeltaAndPull() {
    TestSource source = source(String.class);
    AtomicInteger calls = new AtomicInteger();
    EvalNode eval = net.eval(source, t -> calls.incrementAndGet(), Integer.class);
    net.distinct(eval);
    source.insert("a");
    assertEquals(1, calls.get());
    contents(eval);
    assertEquals(2, calls.get());
    assertThrows(IllegalStateException.class, () -> source.delete("a"),
        "a non-deterministic function makes the network inconsistent");
  }

  @Test
  public void testPullIntoKeepsMultiplicities() {
    TestSource source = source(String.class);
    source.insert("a");
    source.insert("a");
    EvalNode eval = net.eval(source, t -> 1, Integer.class);
    assertEquals(tuples(Tuple.of("a", 1), Tuple.of("a", 1)), contents(eval));
  }
}
