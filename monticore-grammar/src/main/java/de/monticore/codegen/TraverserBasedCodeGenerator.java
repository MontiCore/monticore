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
    // check that this is not used _within_ the code generator itself
    class I {
      public static final ThreadLocal<Boolean> isGenerating =
          ThreadLocal.withInitial(() -> false);
    }
    if (I.isGenerating.get()) {
      throw new IllegalCallerException("generateCode was called recursively");
    }
    I.isGenerating.set(true);

    try {
      String code;
      getPrinter().clearBuffer();
      getTraverser().clearTraversedElements();
      node.accept(getTraverser());
      getPrinter().stripTrailing();
      code = getPrinter().getContent();
      return code;
    }
    finally {
      I.isGenerating.set(false);
    }

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
