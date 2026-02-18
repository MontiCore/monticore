package de.monticore.rte.streams.internal;

import de.monticore.rte.streams.UntimedStream;
import de.monticore.rte.tuples.Tuple2;

import java.util.Optional;

public class ZipStream<A, B> extends UntimedStream<Tuple2<A, B>> {
  protected final UntimedStream<A> first;
  protected final UntimedStream<B> second;

  public ZipStream(UntimedStream<A> first, UntimedStream<B> second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public Tuple2<Optional<Tuple2<A, B>>, UntimedStream<Tuple2<A, B>>> _internal_next() {
    Tuple2<Optional<A>, UntimedStream<A>> first = this.first._internal_next();
    Tuple2<Optional<B>, UntimedStream<B>> second = this.second._internal_next();

    if (first.get0().isEmpty() || second.get0().isEmpty())
      return Tuple2.of(Optional.empty(), UntimedStream.empty());

    Tuple2<A, B> head = Tuple2.of(first.get0().get(), second.get0().get());
    UntimedStream<Tuple2<A, B>> tail = new ZipStream<>(first.get1(), second.get1());
    return Tuple2.of(Optional.of(head), tail);
  }
}
