/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;

public interface LiteralTypeCalculator extends MCLiteralsBasisVisitor {

  public TypeExpression calculateType(ASTLiteral lit);
}
