/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcvardeclarationstatements._ast.*;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsVisitor;

public class MCVarDeclarationStatementsPrettyPrinter implements
        MCVarDeclarationStatementsVisitor {

  protected IndentPrinter printer;

  private MCVarDeclarationStatementsVisitor realThis = this;

  public MCVarDeclarationStatementsPrettyPrinter(IndentPrinter out) {
    this.printer = out;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTVariableDeclarator a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getDeclaratorId().accept(getRealThis());
    if (a.isPresentVariableInit()) {
      getPrinter().print(" = ");
      a.getVariableInit().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTDeclaratorId a) {
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
    for (ASTVariableInit v: a.getVariableInitsList()) {
      getPrinter().print(sep);
      sep = ", ";
      v.accept(getRealThis());
    }
    getPrinter().print("}");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLocalVariableDeclarationStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getLocalVariableDeclaration().accept(getRealThis());
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLocalVariableDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getMCModifiersList().stream().forEach(m -> {getPrinter().print(" "); m.accept(getRealThis()); getPrinter().print(" ");});
    getPrinter().print(" ");
    a.getMCType().accept(getRealThis());
    getPrinter().print(" ");
    String sep = "";
    for (ASTVariableDeclarator v: a.getVariableDeclaratorsList()) {
      getPrinter().print(sep);
      sep = ", ";
      v.accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void setRealThis(MCVarDeclarationStatementsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCVarDeclarationStatementsVisitor getRealThis() {
    return realThis;
  }

}
