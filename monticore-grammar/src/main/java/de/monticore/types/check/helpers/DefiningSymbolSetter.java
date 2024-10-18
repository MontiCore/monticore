/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check.helpers;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.ISymbol;

/** Provides an easy fassade to set the defining symbol for name, field access, and call expressions. */
public interface DefiningSymbolSetter {
  /**
   * Sets the symbol as the {@code definingSymbol} of the expression, given that the expression provides this
   * possibility.
   */
  void setDefiningSymbol(ASTExpression expressionToLink, ISymbol symbolToLink);
}
