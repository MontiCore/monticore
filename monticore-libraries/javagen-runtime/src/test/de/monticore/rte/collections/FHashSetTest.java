/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class FHashSetTest {

  @Test
  public void testConstructorArray() {
    Assertions.assertEquals(
        FSet.of(),
        new FHashSet<>()
    );
    Assertions.assertEquals(
        FSet.of("a"),
        new FHashSet<>(new String[] { "a" })
    );
  }

  @Test
  public void testConstructorFCollection() {
    Assertions.assertEquals(
        FSet.of(),
        new FHashSet<>(FList.of())
    );
    Assertions.assertEquals(
        FSet.of("a"),
        new FHashSet<>(FList.of("a"))
    );
  }

  @Test
  public void testConstructorCollection() {
    Assertions.assertEquals(
        FSet.of(),
        new FHashSet<>(List.of())
    );
    Assertions.assertEquals(
        FSet.of("a"),
        new FHashSet<>(List.of("a"))
    );
  }

  @Test
  public void testWith() {
    Assertions.assertEquals(
        FSet.of("a"),
        FSet.of().with("a")
    );
    Assertions.assertEquals(
        FSet.of("a", "b"),
        FSet.of("a").with("b")
    );
    Assertions.assertEquals(
        FSet.of("a", "b"),
        FSet.of("a", "b").with("b")
    );
  }

  @Test
  public void testWithAll() {
    Assertions.assertEquals(
        FSet.of(),
        FSet.of().withAll(FList.of())
    );
    Assertions.assertEquals(
        FSet.of("a", "b"),
        FSet.of().withAll(FList.of("a", "b"))
    );
  }

  @Test
  public void testWithout() {
    Assertions.assertEquals(
        FSet.of("a", "b"),
        FSet.of("a", "b").without("c")
    );
    Assertions.assertEquals(
        FSet.of("a"),
        FSet.of("a", "b").without("b")
    );
    Assertions.assertEquals(
        FSet.of(),
        FSet.of("a").without("a")
    );
  }

  @Test
  public void testWithoutAll() {
    Assertions.assertEquals(
        FSet.of("a", "b"),
        FSet.of("a", "b").withoutAll(FList.of())
    );
    Assertions.assertEquals(
        FSet.of("a"),
        FSet.of("a", "b").withoutAll(FList.of("b"))
    );
    Assertions.assertEquals(
        FSet.of(),
        FSet.of("a", "b").withoutAll(FList.of("a", "b"))
    );
  }

  @Test
  public void testSize() {
    Assertions.assertEquals(0, FSet.of().size());
    Assertions.assertEquals(2, FSet.of("a", "b").size());
  }

  @Test
  public void testIsEmpty() {
    Assertions.assertTrue(FSet.of().isEmpty());
    Assertions.assertFalse(FSet.of("a").isEmpty());
  }

  @Test
  public void testContains() {
    Assertions.assertFalse(FSet.of().contains("a"));
    Assertions.assertFalse(FSet.of("a", "b").contains("c"));
    Assertions.assertTrue(FSet.of("a", "b").contains("a"));
  }

  @Test
  public void testToJava() {
    Assertions.assertEquals(
        Set.of("a", "b"),
        FSet.of("a", "b").toJava()
    );
  }

  @Test
  public void testIterator() {
    var iter = FSet.of("a").iterator();
    Assertions.assertTrue(iter.hasNext());
    Assertions.assertEquals("a", iter.next());
    Assertions.assertFalse(iter.hasNext());
    Assertions.assertThrowsExactly(NoSuchElementException.class, () -> iter.next());
  }

  @Test
  public void testHashCode() {
    Assertions.assertEquals(
        15,
        FSet.of(5, 10).hashCode()
    );
  }

  @Test
  public void testEquals() {
    Assertions.assertEquals(
        FSet.of(),
        FSet.of()
    );
    Assertions.assertEquals(
        FSet.of("a"),
        FSet.of("a")
    );
    Assertions.assertNotEquals(
        FSet.of("a"),
        FSet.of("b")
    );
    Assertions.assertNotEquals(
        FSet.of("a"),
        FSet.of("a", "b")
    );
    Assertions.assertNotEquals(
        FSet.of("a", "b"),
        FSet.of("a")
    );
  }

  @Test
  public void testToString() {
    Assertions.assertEquals(
        "{}",
        FSet.of().toString()
    );
    Assertions.assertEquals(
        "{a}",
        FSet.of("a").toString()
    );
  }

  @Test
  public void testOf() {
    Assertions.assertEquals(FSet.of(), new FHashSet<>());
    Assertions.assertEquals(FSet.of(new Object[] {}), new FHashSet<>());
    Assertions.assertEquals(FSet.of("a"), new FHashSet<>("a"));
    Assertions.assertEquals(FSet.of("a", "b"), new FHashSet<>("a", "b"));

    Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> FSet.of("a", "a"));
    Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> FSet.of("a", "b", "a"));
  }

}
