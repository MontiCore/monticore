/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Visitor for Derivation of SymType from Literals
 * (Function 2b)
 * i.e. for
 *    literals/MCLiteralsBasis.mc4
 */
public class DeriveSymTypeOfLiterals implements MCLiteralsBasisVisitor {
  
  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  MCLiteralsBasisVisitor realThis = this;
  
  @Override
  public void setRealThis(MCLiteralsBasisVisitor realThis) {
    this.realThis = realThis;
  }
  
  @Override
  public MCLiteralsBasisVisitor getRealThis() {
    return realThis;
  }
  
  // ---------------------------------------------------------- Storage result
  
  /**
   * Storage in the Visitor: result of the last endVisit.
   * This attribute is synthesized upward.
   */
  protected LastResult result;
  
  public LastResult getResult() {
    return result;
  }
  
  public void init() {
    result = new LastResult();
  }
  
  // ---------------------------------------------------------- Visting Methods
  
  /**
   * Derive the Type (through calling the visitor)
   */
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(realThis);
    Optional<SymTypeExpression> result = this.result.getLastOpt();
    this.result.setLastOpt(Optional.empty());
    return result;
  }
  
  // ---------------------------------------------------------- Visting Methods
  
  // This section is deliberately empty:
  // MCLiteralsBasis has no implementing Nonterminals
  
  @Override
  public void visit(ASTLiteral lit){
    // This general method is only called, if no specific exists,:
    // Should not happen.
    Log.error("0xED671 Internal Error: No Type for Literal " + lit
            + " Probably TypeCheck mis-configured.");
  }

  public void setResult(LastResult result) {
    this.result = result;
  }
}
