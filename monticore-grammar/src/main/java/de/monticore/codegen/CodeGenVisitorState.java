/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;

/**
 * Common data and functionality shared between all code generations visitors.
 */
public class CodeGenVisitorState {

  static public final String LOG_NAME = "CodeGenVisitor";

  protected IndentPrinter printer;

  public CodeGenVisitorState(IndentPrinter printer) {
    this.printer = Preconditions.checkNotNull(printer);
  }

  /**
   * The printer to print code to.
   *
   * @return the printer to print code to.
   */
  public IndentPrinter getPrinter() {
    return printer;
  }

}
