/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.monticore.types2.SymTypeExpression;

@Deprecated
public interface LiteralTypeCalculator extends MCLiteralsBasisVisitor {

  @Deprecated
  public SymTypeExpression calculateType(ASTLiteral lit);
}
