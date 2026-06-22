/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.visitor.IHandler;

/**
 * Common functionality shared between all code generations visitors.
 */
public interface ICodeGenVisitor
    extends CodeGenerator, IHandler {

  String LOG_NAME = "ICodeGenVisitor";

  /**
   * generates code for the given {@link ASTNode}.
   * Not to be used by the visitor itself.
   *
   * @param node the node to generate code from
   * @return the generated code
   */
  @Override
  default String generateCode(ASTNode node) {
    getPrinter().clearBuffer();
    getTraverser().clearTraversedElements();
    node.accept(getTraverser());
    getPrinter().stripTrailing();
    return getPrinter().getContent();
  }

  /**
   * The printer to print code to.
   *
   * @return the printer to print code to.
   */
  IndentPrinter getPrinter();
}
