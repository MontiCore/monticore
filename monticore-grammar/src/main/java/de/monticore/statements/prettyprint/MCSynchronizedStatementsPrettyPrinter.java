/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcsynchronizedstatements._ast.ASTSynchronizedStatement;
import de.monticore.statements.mcsynchronizedstatements._visitor.MCSynchronizedStatementsHandler;
import de.monticore.statements.mcsynchronizedstatements._visitor.MCSynchronizedStatementsTraverser;
import de.monticore.statements.mcsynchronizedstatements._visitor.MCSynchronizedStatementsVisitor2;

public class MCSynchronizedStatementsPrettyPrinter implements MCSynchronizedStatementsVisitor2, MCSynchronizedStatementsHandler {

  protected MCSynchronizedStatementsTraverser traverser;

  protected IndentPrinter printer;

  public MCSynchronizedStatementsPrettyPrinter(IndentPrinter out) {
    this.printer = out;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public MCSynchronizedStatementsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCSynchronizedStatementsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTSynchronizedStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("synchronized (");
    a.getExpression().accept(getTraverser());
    getPrinter().print(") ");
    a.getMCJavaBlock().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }
  
}
