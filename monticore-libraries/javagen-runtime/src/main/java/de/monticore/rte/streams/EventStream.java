/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams;

import de.monticore.rte.actions.Action1;
import de.monticore.rte.collections.FList;
import de.monticore.rte.collections.FSet;
import de.monticore.rte.functions.Function1;
import de.monticore.rte.functions.Function2;
import de.monticore.rte.streams.internal.ConcatenatedStream;
import de.monticore.rte.streams.internal.FlattenStream;
import de.monticore.rte.tuples.Tuple2;

/**
 * Timed Event Stream
 */
public class EventStream<T> implements Stream<UntimedStream<T>>, TimeableStream<T> {

  protected final SyncStream<UntimedStream<T>> backing;

  protected EventStream(SyncStream<UntimedStream<T>> backing) {
    this.backing = backing;
  }

  public static <T> EventStream<T> of(FList<UntimedStream<T>> flist) {
    return new EventStream<>(SyncStream.of(flist));
  }

  @SafeVarargs
  public static <T> EventStream<T> of(UntimedStream<T>... ele) {
    return new EventStream<>(SyncStream.of(FList.of(ele)));
  }

  //
  // Interface Implementations
  //

  @Override
  public UntimedStream<T> first() throws IndexOutOfBoundsException {
    return this.backing.first();
  }

  @Override
  public EventStream<T> dropFirst() {
    return new EventStream<>(backing.dropFirst());
  }

  @Override
  public EventStream<T> dropMultiple(long n) {
    return new EventStream<>(backing.dropMultiple(n));
  }

  @Override
  public EventStream<T> dropWhile(Function1<Boolean, UntimedStream<T>> predicate) {
    return new EventStream<>(backing.dropWhile(predicate));
  }

  @Override
  public EventStream<T> take(long n) {
    return new EventStream<>(backing.take(n));
  }

  @Override
  public EventStream<T> takeWhile(Function1<Boolean, UntimedStream<T>> predicate) {
    return new EventStream<>(backing.takeWhile(predicate));
  }

  @Override
  public UntimedStream<T> nth(long n) throws IndexOutOfBoundsException {
    return this.backing.nth(n);
  }

  @Override
  public EventStream<T> times(long n) {
    return new EventStream<>(backing.times(n));
  }

  @Override
  public EventStream<T> infTimes() {
    return new EventStream<>(backing.infTimes());
  }

  @Override
  public long len() {
    return this.backing.len();
  }

  @Override
  public boolean hasInfiniteLen() {
    return this.backing.hasInfiniteLen();
  }

  @Override
  public boolean isEmpty() {
    return this.backing.isEmpty();
  }

  /**
   * Also see EventStream.eMap.
   * We cant give stronger guarantees than SyncStream here, as U might not be an UntimedStream<>,
   * but must be generic to comply with the Stream interface.
   */
  @Override
  public <U> SyncStream<U> map(Function1<U, UntimedStream<T>> f) {
    return this.backing.map(f);
  }

  @Override
  public EventStream<T> filter(Function1<Boolean, UntimedStream<T>> predicate) {
    return new EventStream<>(backing.filter(predicate));
  }

  @Override
  public EventStream<T> rmDups() {
    return new EventStream<>(backing.rmDups());
  }

  /**
   * Also see EventStream.eScanl.
   * We cant give stronger guarantees than SyncStream here, as U might not be an UntimedStream<>,
   * but must be generic to comply with the Stream interface.
   */
  @Override
  public <U> SyncStream<U> scanl(Function2<U, U, UntimedStream<T>> fn, U acc) {
    return this.backing.scanl(fn, acc);
  }

  /**
   * Also see EventStream.eForEach
   */
  @Override
  public void forEach(Action1<UntimedStream<T>> action) {
    this.backing.forEach(action);
  }

  @Override
  public FSet<UntimedStream<T>> values() {
    return this.backing.values();
  }

  @Override
  public EventStream<T> withPrepended(UntimedStream<T> element) {
    return new EventStream<>(this.backing.withPrepended(element));
  }

