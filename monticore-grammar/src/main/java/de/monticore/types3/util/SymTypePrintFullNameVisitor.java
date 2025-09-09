// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;

/**
 * prints SymTypeExpressions with their full name
 * s. {@link SymTypeExpression#printFullName()}
 * <p>
 * * Usage:
 * calculate(mySymType)
 */
public class SymTypePrintFullNameVisitor extends SymTypePrintVisitor {

  @Override
  protected String printTypeSymbol(TypeSymbol symbol) {
    return symbol.getFullName();
  }

  @Override
  protected String printTypeVarSymbol(TypeVarSymbol symbol) {
    return symbol.getFullName();
  }

}
