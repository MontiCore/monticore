/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.collections;

import de.monticore.rte.functions.Function1;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A {@link List} but as an {@link FCollection}.
 *
 * @param <T> The type of elements in this list
 */
public interface FList<T> extends FCollection<T> {

  /**
   * This has the same semantics as {@link List#get(int)}
   */
  T get(int index);

  /**
   * Returns a new list with the provided element prepended. This is equivalent to {@code this.withInserted(0, ele)}.
   *
   * @param ele The element to prepend
   * @return The new list with the extra element
   */
  FList<T> withPrepended(T ele);

  /**
   * Returns a new list with the provided elements prepended.
   * <p>
   * The elements are added in the iteration order of the given collection. E.g. the first element returned by the collection's iterator is at index 0 of the
   * returned list, the last element at index {@code elements.size() - 1}, and the first element of this list is at {@code elements.size()}.
   * <p>
   * This is equivalent to {@code this.withInserted(0, elements)}.
   *
   * @param elements The elements to prepend
   * @return The new list with the extra elements
   */
  FList<T> withPrepended(FCollection<? extends T> elements);

  /**
   * Returns a new list with the provided element inserted at the given index. All elements at, and after that index have their index increased by one.
   *
   * @param index The index at which to insert. It must hold that {@code 0 <= index <= this.size()}
   * @param ele   The element to insert
   * @return The new list with the extra element
   * @throws IndexOutOfBoundsException If the index is out of bounds
   */
  FList<T> withInserted(int index, T ele);

  /**
   * Returns a new list with the provided elements inserted at the given inex. All elements at, and after that index have their index increased by
   * {@code elements.size()}.
   * <p>
   * The elements are added in the iteration order of the given collection. E.g. the first element returned by the collection's iterator is at index
   * {@code index} of the returned list, the last element at index {@code index + elements.size() - 1}, and the element at index {@code index} of this list is
   * at {@code index + elements.size()}.
   *
   * @param index    The index at which to insert. It must hold that {@code 0 <= index <= this.size()}
   * @param elements The elements to insert
   * @return The new list with the extra elements
   * @throws IndexOutOfBoundsException If the index is out of bounds
   */
  FList<T> withInserted(int index, FCollection<? extends T> elements);

  /**
   * @return A new list with the first element removed.
   * @throws NoSuchElementException If this collection {@link #isEmpty()}
   */
  default FList<T> withoutFirst() {
    if (size() == 0) {
      throw new NoSuchElementException();
    }
    return withRemoved(0, 1);
  }

  /**
   * Returns a new list with the first {@code n} elements removed.
   * <p>
   * This is equivalent to {@code withRemoved(0, n)}.
   *
   * @param n The number of elements to remove. It must hold that {@code 0 <= n <= this.size()}
   * @return The new list without the removed elements
   * @throws IllegalArgumentException If the size is negative or too large
   */
  default FList<T> withoutFirst(int n) {
    return withRemoved(0, n);
  }

  /**
   * Returns a new list with the element at index {@code index} and the following {@code n - 1} elements removed.
   *
   * @param index The index at which to start removing elements. It must hold that {@code 0 <= index}
   * @param n     The number of elements to remove. It must hold that {@code 0 <= n} and {@code n + index <= this.size()}
   * @return The new list without the removed elements
   * @throws IndexOutOfBoundsException If the index is negative
   * @throws IllegalArgumentException  If {@code n} is negative or {@code n + index} is too large
   */
  FList<T> withRemoved(int index, int n);

  //

  /**
   * Returns a list with the order of elements reversed. More formally, for every {@code 0 <= i < this.size()}, the element at index {@code i} in this list,
   * is at index {@code this.size() - 1 - i} in the returned list.
   *
   * @return The reversed list
   */
  FList<T> reversed();

  /**
   * Returns a list which only includes elements which pass the given predicate. The order of elements is preserved.
   *
   * @param predicate The predicate to apply to all elements.
   * @return The filtered list
   */
  FList<T> filtered(Function1<Boolean, ? super T> predicate);

  /**
   * Constructs a new list by applying the given mapper to all elements of this list and appending the results to the new list.
   * <p>
   * This is equivalent to:
   *
   * <pre>
   * FList&lt;R&gt; res = FList.of();
   * for(T ele : this) {
   *     res = res.withInserted(res.size(), mapper.apply(ele));
   * }
   * return res;
   * </pre>
   *
   * @param mapper The mapper to apply to all elements
   * @param <R>    The type of the mapping result
   * @return The new list with the mapped elements.
   */
  <R> FList<R> mapped(Function1<? extends R, ? super T> mapper);

  /**
   * Constructs a new list by applying the given mapper to all elements and appending the returned collection's elements to the new list.
   * <p>
   * This is equivalent to:
   *
   * <pre>
   * FList&lt;R&gt; res = FList.of();
   * for(T ele : this) {
   *     res = res.withInserted(res.size(), mapper.apply(ele));
   * }
   * return res;
   * </pre>
   *
   * @param mapper The mapper to apply to all elements
   * @param <R>    The type of the elements in the mapping result
   * @return The new list with the mapped elements
   */
  <R> FList<R> flatMapped(Function1<? extends FCollection<? extends R>, ? super T> mapper);

  /**
   * Returns a list with all duplicate elements removed. For any duplicates only the first element is kept. More formally, for every index
   * {@code 0 <= i < this.size()}, the element in this list at index {@code i} is removed in the returned list if, and only if, for any element at index
   * {@code 0 <= j < i} it holds that {@code Objects.equals(this.get(i), this.get(j)) == true}
   * <p>
   * This is equivalent to {@code filtered(new HashSet()::add)}
   *
   * @return The list without duplicate elements
   */
  FList<T> distinct();

  /**
   * Returns a list with the elements of this list sorted by their {@link Comparable natural order}.
   * <p>
   * This is equivalent to {@code sorted(Comparator.naturalOrder())}.
   *
   * @return The sorted list
   * @throws ClassCastException If the elements do not implement {@link Comparable}
   */
  FList<T> sorted() throws ClassCastException;

  /**
   * Returns a list with the elements of this list sorted according to the given comparator.
   *
   * @param comparator The comparator to use
   * @return The sorted list
   */
  FList<T> sorted(Comparator<? super T> comparator);

  //

  /**
   * This has the same semantics as {@link List#hashCode()}
   */
  @Override
  int hashCode();

  /**
   * This has the same semantics as {@link List#equals(Object)} (just inside the FList system. An {@link FList} is never equal to a {@link List})!
   */
  @Override
  boolean equals(Object other);

  /**
   * @return An unmodifiable {@link List} containing the same elements as this list, in the same order.
   */
  @Override
  List<T> toJava();

  /**
   * @return An iterator which iterates the elements of this list in order.
   */
  @Override
  Iterator<T> iterator();

  /**
   * Returns an empty list
   *
   * @param <T> The type of elements in the list
   * @return A {@link FList} containing no elements.
   */
  static <T> FList<T> of() {
    @SuppressWarnings("unchecked")
    FList<T> l = (FList<T>) FLinkedList.EMPTY;
    return l;
  }

  /**
   * Create a new {@link FList} containing the given elements
   *
   * @param ele The elements which the list should contain. The order is kept.
   * @param <T> The type of the elements
   * @return The new list
   */
  @SafeVarargs
  static <T> FList<T> of(T... ele) {
    if (ele.length == 0) {
      return of();
    }
    return new FLinkedList<>(ele);
  }

  static <T> FSet<T> of(List<T> ele) {
    @SuppressWarnings("unchecked")
    T[] arr = (T[]) ele.toArray();
    return FSet.of(arr);
  }
}
