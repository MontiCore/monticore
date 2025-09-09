/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import java.util.function.Predicate;

public interface ISymbolPredicate extends Predicate<ISymbol> {

  @Override
  boolean test(ISymbol symbol);

}
