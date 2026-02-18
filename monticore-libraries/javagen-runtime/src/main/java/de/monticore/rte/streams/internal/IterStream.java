package de.monticore.rte.streams.internal;

import de.monticore.rte.functions.Function1;
import de.monticore.rte.streams.UntimedStream;
import de.monticore.rte.tuples.Tuple2;

import java.util.Optional;

public class IterStream<T> extends UntimedStream<T> {
  protected final Function1<T, T> fn;
  protected final T acc;

  public IterStream(Function1<T, T> fn, T acc) {
    this.fn = fn;
    this.acc = acc;
  }

  @Override
  public long len() {
    return UntimedStream.INFINITY;
  }

  @Override
  public Tuple2<Optional<T>, UntimedStream<T>> _internal_next() {
    // acc = fn(acc)
    return Tuple2.of(Optional.of(acc), new IterStream<>(fn, fn.apply(acc)));
  }
}
