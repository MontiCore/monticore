/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class CommonExpressionsFullJavaPrinter extends ExpressionsBasisFullJavaPrinter {
  
  protected CommonExpressionsTraverser traverser;
  
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }
  
  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
  
  public CommonExpressionsFullJavaPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = CommonExpressionsMill.traverser();
    
    CommonExpressionsJavaPrinter commonExpressions = new CommonExpressionsJavaPrinter(printer);
    traverser.setCommonExpressionsHandler(commonExpressions);
    traverser.add4CommonExpressions(commonExpressions);
    ExpressionsBasisJavaPrinter basicExpression = new ExpressionsBasisJavaPrinter(printer);
    traverser.setExpressionsBasisHandler(basicExpression);
    traverser.add4ExpressionsBasis(basicExpression);
    
  }
}
