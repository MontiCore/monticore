/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class CommonExpressionsJavaPrinter extends CommonExpressionsPrettyPrinter {
  
  protected CommonExpressionsTraverser traverser;
  
  protected IndentPrinter printer;
  
  @Override
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }
  
  @Override
  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
  
  public IndentPrinter getPrinter() {
    return printer;
  }
  
  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }
  
  public CommonExpressionsJavaPrinter(IndentPrinter printer) {
    super(printer);
    this.printer = printer;
  }
  
  public CommonExpressionsJavaPrinter(){
    super(new IndentPrinter());
  }
  
  @Override
  public void handle(ASTFieldAccessExpression node) {
    node.getExpression().accept(getTraverser());
    String name = "get"+ node.getName().substring(0,1).toUpperCase() + node.getName().substring(1)+"()";
    getPrinter().print("."+name);
  }
  
}
