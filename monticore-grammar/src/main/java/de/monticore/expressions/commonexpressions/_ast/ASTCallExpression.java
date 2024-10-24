/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions._ast;

import de.monticore.symboltable.ISymbol;

import java.util.Optional;

public class ASTCallExpression extends ASTCallExpressionTOP {

  protected ISymbol definingSymbol;

  public Optional<ISymbol> getDefiningSymbol() {
    return Optional.ofNullable(this.definingSymbol);
  }

  public void setDefiningSymbol(ISymbol symbol) {
    this.definingSymbol = symbol;
  }
}
