/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * A {@link LinkedHashSet} posing as a {@link List}
 */
public class SetAsListAdapter<E> implements List<E> {

  protected final LinkedHashSet<E> adaptee;

  public SetAsListAdapter(LinkedHashSet<E> adaptee) {
    this.adaptee = adaptee;
  }

  public SetAsListAdapter() {
    this.adaptee = new LinkedHashSet<>();
  }

  @Override
  public int size() {
    return this.adaptee.size();
  }

  @Override
  public boolean isEmpty() {
    return this.adaptee.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return this.adaptee.contains(o);
  }

  @Override
  @Nonnull
  public Iterator<E> iterator() {
    return this.adaptee.iterator();
  }

  @Override
  @Nonnull
  public Object[] toArray() {
    return this.adaptee.toArray();
  }

  @Override
  @Nonnull
  public <T> T[] toArray(@Nonnull T[] a) {
    return this.adaptee.toArray(a);
  }

  @Override
  public boolean add(E e) {
    return this.adaptee.add(e);
  }

  @Override
  public boolean remove(Object o) {
    return this.adaptee.remove(o);
  }

  @Override
  public boolean containsAll(@Nonnull Collection<?> c) {
    return this.adaptee.containsAll(c);
  }

  @Override
  public boolean addAll(@Nonnull Collection<? extends E> c) {
    return this.adaptee.addAll(c);
  }

  @Override
  public boolean addAll(int index, @Nonnull Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(@Nonnull Collection<?> c) {
    return this.adaptee.removeAll(c);
  }

  @Override
  public boolean retainAll(@Nonnull Collection<?> c) {
    return this.adaptee.retainAll(c);
  }

  @Override
  public void clear() {
    this.adaptee.clear();
  }

  @Override
  public E get(int index) {
    if (index < 0 || index >= this.size()) {
      throw new IndexOutOfBoundsException(index);
    }
    int i = 0;
    for (E elem : this.adaptee) {
      if (i++ == index)
        return elem;
    }
    throw new UnsupportedOperationException();
  }

  @Override
  public E set(int index, E element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(int index, E element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public E remove(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int indexOf(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int lastIndexOf(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Nonnull
  public ListIterator<E> listIterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Nonnull
  public ListIterator<E> listIterator(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Nonnull
  public List<E> subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException();
  }
}
