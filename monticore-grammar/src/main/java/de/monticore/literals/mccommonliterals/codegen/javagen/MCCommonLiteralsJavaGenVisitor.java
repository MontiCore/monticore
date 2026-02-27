/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals.codegen.javagen;

import de.monticore.codegen.javagen.AbstractJavaGenVisitor;
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
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsHandler;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsTraverser;
import de.monticore.prettyprint.IndentPrinter;

public class MCCommonLiteralsJavaGenVisitor extends AbstractJavaGenVisitor
    implements MCCommonLiteralsHandler {

  // Traverser
  protected MCCommonLiteralsTraverser traverser;

  public MCCommonLiteralsJavaGenVisitor(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public MCCommonLiteralsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCCommonLiteralsTraverser traverser) {
    this.traverser = traverser;
  }

  // CodeGen

  @Override
  public void handle(ASTNullLiteral node) {
    getPrinter().print("null");
  }

  @Override
  public void handle(ASTBooleanLiteral node) {
    getPrinter().print(node.getValue());
  }

  @Override
  public void handle(ASTCharLiteral node) {
    getPrinter().print("'");
    getPrinter().print(node.getValue());
    getPrinter().print("'");
  }

  @Override
  public void handle(ASTStringLiteral node) {
    getPrinter().print("\"");
    getPrinter().print(node.getSource());
    getPrinter().print("\"");
  }

  @Override
  public void handle(ASTNatLiteral node) {
    getPrinter().print(node.getSource());
  }

  @Override
  public void handle(ASTSignedNatLiteral node) {
    getPrinter().print(node.getValue());
  }

  @Override
  public void handle(ASTBasicLongLiteral node) {
    getPrinter().print(node.getSource());
  }

  @Override
  public void handle(ASTSignedBasicLongLiteral node) {
    getPrinter().print(node.getSource());
  }

  @Override
  public void handle(ASTBasicFloatLiteral node) {
    getPrinter().print(node.getSource());
  }

  @Override
  public void handle(ASTSignedBasicFloatLiteral node) {
    getPrinter().print(node.getSource());
  }

  @Override
  public void handle(ASTBasicDoubleLiteral node) {
    getPrinter().print(node.getSource());
  }

  @Override
  public void handle(ASTSignedBasicDoubleLiteral node) {
    getPrinter().print(node.getSource());
  }
}
