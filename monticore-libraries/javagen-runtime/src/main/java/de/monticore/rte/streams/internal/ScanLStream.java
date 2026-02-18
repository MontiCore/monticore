package de.monticore.rte.streams.internal;

import de.monticore.rte.functions.Function2;
import de.monticore.rte.streams.UntimedStream;
import de.monticore.rte.tuples.Tuple2;

import java.util.Optional;

public class ScanLStream<U, T> extends UntimedStream<U> {
  protected final UntimedStream<T> stream;
  protected final Function2<U, U, T> fn;
  protected final U acc;

  public ScanLStream(UntimedStream<T> stream, Function2<U, U, T> fn, U acc) {
    this.stream = stream;
    this.fn = fn;
    this.acc = acc;
  }

  @Override
  public Tuple2<Optional<U>, UntimedStream<U>> _internal_next() {
    Tuple2<Optional<T>, UntimedStream<T>> headTail = stream._internal_next();
    Optional<T> optHead = headTail.get0();
    UntimedStream<T> tail = headTail.get1();

    if (optHead.isEmpty())
      return Tuple2.of(Optional.of(acc), UntimedStream.empty());

    U res = fn.apply(acc, optHead.get());
    UntimedStream<U> mappedTail = new ScanLStream<>(tail, fn, res);
    return Tuple2.of(Optional.of(acc), mappedTail);
  }

  @Override
  public long len() {
    return stream.len() + 1;
  }
}
