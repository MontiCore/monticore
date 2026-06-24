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
  
  public ListMatchMapping(@Nonnull List<A> list, @Nonnull Function<A, E> mapper,
                          @Nonnull Function<E, A> mapperReverse) {
    this.list = list;
    this.mapper = mapper;
    this.mapperReverse = mapperReverse;
  }
  
  public ListMatchMapping(@Nonnull List<A> list, @Nonnull Function<A, E> mapper) {
    this(list, mapper,
        e -> {throw new IllegalStateException("Bidirectional ListMatching is not defined");});
  }
  
  @Override
  public E get(int index) {
    return this.mapper.apply(this.list.get(index));
  }
  
  @Override
  public E set(int index, E element) {
    return this.mapper.apply(this.list.set(index, this.mapperReverse.apply(element)));
  }
  
  @Override
  public int size() {
    return this.list.size();
  }
  
  @Override
  @Nonnull
  public Stream<E> stream() {
    return this.list.stream().map(this.mapper);
  }
  
}
