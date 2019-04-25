package de.monticore.typescalculator;

import de.monticore.expressions.commonexpressions._ast.ASTExtLiteralExt;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public interface LiteralTypeCalculator {

  public ASTMCType calculateType(ASTExtLiteralExt lit);
}
