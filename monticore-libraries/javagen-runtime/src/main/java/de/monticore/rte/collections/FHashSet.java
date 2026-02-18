package de.monticore.rte.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementation of a {@link FSet} using {@link Object#hashCode()} to store elements efficiently.
 * This is using {@link FHashMap} as a backend, and the
 * performance guarantees of that class apply to this class as well.
 * <p>
 * There is no iteration order guaranteed.
 * <p>
 * Null elements are permitted.
 *
 * @param <T> The type of the elements to store
 */
public class FHashSet<T> implements FSet<T> {

  static final FSet<?> EMPTY = new FHashSet<>();

  protected final FMap<T, Object> delegate;

  protected FHashSet(FMap<T, Object> delegate) {
    this.delegate = delegate;
  }

  public FHashSet() {
    this.delegate = FMap.of();
  }

  @SafeVarargs
  public FHashSet(T... elements) {
    FMap<T, Object> newDelegate = FMap.of();
    for (T ele : elements) {
      newDelegate = newDelegate.with(ele, null);
    }
    this.delegate = newDelegate;
  }

  public FHashSet(FCollection<T> elements) {
    FMap<T, Object> newDelegate = FMap.of();
    for (T ele : elements) {
      newDelegate = newDelegate.with(ele, null);
    }
    this.delegate = newDelegate;
  }

  public FHashSet(Collection<T> elements) {
    FMap<T, Object> newDelegate = FMap.of();
    for (T ele : elements) {
      newDelegate = newDelegate.with(ele, null);
    }
    this.delegate = newDelegate;
  }

  protected FSet<T> withDelegate(FMap<T, Object> newDelegate) {
    if (this.delegate == newDelegate) {
      return this;
    }
    return new FHashSet<>(newDelegate);
  }

  @Override
  public FSet<T> with(T element) {
    return withDelegate(this.delegate.with(element, null));
  }

  @Override
  public FSet<T> withAll(FCollection<? extends T> other) {
    FMap<T, Object> newDelegate = this.delegate;
    for (T ele : other) {
      newDelegate = newDelegate.with(ele, null);
    }
    return withDelegate(newDelegate);
  }

  @Override
  public FSet<T> without(Object element) {
    return withDelegate(this.delegate.without(element));
  }

  @Override
  public FSet<T> withoutAll(FCollection<?> other) {
    return withDelegate(this.delegate.withoutAll(other));
  }

  @Override
  public int size() {
    return this.delegate.size();
  }

  @Override
  public boolean isEmpty() {
    return this.delegate.isEmpty();
  }

  @Override
  public boolean contains(Object element) {
    return this.delegate.containsKey(element);
  }

  @Override
  public Set<T> toJava() {
    return this.delegate.keySet().toJava();
  }

  @Override
  public Iterator<T> iterator() {
    return this.delegate.keySet().iterator();
  }

  @Override
  public int hashCode() {
    return this.delegate.keySet().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return this.delegate.keySet().equals(obj);
  }

  @Override
  public String toString() {
    return this.delegate.keySet().toString();
  }

}
