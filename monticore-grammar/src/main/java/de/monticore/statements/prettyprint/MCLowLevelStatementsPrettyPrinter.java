/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mclowlevelstatements._ast.ASTBreakStatement;
import de.monticore.statements.mclowlevelstatements._ast.ASTContinueStatement;
import de.monticore.statements.mclowlevelstatements._ast.ASTLabeledStatement;
import de.monticore.statements.mclowlevelstatements._ast.ASTMCLowLevelStatementsNode;
import de.monticore.statements.mclowlevelstatements._visitor.MCLowLevelStatementsVisitor;

public class MCLowLevelStatementsPrettyPrinter  implements
        MCLowLevelStatementsVisitor {


  private MCLowLevelStatementsVisitor realThis = this;

  private IndentPrinter printer;

  public MCLowLevelStatementsPrettyPrinter(IndentPrinter out) {
    this.printer = out;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTLabeledStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(a.getLabel());
    getPrinter().print(": ");
    a.getMCStatement().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTBreakStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("break");
    if (a.isPresentLabel()) {
      getPrinter().print(a.getLabel());
    }
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTContinueStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("continue");
    if (a.isPresentLabel()) {
      getPrinter().print(a.getLabel());
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
  public String prettyprint(ASTMCLowLevelStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public void setRealThis(MCLowLevelStatementsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCLowLevelStatementsVisitor getRealThis() {
    return realThis;
  }
  
}
