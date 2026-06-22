/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.visitor.ITraverser;

/**
 * The interface for Code Generators that rely on a traverser and a printer.
 * This is the expected default.
 */
public interface TraverserBasedCodeGenerator
    extends CodeGenerator {

  /**
   * Generates code for the given node,
   * adding the results to the end of an
   * {@link IndentPrinter}.
   *
   * @param node the node to generate code from.
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
   * @return the printer storing the intermediate/final results
   */
  IndentPrinter getPrinter();

  /**
   * @return the traverser to generate code with.
   */
  ITraverser getTraverser();

}
