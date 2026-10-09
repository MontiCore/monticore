/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Adapter Type for List<Supplier<T>> to List<T>.
 * Allows access to the raw type inside
 */
public class SuppliedList<T> extends AbstractList<T> {

  protected final List<Supplier<T>> backing;

  public SuppliedList(List<Supplier<T>> backing) {
    this.backing = Objects.requireNonNull(backing);
  }

  /**
   * The underlying suppliers (not a copy), which can be accessed without evaluating them.
   */
  public List<Supplier<T>> getSuppliers() {
    return backing;
  }

  /**
   * Creates a new list that supplies the given, already evaluated values.
   */
  @SuppressWarnings("unchecked")
  public static <T> SuppliedList<T> fromValues(Collection<? extends T> values) {
    if (values instanceof SuppliedList) {
      // reuse the suppliers instead of evaluating the elements
      return new SuppliedList<>(new ArrayList<>(((SuppliedList<T>) values).backing));
    }
    List<Supplier<T>> result = new ArrayList<>(values.size());
    for (T value : values) {
      result.add(supplierOf(value));
    }
    return new SuppliedList<>(result);
  }

  protected static <T> Supplier<T> supplierOf(T value) {
    return new ClearingMemorizer<>(() -> value);
  }

  @Override
  public T get(int index) {
    return backing.get(index).get();
  }

  @Override
  public int size() {
    return backing.size();
  }

  @Override
  public T set(int index, T element) {
    return backing.set(index, supplierOf(element)).get();
  }

  @Override
  public void add(int index, T element) {
    backing.add(index, supplierOf(element));
    modCount++;
  }

  @Override
  public T remove(int index) {
    T removed = backing.remove(index).get();
    modCount++;
    return removed;
  }
}
