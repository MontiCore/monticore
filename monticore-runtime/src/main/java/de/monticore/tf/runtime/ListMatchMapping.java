/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime;

import javax.annotation.Nonnull;
import java.util.AbstractList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Maps an assigned variable within a list to a readable data structure.
 *
 * @param <E> map-target
 * @param <A> the list-type
 */
public class ListMatchMapping<E, A> extends AbstractList<E> {
  
  protected final List<A> list;
  protected final Function<A, E> mapper;
  protected final Function<E, A> mapperReverse;

  /**
   * Constructs a {@link ListMatchMapping} with bidirectional mapping functions.
   *
   * @param list the underlying list to wrap
   * @param mapper function to map list elements to the target type
   * @param mapperReverse function to reverse-map target elements back to the list type
   */
  public ListMatchMapping(@Nonnull List<A> list, @Nonnull Function<A, E> mapper,
                          @Nonnull Function<E, A> mapperReverse) {
    this.list = list;
    this.mapper = mapper;
    this.mapperReverse = mapperReverse;
  }
  
  /**
   * Constructs a {@link ListMatchMapping} with a unidirectional mapping function.
   * The reverse mapping is not defined and will throw an exception if used.
   *
   * @param list the underlying list to wrap
   * @param mapper function to map list elements to the target type
   */
  public ListMatchMapping(@Nonnull List<A> list, @Nonnull Function<A, E> mapper) {
    this(list, mapper,
        e -> {throw new IllegalStateException("Bidirectional ListMatching is not defined");});
  }
  
  @Override
  public E get(int index) {
    return this.mapper.apply(this.list.get(index));
  }
  
  /**
   * Sets an element at the specified index by applying the reverse mapper.
   *
   * @param index the index of the element to set
   * @param element the new element to set
   * @return the previously mapped element at this index
   */
  @Override
  public E set(int index, E element) {
    return this.mapper.apply(this.list.set(index, this.mapperReverse.apply(element)));
  }
  
  /**
   * Returns the size of the underlying list.
   *
   * @return the number of elements in the list
   */
  @Override
  public int size() {
    return this.list.size();
  }
  
  /**
   * Returns a stream of mapped elements from the underlying list.
   *
   * @return a stream of elements of type E
   */
  @Override
  @Nonnull
  public Stream<E> stream() {
    return this.list.stream().map(this.mapper);
  }
  
}
