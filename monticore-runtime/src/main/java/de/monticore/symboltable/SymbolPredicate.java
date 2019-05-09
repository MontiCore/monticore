/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import java.util.function.Predicate;

@Deprecated
public interface SymbolPredicate extends Predicate<Symbol> {

  @Override
  boolean test(Symbol symbol);


}
