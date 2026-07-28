/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams.internal;

import de.monticore.rte.functions.Function1;
import de.monticore.rte.streams.UntimedStream;
import de.monticore.rte.tuples.Tuple2;

import java.util.Optional;

public class DropWhileStream<T> extends UntimedStream<T> {
  protected final UntimedStream<T> stream;
  protected final Function1<Boolean, T> predicate;

  public DropWhileStream(UntimedStream<T> stream, Function1<Boolean, T> predicate) {
    this.stream = stream;
    this.predicate = predicate;
  }

  @Override
  public Tuple2<Optional<T>, UntimedStream<T>> _internal_next() {
    Tuple2<Optional<T>, UntimedStream<T>> headTail = stream._internal_next();
    Optional<T> optHead = headTail.get0();
    UntimedStream<T> tail = headTail.get1();

    if (optHead.isEmpty() || !predicate.apply(optHead.get())) {
      // stream is drained or predicate became false
      return headTail;
    }

    // wrap tail in dropwhile
    tail = new DropWhileStream<>(tail, predicate);
    // continue there
    return tail._internal_next();
  }

}
