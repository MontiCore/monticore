/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import de.monticore.prettyprint.IndentPrinter;

/**
 * Note: There is a direct alternative to this Interface,
 * and that is using String(Builder)s directly.
 * This is used instead to allow for further configuration using the printer,
 * which would not be possible with String(Builder)s.
 * <p>
 * Exception: if the corresponding String has to be printed twice,
 * but with different names for, e.g., local variables,
 * then this class can suffice, while String(Builder)s do not.
 * Note, that in most cases, printing an expression twice that
 * exists only once in the model, is likely to indicate an exponential problem,
 * and as such, hopefully, this case does not exist.
 */
@FunctionalInterface
public interface CodeGenPrintAction {

  /**
   * Can be used to print a section of the generated code.
   * This is used to pass parts of code generation into helper functions.
   *
   * @param printer the printer to print the generated code with
   */
  void print(IndentPrinter printer);

}
