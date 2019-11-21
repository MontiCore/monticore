/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcassertstatements._ast.ASTAssertStatement;
import de.monticore.statements.mcassertstatements._ast.ASTMCAssertStatementsNode;
import de.monticore.statements.mcassertstatements._visitor.MCAssertStatementsVisitor;

public class MCAssertStatementsPrettyPrinter extends ExpressionsBasisPrettyPrinter implements MCAssertStatementsVisitor {

  private MCAssertStatementsVisitor realThis = this;

  public MCAssertStatementsPrettyPrinter(IndentPrinter out) {
    super(out);
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTAssertStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("assert ");
    a.getAssertion().accept(getRealThis());
    if (a.isPresentMessage()) {
      getPrinter().print(" : ");
      a.getMessage().accept(getRealThis());
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
  public String prettyprint(ASTMCAssertStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public void setRealThis(MCAssertStatementsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCAssertStatementsVisitor getRealThis() {
    return realThis;
  }
  
}
