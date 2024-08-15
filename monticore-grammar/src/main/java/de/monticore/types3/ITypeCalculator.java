package de.monticore.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;

/**
 * interface to calculate types from an AST
 * @deprecated use {@link TypeCheck3}
 */
@Deprecated
public interface ITypeCalculator {

  SymTypeExpression symTypeFromAST(ASTMCType mcType);

  SymTypeExpression symTypeFromAST(ASTMCVoidType mcVoidType);

  SymTypeExpression symTypeFromAST(ASTMCReturnType mcReturnType);

  SymTypeExpression symTypeFromAST(ASTMCQualifiedName mcQualifiedName);

  SymTypeExpression typeOf(ASTExpression expr);

  default SymTypeExpression typeOf(ASTExpression expr,
      SymTypeExpression targetType) {
    return typeOf(expr);
  }

  SymTypeExpression typeOf(ASTLiteral lit);

}
