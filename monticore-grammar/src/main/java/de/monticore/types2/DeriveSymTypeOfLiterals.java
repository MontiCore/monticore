/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.literals.mccommonliterals._ast.ASTCharLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;
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
  public Optional<SymTypeExpression> result = Optional.empty();
  
  public Optional<SymTypeExpression> getResult() {
    return result;
  }
  
  public void init() {
    result = Optional.empty();
  }
  
  // ---------------------------------------------------------- Visting Methods
  
  /**
   * Derive the Type (through calling the visitor)
   */
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    result = Optional.empty();
    lit.accept(realThis);
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
  
}
