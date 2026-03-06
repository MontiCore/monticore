/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams;

/**
 * Conversion interface between different notions of time for {@link Stream}s.
 *
 * <p>
 * Not part of the {@link Stream} interface, as some stream types (e.g., {@link EventStream})
 * convert into different items-types than they carry.
 */
public interface TimeableStream<T> {

  /**
   * Loose all time information.
   */
  UntimedStream<T> untimed();

  /**
   * Convert into {@link SyncStream}. Each item is interpreted as a time-slice.
   */
  SyncStream<T> sync();

  /**
   * Convert into {@link ToptStream}.
   * Each item is interpreted as a time-slice.
   * Similar to {@link TimeableStream#sync()}, but additionally items are wrapped in {@link java.util.Optional}.
   */
  ToptStream<T> topt();

  /**
   * Convert into {@link EventStream}.
   */
  EventStream<T> event();
}
