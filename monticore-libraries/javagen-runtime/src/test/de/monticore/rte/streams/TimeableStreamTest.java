/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams;

import de.monticore.rte.collections.FList;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeableStreamTest {

  //
  // Event
  //

  @Test
  void testEventToUntimedStream() {
    UntimedStream<Integer> stream1 = UntimedStream.of(FList.of(1, 2, 3));
    UntimedStream<Integer> stream2 = UntimedStream.of(FList.of(4, 5, 6));
    EventStream<Integer> stream = EventStream.of(FList.of(stream1, stream2));

    assertEquals(2, stream.len());
    assertEquals(List.of(1, 2, 3, 4, 5, 6), stream.untimed().asList());
  }

  @Test
  void testEventToSyncAndTopt() {
    UntimedStream<Integer> s1 = UntimedStream.of(FList.of(1));
    UntimedStream<Integer> s2 = UntimedStream.of(FList.of(2));
    EventStream<Integer> stream = EventStream.of(FList.of(s1, s2));

    assertEquals(List.of(1, 2), stream.sync().untimed().asList());
    assertEquals(List.of(Optional.of(1), Optional.of(2)), stream.topt().untimed().asList());
  }

  //
  // Sync
  //

  @Test
  void testSyncToEventStream() {
    assertEquals(
        SyncStream.of(FList.of(1, 2, 3)).event(),
        EventStream.of(FList.of(UntimedStream.of(1), UntimedStream.of(2), UntimedStream.of(3)))
    );
  }

  @Test
  void testSyncToUntimedStream() {
    assertEquals(
        SyncStream.of(FList.of(1, 2, 3)).untimed(),
        UntimedStream.of(FList.of(1, 2, 3))
    );
  }

  //
  // Untimed
  //

  @Test
  void testUntimedToEventStream() {
    assertEquals(
        UntimedStream.of(FList.of(1, 2, 3)).event(),
        EventStream.of(FList.of(UntimedStream.of(1), UntimedStream.of(2), UntimedStream.of(3)))
    );
  }

  @Test
  void testUntimedToSyncStream() {
    assertEquals(
        UntimedStream.of(FList.of(1, 2, 3)).sync(),
        SyncStream.of(FList.of(1, 2, 3))
    );
  }

}
