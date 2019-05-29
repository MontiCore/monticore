package de.monticore.typescalculator;

import de.monticore.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public interface LiteralTypeCalculator extends MCLiteralsBasisVisitor {

  public ASTMCType calculateType(ASTLiteral lit);
}
