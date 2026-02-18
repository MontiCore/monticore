package de.monticore.rte.streams;

import de.monticore.rte.tuples.Tuple2;
import de.monticore.rte.collections.FList;
import de.monticore.rte.collections.FSet;
import de.monticore.rte.streams.internal.FiniteUntimedStream;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamTest {

  @Test
  void docExample() {
    assertEquals(
        199,
        UntimedStream
            .iterate(x -> x + 1, 0)
            .filter(x -> x % 2 != 0)
            .nth(100)
    );
  }

  @Test
  void testOfSingleton() {
    UntimedStream<Integer> stream = UntimedStream.of(42);
    assertEquals(42, stream.head());
    assertEquals(1, stream.len());
  }

  @Test
  void testOfList() {
    FList<Integer> flist = FList.of(1, 2, 3);
    UntimedStream<Integer> stream = UntimedStream.of(flist);

    assertEquals(1, stream.head());
    assertEquals(3, stream.len());
  }

  @Test
  void testDropFirst() {
    UntimedStream<Integer> stream = UntimedStream.of(FList.of(10, 20, 30));
    UntimedStream<Integer> dropped = stream.dropFirst();

    assertEquals(20, dropped.head());
    assertEquals(2, dropped.len());
  }

  @Test
  void testDropMultiple() {
    UntimedStream<Integer> stream = UntimedStream.of(FList.of(10, 20, 30, 40));
    UntimedStream<Integer> dropped = stream.dropMultiple(2);

    assertEquals(30, dropped.head());
    assertEquals(2, dropped.len());
  }

  @Test
  void testTake() {
    UntimedStream<Integer> stream = UntimedStream.of(FList.of(1, 2, 3, 4, 5));
    UntimedStream<Integer> taken = stream.take(3);

    assertEquals(3, taken.len());
    assertEquals(UntimedStream.of(FList.of(1, 2, 3)), taken);
    assertEquals(List.of(1, 2, 3), taken.asList());
  }

  @Test
  void testTimesFinite() {
    UntimedStream<Integer> stream = UntimedStream.of(FList.of(1, 2));
    UntimedStream<Integer> repeated = stream.times(3);

    assertEquals(6, repeated.len());
    assertEquals(List.of(1, 2, 1, 2, 1, 2), repeated.asList());

    assertEquals(List.of(1), repeated.take(1).asList());
    assertEquals(List.of(1, 2), repeated.take(2).asList());
    assertEquals(List.of(1, 2, 1, 2), repeated.dropMultiple(2).asList());
    assertEquals(4, repeated.dropMultiple(2).len());
  }

  @Test
  void testMap() {
    UntimedStream<Integer> stream = UntimedStream.of(FList.of(1, 2, 3));
    UntimedStream<String> mapped = stream.map(Object::toString);

    assertEquals(List.of("1", "2", "3"), mapped.asList());
  }

  @Test
  void testFilter() {
    UntimedStream<Integer> stream = UntimedStream.of(FList.of(1, 2, 3, 4, 5));
    UntimedStream<Integer> filtered = stream.filter(x -> x % 2 == 0);

    assertEquals(FSet.of(2, 4), filtered.values());
  }

  @Test
  void testScanl() {
    UntimedStream<Integer> stream = UntimedStream.of(FList.of(1, 2, 3));
    UntimedStream<Integer> scan = stream.scanl(Integer::sum, 0);

    assertEquals(List.of(0, 1, 3, 6), scan.asList());
  }

  @Test
  void testNth() {
    UntimedStream<String> stream = UntimedStream.of(FList.of("a", "b", "c"));
    assertEquals("b", stream.nth(2));
    assertThrows(IndexOutOfBoundsException.class, () -> stream.nth(4));
  }

  @Test
  void testZip() {
    UntimedStream<Integer> stream1 = UntimedStream.of(FList.of(1, 2, 3));
    UntimedStream<String> stream2 = UntimedStream.of(FList.of("a", "b", "c"));
    UntimedStream<Tuple2<Integer, String>> zipped = stream1.zip(stream2);

    List<Tuple2<Integer, String>> expected = List.of(
        Tuple2.of(1, "a"),
        Tuple2.of(2, "b"),
        Tuple2.of(3, "c")
    );

    assertEquals(expected, zipped.asList());
  }

  @Test
  void testEquals() {
    UntimedStream<Integer> stream1 = UntimedStream.of(FList.of(1, 2, 3));
    UntimedStream<Integer> stream2 = UntimedStream.of(FList.of(1, 2, 3));
    assertEquals(stream1, stream2);

    // even infinite streams if false
    stream1 = Stream.iterate(x -> x + 1, 1);
    stream2 = Stream.iterate(x -> x + 1, 2);
    assertNotEquals(stream1, stream2);
  }

  @Test
  void testProj() {
    FList<Integer> values1 = FList.of(1, 2, 3);
    FList<String> values2 = FList.of("a", "b", "c");

    UntimedStream<Integer> stream1 = UntimedStream.of(values1);
    UntimedStream<String> stream2 = UntimedStream.of(values2);
    UntimedStream<Tuple2<Integer, String>> zipped = stream1.zip(stream2);

    stream1 = (UntimedStream<Integer>) Stream.projFst(zipped);
    stream2 = (UntimedStream<String>) Stream.projSnd(zipped);

    assertEquals(values1.size(), stream1.len());
    assertEquals(values2.size(), stream2.len());

    assertEquals(values1.toJava(), stream1.asList());
    assertEquals(values2.toJava(), stream2.asList());
  }

  @Test
  void testEmptyStream() {
    FiniteUntimedStream<Integer> empty = FiniteUntimedStream.empty();
    assertThrows(IndexOutOfBoundsException.class, () -> empty.head());
    assertEquals(0, empty.len());
    assertTrue(empty._internal_next().get0().isEmpty());
  }

  @Test
  void testInfiniteRepeat() {
    UntimedStream<Integer> stream = Stream.repeat(1, Stream.INFINITY);
    assertEquals(List.of(1, 1, 1, 1, 1), stream.take(5).asList());
    assertTrue(stream.hasInfiniteLen());
    stream = stream.dropMultiple(100);
    assertFalse(stream.isEmpty());
  }

  @Test
  void testIter() {
    UntimedStream<Integer> stream = Stream.iterate(n -> n + 1, 1);
    assertEquals(List.of(1, 2, 3, 4, 5), stream.take(5).asList());
    assertTrue(stream.hasInfiniteLen());
  }

}
