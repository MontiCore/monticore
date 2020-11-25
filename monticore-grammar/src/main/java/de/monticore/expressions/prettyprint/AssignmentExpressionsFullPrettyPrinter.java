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
    traverser.addAssignmentExpressionsVisitor(assignmentExpressions);
    ExpressionsBasisPrettyPrinter basisExpression = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(basisExpression);
    traverser.addExpressionsBasisVisitor(basisExpression);
    MCBasicsPrettyPrinter basic = new MCBasicsPrettyPrinter(printer);
    traverser.addMCBasicsVisitor(basic);
  }


  public AssignmentExpressionsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(AssignmentExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
}
