/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link Set} as a {@link FCollection}
 *
 * @param <T> The type of elements in this set
 */
public interface FSet<T> extends FCollection<T> {

  /**
   * Returns a set with the given element added. If this set already contains the given element, the returned set is equal to this set.
   *
   * @param element The element to add
   * @return The set which contains the given element and all elements of this set
   */
  FSet<T> with(T element);

  /**
   * Returns a set with the given elements added. If this set already contains all the given element, the returned set is equal to this set.
   *
   * @param other The elements to add
   * @return The set which contains the given elements and all elements of this set
   */
  FSet<T> withAll(FCollection<? extends T> other);

  /**
   * Returns a set with the given element removed. If this set does not contain the given element, the returned set is equal to this set.
   *
   * @param element The element to remove
   * @return The set which contains all elements of this set except the given element
   */
  FSet<T> without(Object element);

  /**
   * Returns a set with the given elements removed. If this set does not contain any of the given elements, the returned set is equal to this set.
   *
   * @param other The elements to remove
   * @return The set which contains all elements of this set except any of the given elements
   */
  FSet<T> withoutAll(FCollection<?> other);

  /**
   * This has the same semantics as {@link Set#hashCode()}
   */
  @Override
  int hashCode();

  /**
   * This has the same semantics as {@link Set#equals(Object)} (just inside the FSet system. An {@link FSet} is never equal to a {@link Set})!
   */
  @Override
  boolean equals(Object other);

  /**
   * @return An unmodifiable {@link Set} containing the same elements as this set
   */
  @Override
  Set<T> toJava();

  /**
   * Returns an empty set
   *
   * @param <T> The type of elements in the set
   * @return A {@link FSet} containing no elements.
   */
  static <T> FSet<T> of() {
    @SuppressWarnings("unchecked")
    FSet<T> s = (FSet<T>) FHashSet.EMPTY;
    return s;
  }

  /**
   * Create a new {@link FSet} containing the given elements
   *
   * @param ele The elements which the set should contain. May not have duplicates. (If you want to ignore duplicates, consider using
   *            {@link FHashSet#FHashSet(Object...)})
   * @param <T> The type of the elements
   * @return The new set with all the given elements.
   * @throws IllegalArgumentException If the provided elements contain any duplicates
   */
  @SafeVarargs
  static <T> FSet<T> of(T... ele) {
    if (ele.length == 0) {
      return of();
    }
    FSet<T> res = new FHashSet<>(ele);
    if (res.size() == ele.length) {
      return res;
    }
    Set<T> duplicates = FList.of(ele)
        .<Map<T, Integer>> collect(
            new HashMap<>(),
            (m, e) -> m.merge(e, 1, Integer::sum)
        )
        .entrySet()
        .stream()
        .filter(e -> e.getValue() > 1)
        .map(Entry::getKey)
        .collect(Collectors.toSet());
    throw new IllegalArgumentException("Duplicate elements in array: " + duplicates);
  }

  static <T> FSet<T> of(Collection<T> ele) {
    @SuppressWarnings("unchecked")
    T[] arr = (T[]) ele.toArray();
    return FSet.of(arr);
  }

}
