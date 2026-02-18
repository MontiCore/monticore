package de.monticore.rte.collections;

import de.monticore.rte.functions.Function1;
import de.monticore.rte.functions.Function2;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * A collection of elements very similar to Java's {@link Collection}
 * made for functional programming.
 * The main difference is that modification operations are
 * performed on a copy of the collection and then return that modified copy.
 * The original collection is left as it was before the call (An immutable object).
 * (Note that performance optimizations are of course still possible
 * and not all the data is copied for every modification)
 * <p>
 * Any {@link FCollection} is thread-safe.
 *
 * @param <T> The type of elements stored in this collection
 */
public interface FCollection<T> extends Iterable<T> {

  /**
   * This has the same semantics as {@link Collection#size()}
   */
  int size();

  /**
   * This has the same semantics as {@link Collection#isEmpty()}
   */
  boolean isEmpty();

  /**
   * This has the same semantics as {@link Collection#contains(Object)}
   */
  boolean contains(Object element);

  /**
   * @return An unmodifiable {@link Collection} containing the same elements as this collection.
   */
  Collection<T> toJava();

  /**
   * This has the same semantics as {@link Collection#containsAll(Collection)}
   */
  default boolean containsAll(FCollection<?> other) {
    for (Object ele : other) {
      if (!contains(ele)) {
        return false;
      }
    }
    return true;
  }

  /**
   * This has the same semantics as {@link Collection#toArray()}
   */
  default Object[] toArray() {
    Object[] array = new Object[this.size()];
    int i = 0;
    for (T ele : this) {
      array[i] = ele;
      i++;
    }
    return array;
  }

  /**
   * This has the same semantics as
   * {@link Collection#toArray(java.util.function.IntFunction)}
   */
  default T[] toArray(Function1<T[], Integer> constructor) {
    T[] array = constructor.apply(this.size());
    int i = 0;
    for (T ele : this) {
      array[i] = ele;
      i++;
    }
    return array;
  }

  /**
   * Folds the elements by calling the accumulator with all elements. The elements are provided in this Collections' iteration order.
   *
   * @param initial     The initial value
   * @param accumulator Called for each element and receives its previous output, or for the first element the initial value.
   * @param <R>         The type of the result
   * @return The folded result
   */
  default <R> R fold(R initial, Function2<R, R, T> accumulator) {
    for (T ele : this) {
      initial = accumulator.apply(initial, ele);
    }
    return initial;
  }

  /**
   * Collect all the elements by calling the accumulator with all elements. The elements are provided in this Collections' iteration order.
   *
   * @param result      The result container
   * @param accumulator Called for each element and always also receives the result container.
   * @param <R>         The type of the result container
   * @return The result container
   * @deprecated non-functional
   */
  @Deprecated(forRemoval = true)
  default <R> R collect(R result, BiConsumer<R, T> accumulator) {
    for (T ele : this) {
      accumulator.accept(result, ele);
    }
    return result;
  }
}
