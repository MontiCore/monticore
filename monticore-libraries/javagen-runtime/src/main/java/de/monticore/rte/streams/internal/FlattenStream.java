/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams.internal;

import de.monticore.rte.streams.Stream;
import de.monticore.rte.streams.UntimedStream;
import de.monticore.rte.tuples.Tuple2;

import java.util.Optional;

public class FlattenStream<T> extends UntimedStream<T> {
  protected final Stream<UntimedStream<T>> backing;

  public FlattenStream(Stream<UntimedStream<T>> backing) {
    this.backing = backing;
  }

  @Override
  public Tuple2<Optional<T>, UntimedStream<T>> _internal_next() {
    if (this.backing.isEmpty()) {
      return Tuple2.of(Optional.empty(), UntimedStream.empty());
    }

    UntimedStream<T> head = this.backing.head();
    Stream<UntimedStream<T>> tail = this.backing.dropFirst();

    UntimedStream<T> flattenedTail = new FlattenStream<>(tail);
    UntimedStream<T> stream = new ConcatenatedStream<>(head, flattenedTail);
    return stream._internal_next();
  }
}
