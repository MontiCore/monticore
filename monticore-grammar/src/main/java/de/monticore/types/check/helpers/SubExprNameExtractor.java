/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check.helpers;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.DeriveSymTypeOfCommonExpressions;

/**
 * Collects information about potential naming parts of a call expression, field access expressions, or other composed
 * expressions so that it is easier to resolve fields / types / methods in the TypeCheck. E.g., this interface is used
 * in {@link DeriveSymTypeOfCommonExpressions}.
 */
public interface SubExprNameExtractor {

  /**
   * Collect the subexpressions that the expression call is composed of and if a subexpression represents a name,
   * associate this name with that subexpression.
   * @return the {@link SubExprNameExtractionResult} that contains the information about the subexpressions and
   * potentially represented names.
   */
  SubExprNameExtractionResult calculateNameParts(ASTExpression expression);
}
