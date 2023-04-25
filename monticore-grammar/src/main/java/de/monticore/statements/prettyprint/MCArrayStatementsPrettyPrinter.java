/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcarraystatements._ast.ASTArrayDeclaratorId;
import de.monticore.statements.mcarraystatements._ast.ASTArrayInit;
import de.monticore.statements.mcarraystatements._visitor.MCArrayStatementsHandler;
import de.monticore.statements.mcarraystatements._visitor.MCArrayStatementsTraverser;
import de.monticore.statements.mcarraystatements._visitor.MCArrayStatementsVisitor2;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableInit;

@Deprecated(forRemoval = true)
public class MCArrayStatementsPrettyPrinter implements
    MCArrayStatementsVisitor2, MCArrayStatementsHandler {

  protected MCArrayStatementsTraverser traverser;

  protected IndentPrinter printer;

  public MCArrayStatementsPrettyPrinter(IndentPrinter out) {
    this.printer = out;
  }

  @Override
  public MCArrayStatementsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCArrayStatementsTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTArrayDeclaratorId a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(a.getName());
    for (int i = 0; i < a.getDimList().size(); i++) {
      getPrinter().print("[]");
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTArrayInit a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("{");
    String sep = "";
    for (ASTVariableInit v: a.getVariableInitList()) {
      getPrinter().print(sep);
      sep = ", ";
      v.accept(getTraverser());
    }
    getPrinter().print("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


}
