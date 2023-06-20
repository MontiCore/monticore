// (c) https://github.com/MontiCore/monticore
package de.monticore.symbols.basicsymbols._symboltable;

public interface IBasicSymbolsScope extends IBasicSymbolsScopeTOP {

  /**
   * returns whether a type variable is bound within this scope
   * e.g. class C<T> {} // T is bound within the class
   */
  default boolean isTypeVariableBound(TypeVarSymbol typeVar) {
    if (getLocalTypeVarSymbols().stream()
        .anyMatch(otherTypeVar -> otherTypeVar == typeVar)) {
      return true;
    }
    else if (getEnclosingScope() == null) {
      return false;
    }
    else {
      return getEnclosingScope().isTypeVariableBound(typeVar);
    }
  }


}
