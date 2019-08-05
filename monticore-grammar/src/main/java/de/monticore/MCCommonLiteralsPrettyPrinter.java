/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;

public class MCCommonLiteralsPrettyPrinter extends MCBasicsPrettyPrinter implements MCCommonLiteralsVisitor {
  
  private MCCommonLiteralsVisitor realThis = this;

  public MCCommonLiteralsPrettyPrinter(IndentPrinter printer) {
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
   * Prints a natural literal
   *
   * @param a Nat literal
   */
  @Override
  public void visit(ASTNatLiteral a) {
    printer.print(a.getSource());
  }

  /**
   * Prints a natural literal
   *
   * @param a SignedNat literal
   */
  @Override
  public void visit(ASTSignedNatLiteral a) {
    printer.print(a.getSource());
  }

  /**
   * Prints a natural literal
   *
   * @param a long literal
   */
  @Override
  public void visit(ASTBasicLongLiteral a) {
    printer.print(a.getSource());
  }

  /**
   * Prints a natural literal
   *
   * @param a SignedLong literal
   */
  @Override
  public void visit(ASTSignedBasicLongLiteral a) {
    printer.print(a.getSource());
  }

  /**
   * Prints a natural literal
   *
   * @param a double literal
   */
  @Override
  public void visit(ASTBasicDoubleLiteral a) {
    printer.print(a.getSource());
  }

  /**
   * Prints a natural literal
   *
   * @param a SignedDourble literal
   */
  @Override
  public void visit(ASTSignedBasicDoubleLiteral a) {
    printer.print(a.getSource());
  }

  /**
   * Prints a natural literal
   *
   * @param a float literal
   */
  @Override
  public void visit(ASTBasicFloatLiteral a) {
    printer.print(a.getSource());
  }

  /**
   * Prints a natural literal
   *
   * @param a SignedNat literal
   */
  @Override
  public void visit(ASTSignedBasicFloatLiteral a) {
    printer.print(a.getSource());
  }

  /**
   * This method prettyprints a given node from literal grammar.
   * 
   * @param a A node from literal grammar.
   * @return String representation.
   */
  public String prettyprint(ASTMCCommonLiteralsNode a) {
    a.accept(getRealThis());
    return printer.getContent();
  }

  /**
   * @see de.monticore.literals.literals._visitor.LiteralsVisitor#setRealThis(de.monticore.literals.literals._visitor.LiteralsVisitor)
   */
  @Override
  public void setRealThis(MCCommonLiteralsVisitor realThis) {
    this.realThis = realThis;
  }

  /**
   * @see de.monticore.literals.literals._visitor.LiteralsVisitor#getRealThis()
   */
  @Override
  public MCCommonLiteralsVisitor getRealThis() {
    return realThis;
  }

}
