/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams.internal;

import de.monticore.rte.collections.FList;
import de.monticore.rte.streams.UntimedStream;
import de.monticore.rte.tuples.Tuple2;

import java.util.Optional;

public class FiniteUntimedStream<T> extends UntimedStream<T> {
  protected final FList<T> list;

  static protected final FiniteUntimedStream<?> EMPTY =
      FiniteUntimedStream.of(FList.of());

  public static <T> FiniteUntimedStream<T> empty() {
    @SuppressWarnings("unchecked")
    FiniteUntimedStream<T> s = (FiniteUntimedStream<T>) EMPTY;
    return s;
  }

  protected FiniteUntimedStream(FList<T> list) {
    this.list = list;
  }

  public static <T> FiniteUntimedStream<T> of(FList<T> flist) {
    return new FiniteUntimedStream<>(flist);
  }

  @SafeVarargs
  public static <T> FiniteUntimedStream<T> of(T... ele) {
    return new FiniteUntimedStream<>(FList.of(ele));
  }

  public long len() {
    return this.list.size();
  }

  @Override
  public UntimedStream<T> dropFirst() {
    return FiniteUntimedStream.of(list.withoutFirst());
  }

  @Override
  public Tuple2<Optional<T>, UntimedStream<T>> _internal_next() {
    if (list.isEmpty())
      return Tuple2.of(Optional.empty(), UntimedStream.empty());
    return Tuple2.of(Optional.of(list.get(0)), this.dropFirst());
  }

  @Override
  public T head() throws IndexOutOfBoundsException {
    if (list.isEmpty())
      throw new IndexOutOfBoundsException();
    return this.list.get(0);
  }

  @Override
  public UntimedStream<T> dropMultiple(long n) {
    // we could certainly handle long -> int casting better
    return FiniteUntimedStream.of(list.withoutFirst((int) n));
  }

}
