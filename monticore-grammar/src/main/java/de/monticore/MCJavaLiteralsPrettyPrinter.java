/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.literals.mcjavaliterals._ast.*;
import de.monticore.literals.mcjavaliterals._visitor.MCJavaLiteralsVisitor;
import de.monticore.prettyprint.IndentPrinter;

public class MCJavaLiteralsPrettyPrinter extends MCCommonLiteralsPrettyPrinter implements MCJavaLiteralsVisitor {

  private MCJavaLiteralsVisitor realThis = this;

  // printer to use
  protected IndentPrinter printer = null;

  /**
   * Constructor
   * @param printer
   */
  public MCJavaLiteralsPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.printer = printer;
  }

  /**
   * @return the printer
   */
  public IndentPrinter getPrinter() {
    return this.printer;
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


  /**
   * @see MCJavaLiteralsVisitor#setRealThis(MCJavaLiteralsVisitor)
   */
  @Override
  public void setRealThis(MCJavaLiteralsVisitor realThis) {
    this.realThis = realThis;
  }

  /**
   * @see MCJavaLiteralsVisitor#getRealThis()
   */
  @Override
  public MCJavaLiteralsVisitor getRealThis() {
    return realThis;
  }

}
