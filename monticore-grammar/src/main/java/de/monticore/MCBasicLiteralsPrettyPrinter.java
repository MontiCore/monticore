/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.mcbasicliterals._ast.*;
import de.monticore.mcbasicliterals._visitor.MCBasicLiteralsVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;

public class MCBasicLiteralsPrettyPrinter extends MCBasicsPrettyPrinter implements MCBasicLiteralsVisitor {
  
  private MCBasicLiteralsVisitor realThis = this;

  /**
   * Constructor for de.monticore.literals.prettyprint.LiteralsPrettyPrinterConcreteVisitor.
   * @param printer
   */
  public MCBasicLiteralsPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  /**
   * Prints a "null" literal
   * 
   * @param a null literal
   */
  @Override
  public void visit(ASTNullLiteral a) {
    printer.print("null");
  }
  
  /**
   * Prints a boolean literal
   * 
   * @param a boolean literal
   */
  @Override
  public void visit(ASTBooleanLiteral a) {
    printer.print(a.getValue());
  }
  
  /**
   * Prints a char literal
   * 
   * @param a char literal
   */
  @Override
  public void visit(ASTCharLiteral a) {
    printer.print("'" + a.getSource() + "'");
  }
  
  /**
   * Prints a string literal
   * 
   * @param a string literal
   */
  @Override
  public void visit(ASTStringLiteral a) {
    printer.print("\"" + a.getSource() + "\"");
  }

  
  /**
   * This method prettyprints a given node from literal grammar.
   * 
   * @param a A node from literal grammar.
   * @return String representation.
   */
  public String prettyprint(ASTMCBasicLiteralsNode a) {
    a.accept(getRealThis());
    return printer.getContent();
  }

  /**
   * @see de.monticore.literals.literals._visitor.LiteralsVisitor#setRealThis(de.monticore.literals.literals._visitor.LiteralsVisitor)
   */
  @Override
  public void setRealThis(MCBasicLiteralsVisitor realThis) {
    this.realThis = realThis;
  }

  /**
   * @see de.monticore.literals.literals._visitor.LiteralsVisitor#getRealThis()
   */
  @Override
  public MCBasicLiteralsVisitor getRealThis() {
    return realThis;
  }

}
