/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;

import java.util.LinkedList;
import java.util.List;

/**
 * The usage of this class is to collect information about a call expression
 * so that it is easier to resolve in the TypeCheck
 *
 * We take the call expression, and split it into a qualified name and an expression having a type
 * such that type.qualifiedName(args) has the type of the call expression
 * if no type can be found, the expression is set to the inner expression for compatibility reasons
 *
 * This makes it far easier to calculate the type of a call expression
 *
 * this is called in DeriveSymTypeOfCommonExpressions
 */

public class NameToCallExpressionVisitor implements CommonExpressionsVisitor2, CommonExpressionsHandler, ExpressionsBasisVisitor2, ExpressionsBasisHandler {

  protected CommonExpressionsTraverser traverser;

  // these are the traverser and typeCheckResult of the typecheck using this visitor
  protected CommonExpressionsTraverser typeCheckTraverser;
  protected TypeCheckResult typeCheckResult;

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void setTraverser(ExpressionsBasisTraverser traverser) {
    //the traverser is always a CommonExpressionsTraverser -> cast necessary
    this.traverser = (CommonExpressionsTraverser) traverser;
  }

  protected CommonExpressionsTraverser getTypeCheckTraverser() {
    return typeCheckTraverser;
  }

  public void setTypeCheckTraverser(
      CommonExpressionsTraverser typeCheckTraverser) {
    this.typeCheckTraverser = typeCheckTraverser;
  }

  protected TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }

  public void setTypeCheckResult(TypeCheckResult typeCheckResult) {
    this.typeCheckResult = typeCheckResult;
  }

  protected List<String> name = new LinkedList<>();
  protected ASTExpression lastExpression = null;

  @Override
  public void traverse(ASTCallExpression expr){
    // avoid run if Name is already set
    if (name.isEmpty()) {
      expr.getExpression().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTFieldAccessExpression expr){
    name.add(0, expr.getName());
    getTypeCheckResult().reset();
    expr.getExpression().accept(getTypeCheckTraverser());
    if(getTypeCheckResult().isPresentResult() || lastExpression == null) {
      lastExpression = expr.getExpression();
    }
    if(!getTypeCheckResult().isPresentResult()) {
      expr.getExpression().accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTNameExpression expr){
    name.add(0, expr.getName());
  }

  public ASTExpression getLastExpression() {
    return lastExpression;
  }

  public List<String> getName() {
    return name;
  }

  @Deprecated
  public String getLastName() {
    return name.get(name.size() - 1);
  }
}
