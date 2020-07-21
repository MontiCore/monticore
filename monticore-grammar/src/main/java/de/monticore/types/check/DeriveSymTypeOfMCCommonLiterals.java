/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;

/**
 * Visitor for Derivation of SymType from Literals
 * (Function 2b)
 * i.e. for
 *    literals/MCLiteralsBasis.mc4
 */
public class DeriveSymTypeOfMCCommonLiterals extends DeriveSymTypeOfLiterals
                              implements MCCommonLiteralsVisitor {
  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  MCCommonLiteralsVisitor realThis = this;
  
  @Override
  public void setRealThis(MCCommonLiteralsVisitor realThis) {
    this.realThis = realThis;
    super.realThis = realThis;  // not necessarily needed, but to be safe ...
  }
  
  @Override
  public MCCommonLiteralsVisitor getRealThis() {
    return realThis;
  }
  
  // ---------------------------------------------------------- Visting Methods
  
  @Override
  public void visit(ASTNatLiteral lit){
    result.setCurrentResult(DefsTypeBasic._intSymType);
  }

  @Override
  public void visit(ASTCharLiteral lit){
    result.setCurrentResult(DefsTypeBasic._charSymType);
  }

  @Override
  public void visit(ASTBooleanLiteral lit){
    result.setCurrentResult(DefsTypeBasic._booleanSymType);
  }

  @Override
  public void visit(ASTBasicDoubleLiteral lit){
    result.setCurrentResult(DefsTypeBasic._doubleSymType);
  }

  @Override
  public void visit(ASTBasicFloatLiteral lit){
    result.setCurrentResult(DefsTypeBasic._floatSymType);
  }

  @Override
  public void visit(ASTBasicLongLiteral lit){
    result.setCurrentResult(DefsTypeBasic._longSymType);
  }

  @Override
  public void visit(ASTStringLiteral lit){
    result.setCurrentResult(DefsTypeBasic._StringSymType);
  }

  @Override
  public void visit(ASTSignedNatLiteral lit) {
    result.setCurrentResult(DefsTypeBasic._intSymType);
  }

  @Override
  public void visit(ASTSignedBasicDoubleLiteral lit) {
    result.setCurrentResult(DefsTypeBasic._doubleSymType);
  }

  @Override
  public void visit(ASTSignedBasicFloatLiteral lit) {
    result.setCurrentResult(DefsTypeBasic._floatSymType);
  }

  @Override
  public void visit(ASTSignedBasicLongLiteral lit) {
    result.setCurrentResult(DefsTypeBasic._longSymType);
  }

  /**
   * Literal "null" gets marked with implicit SymType _null
   */
  @Override
  public void visit(ASTNullLiteral lit){
    result.setCurrentResult(DefsTypeBasic._nullSymType);
  }
  
}
