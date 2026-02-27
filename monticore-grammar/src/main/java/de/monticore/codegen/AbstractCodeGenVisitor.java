/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.visitor.IHandler;

public abstract class AbstractCodeGenVisitor
    implements CodeGenerator, IHandler {

  protected static final String LOG_NAME = "CodeGenVisitor";

  protected IndentPrinter printer;

  protected AbstractCodeGenVisitor(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public String generateCode(ASTNode node) {
    getPrinter().clearBuffer();
    getTraverser().clearTraversedElements();
    node.accept(getTraverser());
    getPrinter().stripTrailing();
    return getPrinter().getContent();
  }

  @Override
  public IndentPrinter getPrinter() {
    return this.printer;
  }
}
