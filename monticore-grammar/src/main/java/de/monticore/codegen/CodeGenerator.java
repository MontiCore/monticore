/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.IndentPrinter;

public interface CodeGenerator {

  /**
   * Generates code for the given node,
   * adding the results to the end of an
   * {@link IndentPrinter}.
   * Further details are to be specified by the concrete CodeGenerators.
   */
  String generateCode(ASTNode node);

  /**
   * @return the printer storing the (intermediate/final results)
   */
  IndentPrinter getPrinter();

}
