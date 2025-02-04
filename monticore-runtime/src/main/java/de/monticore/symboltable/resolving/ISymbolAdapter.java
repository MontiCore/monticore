/* (c) https://github.com/MontiCore/monticore */


package de.monticore.symboltable.resolving;

import de.monticore.symboltable.ISymbol;

public interface ISymbolAdapter<T extends ISymbol> {

  T getAdaptee();

  default String getName() {
    return getAdaptee().getName();
  }
}
