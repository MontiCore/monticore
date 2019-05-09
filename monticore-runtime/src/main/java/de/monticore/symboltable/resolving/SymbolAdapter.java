/* (c) https://github.com/MontiCore/monticore */


package de.monticore.symboltable.resolving;

import de.monticore.symboltable.Symbol;

@Deprecated
public interface SymbolAdapter<T extends Symbol> {

  T getAdaptee();

  default String getName() {
    return getAdaptee().getName();
  }
}
