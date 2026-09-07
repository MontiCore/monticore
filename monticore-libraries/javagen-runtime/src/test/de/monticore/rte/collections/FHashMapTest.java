/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FHashMapTest {

  protected static class TestObject {
    protected final int hash;

    protected TestObject(int hash) {
      this.hash = hash;
    }

    @Override
    public int hashCode() {
      return hash;
    }

  }

  protected static final TestObject T10 = new TestObject(10);
  protected static final TestObject T12 = new TestObject(12);
  protected static final TestObject T15 = new TestObject(15);
  protected static final TestObject T17 = new TestObject(17);
  protected static final TestObject T20 = new TestObject(20);
  protected static final TestObject T23 = new TestObject(23);
  protected static final TestObject T25 = new TestObject(25);
  protected static final TestObject T30 = new TestObject(30);
  protected static final TestObject T52 = new TestObject(52);

  protected static final TestObject A10 = new TestObject(10);
  protected static final TestObject A15 = new TestObject(15);
  protected static final TestObject A17 = new TestObject(17);
  protected static final TestObject A20 = new TestObject(20);
  protected static final TestObject A25 = new TestObject(25);
  protected static final TestObject A30 = new TestObject(30);

  protected static final TestObject B17 = new TestObject(17);
  protected static final TestObject B20 = new TestObject(20);

  protected FMap<TestObject, String> getTestMap() {
    //        20
    //      /    \
    //    10      30
    //   /  \    /
    // null 15  25
    //       \
    //       17
    return FMap.<TestObject, String> of()
        // This builds the tree in a balanced way (no rotations needed)
        .with(T20, "a")
        .with(T30, "b")
        .with(T10, "c")
        .with(T15, "d")
        .with(T25, "e")
        .with(null, "f") // null has hash 0
        .with(T17, "g")
        // Some hash collisions
        .with(A20, null)
        .with(A17, "h")
        .with(B17, "i")
        ;
  }

  @Test
  public void testSize() {
    Assertions.assertEquals(FMap.of().size(), 0);
    Assertions.assertEquals(FMap.of("a", 5).size(), 1);
    Assertions.assertEquals(FMap.of("a", 5, "b", 3).size(), 2);
  }

  @Test
  public void testIsEmpty() {
    Assertions.assertTrue(FMap.of().isEmpty());
    Assertions.assertFalse(FMap.of("a", 5).isEmpty());
  }

  @Test
  public void testContainsKey() {
    FMap<TestObject, String> map = getTestMap();

    Assertions.assertTrue(map.containsKey(T20));
    Assertions.assertTrue(map.containsKey(T30));
    Assertions.assertTrue(map.containsKey(T10));
    Assertions.assertTrue(map.containsKey(T15));
    Assertions.assertTrue(map.containsKey(T25));
    Assertions.assertTrue(map.containsKey(null));
    Assertions.assertTrue(map.containsKey(T17));
    Assertions.assertTrue(map.containsKey(A20));
    Assertions.assertTrue(map.containsKey(A17));
    Assertions.assertTrue(map.containsKey(B17));

    Assertions.assertFalse(map.containsKey(A10));
    Assertions.assertFalse(map.containsKey(B20));
    Assertions.assertFalse(map.containsKey(T12));
    Assertions.assertFalse(map.containsKey(T52));
  }

  @Test
  public void testContainsValue() {
    FMap<TestObject, String> map = getTestMap();

    Assertions.assertTrue(map.containsValue("a"));
    Assertions.assertTrue(map.containsValue("b"));
    Assertions.assertTrue(map.containsValue("c"));
    Assertions.assertTrue(map.containsValue("d"));
    Assertions.assertTrue(map.containsValue("e"));
    Assertions.assertTrue(map.containsValue("f"));
    Assertions.assertTrue(map.containsValue("g"));
    Assertions.assertTrue(map.containsValue(null));
    Assertions.assertTrue(map.containsValue("h"));
    Assertions.assertTrue(map.containsValue("i"));

    Assertions.assertFalse(map.containsValue("j"));
    Assertions.assertFalse(map.containsValue("k"));
  }

  @Test
  public void testGet() {
    FMap<TestObject, String> map = getTestMap();

    Assertions.assertEquals("a", map.get(T20));
    Assertions.assertEquals("b", map.get(T30));
    Assertions.assertEquals("c", map.get(T10));
    Assertions.assertEquals("d", map.get(T15));
    Assertions.assertEquals("e", map.get(T25));
    Assertions.assertEquals("f", map.get(null));
    Assertions.assertEquals("g", map.get(T17));
    Assertions.assertNull(map.get(A20));
    Assertions.assertEquals("h", map.get(A17));
    Assertions.assertEquals("i", map.get(B17));

    Assertions.assertNull(map.get(A10));
    Assertions.assertNull(map.get(B20));
    Assertions.assertNull(map.get(T12));
    Assertions.assertNull(map.get(T52));
  }

  @Test
  public void testGetOrDefault() {
    var map = getTestMap();

    Assertions.assertEquals("a", map.getOrDefault(T20, "X"));
    Assertions.assertEquals("X", map.getOrDefault(A25, "X"));
    Assertions.assertNull(map.getOrDefault(A20, "X"));
  }

  @Test
  public void testWithRotations() {
    FMap<TestObject, String> map = FMap.of();

    map = map.with(T20, "t20");
    Assertions.assertEquals(1, map.size());
    Assertions.assertEquals("t20", map.get(T20));

    map = map.with(T25, "t25");
    Assertions.assertEquals(2, map.size());
    Assertions.assertEquals("t20", map.get(T20));
    Assertions.assertEquals("t25", map.get(T25));

    // left rotation
    map = map.with(T30, "t30");
    //   25
    //  /  \
    // 20  30
    Assertions.assertEquals(3, map.size());
    Assertions.assertEquals("t20", map.get(T20));
    Assertions.assertEquals("t25", map.get(T25));
    Assertions.assertEquals("t30", map.get(T30));

    // right rotation
    map = map.with(T15, "t15").with(T10, "t10");
    //     25
    //    /  \
    //   15  30
    //  /  \
    // 10  20
    Assertions.assertEquals(5, map.size());
    Assertions.assertEquals("t10", map.get(T10));
    Assertions.assertEquals("t15", map.get(T15));
    Assertions.assertEquals("t20", map.get(T20));
    Assertions.assertEquals("t25", map.get(T25));
    Assertions.assertEquals("t30", map.get(T30));

    // left, then right rotation
    map = map.with(T23, "t23");
    //     20
    //    /  \
    //   15  25
    //  /   /  \
    // 10  23  30
    Assertions.assertEquals(6, map.size());
    Assertions.assertEquals("t10", map.get(T10));
    Assertions.assertEquals("t15", map.get(T15));
    Assertions.assertEquals("t20", map.get(T20));
    Assertions.assertEquals("t23", map.get(T23));
    Assertions.assertEquals("t25", map.get(T25));
    Assertions.assertEquals("t30", map.get(T30));

    map = FMap.<TestObject, String> of()
        .with(T15, "t15")
        .with(T10, "t10")
        .with(T25, "t25")
        .with(T20, "t20")
        .with(T30, "t30")
    ;
    //     15
    //    /  \
    //   10  25
    //      /  \
    //     20  30

    // right, then left rotation
    map = map.with(T17, "t17");
    //     20
    //    /  \
    //   15  25
    //  /  \   \
    // 10  17  30
    Assertions.assertEquals(6, map.size());
    Assertions.assertEquals("t10", map.get(T10));
    Assertions.assertEquals("t15", map.get(T15));
    Assertions.assertEquals("t17", map.get(T17));
    Assertions.assertEquals("t20", map.get(T20));
    Assertions.assertEquals("t25", map.get(T25));
    Assertions.assertEquals("t30", map.get(T30));
  }

  @Test
  public void testWithCollisions() {
    FMap<TestObject, String> map = FMap.of();

    map = map.with(T17, "t17");
    Assertions.assertEquals(map.size(), 1);
    Assertions.assertEquals("t17", map.get(T17));

    // Remap value
    map = map.with(T17, "t17!");
    Assertions.assertEquals(1, map.size());
    Assertions.assertEquals("t17!", map.get(T17));

    // Same value
    map = map.with(T17, "t17!");
    Assertions.assertEquals(1, map.size());
    Assertions.assertEquals("t17!", map.get(T17));

    // new key, now using multi store
    map = map.with(A17, "a17");
    Assertions.assertEquals(2, map.size());
    Assertions.assertEquals("t17!", map.get(T17));
    Assertions.assertEquals("a17", map.get(A17));

    // new key
    map = map.with(B17, "b17");
    Assertions.assertEquals(3, map.size());
    Assertions.assertEquals("t17!", map.get(T17));
    Assertions.assertEquals("a17", map.get(A17));
    Assertions.assertEquals("b17", map.get(B17));

    // Remap value
    map = map.with(A17, "a17!");
    Assertions.assertEquals(3, map.size());
    Assertions.assertEquals("t17!", map.get(T17));
    Assertions.assertEquals("a17!", map.get(A17));
    Assertions.assertEquals("b17", map.get(B17));

    // Same value
    map = map.with(A17, "a17!");
    Assertions.assertEquals(3, map.size());
    Assertions.assertEquals("t17!", map.get(T17));
    Assertions.assertEquals("a17!", map.get(A17));
    Assertions.assertEquals("b17", map.get(B17));
  }

  @Test
  public void testWithSameValueInTree() {
    FMap<TestObject, String> map = FMap.of();

    map = map.with(T20, "t20").with(T10, "t10").with(T30, "t30");
    Assertions.assertEquals(3, map.size());
    Assertions.assertEquals("t10", map.get(T10));
    Assertions.assertEquals("t20", map.get(T20));
    Assertions.assertEquals("t30", map.get(T30));

    // same values
    map = map.with(T20, "t20").with(T10, "t10").with(T30, "t30");
    Assertions.assertEquals(3, map.size());
    Assertions.assertEquals("t10", map.get(T10));
    Assertions.assertEquals("t20", map.get(T20));
    Assertions.assertEquals("t30", map.get(T30));
  }

  @Test
  public void testWithout() {
    FMap<TestObject, String> map = getTestMap();
    Assertions.assertEquals(10, map.size());
    Assertions.assertEquals("h", map.get(A17));

    // Remove middle from multi store with 3 values
    map = map.without(A17);
    Assertions.assertEquals(9, map.size());
    Assertions.assertFalse(map.containsKey(A17));

    Assertions.assertEquals("a", map.get(T20));
    Assertions.assertEquals("b", map.get(T30));
    Assertions.assertEquals("c", map.get(T10));
    Assertions.assertEquals("d", map.get(T15));
    Assertions.assertEquals("e", map.get(T25));
    Assertions.assertEquals("f", map.get(null));
    Assertions.assertEquals("g", map.get(T17));
    Assertions.assertNull(map.get(A20));
    Assertions.assertEquals("i", map.get(B17));

    // Remove first from multi store with 2 values
    map = map.without(T17);
    Assertions.assertEquals(8, map.size());
    Assertions.assertFalse(map.containsKey(T17));

    Assertions.assertEquals("a", map.get(T20));
    Assertions.assertEquals("b", map.get(T30));
    Assertions.assertEquals("c", map.get(T10));
    Assertions.assertEquals("d", map.get(T15));
    Assertions.assertEquals("e", map.get(T25));
    Assertions.assertEquals("f", map.get(null));
    Assertions.assertNull(map.get(A20));
    Assertions.assertEquals("i", map.get(B17));

    // Remove last from multi store with 2 values
    map = map.without(A20);
    Assertions.assertEquals(7, map.size());
    Assertions.assertFalse(map.containsKey(A20));

    Assertions.assertEquals("a", map.get(T20));
    Assertions.assertEquals("b", map.get(T30));
    Assertions.assertEquals("c", map.get(T10));
    Assertions.assertEquals("d", map.get(T15));
    Assertions.assertEquals("e", map.get(T25));
    Assertions.assertEquals("f", map.get(null));
    Assertions.assertEquals("i", map.get(B17));

    // Remove node with one right child
    map = map.without(T15);
    //        20
    //      /    \
    //    10      30
    //   /  \    /
    // null 17  25
    Assertions.assertEquals(6, map.size());
    Assertions.assertFalse(map.containsKey(T15));

    Assertions.assertEquals("a", map.get(T20));
    Assertions.assertEquals("b", map.get(T30));
    Assertions.assertEquals("c", map.get(T10));
    Assertions.assertEquals("e", map.get(T25));
    Assertions.assertEquals("f", map.get(null));
    Assertions.assertEquals("i", map.get(B17));

    // Remove node with one left child
    map = map.without(T30);
    //        20
    //      /    \
    //    10      25
    //   /  \
    // null 17
    Assertions.assertEquals(5, map.size());
    Assertions.assertFalse(map.containsKey(T30));

    Assertions.assertEquals("a", map.get(T20));
    Assertions.assertEquals("c", map.get(T10));
    Assertions.assertEquals("e", map.get(T25));
    Assertions.assertEquals("f", map.get(null));
    Assertions.assertEquals("i", map.get(B17));

    // Remove node with no children
    map = map.without(T25);
    Assertions.assertEquals(4, map.size());
    Assertions.assertFalse(map.containsKey(T25));

    Assertions.assertEquals("a", map.get(T20));
    Assertions.assertEquals("c", map.get(T10));
    Assertions.assertEquals("f", map.get(null));
    Assertions.assertEquals("i", map.get(B17));

    // New test map
    map = getTestMap().without(A20).without(B17);
    Assertions.assertEquals(8, map.size());

    // Remove non-existent, unknown hash
    map = map.without(T52);
    Assertions.assertEquals(8, map.size());
    Assertions.assertFalse(map.containsKey(T52));

    // Remove non-existent, same hash as other node
    map = map.without(A20);
    Assertions.assertEquals(8, map.size());
    Assertions.assertFalse(map.containsKey(A20));

    // Remove non-existent, same hash as other multi store node
    map = map.without(B17);
    Assertions.assertEquals(8, map.size());
    Assertions.assertFalse(map.containsKey(B17));

    // Remove node with two children
    map = map.without(T20);
    Assertions.assertEquals(7, map.size());
    Assertions.assertFalse(map.containsKey(T20));

    Assertions.assertEquals("b", map.get(T30));
    Assertions.assertEquals("c", map.get(T10));
    Assertions.assertEquals("d", map.get(T15));
    Assertions.assertEquals("e", map.get(T25));
    Assertions.assertEquals("f", map.get(null));
    Assertions.assertEquals("g", map.get(T17));
    Assertions.assertEquals("h", map.get(A17));

    // Remove root
    map = FMap.of(A20, "a");
    Assertions.assertEquals(map.size(), 1);
    Assertions.assertEquals("a", map.get(A20));
    map = map.without(A20);
    Assertions.assertEquals(map.size(), 0);
    Assertions.assertFalse(map.containsKey(A20));
  }

  @Test
  public void testWithoutValueCheck() {
    var map = FMap.of(A20, "a", A30, null);
    Assertions.assertEquals(
        FMap.of(A20, "a", A30, null),
        map.without(A20, "b")
    );
    Assertions.assertEquals(
        FMap.of(A30, null),
        map.without(A20, "a")
    );
    Assertions.assertEquals(
        FMap.of(A20, "a"),
        map.without(A30, null)
    );
  }

  @Test
  public void testWithAll() {
    FMap<TestObject, String> map = FMap.of();

    map = map.withAll(FMap.of());
    Assertions.assertEquals(0, map.size());

    map = map.withAll(FMap.of(A20, "a20"));
    Assertions.assertEquals(1, map.size());
    Assertions.assertEquals("a20", map.get(A20));

    map = map.withAll(FMap.of(T10, "t10", T15, "t15"));
    Assertions.assertEquals(3, map.size());
    Assertions.assertEquals("a20", map.get(A20));
    Assertions.assertEquals("t10", map.get(T10));
    Assertions.assertEquals("t15", map.get(T15));

    map = map.withAll(FMap.of(T10, "t10", T15, "t15"));
    Assertions.assertEquals(3, map.size());
    Assertions.assertEquals("a20", map.get(A20));
    Assertions.assertEquals("t10", map.get(T10));
    Assertions.assertEquals("t15", map.get(T15));
  }

  @Test
  public void testWithoutAll() {
    FMap<TestObject, String> map = FMap.of(A20, "a20", T10, "t10", T15, "t15");

    Assertions.assertEquals(3, map.size());
    Assertions.assertEquals("a20", map.get(A20));
    Assertions.assertEquals("t10", map.get(T10));
    Assertions.assertEquals("t15", map.get(T15));

    map = map.withoutAll(FList.of());
    Assertions.assertEquals(3, map.size());
    Assertions.assertEquals("a20", map.get(A20));
    Assertions.assertEquals("t10", map.get(T10));
    Assertions.assertEquals("t15", map.get(T15));

    map = map.withoutAll(FList.of(A20, T10));
    Assertions.assertEquals(1, map.size());
    Assertions.assertEquals("t15", map.get(T15));
    Assertions.assertFalse(map.containsKey(A20));
    Assertions.assertFalse(map.containsKey(T10));

    map = map.withoutAll(FList.of(A20, T15));
    Assertions.assertEquals(0, map.size());
    Assertions.assertFalse(map.containsKey(T15));
  }

  @Test
  public void testForEach() {
    FMap<TestObject, String> map = getTestMap();
    Set<Map.Entry<TestObject, String>> entries = new HashSet<>();
    map.forEach((k, v) -> entries.add(new SimpleEntry<>(k, v)));

    Assertions.assertEquals(
        Set.of(
            new SimpleEntry<>(T20, "a"),
            new SimpleEntry<>(T30, "b"),
            new SimpleEntry<>(T10, "c"),
            new SimpleEntry<>(T15, "d"),
            new SimpleEntry<>(T25, "e"),
            new SimpleEntry<>(null, "f"),
            new SimpleEntry<>(T17, "g"),
            new SimpleEntry<>(A20, null),
            new SimpleEntry<>(A17, "h"),
            new SimpleEntry<>(B17, "i")
        ),
        entries
    );
  }

  @Test
  public void testKeySet() {
    FMap<TestObject, String> map = getTestMap();
    Assertions.assertEquals(10, map.size());
    FSet<TestObject> keySet = map.keySet();

    Assertions.assertEquals(10, keySet.size());
    Assertions.assertFalse(keySet.isEmpty());

    Assertions.assertTrue(keySet.contains(T20));
    Assertions.assertFalse(keySet.contains(T52));

    Assertions.assertEquals(0, FMap.of().keySet().hashCode());
    Assertions.assertEquals(5, FMap.of(5, 6).keySet().hashCode());
    Assertions.assertEquals(5 + 2, FMap.of(5, 6, 2, 3).keySet().hashCode());

    Assertions.assertTrue(FMap.of().keySet().equals(FSet.of()));
    Assertions.assertTrue(FMap.of(5, 10).keySet().equals(FSet.of(5)));
    Assertions.assertTrue(FMap.of(5, 10, 6, 30).keySet().equals(FSet.of(5, 6)));
    Assertions.assertFalse(FMap.of().keySet().equals(FSet.of(5)));
    Assertions.assertFalse(FMap.of(5, 10).keySet().equals(FSet.of(6)));
    Assertions.assertFalse(FMap.of(5, 10).keySet().equals(null));

    Assertions.assertEquals(Set.of(5, 6), FMap.of(5, 10, 6, 30).keySet().toJava());

    Assertions.assertEquals(FSet.of(5, 6), FMap.of(5, 10).keySet().with(6));
    Assertions.assertEquals(FSet.of(5, 6, 7), FMap.of(5, 10).keySet().withAll(FList.of(6, 7)));
    Assertions.assertEquals(FSet.of(5), FMap.of(5, 10, 6, 11).keySet().without(6));
    Assertions.assertEquals(FSet.of(), FMap.of(5, 10, 6, 11).keySet().withoutAll(FList.of(5, 6)));

    Set<TestObject> itSet = StreamSupport.stream(Spliterators.spliteratorUnknownSize(
        getTestMap().without(null).keySet().iterator(),
        Spliterator.ORDERED
    ), false).collect(Collectors.toSet());

    Assertions.assertEquals(Set.of(T20, T30, T10, T15, T25, T17, A20, A17, B17), itSet);

    itSet = new HashSet<>();
    getTestMap().without(null).keySet().forEach(itSet::add);
    Assertions.assertEquals(Set.of(T20, T30, T10, T15, T25, T17, A20, A17, B17), itSet);

    Assertions.assertEquals(
        "{}",
        FMap.of().keySet().toString()
    );
    Assertions.assertEquals(
        "{a}",
        FMap.of("a", 5).keySet().toString()
    );
    Assertions.assertTrue(
        FSet.of("{a, b}", "{b, a}").contains(
            FMap.of("a", 5, "b", 6).keySet().toString()
        )
    );
  }

  @Test
  public void testEntrySet() {
    FMap<TestObject, String> map = getTestMap();
    Assertions.assertEquals(10, map.size());
    FSet<Map.Entry<TestObject, String>> entrySet = map.entrySet();

    Assertions.assertEquals(10, entrySet.size());
    Assertions.assertFalse(entrySet.isEmpty());

    Assertions.assertTrue(entrySet.contains(new SimpleEntry<>(T20, "a")));
    Assertions.assertFalse(entrySet.contains(new SimpleEntry<>(T20, "b")));
    Assertions.assertFalse(entrySet.contains(new SimpleEntry<>(T52, "f")));
    Assertions.assertFalse(entrySet.contains(T20));

    Assertions.assertEquals(0, FMap.of().entrySet().hashCode());
    Assertions.assertEquals(5 ^ 6, FMap.of(5, 6).entrySet().hashCode());
    Assertions.assertEquals((5 ^ 6) + (2 ^ 3), FMap.of(5, 6, 2, 3).entrySet().hashCode());

    Assertions.assertTrue(FMap.of().entrySet().equals(FSet.of()));
    Assertions.assertTrue(FMap.of(5, 10).entrySet().equals(FSet.of(new SimpleEntry<>(5, 10))));
    Assertions.assertTrue(FMap.of(5, 10, 6, 30).entrySet().equals(FSet.of(new SimpleEntry<>(5, 10), new SimpleEntry<>(6, 30))));
    Assertions.assertFalse(FMap.of().entrySet().equals(FSet.of(new SimpleEntry(5, 10))));
    Assertions.assertFalse(FMap.of(5, 10).entrySet().equals(FSet.of(new SimpleEntry<>(5, 11))));
    Assertions.assertFalse(FMap.of(5, 10).entrySet().equals(null));
    Assertions.assertFalse(FMap.of(5, 10).entrySet().equals(FSet.of(5)));

    Assertions.assertEquals(Set.of(new SimpleEntry<>(5, 10), new SimpleEntry<>(6, 30)), FMap.of(5, 10, 6, 30).entrySet().toJava());

    Assertions.assertEquals(FSet.of(
        new SimpleEntry<>(5, 10),
        new SimpleEntry<>(6, 30)
    ), FMap.of(5, 10).entrySet().with(new SimpleEntry<>(6, 30)));
    Assertions.assertEquals(FSet.of(
        new SimpleEntry<>(5, 10),
        new SimpleEntry<>(6, 30),
        new SimpleEntry<>(7, 40)
    ), FMap.of(5, 10).entrySet().withAll(FList.of(new SimpleEntry<>(6, 30), new SimpleEntry<>(7, 40))));
    Assertions.assertEquals(FSet.of(
        new SimpleEntry<>(5, 10)
    ), FMap.of(5, 10, 6, 30).entrySet().without(new SimpleEntry<>(6, 30)));
    Assertions.assertEquals(FSet.of(), FMap.of(5, 10, 6, 30).entrySet().withoutAll(FList.of(
        new SimpleEntry<>(5, 10),
        new SimpleEntry<>(6, 30)
    )));

    Set<Map.Entry<TestObject, String>> itSet = StreamSupport.stream(Spliterators.spliteratorUnknownSize(
        getTestMap().without(null).entrySet().iterator(),
        Spliterator.ORDERED
    ), false).collect(Collectors.toSet());

    Set<Map.Entry<TestObject, String>> actualItSet = Set.of(
        new SimpleEntry<>(T20, "a"),
        new SimpleEntry<>(T30, "b"),
        new SimpleEntry<>(T10, "c"),
        new SimpleEntry<>(T15, "d"),
        new SimpleEntry<>(T25, "e"),
        new SimpleEntry<>(T17, "g"),
        new SimpleEntry<>(A20, null),
        new SimpleEntry<>(A17, "h"),
        new SimpleEntry<>(B17, "i")
    );

    Assertions.assertEquals(actualItSet, itSet);

    itSet = new HashSet<>();
    getTestMap().without(null).entrySet().forEach(itSet::add);
    Assertions.assertEquals(actualItSet, itSet);

    Assertions.assertThrowsExactly(NoSuchElementException.class, () -> FMap.of().entrySet().iterator().next());

    Assertions.assertEquals(
        "{}",
        FMap.of().entrySet().toString()
    );
    Assertions.assertEquals(
        "{a=5}",
        FMap.of("a", 5).entrySet().toString()
    );
    Assertions.assertTrue(
        FSet.of("{a=5, b=6}", "{b=6, a=5}").contains(
            FMap.of("a", 5, "b", 6).entrySet().toString()
        )
    );
  }

  @Test
  public void testValues() {
    FMap<TestObject, String> map = getTestMap();
    Assertions.assertEquals(10, map.size());
    FCollection<String> valueCollection = map.values();

    Assertions.assertEquals(10, valueCollection.size());
    Assertions.assertFalse(valueCollection.isEmpty());

    Assertions.assertTrue(valueCollection.contains("a"));
    Assertions.assertFalse(valueCollection.contains("x"));

    Assertions.assertEquals(List.of(10, 30), new ArrayList<>(FMap.of(5, 10, 6, 30).values().toJava()));

    Set<String> itSet = StreamSupport.stream(Spliterators.spliteratorUnknownSize(
        getTestMap().without(A20).values().iterator(),
        Spliterator.ORDERED
    ), false).collect(Collectors.toSet());

    Assertions.assertEquals(Set.of("a", "b", "c", "d", "e", "f", "g", "h", "i"), itSet);

    itSet = new HashSet<>();
    getTestMap().without(A20).values().forEach(itSet::add);
    Assertions.assertEquals(Set.of("a", "b", "c", "d", "e", "f", "g", "h", "i"), itSet);

    Assertions.assertEquals(
        "()",
        FMap.of().values().toString()
    );
    Assertions.assertEquals(
        "(5)",
        FMap.of("a", 5).values().toString()
    );
    Assertions.assertTrue(
        FSet.of("(5, 6)", "(6, 5)").contains(
            FMap.of("a", 5, "b", 6).values().toString()
        )
    );
  }

  @Test
  public void testToJava() {
    Assertions.assertEquals(Map.of(), FMap.of().toJava());
    Assertions.assertEquals(Map.of(1, "a"), FMap.of(1, "a").toJava());
    Assertions.assertEquals(Map.of(1, "a", 2, "b"), FMap.of(1, "a", 2, "b").toJava());
    Assertions.assertEquals(Map.of(1, "a", 2, "b", 3, "c"), FMap.of(1, "a", 2, "b", 3, "c").toJava());
  }

  @Test
  public void testEquals() {
    Assertions.assertEquals(
        FMap.of(),
        FMap.of()
    );
    Assertions.assertEquals(
        FMap.of("a", null),
        FMap.of("a", null)
    );
    Assertions.assertNotEquals(
        FMap.of("a", null),
        FMap.of("a", 5)
    );
    Assertions.assertFalse(
        FMap.of().equals(
            Map.of() // A java map!
        )
    );
  }

  @Test
  public void testHashCode() {
    Assertions.assertEquals(
        FMap.of().entrySet().hashCode(),
        FMap.of().hashCode()
    );
    Assertions.assertEquals(
        FMap.of("a", 5).entrySet().hashCode(),
        FMap.of("a", 5).hashCode()
    );
  }

  @Test
  public void testToString() {
    Assertions.assertEquals(
        "{}",
        FMap.of().toString()
    );
    Assertions.assertEquals(
        "{a=5}",
        FMap.of("a", 5).toString()
    );
    Assertions.assertTrue(
        FSet.of("{a=5, b=6}", "{b=6, a=5}").contains(
            FMap.of("a", 5, "b", 6).toString()
        )
    );
  }

  @Test
  public void testOf() {
    Assertions.assertEquals(FMap.of(), new FHashMap<>());
    Assertions.assertEquals(FMap.of("a", 1), new FHashMap<>().with("a", 1));
    Assertions.assertEquals(FMap.of("a", 1, "b", 2), new FHashMap<>().with("a", 1).with("b", 2));
    Assertions.assertEquals(FMap.of("a", 1, "b", 2, "c", 3), new FHashMap<>().with("c", 3).with("a", 1).with("b", 2));

    Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> FMap.of("a", 1, "a", 2));
    Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> FMap.of("a", 1, "a", 2, "b", 3));
    Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> FMap.of("a", 1, "b", 2, "a", 3));
    Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> FMap.of("b", 1, "a", 2, "a", 3));
  }
}
