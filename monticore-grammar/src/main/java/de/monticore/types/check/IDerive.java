/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;

import java.util.Optional;

/**
 * A common interface that can be used to derive SymTypeExpressions from expressions and literals
 */
public interface IDerive {

  TypeCheckResult deriveType(ASTExpression expr);

  TypeCheckResult deriveType(ASTLiteral lit);

}
