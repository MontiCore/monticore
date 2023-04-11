/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;

import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.prettyprint.IndentPrinter;

@Deprecated(forRemoval = true)
public class ExpressionsBasisFullJavaPrinter {
  
  protected ExpressionsBasisTraverser traverser;
  
  protected IndentPrinter printer;
  
  public ExpressionsBasisFullJavaPrinter(IndentPrinter printer) {
    this.printer = printer;
    this.traverser = ExpressionsBasisMill.traverser();
  
    ExpressionsBasisJavaPrinter basisExpression = new ExpressionsBasisJavaPrinter(printer);
    traverser.setExpressionsBasisHandler(basisExpression);
    traverser.add4ExpressionsBasis(basisExpression);
  }
  
  public ExpressionsBasisTraverser getTraverser() {
    return traverser;
  }
  
  public void setTraverser(ExpressionsBasisTraverser traverser) {
    this.traverser = traverser;
  }
  
  public IndentPrinter getPrinter() {
    return printer;
  }
  
  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }
  
  public String print(ASTExpressionsBasisNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }
  
}
