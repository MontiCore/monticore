/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable;

import java.util.function.Supplier;

/**
 * Internal marker wrapper used by generated symbol classes for symbolrule attributes whose value
 * may not be resolvable yet (see {@code AccessAsSupplierTypes} in the generator).
 * <p>
 * A dedicated type (instead of a plain {@link Supplier}) is required in case someone wants to actually return a supplier
 */
public class __internal__Supplier<T> implements Supplier<T> {

  protected final Supplier<T> supplier;

  public __internal__Supplier(Supplier<T> supplier) {
    this.supplier = supplier;
  }

  @Override
  public T get() {
    return supplier.get();
  }
}
