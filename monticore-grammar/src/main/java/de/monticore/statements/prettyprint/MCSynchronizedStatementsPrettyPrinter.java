/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcsynchronizedstatements._ast.ASTMCSynchronizedStatementsNode;
import de.monticore.statements.mcsynchronizedstatements._ast.ASTSynchronizedStatement;
import de.monticore.statements.mcsynchronizedstatements._visitor.MCSynchronizedStatementsVisitor;

public class MCSynchronizedStatementsPrettyPrinter extends MCCommonStatementsPrettyPrinter
    implements MCSynchronizedStatementsVisitor {

  private MCSynchronizedStatementsVisitor realThis = this;

  public MCSynchronizedStatementsPrettyPrinter(IndentPrinter out) {
    super(out);
  }

  @Override
  public void handle(ASTSynchronizedStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("synchronized (");
    a.getExpression().accept(getRealThis());
    getPrinter().print(") ");
    a.getMCJavaBlock().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  /**
   * This method prettyprints a given node from Java.
   *
   * @param a A node from Java.
   * @return String representation.
   */
  public String prettyprint(ASTMCSynchronizedStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public void setRealThis(MCSynchronizedStatementsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCSynchronizedStatementsVisitor getRealThis() {
    return realThis;
  }
  
}
