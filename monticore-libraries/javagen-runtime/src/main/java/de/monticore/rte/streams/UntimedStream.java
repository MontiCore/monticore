package de.monticore.rte.streams;

import de.monticore.rte.actions.Action1;
import de.monticore.rte.collections.FList;
import de.monticore.rte.collections.FSet;
import de.monticore.rte.functions.Function1;
import de.monticore.rte.functions.Function2;
import de.monticore.rte.streams.internal.ConcatenatedStream;
import de.monticore.rte.streams.internal.DropWhileStream;
import de.monticore.rte.streams.internal.FilterStream;
import de.monticore.rte.streams.internal.FiniteUntimedStream;
import de.monticore.rte.streams.internal.IterStream;
import de.monticore.rte.streams.internal.MapStream;
import de.monticore.rte.streams.internal.RepeatedStream;
import de.monticore.rte.streams.internal.ScanLStream;
import de.monticore.rte.streams.internal.TakeWhileStream;
import de.monticore.rte.streams.internal.ZipStream;
import de.monticore.rte.tuples.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * (Untimed) Streams carry (possibly infinitely many) items in order.
 * To convert into other time notions use the {@link TimeableStream} interface.
 */
public abstract class UntimedStream<T> implements Stream<T>, TimeableStream<T> {

  public static <T> UntimedStream<T> empty() {
    return FiniteUntimedStream.empty();
  }

  @Override
  public T head() throws IndexOutOfBoundsException {
    Optional<T> head = _internal_next().get0();
    if (head.isEmpty())
      throw new IndexOutOfBoundsException();
    return head.get();
  }

  @Override
  public UntimedStream<T> dropFirst() {
    return dropMultiple(1);
  }

  @Override
  public UntimedStream<T> dropMultiple(long n) {
    UntimedStream<Long> indexes = Stream.iterate(x -> x + 1, 0L);
    return this
        .zip(indexes)
        .dropWhile(t -> t.get1() < n)
        .map(Tuple2::get0);
  }

  @Override
  public UntimedStream<T> dropWhile(Function1<Boolean, T> predicate) {
    return new DropWhileStream<>(this, predicate);
  }

  @Override
  public UntimedStream<T> take(long n) {
    UntimedStream<Long> indexes = Stream.iterate(x -> x + 1, 0L);
    return this
        .zip(indexes)
        .takeWhile(t -> t.get1() < n)
        .map(Tuple2::get0);
  }

  @Override
  public UntimedStream<T> takeWhile(Function1<Boolean, T> predicate) {
    return new TakeWhileStream<>(this, predicate);
  }

  @Override
  public T nth(long n) throws IndexOutOfBoundsException {
    return dropMultiple(n - 1).head();
  }

  @Override
  public UntimedStream<T> times(long n) {
    return RepeatedStream.of(this, n);
  }

  @Override
  public UntimedStream<T> infTimes() {
    return this.times(INFINITY);
  }

  @Override
  public <U> UntimedStream<U> map(Function1<U, T> f) {
    return new MapStream<>(this, f);
  }

  @Override
  public UntimedStream<T> filter(Function1<Boolean, T> predicate) {
    return new FilterStream<>(this, predicate);
  }

  @Override
  public UntimedStream<T> rmDups() {
    Set<T> seen = new java.util.HashSet<>();
    return this.filter(seen::add);
  }

  @Override
  public <U> UntimedStream<U> scanl(Function2<U, U, T> fn, U acc) {
    return new ScanLStream<>(this, fn, acc);
  }

  @Override
  public void forEach(Action1<T> action) {
    Tuple2<Optional<T>, UntimedStream<T>> headTail = _internal_next();
    while (headTail.get0().isPresent()) {
      action.apply(headTail.get0().get());
      headTail = headTail.get1()._internal_next();
    }
  }

  @Override
  public FSet<T> values() {
    Set<T> seen = new java.util.HashSet<>();
    this.forEach(seen::add);
    return FSet.of(seen);
  }

  public List<T> asList() {
    List<T> result = new ArrayList<>();
    this.forEach(result::add);
    return result;
  }

  @Override
  public long len() {
    // iterator based len, implementors are encouraged to overwrite

    // counter object are by-reference, longs are copied
    class Counter {
      long count = 0;
    }
    Counter counter = new Counter();
    this.forEach(elem -> counter.count++);
    return counter.count;
  }

  @Override
  public boolean isEmpty() {
    return this._internal_next().get0().isEmpty();
  }

  @Override
  public boolean hasInfiniteLen() {
    return this.len() == INFINITY;
  }

  @Override
  public UntimedStream<T> withPrepended(T element) {
    return UntimedStream.of(element).concat(this);
  }

  // Untimed Specific

  public <U> UntimedStream<Tuple2<T, U>> zip(UntimedStream<U> second) {
    return new ZipStream<>(this, second);
  }

  public UntimedStream<T> concat(UntimedStream<T> other) {
    return new ConcatenatedStream<>(this, other);
  }

  /**
   * New stream by function iteration (siterate)
   *
   * @param fn   function to apply
   * @param elem value to apply function on
   * @return stream of form <elem, f(elem), f(f(elem)), f^3(elem), ...)
   */
  static <S> UntimedStream<S> iterate(Function1<S, S> fn, S elem) {
    return new IterStream<>(fn, elem);
  }

  static <T> UntimedStream<T> of(FList<T> flist) {
    return FiniteUntimedStream.of(flist);
  }

  /**
   * New Stream
   */
  @SafeVarargs
  static <T> UntimedStream<T> of(T... ele) {
    return FiniteUntimedStream.of(FList.of(ele));
  }

  /**
   * New stream by repeating elem n times
   */
  static <S> UntimedStream<S> repeat(S elem, long n) {
    return FiniteUntimedStream.of(elem).times(n);
  }

  @Override
  public UntimedStream<T> untimed() {
    return this;
  }

  @Override
  public SyncStream<T> sync() {
    return new SyncStream<>(this);
  }

  @Override
  public ToptStream<T> topt() {
    return this.sync().topt();
  }

  @Override
  public EventStream<T> event() {
    return this.sync().event();
  }

  @Override
  public int hashCode() {
    class Wrapper {
      int hash = 0;
    }
    Wrapper w = new Wrapper();
    this.forEach(x -> w.hash ^= x.hashCode());
    return w.hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof UntimedStream))
      return false;

    UntimedStream<?> other = (UntimedStream<?>) obj;

    if (this.isEmpty() != other.isEmpty())
      return false;

    // wrapper, because booleans are passed by copy
    class Wrapper {
      boolean equals = true;
    }
    Wrapper w = new Wrapper();

    // this is just a for each, but with early abort on the first false
    this.zip(other)
        .map(x -> x.get0().equals(x.get1()))
        .map(b -> w.equals = w.equals && b)
        .takeWhile(x -> x)
        .forEach(x -> {/* consume */});
    return w.equals;
  }

  // internal

  /**
   * Streams are a lot like iterators,
   * in the same manner, next() splits the next element ("head")
   * from the rest of the stream ("tail").
   * If head is empty, then tail should be assumed to be empty.
   * The public interface exposes this function using head() and dropFirst().
   * Internal streams just need to implement this function, and the rest comes "for free".
   */
  public abstract Tuple2<Optional<T>, UntimedStream<T>> _internal_next();
}
