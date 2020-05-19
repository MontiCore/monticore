/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._symboltable.MCCommonLiteralsScope;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;
import de.se_rwth.commons.logging.Log;

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
    result.setLast(DefsTypeBasic._intSymType);
  }

  @Override
  public void visit(ASTCharLiteral lit){
    result.setLast(DefsTypeBasic._charSymType);
  }

  @Override
  public void visit(ASTBooleanLiteral lit){
    result.setLast(DefsTypeBasic._booleanSymType);
  }

  @Override
  public void visit(ASTBasicDoubleLiteral lit){
    result.setLast(DefsTypeBasic._doubleSymType);
  }

  @Override
  public void visit(ASTBasicFloatLiteral lit){
    result.setLast(DefsTypeBasic._floatSymType);
  }

  @Override
  public void visit(ASTBasicLongLiteral lit){
    result.setLast(DefsTypeBasic._longSymType);
  }

  @Override
  public void visit(ASTStringLiteral lit){
    result.setLast(DefsTypeBasic._StringSymType);
  }

  @Override
  public void visit(ASTSignedNatLiteral lit) {
    result.setLast(DefsTypeBasic._intSymType);
  }

  @Override
  public void visit(ASTSignedBasicDoubleLiteral lit) {
    result.setLast(DefsTypeBasic._doubleSymType);
  }

  @Override
  public void visit(ASTSignedBasicFloatLiteral lit) {
    result.setLast(DefsTypeBasic._floatSymType);
  }

  @Override
  public void visit(ASTSignedBasicLongLiteral lit) {
    result.setLast(DefsTypeBasic._longSymType);
  }

  /**
   * Literal "null" gets marked with implicit SymType _null
   */
  @Override
  public void visit(ASTNullLiteral lit){
    result.setLast(DefsTypeBasic._nullSymType);
  }
  
}
