/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Supplies a symbol on demand.
 * This class acts as a replacement to the SymbolSurrogates
 * (which did not support composition, a TypeSurrogate
 * did not actually surrogate a CDTypeSurrogate, with CDTypeSym extends TypeSym.)
 * Errors during resolving are logged and an empty optional is returned.
 *
 * @param <T> the symbol type
 */
public interface ISymbolSupplier<T> extends Supplier<Optional<T>> {


  interface IMemoizedSymbolSupplier<T> extends ISymbolSupplier<T> {

  }

  /**
   * Memoize a supplier: Only attempt to resolve once
   *
   * @param supplier the supplier
   * @param <T>      the symbol type
   * @return a memoized supplier (that only resolved once)
   */
  @SuppressWarnings("all")
  public static <T> IMemoizedSymbolSupplier<T> memoize(ISymbolSupplier<T> supplier) {
    AtomicReference<Optional<T>> value = new AtomicReference<>();
    return () -> {
      Optional<T> val = value.get();
      if (val == null) {
        val = value.updateAndGet(cur -> cur == null ?
                Objects.requireNonNull(supplier.get()) : cur);
      }
      return val;
    };
  }
}
