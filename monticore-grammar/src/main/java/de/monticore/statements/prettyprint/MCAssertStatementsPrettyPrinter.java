/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcassertstatements._ast.ASTAssertStatement;
import de.monticore.statements.mcassertstatements._visitor.MCAssertStatementsHandler;
import de.monticore.statements.mcassertstatements._visitor.MCAssertStatementsTraverser;
import de.monticore.statements.mcassertstatements._visitor.MCAssertStatementsVisitor2;

@Deprecated(forRemoval = true)
public class MCAssertStatementsPrettyPrinter implements MCAssertStatementsVisitor2, MCAssertStatementsHandler {

  protected MCAssertStatementsTraverser traverser;

  protected IndentPrinter printer;

  public MCAssertStatementsPrettyPrinter(IndentPrinter out) {
    this.printer = out;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void setTraverser(MCAssertStatementsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public MCAssertStatementsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void handle(ASTAssertStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("assert ");
    a.getAssertion().accept(getTraverser());
    if (a.isPresentMessage()) {
      getPrinter().print(" : ");
      a.getMessage().accept(getTraverser());
    }
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }
  
}
