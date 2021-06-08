/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcreturnstatements._ast.ASTReturnStatement;
import de.monticore.statements.mcreturnstatements._visitor.MCReturnStatementsHandler;
import de.monticore.statements.mcreturnstatements._visitor.MCReturnStatementsTraverser;
import de.monticore.statements.mcreturnstatements._visitor.MCReturnStatementsVisitor2;

public class MCReturnStatementsPrettyPrinter implements MCReturnStatementsVisitor2, MCReturnStatementsHandler {

  protected MCReturnStatementsTraverser traverser;

  protected IndentPrinter printer;

  public MCReturnStatementsPrettyPrinter(IndentPrinter out) {
    this.printer = out;
  }

  @Override
  public MCReturnStatementsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCReturnStatementsTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTReturnStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("return ");
    if (a.isPresentExpression()) {
      a.getExpression().accept(getTraverser());
    }
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }
  
}
