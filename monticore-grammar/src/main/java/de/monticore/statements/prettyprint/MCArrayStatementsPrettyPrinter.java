/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcarraystatements._ast.ASTArrayDeclaratorId;
import de.monticore.statements.mcarraystatements._ast.ASTArrayInit;
import de.monticore.statements.mcarraystatements._ast.ASTMCArrayStatementsNode;
import de.monticore.statements.mcarraystatements._visitor.MCArrayStatementsVisitor;
import de.monticore.statements.mcassertstatements._ast.ASTMCAssertStatementsNode;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableInit;

public class MCArrayStatementsPrettyPrinter extends MCVarDeclarationStatementsPrettyPrinter implements
        MCArrayStatementsVisitor {

  private MCArrayStatementsVisitor realThis = this;

  public MCArrayStatementsPrettyPrinter(IndentPrinter out) {
    super(out);
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
      v.accept(getRealThis());
    }
    getPrinter().print("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  /**
   * This method prettyprints a given node from Java.
   *
   * @param a A node from Java.
   * @return String representation.
   */
  public String prettyprint(ASTMCArrayStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public void setRealThis(MCArrayStatementsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCArrayStatementsVisitor getRealThis() {
    return realThis;
  }


}
