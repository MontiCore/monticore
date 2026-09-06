/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams.internal;

import de.monticore.rte.functions.Function1;
import de.monticore.rte.streams.UntimedStream;
import de.monticore.rte.tuples.Tuple2;

import java.util.Optional;

public class MapStream<U, T> extends UntimedStream<U> {
  protected final UntimedStream<T> stream;
  protected final Function1<U, T> mapper;

  public MapStream(UntimedStream<T> stream, Function1<U, T> mapper) {
    this.stream = stream;
    this.mapper = mapper;
  }

  @Override
  public Tuple2<Optional<U>, UntimedStream<U>> _internal_next() {
    Tuple2<Optional<T>, UntimedStream<T>> headTail = stream._internal_next();
    Optional<T> optHead = headTail.get0();
    UntimedStream<T> tail = headTail.get1();

    if (optHead.isEmpty()) {
      return Tuple2.of(Optional.empty(), UntimedStream.empty());
    }

    U res = mapper.apply(optHead.get());
    UntimedStream<U> mappedTail = new MapStream<>(tail, mapper);
    return Tuple2.of(Optional.of(res), mappedTail);
  }

  @Override
  public long len() {
    return stream.len();
  }
}
