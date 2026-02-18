package de.monticore.rte.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class FLinkedListTest {

  public FLinkedListTest() {

  }

  protected FList<String> of(String str) {
    FList<String> res = FList.of();
    for (char c : str.toCharArray()) {
      res = res.withPrepended(Character.toString(c));
    }
    return res.reversed();
  }

  @SuppressWarnings("unchecked")
  protected <T> FList<T> repeated(int count, T... values) {
    Object[] fullArray = new Object[values.length * count];
    for (int i = 0; i < count; i++) {
      System.arraycopy(values, 0, fullArray, i * values.length, values.length);
    }
    return (FList<T>) FList.of(fullArray);
  }

  @Test
  public void testOf() {
    Assertions.assertEquals(new FLinkedList<>(), FList.of());
    Assertions.assertEquals(new FLinkedList<>(), FList.of(new String[0]));
    Assertions.assertEquals(new FLinkedList<>(List.of("a")), FList.of("a"));
  }

  @Test
  public void testEmptyConstructor() {
    FList<String> l = FList.of();
    Assertions.assertTrue(l.isEmpty());
    Assertions.assertEquals(0, l.size());
  }

  @Test
  public void testConstructorArray() {
    FList<String> l = new FLinkedList<>(new String[0]);
    Assertions.assertTrue(l.isEmpty());
    Assertions.assertEquals(0, l.size());

    l = new FLinkedList<>(new String[] { "h" });
    Assertions.assertFalse(l.isEmpty());
    Assertions.assertEquals(1, l.size());
    Assertions.assertEquals("h", l.get(0));

    l = new FLinkedList<>(new String[] { "h", "e", "l" });
    Assertions.assertFalse(l.isEmpty());
    Assertions.assertEquals(3, l.size());
    Assertions.assertEquals("h", l.get(0));
    Assertions.assertEquals("e", l.get(1));
    Assertions.assertEquals("l", l.get(2));

    l = new FLinkedList<>(repeated(FLinkedList.MAX_ARRAY_SIZE, "a", "b").toArray(String[]::new));
    Assertions.assertFalse(l.isEmpty());
    Assertions.assertEquals(2 * FLinkedList.MAX_ARRAY_SIZE, l.size());
    Assertions.assertEquals("a", l.get(0));
    Assertions.assertEquals("b", l.get(1));
    Assertions.assertEquals("b", l.get(FLinkedList.MAX_ARRAY_SIZE - 1));
    Assertions.assertEquals("a", l.get(FLinkedList.MAX_ARRAY_SIZE));
    Assertions.assertEquals("b", l.get(FLinkedList.MAX_ARRAY_SIZE + 1));
    Assertions.assertEquals("b", l.get(2 * FLinkedList.MAX_ARRAY_SIZE - 1));
  }

  @Test
  public void testConstructorFCollection() {
    FList<String> l = new FLinkedList<>(FList.of());
    Assertions.assertTrue(l.isEmpty());
    Assertions.assertEquals(0, l.size());

    l = new FLinkedList<>(FList.of("h"));
    Assertions.assertFalse(l.isEmpty());
    Assertions.assertEquals(1, l.size());
    Assertions.assertEquals("h", l.get(0));

    l = new FLinkedList<>(FList.of("h", "e", "l"));
    Assertions.assertFalse(l.isEmpty());
    Assertions.assertEquals(3, l.size());
    Assertions.assertEquals("h", l.get(0));
    Assertions.assertEquals("e", l.get(1));
    Assertions.assertEquals("l", l.get(2));

    l = new FLinkedList<>(repeated(FLinkedList.MAX_ARRAY_SIZE, "a", "b"));
    Assertions.assertFalse(l.isEmpty());
    Assertions.assertEquals(2 * FLinkedList.MAX_ARRAY_SIZE, l.size());
    Assertions.assertEquals("a", l.get(0));
    Assertions.assertEquals("b", l.get(1));
    Assertions.assertEquals("b", l.get(FLinkedList.MAX_ARRAY_SIZE - 1));
    Assertions.assertEquals("a", l.get(FLinkedList.MAX_ARRAY_SIZE));
    Assertions.assertEquals("b", l.get(FLinkedList.MAX_ARRAY_SIZE + 1));
    Assertions.assertEquals("b", l.get(2 * FLinkedList.MAX_ARRAY_SIZE - 1));
  }

  @Test
  public void testConstructorCollection() {
    FList<String> l = new FLinkedList<>(List.of());
    Assertions.assertTrue(l.isEmpty());
    Assertions.assertEquals(0, l.size());

    l = new FLinkedList<>(List.of("h"));
    Assertions.assertFalse(l.isEmpty());
    Assertions.assertEquals(1, l.size());
    Assertions.assertEquals("h", l.get(0));

    l = new FLinkedList<>(List.of("h", "e", "l"));
    Assertions.assertFalse(l.isEmpty());
    Assertions.assertEquals(3, l.size());
    Assertions.assertEquals("h", l.get(0));
    Assertions.assertEquals("e", l.get(1));
    Assertions.assertEquals("l", l.get(2));

    l = new FLinkedList<>(repeated(FLinkedList.MAX_ARRAY_SIZE, "a", "b").toJava());
    Assertions.assertFalse(l.isEmpty());
    Assertions.assertEquals(2 * FLinkedList.MAX_ARRAY_SIZE, l.size());
    Assertions.assertEquals("a", l.get(0));
    Assertions.assertEquals("b", l.get(1));
    Assertions.assertEquals("b", l.get(FLinkedList.MAX_ARRAY_SIZE - 1));
    Assertions.assertEquals("a", l.get(FLinkedList.MAX_ARRAY_SIZE));
    Assertions.assertEquals("b", l.get(FLinkedList.MAX_ARRAY_SIZE + 1));
    Assertions.assertEquals("b", l.get(2 * FLinkedList.MAX_ARRAY_SIZE - 1));
  }

  @Test
  public void testSize() {
    Assertions.assertEquals(0, FList.of().size());
    Assertions.assertEquals(1, FList.of("h").size());
    Assertions.assertEquals(2, FList.of("h", "e").size());
    Assertions.assertEquals(FLinkedList.MAX_ARRAY_SIZE * 10, FList.of(new String[FLinkedList.MAX_ARRAY_SIZE * 10]).size());
  }

  @Test
  public void testIsEmpty() {
    Assertions.assertTrue(FList.of().isEmpty());
    Assertions.assertFalse(FList.of("h").isEmpty());
    Assertions.assertFalse(FList.of("h", "e").isEmpty());
  }

  @Test
  public void testContains() {
    Assertions.assertFalse(FList.of().contains("h"));
    Assertions.assertFalse(FList.of("e").contains("h"));
    Assertions.assertTrue(FList.of("e").contains("e"));
    Assertions.assertFalse(FList.of("e", "l", "f").contains("h"));
    Assertions.assertTrue(FList.of("e", "l", "f").contains("e"));
    Assertions.assertTrue(FList.of("e", "l", "f").contains("l"));
    Assertions.assertTrue(FList.of("e", "l", "f").contains("f"));
  }

  @Test
  public void testIterator() {
    Iterator<String> iter = FList.<String> of().iterator();
    Assertions.assertFalse(iter.hasNext());
    Assertions.assertThrowsExactly(NoSuchElementException.class, iter::next);
    Assertions.assertThrowsExactly(UnsupportedOperationException.class, iter::remove);
    iter.forEachRemaining(s -> {
      throw new AssertionError();
    });

    iter = FList.of("h").iterator();
    Assertions.assertTrue(iter.hasNext());
    Assertions.assertEquals("h", iter.next());
    Assertions.assertFalse(iter.hasNext());
    Assertions.assertThrowsExactly(NoSuchElementException.class, iter::next);
    iter.forEachRemaining(s -> {
      throw new AssertionError();
    });

    FList<String> l = of("j").withPrepended(of("ghi")).withPrepended(of("f")).withPrepended(of("de"));
    iter = l.iterator();
    Assertions.assertTrue(iter.hasNext());
    Assertions.assertEquals("d", iter.next());
    Assertions.assertTrue(iter.hasNext());
    List<String> r = new ArrayList<>();
    iter.forEachRemaining(r::add);
    Assertions.assertEquals(of("efghij").toJava(), r);
  }

  @Test
  public void testForEach() {
    Assertions.assertDoesNotThrow(() -> of("").forEach(e -> {
      throw new AssertionError();
    }));

    FList<String> l = of("j").withPrepended(of("ghi")).withPrepended(of("f")).withPrepended(of("de"));

    List<String> r = new ArrayList<>();
    l.forEach(r::add);
    Assertions.assertEquals(of("defghij").toJava(), r);
  }

  @Test
  public void testEquals() {
    Assertions.assertEquals(
        of(""),
        of("")
    );
    Assertions.assertNotEquals(
        of(""),
        of("h")
    );
    Assertions.assertNotEquals(
        of("f"),
        of("h")
    );
    Assertions.assertNotEquals(
        of("fdb"),
        of("hdba")
    );
    Assertions.assertNotEquals(
        of("hdba"),
        of("hdb")
    );
    Assertions.assertNotEquals(
        of("hdb"),
        of("hdba")
    );
    Assertions.assertEquals(
        of("fdba"),
        of("fdba")
    );
    Assertions.assertEquals(
        of("f"),
        of("f")
    );
    FList<String> l = of("j").withPrepended(of("ghi")).withPrepended(of("f")).withPrepended(of("de"));
    Assertions.assertEquals(
        of("defghij"),
        l
    );
    Assertions.assertNotEquals(
        of("deFghij"),
        l
    );
    Assertions.assertNotEquals(
        of("defghiJ"),
        l
    );
    Assertions.assertNotEquals(
        of("defGhiJ"),
        l
    );
    Assertions.assertNotEquals(
        of("defGhIJ"),
        l
    );
    Assertions.assertNotEquals(
        of("defghi"),
        l
    );
    Assertions.assertNotEquals(
        of("defghijk"),
        l
    );
    Assertions.assertNotEquals(
        FList.of(),
        List.of() // A java list!
    );
  }

  @Test
  public void testHashCode() {
    Assertions.assertEquals(
        FList.of().hashCode(),
        FList.of().hashCode()
    );
    Assertions.assertEquals(
        FList.of("f", "d", "b", "a").hashCode(),
        FList.of("f", "d", "b", "a").hashCode()
    );
    Assertions.assertEquals(
        FList.of("f").hashCode(),
        FList.of("f").hashCode()
    );
  }

  @Test
  public void testToString() {
    Assertions.assertEquals(
        "[]",
        FList.of().toString()
    );
    Assertions.assertEquals(
        "[a]",
        FList.of("a").toString()
    );
    Assertions.assertEquals(
        "[a, b, c]",
        FList.of("a", "b", "c").toString()
    );
  }

  @Test
  public void testGetFirst() {
    Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, () -> FList.of().get(0));
    Assertions.assertEquals("h", FList.of("h").get(0));
    Assertions.assertEquals("h", FList.of("h", "e", "l").get(0));
  }

  @Test
  public void testGet() {
    Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, () -> FList.of("h").get(1));
    Assertions.assertEquals("h", FList.of("h").get(0));

    FList<String> l = FList.of("h", "e", "l");
    Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, () -> l.get(-1));
    Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, () -> l.get(3));
    Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, () -> l.get(10000));
    Assertions.assertEquals("h", l.get(0));
    Assertions.assertEquals("e", l.get(1));
    Assertions.assertEquals("l", l.get(2));
  }

  @Test
  public void testWithPrepended() {
    FList<String> l = FList.of("l", "l", "o");
    Assertions.assertEquals(FList.of("e", "l", "l", "o"), l.withPrepended("e"));
    Assertions.assertEquals(FList.of("h", "e", "l", "l", "o"), l.withPrepended(FList.of("h", "e")));
    Assertions.assertEquals(FList.of("e", "l", "l", "o"), l.withPrepended(FList.of("e")));
    Assertions.assertEquals(FList.of("l", "l", "o"), l.withPrepended(FList.of()));
    Assertions.assertEquals(FList.of("l", "l", "o"), l);
  }

  @Test
  public void testWithInsertedElement() {
    FList<String> l = of("j").withPrepended(of("ghi")).withPrepended(of("f")).withPrepended(of("de"));
    Assertions.assertEquals(of("Xdefghij"), l.withInserted(0, "X"));
    Assertions.assertEquals(of("dXefghij"), l.withInserted(1, "X"));
    Assertions.assertEquals(of("deXfghij"), l.withInserted(2, "X"));
    Assertions.assertEquals(of("defXghij"), l.withInserted(3, "X"));
    Assertions.assertEquals(of("defgXhij"), l.withInserted(4, "X"));
    Assertions.assertEquals(of("defghXij"), l.withInserted(5, "X"));
    Assertions.assertEquals(of("defghiXj"), l.withInserted(6, "X"));
    Assertions.assertEquals(of("defghijX"), l.withInserted(7, "X"));

    Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, () -> l.withInserted(-1, "X"));
    Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, () -> l.withInserted(l.size() + 1, "X"));

    // Choice 1 in skipElements
    var s = repeated(FLinkedList.MAX_ARRAY_SIZE, "x").withPrepended("y");
    Assertions.assertEquals(of("y" + "x".repeat(FLinkedList.MAX_ARRAY_SIZE) + "Z"), s.withInserted(s.size(), "Z"));

    // Choice 3 with/without isLastSkipAndSplittingArray in skipElements
    s = of("zzz");
    for (int i = 0; i < FLinkedList.MAX_ARRAY_SIZE - 1; i++) {
      s = s.withPrepended("y");
    }
    Assertions.assertEquals(of("y".repeat(FLinkedList.MAX_ARRAY_SIZE - 1) + "zzzZ"), s.withInserted(s.size(), "Z"));
    Assertions.assertEquals(of("y".repeat(FLinkedList.MAX_ARRAY_SIZE - 1) + "zzZz"), s.withInserted(s.size() - 1, "Z"));

    s = of("z".repeat(FLinkedList.MIN_NO_MERGE_ARRAY_SIZE + 1));
    for (int i = 0; i < FLinkedList.MAX_ARRAY_SIZE - 1; i++) {
      s = s.withPrepended("y");
    }
    Assertions.assertEquals(of("y".repeat(FLinkedList.MAX_ARRAY_SIZE - 1) + "z".repeat(FLinkedList.MIN_NO_MERGE_ARRAY_SIZE) + "Zz"), s.withInserted(s.size() - 1, "Z"));

    // chunking array filled with single element links
    s = of("z");
    for (int i = 0; i < 2 * FLinkedList.MAX_ARRAY_SIZE; i++) {
      s = s.withPrepended("y");
    }
    Assertions.assertEquals(of("y".repeat(2 * FLinkedList.MAX_ARRAY_SIZE) + "Zz"), s.withInserted(s.size() - 1, "Z"));

    // chunking array filled with arrays links
    s = of("yy");
    for (int i = 0; i < FLinkedList.MAX_ARRAY_SIZE - 2; i++) {
      s = s.withPrepended("y");
    }
    Assertions.assertEquals(of("y".repeat(FLinkedList.MAX_ARRAY_SIZE) + "Z"), s.withInserted(s.size(), "Z"));

  }

  @Test
  public void testWithInsertedCollection() {
    FList<String> l = of("j").withPrepended(of("ghi")).withPrepended(of("f")).withPrepended(of("de"));
    Assertions.assertEquals(of("XYdefghij"), l.withInserted(0, of("XY")));
    Assertions.assertEquals(of("dXYefghij"), l.withInserted(1, of("XY")));
    Assertions.assertEquals(of("deXYfghij"), l.withInserted(2, of("XY")));
    Assertions.assertEquals(of("defXYghij"), l.withInserted(3, of("XY")));
    Assertions.assertEquals(of("defgXYhij"), l.withInserted(4, of("XY")));
    Assertions.assertEquals(of("defghXYij"), l.withInserted(5, of("XY")));
    Assertions.assertEquals(of("defghiXYj"), l.withInserted(6, of("XY")));
    Assertions.assertEquals(of("defghijXY"), l.withInserted(7, of("XY")));

    Assertions.assertEquals(of("defghij"), l.withInserted(5, of("")));
    Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, () -> l.withInserted(-1, of("X")));
    Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, () -> l.withInserted(l.size() + 1, of("X")));
  }

  @Test
  public void testWithoutFirst() {
    FList<String> l = of("hello");
    Assertions.assertEquals(of("ello"), l.withoutFirst());
    Assertions.assertEquals(of("hello"), l.withoutFirst(0));
    Assertions.assertEquals(of("ello"), l.withoutFirst(1));
    Assertions.assertEquals(of("llo"), l.withoutFirst(2));
    Assertions.assertEquals(of("o"), l.withoutFirst(4));
    Assertions.assertEquals(of(""), l.withoutFirst(5));
    Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> l.withoutFirst(-1));
    Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> l.withoutFirst(6));

    Assertions.assertThrowsExactly(NoSuchElementException.class, () -> FList.of().withoutFirst());
    Assertions.assertEquals(FList.of(), FList.of("h").withoutFirst(1));
    Assertions.assertEquals(FList.of(), FList.of("h").withoutFirst());
    Assertions.assertEquals(FList.of(), FList.of().withoutFirst(0));
  }

  @Test
  public void testWithRemoved() {
    FList<String> l = of("j").withPrepended(of("ghi")).withPrepended(of("f")).withPrepended(of("de"));
    Assertions.assertEquals(of("defghij"), l.withRemoved(0, 0));
    Assertions.assertEquals(of("defghij"), l.withRemoved(3, 0));
    Assertions.assertEquals(of("defghij"), l.withRemoved(l.size(), 0));

    Assertions.assertEquals(of("efghij"), l.withRemoved(0, 1));
    Assertions.assertEquals(of("fghij"), l.withRemoved(0, 2));
    Assertions.assertEquals(of("ghij"), l.withRemoved(0, 3));
    Assertions.assertEquals(of("hij"), l.withRemoved(0, 4));
    Assertions.assertEquals(of("ij"), l.withRemoved(0, 5));
    Assertions.assertEquals(of("j"), l.withRemoved(0, 6));
    Assertions.assertEquals(of(""), l.withRemoved(0, 7));

    Assertions.assertEquals(of("dfghij"), l.withRemoved(1, 1));
    Assertions.assertEquals(of("deghij"), l.withRemoved(2, 1));
    Assertions.assertEquals(of("defhij"), l.withRemoved(3, 1));
    Assertions.assertEquals(of("defgij"), l.withRemoved(4, 1));
    Assertions.assertEquals(of("defghj"), l.withRemoved(5, 1));
    Assertions.assertEquals(of("defghi"), l.withRemoved(6, 1));

    Assertions.assertEquals(of("fghij"), l.withRemoved(0, 2));
    Assertions.assertEquals(of("dghij"), l.withRemoved(1, 2));
    Assertions.assertEquals(of("dehij"), l.withRemoved(2, 2));
    Assertions.assertEquals(of("defij"), l.withRemoved(3, 2));
    Assertions.assertEquals(of("defgj"), l.withRemoved(4, 2));
    Assertions.assertEquals(of("defgh"), l.withRemoved(5, 2));

    Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> l.withRemoved(0, -1));
    Assertions.assertThrowsExactly(IndexOutOfBoundsException.class, () -> l.withRemoved(-1, 1));
    Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> l.withRemoved(5, 100));
  }

  @Test
  public void testToJava() {
    Assertions.assertEquals(List.of(), FList.of().toJava());
    Assertions.assertEquals(List.of("h"), FList.of("h").toJava());
    Assertions.assertEquals(List.of("h", "e", "l"), FList.of("h", "e", "l").toJava());

    FList<String> l = FList.of("h", "e", "l");
    List<String> javaL = l.toJava();

    Assertions.assertThrows(Exception.class, () -> javaL.add("u"));
    Assertions.assertThrows(Exception.class, () -> javaL.remove(0));
    Assertions.assertEquals(l, FList.of("h", "e", "l"));
  }

  @Test
  public void testReversed() {
    Assertions.assertEquals(of(""), of("").reversed());
    Assertions.assertEquals(of("a"), of("a").reversed());
    Assertions.assertEquals(of("dcba"), of("abcd").reversed());
  }

  @Test
  public void testFiltered() {
    Assertions.assertEquals(of(""), of("").filtered(e -> true));
    Assertions.assertEquals(of("abc"), of("abc").filtered(e -> true));
    Assertions.assertEquals(of(""), of("abc").filtered(e -> false));
    Assertions.assertEquals(of("ac"), of("aBcD").filtered(e -> Character.isLowerCase(e.charAt(0))));
  }

  @Test
  public void testMapped() {
    Assertions.assertEquals(of(""), of("").mapped(String::toUpperCase));
    Assertions.assertEquals(of("A"), of("a").mapped(String::toUpperCase));
    Assertions.assertEquals(of("ABC"), of("abc").mapped(String::toUpperCase));
  }

  @Test
  public void testFlatMapped() {
    Assertions.assertEquals(of(""), of("").flatMapped(ele -> of(ele.repeat(2))));
    Assertions.assertEquals(of("aabb"), of("ab").flatMapped(ele -> of(ele.repeat(2))));
    Assertions.assertEquals(of(""), of("ab").flatMapped(ele -> of("")));
  }

  @Test
  public void distinct() {
    Assertions.assertEquals(of(""), of("").distinct());
    Assertions.assertEquals(of("a"), of("a").distinct());
    Assertions.assertEquals(of("a"), of("aaaaaaaaaaaaaa").distinct());
    Assertions.assertEquals(of("abcdfeg"), of("abbcdcafefg").distinct());
  }

  @Test
  public void sorted() {
    Assertions.assertEquals(of(""), of("").sorted());
    Assertions.assertEquals(of("a"), of("a").sorted());
    Assertions.assertEquals(of("ab"), of("ab").sorted());
    Assertions.assertEquals(of("ab"), of("ba").sorted());
    Assertions.assertEquals(of("aabcdeefg"), of("efageacbd").sorted());
  }

  @Test
  public void sortedComparator() {
    Assertions.assertEquals(of(""), of("").sorted(Comparator.reverseOrder()));
    Assertions.assertEquals(of("a"), of("a").sorted(Comparator.reverseOrder()));
    Assertions.assertEquals(of("ba"), of("ab").sorted(Comparator.reverseOrder()));
    Assertions.assertEquals(of("ba"), of("ba").sorted(Comparator.reverseOrder()));
    Assertions.assertEquals(of("gfeedcbaa"), of("efageacbd").sorted(Comparator.reverseOrder()));
  }
}
