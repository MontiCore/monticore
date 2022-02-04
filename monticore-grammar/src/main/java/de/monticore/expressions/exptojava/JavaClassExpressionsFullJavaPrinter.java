/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;

import de.monticore.expressions.javaclassexpressions.JavaClassExpressionsMill;
import de.monticore.expressions.javaclassexpressions._ast.ASTGenericInvocationSuffix;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class JavaClassExpressionsFullJavaPrinter extends CommonExpressionsFullJavaPrinter {
  
  protected JavaClassExpressionsTraverser traverser;
  
  @Override
  public JavaClassExpressionsTraverser getTraverser() {
    return traverser;
  }
  
  public void setTraverser(JavaClassExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
  
  public JavaClassExpressionsFullJavaPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = JavaClassExpressionsMill.traverser();
  
    CommonExpressionsJavaPrinter commonExpression = new CommonExpressionsJavaPrinter(printer);
    traverser.setCommonExpressionsHandler(commonExpression);
    traverser.add4CommonExpressions(commonExpression);
    ExpressionsBasisJavaPrinter expressionBasis = new ExpressionsBasisJavaPrinter(printer);
    traverser.setExpressionsBasisHandler(expressionBasis);
    traverser.add4ExpressionsBasis(expressionBasis);
    JavaClassExpressionsJavaPrinter javaClassExpression = new JavaClassExpressionsJavaPrinter(printer);
    traverser.setJavaClassExpressionsHandler(javaClassExpression);
    traverser.add4JavaClassExpressions(javaClassExpression);
  }
  
  public String prettyprint(ASTGenericInvocationSuffix node){
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }
  
}
