/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mclowlevelstatements._ast.ASTLabelledBreakStatement;
import de.monticore.statements.mclowlevelstatements._ast.ASTContinueStatement;
import de.monticore.statements.mclowlevelstatements._ast.ASTLabel;
import de.monticore.statements.mclowlevelstatements._visitor.MCLowLevelStatementsHandler;
import de.monticore.statements.mclowlevelstatements._visitor.MCLowLevelStatementsTraverser;
import de.monticore.statements.mclowlevelstatements._visitor.MCLowLevelStatementsVisitor2;

@Deprecated(forRemoval = true)
public class MCLowLevelStatementsPrettyPrinter implements MCLowLevelStatementsVisitor2, MCLowLevelStatementsHandler {

  protected MCLowLevelStatementsTraverser traverser;

  protected IndentPrinter printer;

  public MCLowLevelStatementsPrettyPrinter(IndentPrinter out) {
    this.printer = out;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public MCLowLevelStatementsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCLowLevelStatementsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTLabel a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(a.getName());
    getPrinter().print(" : ");
    a.getMCStatement().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLabelledBreakStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("break ");
    if (a.isPresentLabel()) {
      getPrinter().print(a.getLabel());
    }
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTContinueStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("continue ");
    if (a.isPresentLabel()) {
      getPrinter().print(a.getLabel());
    }
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

}
