/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.visitor.IHandler;

public abstract class AbstractCodeGenVisitor
    implements CodeGenerator, IHandler {

  protected static final String LOG_NAME = "CodeGenVisitor";

  protected boolean isWithinGenerateCode = false;

  protected IndentPrinter printer;

  protected AbstractCodeGenVisitor(IndentPrinter printer) {
    this.printer = printer;
  }

  /**
   * Generates code for the given ASTNode.
   * <p>
   * This method may not be called recursively,
   * as the buffer is cleared.
   *
   * @param node The node to generate code for.
   * @return the generated code
   */
  @Override
  public String generateCode(ASTNode node) {
    if (isWithinGenerateCode) {
      throw new IllegalCallerException(
          "0xFDC62 internal error: generateCode() was called recursively."
      );
    }
    isWithinGenerateCode = true;
    getPrinter().clearBuffer();
    getTraverser().clearTraversedElements();
    node.accept(getTraverser());
    getPrinter().stripTrailing();
    String code = getPrinter().getContent();
    isWithinGenerateCode = false;
    return code;
  }

  @Override
  public IndentPrinter getPrinter() {
    return this.printer;
  }
}
