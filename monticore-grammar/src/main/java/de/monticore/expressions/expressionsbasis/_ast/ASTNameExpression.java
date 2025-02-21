/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis._ast;

import de.monticore.symboltable.ISymbol;

import java.util.Optional;

public class ASTNameExpression extends ASTNameExpressionTOP {

  @Deprecated
  protected ISymbol definingSymbol;

  /**
   * @deprecated part of TypeCheck1. instead use
   *     {@link de.monticore.types.check.SymTypeExpression#getSourceInfo()}.
   */
  @Deprecated
  public Optional<ISymbol> getDefiningSymbol() {
    return Optional.ofNullable(this.definingSymbol);
  }

  /**
   * @deprecated part of TypeCheck1. Not required in TypeCheck3.
   */
  @Deprecated
  public void setDefiningSymbol(ISymbol symbol) {
    this.definingSymbol = symbol;
  }
}
