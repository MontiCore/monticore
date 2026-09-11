/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable;

import com.google.common.base.Preconditions;

import java.util.Optional;

public class SimpleSymbolSupplier<T extends ISymbol> implements ISymbolSupplier<T> {

  private final T value;

  public SimpleSymbolSupplier(T value){
    Preconditions.checkNotNull(value);
    this.value = value;
  }

  @Override
  public Optional<T> get() {
    return Optional.of(value);
  }
}
