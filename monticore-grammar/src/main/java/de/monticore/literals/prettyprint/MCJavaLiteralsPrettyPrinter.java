/* (c) https://github.com/MontiCore/monticore */

package de.monticore.literals.prettyprint;

import de.monticore.literals.mcjavaliterals._ast.*;
import de.monticore.literals.mcjavaliterals._visitor.MCJavaLiteralsHandler;
import de.monticore.literals.mcjavaliterals._visitor.MCJavaLiteralsTraverser;
import de.monticore.literals.mcjavaliterals._visitor.MCJavaLiteralsVisitor2;
import de.monticore.prettyprint.IndentPrinter;

public class MCJavaLiteralsPrettyPrinter implements MCJavaLiteralsVisitor2, MCJavaLiteralsHandler {

  private MCJavaLiteralsTraverser traverser;

  // printer to use
  protected IndentPrinter printer;

  /**
   * Constructor
   * @param printer
   */
  public MCJavaLiteralsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  /**
   * @return the printer
   */
  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public MCJavaLiteralsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MCJavaLiteralsTraverser traverser) {
    this.traverser = traverser;
  }

  /**
   * Prints a int literal
   *
   * @param a int literal
   */
  @Override
  public void visit(ASTIntLiteral a) {
    printer.print(a.getSource());
  }

  /**
   * Prints a long literal
   *
   * @param a long literal
   */
  @Override
  public void visit(ASTLongLiteral a) {
    printer.print(a.getSource());
  }

  /**
   * Prints a float literal
   *
   * @param a float literal
   */
  @Override
  public void visit(ASTFloatLiteral a) {
    printer.print(a.getSource());
  }

  /**
   * Prints a double literal
   *
   * @param a double literal
   */
  @Override
  public void visit(ASTDoubleLiteral a) {
    printer.print(a.getSource());
  }

}
