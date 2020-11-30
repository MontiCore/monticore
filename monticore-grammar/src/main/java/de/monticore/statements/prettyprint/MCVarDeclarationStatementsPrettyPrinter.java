/* (c) https://github.com/MontiCore/monticore */

package de.monticore.statements.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTDeclaratorId;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclarationStatement;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsHandler;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsTraverser;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsVisitor2;

public class MCVarDeclarationStatementsPrettyPrinter implements
    MCVarDeclarationStatementsVisitor2, MCVarDeclarationStatementsHandler {

  protected IndentPrinter printer;

  private MCVarDeclarationStatementsTraverser traverser;

  public MCVarDeclarationStatementsPrettyPrinter(IndentPrinter out) {
    this.printer = out;
  }

  @Override
  public MCVarDeclarationStatementsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCVarDeclarationStatementsTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTVariableDeclarator a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getDeclarator().accept(getTraverser());
    if (a.isPresentVariableInit()) {
      getPrinter().print(" = ");
      a.getVariableInit().accept(getTraverser());
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTDeclaratorId a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(a.getName());
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLocalVariableDeclarationStatement a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getLocalVariableDeclaration().accept(getTraverser());
    getPrinter().println(";");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLocalVariableDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    a.getMCModifierList().stream().forEach(m -> {getPrinter().print(" "); m.accept(getTraverser()); getPrinter().print(" ");});
    getPrinter().print(" ");
    a.getMCType().accept(getTraverser());
    getPrinter().print(" ");
    String sep = "";
    for (ASTVariableDeclarator v: a.getVariableDeclaratorList()) {
      getPrinter().print(sep);
      sep = ", ";
      v.accept(getTraverser());
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

}
