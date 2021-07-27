/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.subConstraints;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanOrOpExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

/**
 * Created by Alexander Wilts on 16.01.2017.
 *
 * Determines the context of an Optional.
 * If it is used inside a OR-Expression it has to be handled differently from an AND-Expression.
 */
public class CalculateContextVisitor implements
        CommonExpressionsVisitor2 {

  protected ASTExpression targetNode;
  public boolean inOrContext = false;

  public CalculateContextVisitor(ASTExpression targetNode){
    super();
    this.targetNode = targetNode;
  }

  @Override
  public void visit(ASTBooleanOrOpExpression node) {
    CommonExpressionsTraverser subExprVisitorTraverser = CommonExpressionsMill
            .inheritanceTraverser();
    CommonExpressionsTraverser containsTargetVisitorTraverser = CommonExpressionsMill
            .inheritanceTraverser();
    FindSubExpressionVisitor subExprVisitor = new FindSubExpressionVisitor();
    subExprVisitorTraverser.add4CommonExpressions(subExprVisitor);
    CheckIfContainsTargetVisitor containsTargetVisitor = new CheckIfContainsTargetVisitor(targetNode);
    containsTargetVisitorTraverser.add4IVisitor(containsTargetVisitor);
    //check if lhs or rhs contains targetNode
    node.getLeft().accept(containsTargetVisitorTraverser);
    if(containsTargetVisitor.containsTarget){
      //check if that side is final (contains no further || or &&)
      node.getLeft().accept(subExprVisitorTraverser);
      if(!subExprVisitor.subExpressionIsPresent){
        inOrContext = true;
      }
    }
    node.getRight().accept(containsTargetVisitorTraverser);
    if(containsTargetVisitor.containsTarget){
      node.getRight().accept(subExprVisitorTraverser);
      if(!subExprVisitor.subExpressionIsPresent){
        inOrContext = true;
      }
    }
  }





}
