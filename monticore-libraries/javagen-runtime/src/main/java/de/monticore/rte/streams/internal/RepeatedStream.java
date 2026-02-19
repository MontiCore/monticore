/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams.internal;

import de.monticore.rte.streams.Stream;
import de.monticore.rte.streams.UntimedStream;
import de.monticore.rte.tuples.Tuple2;

import java.util.Optional;

public class RepeatedStream<T> extends UntimedStream<T> {
  protected final UntimedStream<T> fullStream; // we reset to this stream once the working stream is drained
  protected final UntimedStream<T> workingStream;
  protected final long n;

  protected RepeatedStream(UntimedStream<T> fullStream, UntimedStream<T> workingStream, long n) {
    this.fullStream = fullStream;
    this.workingStream = workingStream;
    this.n = n;
  }

  public static <T> UntimedStream<T> of(UntimedStream<T> stream, long n) {
    return new RepeatedStream<>(stream, UntimedStream.empty(), n);
  }

  @Override
  public long len() {
    if (n == Stream.INFINITY) {
      return Stream.INFINITY;
    }

    if (fullStream.hasInfiniteLen()) {
      return Stream.INFINITY;
    }

    return fullStream.len() * n + workingStream.len();
  }

  @Override
  public Tuple2<Optional<T>, UntimedStream<T>> _internal_next() {
    Tuple2<Optional<T>, UntimedStream<T>> headTail = workingStream._internal_next();
    Optional<T> optHead = headTail.get0();
    UntimedStream<T> tail = headTail.get1();

    if (optHead.isEmpty()) {
      long newN = n == Stream.INFINITY ? Stream.INFINITY : n - 1;

      if (newN >= 1) {
        return new RepeatedStream<>(this.fullStream, this.fullStream, newN)._internal_next();
      }

      // return fullstream one last time (unwrapped)
      return this.fullStream._internal_next();
    }

    // progress working stream
    tail = new RepeatedStream<>(this.fullStream, tail, n);
    return Tuple2.of(optHead, tail);
  }
}
