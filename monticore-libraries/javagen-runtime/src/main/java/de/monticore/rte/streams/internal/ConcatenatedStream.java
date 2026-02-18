package de.monticore.rte.streams.internal;

import de.monticore.rte.collections.FList;
import de.monticore.rte.streams.UntimedStream;
import de.monticore.rte.tuples.Tuple2;

import java.util.Iterator;
import java.util.Optional;

public class ConcatenatedStream<T> extends UntimedStream<T> {
  protected final UntimedStream<T> first;
  protected final UntimedStream<T> second;

  public ConcatenatedStream(UntimedStream<T> first, UntimedStream<T> second) {
    this.first = first;
    this.second = second;
  }

  public static <T> UntimedStream<T> many(FList<UntimedStream<T>> list) {
    if (list.isEmpty())
      return UntimedStream.empty();

    Iterator<UntimedStream<T>> it = list.iterator();
    UntimedStream<T> result = it.next();

    while (it.hasNext()) {
      result = new ConcatenatedStream<>(result, it.next());
    }

    return result;
  }

  @Override
  public long len() {
    if (first.hasInfiniteLen() || second.hasInfiniteLen())
      return UntimedStream.INFINITY;
    return first.len() + second.len();
  }

  @Override
  public Tuple2<Optional<T>, UntimedStream<T>> _internal_next() {
    Tuple2<Optional<T>, UntimedStream<T>> firstHeadTail = first._internal_next();
    Optional<T> optHead = firstHeadTail.get0();
    UntimedStream<T> tail = firstHeadTail.get1();

    // first stream exhausted
    if (optHead.isEmpty())
      return second._internal_next();

    tail = new ConcatenatedStream<>(tail, second);
    return Tuple2.of(optHead, tail);
  }
}
