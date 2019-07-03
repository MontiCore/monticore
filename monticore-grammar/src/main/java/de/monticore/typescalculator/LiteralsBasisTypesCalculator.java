package de.monticore.typescalculator;

import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public class LiteralsBasisTypesCalculator implements LiteralTypeCalculator {

  private ASTMCType result;
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
  public ASTMCType calculateType(ASTLiteral lit) {
    lit.accept(realThis);
    return result;
  }
}
