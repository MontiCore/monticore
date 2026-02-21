/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams;

import de.monticore.rte.collections.FList;
import de.monticore.rte.collections.FSet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventStreamTest {

  @Test
  void testOfSingleton() {
    UntimedStream<Integer> stream1 = UntimedStream.of(42);
    EventStream<Integer> stream = EventStream.of(stream1);
    assertEquals(stream1, stream.head());
    assertEquals(1, stream.len());
    assertThrows(IndexOutOfBoundsException.class, () -> stream.dropFirst().head());
    assertTrue(stream.dropFirst().isEmpty());
  }

  @Test
  void testOfMultiple() {
    UntimedStream<Integer> stream1 = UntimedStream.of(FList.of(1, 2, 3));
    UntimedStream<Integer> stream2 = UntimedStream.of(FList.of(4, 5, 6));
    EventStream<Integer> stream = EventStream.of(FList.of(stream1, stream2));
    assertEquals(2, stream.len());
    assertEquals(stream1, stream.head());
    assertEquals(stream2, stream.dropFirst().head());
    assertEquals(stream2, stream.nth(2));
    assertTrue(stream.dropMultiple(2).isEmpty());
  }

  @Test
  void testValues() {
    UntimedStream<Integer> stream1 = UntimedStream.of(FList.of(1, 2, 3));
    UntimedStream<Integer> stream2 = UntimedStream.of(FList.of(4, 5, 6));
    UntimedStream<Integer> stream3 = UntimedStream.of(FList.of(4, 5, 6));
    assertEquals(stream2.hashCode(), stream3.hashCode());
    EventStream<Integer> stream = EventStream.of(FList.of(stream1, stream2, stream1, stream3));
    assertEquals(FSet.of(stream1, stream2), stream.values());
  }

  @Test
  void testRmDups() {
    UntimedStream<Integer> stream1 = UntimedStream.of(FList.of(1, 2, 3));
    UntimedStream<Integer> stream2 = UntimedStream.of(FList.of(4, 5, 6));
    UntimedStream<Integer> stream3 = UntimedStream.of(FList.of(4, 5, 6));
    assertEquals(stream2.hashCode(), stream3.hashCode());
    EventStream<Integer> streamA = EventStream.of(FList.of(stream1, stream2, stream1, stream3));
    assertEquals(2, streamA.rmDups().len());
  }

  @Test
  void testTakeWhile() {
    UntimedStream<Integer> stream1 = UntimedStream.of(FList.of(1, 2, 3));
    UntimedStream<Integer> stream2 = UntimedStream.of(FList.of(4, 5, 6));

    EventStream<Integer> stream = EventStream.of(FList.of(stream1, stream2));

    EventStream<Integer> taken = stream.takeWhile(s -> !s.values().contains(5));

    assertEquals(1, taken.len());
    assertEquals(stream1, taken.head());
  }

  @Test
  void testTake() {
    UntimedStream<Integer> stream1 = UntimedStream.of(FList.of(1, 2, 3));
    UntimedStream<Integer> stream2 = UntimedStream.of(FList.of(4, 5, 6));
    UntimedStream<Integer> stream3 = UntimedStream.of(FList.of(6));

    EventStream<Integer> stream = EventStream.of(FList.of(stream1, stream2, stream3));

    EventStream<Integer> taken = stream.take(2);

    assertEquals(2, taken.len());
    assertEquals(stream1, taken.head());
    assertEquals(stream2, taken.nth(2));
  }

  @Test
  void testDropWhile() {
    UntimedStream<Integer> stream1 = UntimedStream.of(FList.of(1, 2, 3));
    UntimedStream<Integer> stream2 = UntimedStream.of(FList.of(4, 5, 6));

    EventStream<Integer> stream = EventStream.of(FList.of(stream1, stream2));

    EventStream<Integer> dropped = stream.dropWhile(s -> !s.values().contains(5));

    assertEquals(1, dropped.len());
    assertEquals(stream2, dropped.head());
  }

  @Test
  void testEMap() {
    UntimedStream<Integer> stream1 = UntimedStream.of(FList.of(1, 2));
    UntimedStream<Integer> stream2 = UntimedStream.of(FList.of(3, 4));
    EventStream<Integer> stream = EventStream.of(FList.of(stream1, stream2));

    EventStream<String> mapped = stream.eMap(i -> "x" + i);

    assertEquals(2, mapped.len());
    assertEquals(List.of("x1", "x2"), mapped.nth(1).asList());
    assertEquals(List.of("x3", "x4"), mapped.nth(2).asList());
  }

  @Test
  void testEScanl() {
    UntimedStream<Integer> stream1 = UntimedStream.of(FList.of(1));
    UntimedStream<Integer> stream2 = UntimedStream.of(FList.of(2));
    EventStream<Integer> stream = EventStream.of(FList.of(stream1, stream2));

    EventStream<Integer> scanned = stream.eScanl(
        (a, b) -> UntimedStream.of(a.head() + b.head()),
        UntimedStream.of(0)
    );

    assertEquals(List.of(0), scanned.nth(1).asList());
    assertEquals(List.of(1), scanned.nth(2).asList());
    assertEquals(List.of(3), scanned.nth(3).asList());
    assertEquals(3, scanned.len());
  }

  @Test
  void testEForEach() {
    UntimedStream<Integer> stream1 = UntimedStream.of(FList.of(1, 2));
    UntimedStream<Integer> stream2 = UntimedStream.of(FList.of(3));
    EventStream<Integer> stream = EventStream.of(FList.of(stream1, stream2));

    List<Integer> collected = new ArrayList<>();
    stream.eForEach(collected::add);

    assertEquals(List.of(1, 2, 3), collected);
  }

  @Test
  void testDelay() {
    UntimedStream<Integer> stream1 = UntimedStream.of(FList.of(1));
    UntimedStream<Integer> stream2 = UntimedStream.of(FList.of(2));
    EventStream<Integer> stream = EventStream.of(FList.of(stream1, stream2));

    EventStream<Integer> delayed = stream.delay(1);

    // The first stream is EMPTY (delayed), then original streams follow
    assertEquals(3, delayed.len());
    assertEquals(stream1, delayed.nth(2));
  }

  @Test
  void testRougherTime() {
    UntimedStream<Integer> stream1 = UntimedStream.of(FList.of(1));
    UntimedStream<Integer> stream2 = UntimedStream.of(FList.of(2));
    UntimedStream<Integer> stream3 = UntimedStream.of(FList.of(3));
    UntimedStream<Integer> stream4 = UntimedStream.of(FList.of(4));
    EventStream<Integer> stream = EventStream.of(FList.of(stream1, stream2, stream3, stream4));

    EventStream<Integer> rough = stream.rougherTime(2);

    assertEquals(2, rough.len());
    assertEquals(List.of(1, 2), rough.nth(1).asList());
    assertEquals(List.of(3, 4), rough.nth(2).asList());
  }

  @Test
  void testEqualsAndHashCode() {
    UntimedStream<Integer> s1 = UntimedStream.of(FList.of(1));
    UntimedStream<Integer> s2 = UntimedStream.of(FList.of(2));
    EventStream<Integer> streamA = EventStream.of(FList.of(s1, s2));
    EventStream<Integer> streamB = EventStream.of(FList.of(s1, s2));

    assertEquals(streamA, streamB);
    assertEquals(streamA.hashCode(), streamB.hashCode());
  }

  @Test
  void testEventReturnsSelf() {
    EventStream<Integer> stream = EventStream.of(UntimedStream.of(1));
    assertSame(stream, stream.event());
  }

}
