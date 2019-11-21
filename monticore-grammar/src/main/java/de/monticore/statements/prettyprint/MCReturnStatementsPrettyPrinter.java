/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcreturnstatements._ast.ASTMCReturnStatementsNode;
import de.monticore.statements.mcreturnstatements._ast.ASTReturnStatement;
import de.monticore.statements.mcreturnstatements._visitor.MCReturnStatementsVisitor;

public class MCReturnStatementsPrettyPrinter extends ExpressionsBasisPrettyPrinter implements MCReturnStatementsVisitor {

  private MCReturnStatementsVisitor realThis = this;

  public MCReturnStatementsPrettyPrinter(IndentPrinter out) {
    super(out);
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTReturnStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("return ");
    if (a.isPresentExpression()) {
      a.getExpression().accept(getRealThis());
    }
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  /**
   * This method prettyprints a given node from Java.
   *
   * @param a A node from Java.
   * @return String representation.
   */
  public String prettyprint(ASTMCReturnStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public void setRealThis(MCReturnStatementsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCReturnStatementsVisitor getRealThis() {
    return realThis;
  }
  
}
