/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;

public class LiteralsBasisTypesCalculator implements LiteralTypeCalculator {

  private SymTypeExpression result;
  private MCLiteralsBasisVisitor realThis;

  @Override
  public void setRealThis(MCLiteralsBasisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCLiteralsBasisVisitor getRealThis() {
    return realThis;
  }

  @Override
  public SymTypeExpression calculateType(ASTLiteral lit) {
    lit.accept(realThis);
    return result;
  }
}
