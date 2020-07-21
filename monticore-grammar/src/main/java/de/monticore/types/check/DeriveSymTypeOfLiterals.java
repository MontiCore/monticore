/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.mcliteralsbasis._symboltable.IMCLiteralsBasisScope;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.se_rwth.commons.logging.Log;

/**
 * Visitor for Derivation of SymType from Literals
 * (Function 2b)
 * i.e. for
 *    literals/MCLiteralsBasis.mc4
 */
public class DeriveSymTypeOfLiterals implements MCLiteralsBasisVisitor {

  public IOOSymbolsScope getScope (IMCLiteralsBasisScope mcLiteralsBasisScope){
    // is accepted only here, decided on 07.04.2020
    if(!(mcLiteralsBasisScope instanceof IOOSymbolsScope)){
      Log.error("0xA0309 the enclosing scope of the literal does not implement the interface IOOSymbolsScope");
    }
    // is accepted only here, decided on 07.04.2020
    return (IOOSymbolsScope) mcLiteralsBasisScope;
  }
  
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
  protected TypeCheckResult result;
  
  public TypeCheckResult getTypeCheckResult() {
    return result;
  }
  
  public void init() {
    result = new TypeCheckResult();
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

  public void setTypeCheckResult(TypeCheckResult result) {
    this.result = result;
  }
}
