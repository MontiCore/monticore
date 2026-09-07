/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.streams;

import de.monticore.rte.actions.Action1;
import de.monticore.rte.collections.FSet;
import de.monticore.rte.functions.Function1;
import de.monticore.rte.functions.Function2;
import de.monticore.rte.streams.internal.FiniteUntimedStream;
import de.monticore.rte.tuples.Tuple2;

/**
 * Streams are (possibly infinite) lists of items.
 *
 * <p>
 * The following implementors are available:
 * <ul>
 *     <li>{@link UntimedStream} - Streams without a notion of time.</li>
 *     <li>{@link SyncStream} - Streams, for which item corresponds to a "time-slice".</li>
 *     <li>{@link ToptStream} - SyncStreams, with empty time-slices.</li>
 *     <li>{@link EventStream} - Time Slices themselves carry UntimedStreams.</li>
 * </ul>
 * <p>
 * These implement the {@link TimeableStream} interface, which allow conversion between these time notions.
 * <a href="https://www.ijsi.org/ijsi/article/abstract/i75">Read more about the theoretical foundations of streams here.</a>
 *
 * <p>
 * Operations are, unless documented differently, implemented lazily.
 * For example one may get the 100th uneven natural number:
 *
 * <pre>
 *     UntimedStream
 *      .iterate(x -> x + 1, 0)
 *      .filter(x -> x % 2 != 0)
 *      .nth(100) // 199
 * </pre>
 */
public interface Stream<T> {

  //
  // Constants, Constructors, Statics
  //

  long INFINITY = Long.MAX_VALUE;

  /**
   * @return the empty Stream
   */
  static <T> Stream<T> empty() {
    return FiniteUntimedStream.empty();
  }

  /**
   * See {@link  UntimedStream#repeat(Object, long)}
   */
  static <S> UntimedStream<S> repeat(S elem, long n) {
    return UntimedStream.repeat(elem, n);
  }

  /**
   * See {@link  UntimedStream#iterate(Function1, Object)}
   */
  static <S> UntimedStream<S> iterate(Function1<S, S> fn, S elem) {
    return UntimedStream.iterate(fn, elem);
  }

  /**
   * Given a tuple stream, get the stream of first elements.
   * Also see {@link Stream#projSnd(Stream)}.
   *
   * @param s stream of tuples
   * @return stream of first elements
   */
  static <S, U> Stream<S> projFst(Stream<Tuple2<S, U>> s) {
    return s.map(Tuple2::get0);
  }

  /**
   * Given a tuple stream, get the stream of second elements
   * Also see {@link Stream#projFst(Stream)}.
   *
   * @param s stream of tuples
   * @return stream of second elements
   */
  static <S, U> Stream<U> projSnd(Stream<Tuple2<S, U>> s) {
    return s.map(Tuple2::get1);
  }

  //
  // Abstract Interface
  //

  /**
   * The first element on the stream.
   *
   * @throws IndexOutOfBoundsException if no further elements accessible. Guard with {@link Stream#isEmpty()}!
   */
  T first() throws IndexOutOfBoundsException;

  /**
   * The stream without its first element ("first"). Also see {@link Stream#dropMultiple(long)}.
   */
  Stream<T> dropFirst();

  /**
   * The stream without its first n elements. Also see {@link Stream#dropFirst()}
   */
  Stream<T> dropMultiple(long n);

  /**
   * The stream obtained by dropping elements, until predicate becomes false
   */
  Stream<T> dropWhile(Function1<Boolean, T> predicate);

  /**
   * The stream obtained by taking up to n elements (i.e., the result has a length of at most n).
   */
  Stream<T> take(long n);

  /**
   * The stream obtained by taking elements until the predicate becomes false.
   * Also see {@link Stream#take(long)}.
   */
  Stream<T> takeWhile(Function1<Boolean, T> predicate);

  /**
   * The n-th element on the stream.
   *
   * @throws IndexOutOfBoundsException if stream length < n.
   */
  T nth(long n) throws IndexOutOfBoundsException;

  /**
   * Repeat the stream n times
   */
  Stream<T> times(long n);

  /**
   * <pre>stream.times(Stream.INFINITY)</pre>
   */
  Stream<T> infTimes();

  /**
   * Apply f to each element of the stream
   */
  <U> Stream<U> map(Function1<U, T> f);

  /**
   * Drop all values from the stream, for which predicate is false
   */
  Stream<T> filter(Function1<Boolean, T> predicate);

  /**
   * Filter values seen before out of the stream
   */
  Stream<T> rmDups();

  /**
   * Scan Left:
   * Returns a stream, starting with acc, then applies the element output last, with the next element of the underlying stream.
   * The resulting value is appended to the output stream and so on.
   *
   * <pre>
   *     Acc: 1
   *     F: (x,y) -> x+y
   *     In: <2, 2, 2>
   *     Out: <1, 3, 5, 7>
   * </pre>
   */
  <U> Stream<U> scanl(Function2<U, U, T> fn, U acc);

  /**
   * Construct a new stream with <pre>element</pre> as first element.
   */
  Stream<T> withPrepended(T element);

  /**
   * Execute action for every element of the stream. This will not terminate, if the stream is infinite.
   */
  void forEach(Action1<T> action);

  /**
   * Collect all values of the stream into one Set. This will not terminate, if the stream is infinite.
   */
  FSet<T> values();

  /**
   * Length of the stream, may not terminate! Also see {@link Stream#INFINITY} and {@link Stream#hasInfiniteLen()}.
   */
  long len();

  /**
   * If you expect true, use with caution!
   * There is a class of streams, for which we can tell they are infinite in length,
   * but there also is a class of streams, for which this is not the case.
   * This means this function will not terminate!
   */
  boolean hasInfiniteLen();

  /**
   * Whether the stream is drained (i.e., if {@link Stream#head()} will fail next)
   */
  boolean isEmpty();

  /**
   * Streams of different time notions are never the same.
   * Streams are the same, if they carry the same items,
   * as determined by {@link Object#equals(Object)}.
   * May not terminate, if infinite!
   */
  @Override
  boolean equals(Object obj);

  /**
   * Equal streams have the same hash (see {@link Stream#equals(Object)}).
   * May not terminate, if infinite!
   */
  @Override
  int hashCode();
}
