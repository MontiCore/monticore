/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;

/**
 * A common interface that can be used to derive SymTypeExpressions from expressions and literals
 * @deprecated use {@link de.monticore.types3.TypeCheck3}
 */
@Deprecated
public interface IDerive {

  TypeCheckResult deriveType(ASTExpression expr);

  TypeCheckResult deriveType(ASTLiteral lit);

}
