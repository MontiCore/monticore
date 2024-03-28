/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcexceptionstatements._ast.*;
import de.monticore.statements.mcexceptionstatements._visitor.MCExceptionStatementsHandler;
import de.monticore.statements.mcexceptionstatements._visitor.MCExceptionStatementsTraverser;
import de.monticore.statements.mcexceptionstatements._visitor.MCExceptionStatementsVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.Iterator;

@Deprecated(forRemoval = true)
public class MCExceptionStatementsPrettyPrinter implements
    MCExceptionStatementsVisitor2, MCExceptionStatementsHandler {

  protected MCExceptionStatementsTraverser traverser;
  
  protected IndentPrinter printer;

  public MCExceptionStatementsPrettyPrinter(IndentPrinter out) {
    this.printer = out;
  }

  @Override
  public MCExceptionStatementsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCExceptionStatementsTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public void handle(ASTTryStatement1 a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("try ");
    a.getCore().accept(getTraverser());
    printExceptionStatementsList(a.getCatchClauseList().iterator(), "");
    getPrinter().println();
    if (a.isPresentFinally()) {
      getPrinter().print(" finally ");
      a.getFinally().accept(getTraverser());
    }
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTTryStatement2 a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("try ");
    a.getCore().accept(getTraverser());
    printExceptionStatementsList(a.getCatchClauseList().iterator(), "");
    getPrinter().println();
    getPrinter().print(" finally ");
    a.getFinally().accept(getTraverser());
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTTryStatement3 a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("try (");
    String sep = "";
    for (ASTTryLocalVariableDeclaration l: a.getTryLocalVariableDeclarationList()) {
      getPrinter().print(sep);
      sep = "; ";
      l.accept(getTraverser());
    }
    getPrinter().print(")");
    a.getCore().accept(getTraverser());
    printExceptionStatementsList(a.getCatchClauseList().iterator(), "");
    getPrinter().println();
    if (a.isPresentFinally()) {
      getPrinter().print(" finally ");
      a.getFinally().accept(getTraverser());
    }
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTTryLocalVariableDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getJavaModifierList().stream().forEach(m -> {m.accept(getTraverser()); getPrinter().print(" ");});
    a.getMCType().accept(getTraverser());
    getPrinter().print(" ");
    a.getDeclaratorId().accept(getTraverser());
    getPrinter().print(" = ");
    a.getExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


  @Override
  public void handle(ASTThrowStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("throw ");
    a.getExpression().accept(getTraverser());
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTCatchClause a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("catch (");
    a.getJavaModifierList().stream().forEach(m -> {m.accept(getTraverser()); getPrinter().print(" ");});
    a.getCatchTypeList().accept(getTraverser());
    getPrinter().print(" ");
    getPrinter().print(a.getName());
    getPrinter().print(") ");
    a.getMCJavaBlock().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTCatchTypeList a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    String sep = "";
    for (ASTMCQualifiedName q: a.getMCQualifiedNameList()) {
      getPrinter().print(sep);
      q.accept(getTraverser());
      sep = "|";
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


  protected void printExceptionStatementsList(Iterator<? extends ASTMCExceptionStatementsNode> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getTraverser());
      sep = separator;
    }
  }
  
  
}
