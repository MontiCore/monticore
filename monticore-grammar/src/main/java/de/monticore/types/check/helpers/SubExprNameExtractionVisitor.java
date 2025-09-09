/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check.helpers;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.types.check.DeriveSymTypeOfCommonExpressions;

/**
 * The usage of this class is to collect information about a call expression
 * so that it is easier to resolve in the TypeCheck
 *
 * We take the call expression, and split it into a qualified name and an expression having a type
 * such that type.qualifiedName(args) has the type of the call expression
 * if no type can be found, the expression is set to the inner expression for compatibility reasons
 * This makes it far easier to calculate the type of call expressions.
 *
 * To use this class:
 * <ol>
 *   <li>Create an inheritance traverser</li>
 *   <li>Create an instance of this class and set {@link #subExpressions}. This is a shared state object that should be
 *       synchronized among the all name extracting visitors of the different languages</li>
 *   <li>Register the instance of this class to be a visitor for ExpressionsBasis and CommonExpressions in the
 *   traverser.</li>
 * </ol>,
 *
 * This class is used in {@link DeriveSymTypeOfCommonExpressions}.
 */

public class SubExprNameExtractionVisitor implements CommonExpressionsVisitor2, ExpressionsBasisVisitor2 {

  /** @see #subExpressions */
  public SubExprNameExtractionResult getSubExpressions() {
    return subExpressions;
  }

  /**
   * Collects the name parts of the expression that the visitor is applied to.
   */
  protected SubExprNameExtractionResult subExpressions;

  public SubExprNameExtractionVisitor(SubExprNameExtractionResult commonNamePartsRecord) {
    this.subExpressions = commonNamePartsRecord;
  }

  /**
   * Default implementation that marks an expression as *not* being a name part. This marking will be ignored if the
   * expression indeed represents a valid name. Use
   * {@link SubExprNameExtractionResult#putNameAtStart(ASTExpression, String)}  on {@link #subExpressions} in the visit
   * implementations of the expressions representing names to overwrite this "invalidity" marking.
   */
  @Override
  public void visit(ASTExpression expr) {
    subExpressions.maybeAppendInvalidExprAtStart(expr);
  }

  @Override
  public void visit(ASTFieldAccessExpression expr){
    subExpressions.putNameAtStart(expr, expr.getName());
  }

  @Override
  public void visit(ASTNameExpression expr){
    subExpressions.putNameAtStart(expr, expr.getName());
  }
}
