/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable;

import java.util.function.Supplier;

/**
 * Internal marker wrapper used by generated symbol classes for symbolrule attributes whose value
 * may not be resolvable yet (see {@code AccessAsSupplierTypes} in the generator).
 * <p>
 * A dedicated type (instead of a plain {@link Supplier}) is required in case someone wants to actually return a supplier.
 */
public final class __internal__Supplier<T> implements Supplier<T> {

  private final Supplier<T> supplier;

  public __internal__Supplier(Supplier<T> supplier) {
    this.supplier = supplier;
  }

  /**
   * Wraps the supplier, unless it already is wrapped (avoids double-wrapping).
   */
  public static <T> __internal__Supplier<T> of(Supplier<T> supplier) {
    if (supplier instanceof __internal__Supplier<T> wrapped) {
      return wrapped;
    }
    return new __internal__Supplier<>(supplier);
  }

  @Override
  public T get() {
    return supplier.get();
  }
}
