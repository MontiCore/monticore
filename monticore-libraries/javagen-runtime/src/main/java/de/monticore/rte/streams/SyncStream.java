/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams;

import de.monticore.rte.actions.Action1;
import de.monticore.rte.collections.FList;
import de.monticore.rte.collections.FSet;
import de.monticore.rte.functions.Function1;
import de.monticore.rte.functions.Function2;
import de.monticore.rte.streams.internal.ConcatenatedStream;
import de.monticore.rte.tuples.Tuple2;

import java.util.Optional;

/**
 * A sync stream is a timed stream, each time slice has exactly one message.
 * If you can't guarantee that, use ToptStream.
 */
public class SyncStream<T> implements Stream<T>, TimeableStream<T> {

  protected final UntimedStream<T> backing;

  protected SyncStream(UntimedStream<T> backing) {
    this.backing = backing;
  }

  @SafeVarargs
  public static <S> SyncStream<S> of(S... elem) {
    return SyncStream.of(FList.of(elem));
  }

  public static <T> SyncStream<T> of(FList<T> flist) {
    return new SyncStream<>(UntimedStream.of(flist));
  }

  /**
   * Construct a stream, which repeats elem n times (n may be Stream.INFINITY).
   */
  public static <S> SyncStream<S> repeat(S elem, long n) {
    return new SyncStream<>(Stream.repeat(elem, n));
  }

  public static <S> SyncStream<S> syncIterate(Function1<S, S> fn, S elem) {
    return new SyncStream<>(UntimedStream.iterate(fn, elem));
  }

  public static <S, U> SyncStream<S> syncProjFst(SyncStream<Tuple2<S, U>> s) {
    // Untimed-ness guaranteed by .zip()
    return new SyncStream<>((UntimedStream<S>) Stream.projFst(s.backing));
  }

  public static <S, U> SyncStream<U> syncProjSnd(SyncStream<Tuple2<S, U>> s) {
    // Untimed-ness guaranteed by .zip()
    return new SyncStream<>((UntimedStream<U>) Stream.projSnd(s.backing));
  }

  //
  // Interface Implementations
  //

  @Override
  public T first() throws IndexOutOfBoundsException {
    return this.backing.first();
  }

  @Override
  public SyncStream<T> dropFirst() {
    return new SyncStream<>(backing.dropFirst());
  }

  @Override
  public SyncStream<T> take(long n) {
    return new SyncStream<>(backing.take(n));
  }

  @Override
  public SyncStream<T> dropMultiple(long n) {
    return new SyncStream<>(backing.dropMultiple(n));
  }

  @Override
  public SyncStream<T> times(long n) {
    return new SyncStream<>(backing.times(n));
  }

  @Override
  public SyncStream<T> infTimes() {
    return new SyncStream<>(backing.infTimes());
  }

  @Override
  public <U> SyncStream<U> map(Function1<U, T> f) {
    return new SyncStream<>(backing.map(f));
  }

  @Override
  public SyncStream<T> filter(Function1<Boolean, T> predicate) {
    return new SyncStream<>(backing.filter(predicate));
  }

  @Override
  public SyncStream<T> takeWhile(Function1<Boolean, T> predicate) {
    return new SyncStream<>(backing.takeWhile(predicate));
  }

  @Override
  public T nth(long n) throws IndexOutOfBoundsException {
    return backing.nth(n);
  }

  @Override
  public SyncStream<T> dropWhile(Function1<Boolean, T> predicate) {
    return new SyncStream<>(backing.dropWhile(predicate));
  }

  @Override
  public SyncStream<T> rmDups() {
    return new SyncStream<>(backing.rmDups());
  }

  @Override
  public <U> SyncStream<U> scanl(Function2<U, U, T> fn, U acc) {
    return new SyncStream<>(backing.scanl(fn, acc));
  }

  @Override
  public void forEach(Action1<T> action) {
    backing.forEach(action);
  }

  @Override
  public FSet<T> values() {
    return backing.values();
  }

  @Override
  public long len() {
    return this.backing.len();
  }

  @Override
  public boolean hasInfiniteLen() {
    return this.len() == Stream.INFINITY;
  }

  @Override
  public boolean isEmpty() {
    return this.backing.isEmpty();
  }

  @Override
  public SyncStream<T> withPrepended(T element) {
    return SyncStream.of(element).concat(this);
  }

  @Override
  public UntimedStream<T> untimed() {
    return backing;
  }

  @Override
  public ToptStream<T> topt() {
    return new ToptStream<>(backing.map(Optional::of));
  }

  @Override
  public EventStream<T> event() {
    return new EventStream<>(this.map(UntimedStream::of));
  }

  @Override
  public SyncStream<T> sync() {
    return this;
  }

  // Sync specific methods

  public <U> SyncStream<Tuple2<T, U>> zip(SyncStream<U> second) {
    return new SyncStream<>(backing.zip(second.backing));
  }

  public SyncStream<T> concat(SyncStream<T> other) {
    return new SyncStream<>(new ConcatenatedStream<>(this.backing, other.backing));
  }

  public SyncStream<T> delay(long n, T elem) {
    return new SyncStream<>(new ConcatenatedStream<>(UntimedStream.repeat(elem, n), this.backing));
  }

  @Override
  public int hashCode() {
    return this.backing.hashCode() ^ 2;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof SyncStream))
      return false;

    return this.backing.equals(((SyncStream<?>) obj).backing);
  }

  protected UntimedStream<T> getBacking() {
    return this.backing;
  }
}
