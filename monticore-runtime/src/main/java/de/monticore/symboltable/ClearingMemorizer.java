/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Evaluates the wrapped supplier at most once and caches the result. Afterwards the
 * supplier is released (and with it everything it captured, e.g. the JSON it deserializes from).
 */
public class ClearingMemorizer<T> implements Supplier<T> {

  private Supplier<T> supplier;

  private T cache;

  public ClearingMemorizer(Supplier<T> supplier) {
    this.supplier = supplier;
  }

  /**
   * Lazily applies the mapper to the value of the source, evaluated at most once.
   * A null value of the source stays null. Nothing is evaluated until the result is requested,
   * e.g. to copy a not-yet-resolvable attribute of a symbol without forcing it.
   */
  public static <T, R> Supplier<R> map(Supplier<T> source, Function<? super T, ? extends R> mapper) {
    return new ClearingMemorizer<>(() -> {
      T value = source.get();
      return value == null ? null : mapper.apply(value);
    });
  }

  @Override
  public T get() {
    if (supplier != null) {
      // a null result is cached as well
      cache = supplier.get();
      supplier = null;
    }
    return cache;
  }
}
