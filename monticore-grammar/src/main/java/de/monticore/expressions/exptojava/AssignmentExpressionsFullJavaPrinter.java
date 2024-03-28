/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;

import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;

@Deprecated(forRemoval = true)
public class AssignmentExpressionsFullJavaPrinter extends ExpressionsBasisFullJavaPrinter{
  
  protected AssignmentExpressionsTraverser traverser;
  
  @Override
  public AssignmentExpressionsTraverser getTraverser() {
    return traverser;
  }
  
  public void setTraverser(AssignmentExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
  
  public AssignmentExpressionsFullJavaPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = AssignmentExpressionsMill.traverser();
  
    AssignmentExpressionsJavaPrinter assignmentExpressions = new AssignmentExpressionsJavaPrinter(printer);
    traverser.setAssignmentExpressionsHandler(assignmentExpressions);
    traverser.add4AssignmentExpressions(assignmentExpressions);
    ExpressionsBasisJavaPrinter basisExpression = new ExpressionsBasisJavaPrinter(printer);
    traverser.setExpressionsBasisHandler(basisExpression);
    traverser.add4ExpressionsBasis(basisExpression);
  }
}
