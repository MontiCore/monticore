package de.monticore.typescalculator;

import de.monticore.expressions.commonexpressions._ast.ASTExtLiteralExt;
import de.monticore.mcbasicliterals._ast.ASTLiteral;
import de.monticore.mcbasicliterals._visitor.MCBasicLiteralsVisitor;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public interface LiteralTypeCalculator extends MCBasicLiteralsVisitor {

  public ASTMCType calculateType(ASTLiteral lit);
}
