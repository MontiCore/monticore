/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._ast.*;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Iterator;

public class MCCommonStatementsPrettyPrinter implements
        MCCommonStatementsVisitor {

  protected IndentPrinter printer;

  private MCCommonStatementsVisitor realThis = this;

  public MCCommonStatementsPrettyPrinter(IndentPrinter out) {
    this.printer = out;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  protected void printNode(String s) {
    getPrinter().print(s);
  }

  protected void printExpressionsList(Iterator<? extends ASTExpression> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = separator;
    }
  }

  protected void printMCTypeList(Iterator<? extends ASTMCType> iter, String separator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getRealThis());
      sep = separator;
    }
  }

  @Override
  public void handle(ASTMCJavaBlock a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("{");
    getPrinter().indent();
    a.getMCBlockStatementsList().stream().forEach(m -> m.accept(getRealThis()));
    getPrinter().unindent();
    getPrinter().println("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


  @Override
  public void handle(ASTIfStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("if (");
    a.getCondition().accept(getRealThis());
    getPrinter().print(") ");
    a.getThenStatement().accept(getRealThis());
    if (a.isPresentElseStatement()) {
      getPrinter().println("else ");
      a.getElseStatement().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTForStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("for (");
    a.getForControl().accept(getRealThis());
    getPrinter().print(")");
    a.getMCStatement().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTWhileStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("while (");
    a.getCondition().accept(getRealThis());
    getPrinter().print(")");
    a.getMCStatement().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTDoWhileStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("do ");
    a.getMCStatement().accept(getRealThis());
    getPrinter().print("while (");
    a.getCondition().accept(getRealThis());
    getPrinter().println(");");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


  @Override
  public void handle(ASTCommonForControl a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    if (a.isPresentForInit()) {
      a.getForInit().accept(getRealThis());
    }
    getPrinter().print(";");
    if (a.isPresentCondition()) {
      a.getCondition().accept(getRealThis());
    }
    getPrinter().print(";");
    printExpressionsList(a.getExpressionsList().iterator(), ",");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTForInitByExpressions a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(" ");
    printExpressionsList(a.getExpressionsList().iterator(), ", ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


  @Override
  public void handle(ASTJavaModifier a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(" " + printModifier(a.getModifier()) + " ");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }


 @Override
  public void handle(ASTEmptyStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTExpressionStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getExpression().accept(getRealThis());
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTSwitchStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("switch (");
    a.getExpression().accept(getRealThis());
    getPrinter().println(") {");
    getPrinter().indent();
    printSeparated(a.getSwitchBlockStatementGroupsList().iterator(), "");
    printSeparated(a.getSwitchLabelsList().iterator(), "");
    getPrinter().unindent();
    getPrinter().println("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTConstantExpressionSwitchLabel a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("case ");
    a.getConstant().accept(getRealThis());
    getPrinter().println(":");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTEnumConstantSwitchLabel a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("case ");
    printNode(a.getEnumConstant());
    getPrinter().println(":");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTDefaultSwitchLabel a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().println("default:");
  }


  protected void printSeparated(Iterator<? extends ASTMCCommonStatementsNode> iter, String separator) {
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
  public String prettyprint(ASTMCCommonStatementsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  private String printModifier(int constant) {

    switch (constant) {
      case ASTConstantsMCCommonStatements.PRIVATE:
        return "private";
      case ASTConstantsMCCommonStatements.PUBLIC:
        return "public";
      case ASTConstantsMCCommonStatements.PROTECTED:
        return "protected";
      case ASTConstantsMCCommonStatements.STATIC:
        return "static";
      case ASTConstantsMCCommonStatements.TRANSIENT:
        return "transient";
      case ASTConstantsMCCommonStatements.FINAL:
        return "final";
      case ASTConstantsMCCommonStatements.ABSTRACT:
        return "abstract";
      case ASTConstantsMCCommonStatements.NATIVE:
        return "native";
      case ASTConstantsMCCommonStatements.THREADSAFE:
        return "threadsafe";
      case ASTConstantsMCCommonStatements.SYNCHRONIZED:
        return "synchronized";
      case ASTConstantsMCCommonStatements.VOLATILE:
        return "volatile";
      case ASTConstantsMCCommonStatements.STRICTFP:
        return "strictfp";
      default:
        return null;
    }
  }

  @Override
  public void setRealThis(MCCommonStatementsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCCommonStatementsVisitor getRealThis() {
    return realThis;
  }
  
  
}
