/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ColumnTypesTest {

  static class First {
  }

  static class Second {
  }

  @Test
  public void testMayOverlap() {
    assertTrue(ColumnTypes.mayOverlap(String.class, String.class));
    assertTrue(ColumnTypes.mayOverlap(CharSequence.class, String.class), "supertype");
    assertTrue(ColumnTypes.mayOverlap(String.class, Object.class), "subtype");
    assertTrue(ColumnTypes.mayOverlap(Runnable.class, CharSequence.class), "two interfaces");
    assertTrue(ColumnTypes.mayOverlap(Runnable.class, First.class), "subclass may implement");
    assertTrue(ColumnTypes.mayOverlap(First.class, Runnable.class), "symmetric");
    assertFalse(ColumnTypes.mayOverlap(Runnable.class, String.class), "final class");
    assertFalse(ColumnTypes.mayOverlap(String.class, Runnable.class), "symmetric");
    assertFalse(ColumnTypes.mayOverlap(First.class, Second.class), "unrelated classes");
  }

  @Test
  public void testNarrower() {
    assertSame(String.class, ColumnTypes.narrower(CharSequence.class, String.class));
    assertSame(String.class, ColumnTypes.narrower(String.class, CharSequence.class));
    assertSame(Runnable.class, ColumnTypes.narrower(Runnable.class, CharSequence.class),
        "unrelated types keep the first one");
  }

  @Test
  public void testCommonSupertype() {
    assertSame(CharSequence.class, ColumnTypes.commonSupertype(CharSequence.class, String.class));
    assertSame(CharSequence.class, ColumnTypes.commonSupertype(String.class, CharSequence.class));
    assertSame(Number.class, ColumnTypes.commonSupertype(Integer.class, Long.class));
    assertSame(AbstractList.class, ColumnTypes.commonSupertype(ArrayList.class, LinkedList.class),
        "superclass before interfaces");
    assertSame(Serializable.class, ColumnTypes.commonSupertype(String.class, Integer.class),
        "interface");
    assertSame(Object.class, ColumnTypes.commonSupertype(First.class, Second.class));
    assertSame(List.class, ColumnTypes.commonSupertype(List.class, ArrayList.class));
  }
}
