/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcexceptionstatements._ast.*;
import de.monticore.statements.mcexceptionstatements._visitor.MCExceptionStatementsVisitor;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.Iterator;

public class MCExceptionStatementsPrettyPrinter extends MCCommonStatementsPrettyPrinter implements
        MCExceptionStatementsVisitor {


  private MCExceptionStatementsVisitor realThis = this;

  public MCExceptionStatementsPrettyPrinter(IndentPrinter out) {
    super(out);
  }

  @Override
  public void handle(ASTTryStatement1 a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("try ");
    a.getCore().accept(getRealThis());
    printExceptionStatementsList(a.getCatchClausesList().iterator(), "");
    getPrinter().println();
    if (a.isPresentFinally()) {
      getPrinter().print(" finally ");
      a.getFinally().accept(getRealThis());
    }
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTTryStatement2 a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("try ");
    a.getCore().accept(getRealThis());
    printExceptionStatementsList(a.getCatchClausesList().iterator(), "");
    getPrinter().println();
    getPrinter().print(" finally ");
    a.getFinally().accept(getRealThis());
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTTryStatement3 a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("try (");
    String sep = "";
    for (ASTTryLocalVariableDeclaration l: a.getTryLocalVariableDeclarationsList()) {
      getPrinter().print(sep);
      sep = "; ";
      l.accept(getRealThis());
    }
    getPrinter().print(")");
    a.getCore().accept(getRealThis());
    printExceptionStatementsList(a.getCatchClausesList().iterator(), "");
    getPrinter().println();
    if (a.isPresentFinally()) {
      getPrinter().print(" finally ");
      a.getFinally().accept(getRealThis());
    }
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTTryLocalVariableDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getJavaModifiersList().stream().forEach(m -> {m.accept(getRealThis()); getPrinter().print(" ");});
    a.getMCType().accept(getRealThis());
    getPrinter().print(" ");
    a.getDeclaratorId().accept(getRealThis());
    getPrinter().print(" = ");
    a.getExpression().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


  @Override
  public void handle(ASTThrowStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("throw ");
    a.getExpression().accept(getRealThis());
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTCatchClause a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("catch (");
    a.getJavaModifiersList().stream().forEach(m -> {m.accept(getRealThis()); getPrinter().print(" ");});
    a.getCatchTypeList().accept(getRealThis());
    getPrinter().print(" ");
    getPrinter().print(a.getName());
    getPrinter().print(") ");
    a.getMCJavaBlock().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTCatchTypeList a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    String sep = "";
    for (ASTMCQualifiedName q: a.getMCQualifiedNamesList()) {
      getPrinter().print(sep);
      q.accept(getRealThis());
      sep = "|";
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


  private void printExceptionStatementsList(Iterator<? extends ASTMCExceptionStatementsNode> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = separator;
    }
  }


  /**
   * This method prettyprints a given node from Java.
   *
   * @param a A node from Java.
   * @return String representation.
   */
  public String prettyprint(ASTMCExceptionStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }


  @Override
  public void setRealThis(MCExceptionStatementsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCExceptionStatementsVisitor getRealThis() {
    return realThis;
  }
  
  
}
