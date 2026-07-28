/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBasicFloatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBasicLongLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTCharLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNullLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicFloatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicLongLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsInheritanceHandler;
import de.monticore.prettyprint.IndentPrinter;

public class MCCommonLiteralsJavaGenVisitor
    extends MCCommonLiteralsInheritanceHandler {

  protected JavaGenVisitorState state;

  public MCCommonLiteralsJavaGenVisitor(JavaGenVisitorState state) {
    this.state = Preconditions.checkNotNull(state);
  }

  protected IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  // CodeGen

  @Override
  public void traverse(ASTNullLiteral node) {
    getPrinter().print("null");
  }

  @Override
  public void traverse(ASTBooleanLiteral node) {
    getPrinter().print(node.getValue());
  }

  @Override
  public void traverse(ASTCharLiteral node) {
    getPrinter().print("'");
    getPrinter().print(node.getValue());
    getPrinter().print("'");
  }

  @Override
  public void traverse(ASTStringLiteral node) {
    getPrinter().print("\"");
    getPrinter().print(node.getSource());
    getPrinter().print("\"");
  }

  @Override
  public void traverse(ASTNatLiteral node) {
    getPrinter().print(node.getSource());
  }

  @Override
  public void traverse(ASTSignedNatLiteral node) {
    getPrinter().print(node.getValue());
  }

  @Override
  public void traverse(ASTBasicLongLiteral node) {
    getPrinter().print(node.getSource());
  }

  @Override
  public void traverse(ASTSignedBasicLongLiteral node) {
    getPrinter().print(node.getSource());
  }

  @Override
  public void traverse(ASTBasicFloatLiteral node) {
    getPrinter().print(node.getSource());
  }

  @Override
  public void traverse(ASTSignedBasicFloatLiteral node) {
    getPrinter().print(node.getSource());
  }

  @Override
  public void traverse(ASTBasicDoubleLiteral node) {
    getPrinter().print(node.getSource());
  }

  @Override
  public void traverse(ASTSignedBasicDoubleLiteral node) {
    getPrinter().print(node.getSource());
  }
}
