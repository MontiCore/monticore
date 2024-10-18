/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.subConstraints;


import de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanOrOpExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;

/**
 * Created by Alexander Wilts on 16.01.2017.
 *
 * This visitor calculates if the given expression contains any further subExpressions.
 * A subExpression is every expression, that does not contain any further '&&' or '||' operators.
 */
public class FindSubExpressionVisitor implements
        CommonExpressionsVisitor2 {

  public boolean subExpressionIsPresent;

  public FindSubExpressionVisitor(){
    super();
    subExpressionIsPresent = false;
  }

  @Override
  public void visit(ASTBooleanOrOpExpression node) {
      subExpressionIsPresent = true;
  }

  @Override
  public void visit(ASTBooleanAndOpExpression node) {
      subExpressionIsPresent = true;
  }

}
