/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpressionsNode;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;


public class AssignmentExpressionsFullPrettyPrinter extends ExpressionsBasisFullPrettyPrinter {

  private AssignmentExpressionsTraverser traverser;

  public AssignmentExpressionsFullPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.traverser = AssignmentExpressionsMill.traverser();

    AssignmentExpressionsPrettyPrinter assignmentExpressions = new AssignmentExpressionsPrettyPrinter(printer);
    traverser.setAssignmentExpressionsHandler(assignmentExpressions);
    traverser.add4AssignmentExpressions(assignmentExpressions);
    ExpressionsBasisPrettyPrinter basisExpression = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(basisExpression);
    traverser.add4ExpressionsBasis(basisExpression);
    MCBasicsPrettyPrinter basic = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basic);
  }


  public AssignmentExpressionsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(AssignmentExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
}
