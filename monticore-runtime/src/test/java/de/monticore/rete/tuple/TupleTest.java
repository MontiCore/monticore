/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.tuple;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TupleTest {

  @Test
  public void testEqualityAndHashCode() {
    assertEquals(Tuple.of("a", 1), Tuple.of("a", 1));
    assertEquals(Tuple.of("a", 1).hashCode(), Tuple.of("a", 1).hashCode());
    assertNotEquals(Tuple.of("a", 1), Tuple.of(1, "a"));
    assertSame(Tuple.EMPTY, Tuple.of());
  }

  @Test
  public void testNullValuesRejected() {
    assertThrows(NullPointerException.class, () -> Tuple.of("a", null));
    assertThrows(NullPointerException.class, () -> Tuple.of("a").append(null));
  }

  @Test
  public void testAppendAndConcat() {
    Tuple t = Tuple.of("a", "b");
    assertEquals(Tuple.of("a", "b", "c"), t.append("c"));
    assertEquals(Tuple.of("a", "b", "z", "x"),
        t.concat(Tuple.of("x", "y", "z"), TupleMask.of(3, 2, 0)));
    assertEquals(t, t.concat(Tuple.of("x"), TupleMask.empty(1)));
  }

  @Test
  public void testMaskTransform() {
    TupleMask mask = TupleMask.of(3, 2, 0, 2);
    assertEquals(Tuple.of("c", "a", "c"), mask.transform(Tuple.of("a", "b", "c")));
    assertEquals(List.of("c", "a", "c"), mask.transform(List.of("a", "b", "c")));
    assertThrows(IllegalArgumentException.class, () -> mask.transform(Tuple.of("a")));
  }

  @Test
  public void testMaskComplementAndIdentity() {
    assertEquals(TupleMask.of(4, 1, 3), TupleMask.of(4, 2, 0).complement());
    assertTrue(TupleMask.identity(3).isIdentity());
    assertFalse(TupleMask.of(3, 0, 1).isIdentity());
    assertEquals(0, TupleMask.identity(2).complement().getSize());
  }

  /**
   * {@link Tuple#ABSENT} (⊥) is a value like any other for creating and
   * comparing tuples: it equals itself, so tuples containing it can be found
   * in memories. The getters return {@code null} for it.
   */
  @Test
  public void testAbsent() {
    Tuple t = Tuple.of("a", Tuple.ABSENT);
    assertEquals(Tuple.of("a", Tuple.ABSENT), t);
    assertEquals(Tuple.of("a", Tuple.ABSENT).hashCode(), t.hashCode());
    assertNotEquals(Tuple.of("a", "b"), t);
    assertEquals("[a, ⊥]", t.toString());

    assertEquals("a", t.get(0));
    assertNull(t.get(1));
    assertNull(t.get(1, String.class));
    assertFalse(t.isAbsent(0));
    assertTrue(t.isAbsent(1));
    assertTrue(t.containsAbsent());
    assertFalse(Tuple.of("a").containsAbsent());
    assertEquals(Arrays.asList("a", null), t.asList());
  }

  /**
   * Appending, concatenating and masks copy {@link Tuple#ABSENT} as it is.
   */
  @Test
  public void testAbsentIsCopied() {
    Tuple t = Tuple.of("a", Tuple.ABSENT);
    assertEquals(Tuple.of("a", Tuple.ABSENT, Tuple.ABSENT), t.append(Tuple.ABSENT));
    assertEquals(Tuple.of("x", Tuple.ABSENT), Tuple.of("x").concat(t, TupleMask.of(2, 1)));
    assertEquals(Tuple.of(Tuple.ABSENT, "a"), TupleMask.of(2, 1, 0).transform(t));
    assertTrue(TupleMask.of(2, 1).transform(t).isAbsent(0));
  }

  @Test
  public void testInvalidMask() {
    assertThrows(IllegalArgumentException.class, () -> TupleMask.of(2, 2));
    assertThrows(IllegalArgumentException.class, () -> TupleMask.of(2, -1));
  }
}