  @Override
  public UntimedStream<T> untimed() {
    return new FlattenStream<>(this.backing);
  }

  @Override
  public SyncStream<T> sync() {
    return untimed().sync();
  }

  @Override
  public ToptStream<T> topt() {
    return untimed().topt();
  }

  @Override
  public EventStream<T> event() {
    return this;
  }

  // Event Specific

  public <U> EventStream<U> eMap(Function1<U, T> f) {
    return new EventStream<>(this.map(s -> s.map(f)));
  }

  // Note: zip
  // The return type would be an UntimedStream<Tuple2<UntimedStream<T>, UntimedStream<U>>>,
  // which can not be nicely represented as an EventStream
  // At that point, we are better of letting the user convert to UntimedStreams themselves.

  public EventStream<T> concat(EventStream<T> other) {
    return new EventStream<>(this.backing.concat(other.backing));
  }

  public <U> EventStream<U> eScanl(Function2<UntimedStream<U>, UntimedStream<U>, UntimedStream<T>> fn,
      UntimedStream<U> acc) {
    return new EventStream<>(this.scanl(fn, acc));
  }

  /**
   * Prepend an element into the first time-slice and return the resulting stream.
   */
  public EventStream<T> eWithPrepended(T element) {
    if (this.isEmpty())
      return EventStream.of(UntimedStream.of(element));

    UntimedStream<T> newFirst = this.first().withPrepended(element);
    return this.withPrepended(newFirst);
  }

  /**
   * {@link EventStream#forEach(Action1)} but on each element, rather than each slice.
   */
  public void eForEach(Action1<T> action) {
    this.backing.forEach(s -> s.forEach(action));
  }

  public EventStream<T> delay(long n) {
    return new EventStream<T>(this.backing.delay(n, UntimedStream.empty()));
  }

  public EventStream<T> rougherTime(long slices) {
    if (slices <= 0)
      throw new IllegalArgumentException("slices must be a positive long");

        /*
        The implementation looks more complex than it is.
        We simply create "buffers" (lists) of the slices until they are full (i.e. have slices-many items).
        Then we start the next buffer.
        We later filter out buffers which are not full.
        The edge-case of the end of the stream is handled by ziping-in a boolean,
        which can predict, if there will be a next slice.
        If not, even the un-full buffer will be "released".
         */

    // fill the previous list, or start a new one, if full
    Function2<FList<UntimedStream<T>>, FList<UntimedStream<T>>, UntimedStream<T>> grouper
        = (l, s) -> l.size() < slices ? l.withPrepended(s) : FList.of(s);

    // say slices = 3, then
    // grouped = <[], [<>], [<>, <>], [<>, <>, <>], [<>], [<>, <>], ...>
    SyncStream<FList<UntimedStream<T>>> grouped = this.backing.scanl(grouper, FList.of());

    // false for every time slice, append true at the end
    SyncStream<Boolean> isEndingNextSliceStream = new SyncStream<>(
        new ConcatenatedStream<>(
            grouped.map(s -> Boolean.FALSE).untimed(),
            UntimedStream.of(true)
        )
    );

    grouped = grouped.delay(1, FList.of()); // as the other stream is one slice "ahead"
    SyncStream<Tuple2<FList<UntimedStream<T>>, Boolean>> zipped = grouped.zip(isEndingNextSliceStream);

    // filter out lists, which are not full
    SyncStream<FList<UntimedStream<T>>> filtered = zipped
        .filter(t -> t.get0().size() == slices || t.get1())
        .map(Tuple2::get0);

    // map each to the concatenated stream
    SyncStream<UntimedStream<T>> mapped = filtered
        .map(l -> ConcatenatedStream.many(l.reversed()));

    return new EventStream<>(mapped);
  }

  @Override
  public int hashCode() {
    return this.backing.hashCode() ^ 1;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof EventStream))
      return false;

    return this.backing.equals(((EventStream<?>) obj).backing);
  }
}
