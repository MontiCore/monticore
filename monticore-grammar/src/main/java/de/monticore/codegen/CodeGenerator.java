/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import de.monticore.ast.ASTNode;

/**
 * The most generic interface for code generation.
 */
public interface CodeGenerator {

  /**
   * Generates code for the given node.
   * Further details are to be specified by the concrete CodeGenerators.
   */
  String generateCode(ASTNode node);

}
