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
  public void handle(ASTTryStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("try ");
    a.getMCJavaBlock().accept(getRealThis());
    getPrinter().println();
    a.getExceptionHandler().accept(getRealThis());
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTCatchExceptionsHandler a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    printExceptionStatementsList(a.getCatchClauseList().iterator(), "");
    if (a.isPresentMCJavaBlock()) {
      getPrinter().println();
      getPrinter().println("finally");
      getPrinter().indent();
      a.getMCJavaBlock().accept(getRealThis());
      getPrinter().unindent();
    }
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTFinallyBlockOnlyHandler a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("finally");
    getPrinter().indent();
    a.getMCJavaBlock().accept(getRealThis());
    getPrinter().unindent();
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTTryStatementWithResources a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("try (");
    printExceptionStatementsList(a.getResourceList().iterator(), ";");
    getPrinter().println(") ");
    a.getMCJavaBlock().accept(getRealThis());
    printExceptionStatementsList(a.getCatchClauseList().iterator(), "");
    if (a.isPresentFinallyBlock()) {
      getPrinter().println();
      getPrinter().println("finally");
      getPrinter().indent();
      a.getFinallyBlock().accept(getRealThis());
      getPrinter().unindent();
    }
    getPrinter().println();
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTResource a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getPrimitiveModifierList().stream().forEach(m -> {m.accept(getRealThis()); getPrinter().print(" ");});
    a.getMCType().accept(getRealThis());
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
    a.getPrimitiveModifierList().stream().forEach(m -> {m.accept(getRealThis()); getPrinter().print(" ");});
    a.getCatchType().accept(getRealThis());
    getPrinter().print(" ");
    printNode(a.getName());
    getPrinter().print(") ");
    a.getMCJavaBlock().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTCatchType a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    String sep = "";
    for (ASTMCQualifiedName q: a.getMCQualifiedNameList()) {
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
