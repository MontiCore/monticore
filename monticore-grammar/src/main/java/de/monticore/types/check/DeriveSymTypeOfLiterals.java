/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.mcliteralsbasis._symboltable.IMCLiteralsBasisScope;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor2;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.se_rwth.commons.logging.Log;

/**
 * Visitor for Derivation of SymType from Literals
 * (Function 2b)
 * i.e. for
 *    literals/MCLiteralsBasis.mc4
 */
public class DeriveSymTypeOfLiterals implements MCLiteralsBasisVisitor2 {

  public IOOSymbolsScope getScope (IMCLiteralsBasisScope mcLiteralsBasisScope){
    // is accepted only here, decided on 07.04.2020
    if(!(mcLiteralsBasisScope instanceof IOOSymbolsScope)){
      Log.error("0xA0309 the enclosing scope of the literal does not implement the interface IOOSymbolsScope");
    }
    // is accepted only here, decided on 07.04.2020
    return (IOOSymbolsScope) mcLiteralsBasisScope;
  }

  
  /**
   * Storage in the Visitor: result of the last endVisit.
   * This attribute is synthesized upward.
   */
  protected TypeCheckResult typeCheckResult;
  
  public TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }
  
  public void init() {
    typeCheckResult = new TypeCheckResult();
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
    this.typeCheckResult = result;
  }
}
