/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;

/**
 * Visitor for Derivation of SymType from Literals
 * (Function 2b)
 * i.e. for
 *    literals/MCLiteralsBasis.mc4
 */
public class DeriveSymTypeOfMCCommonLiterals implements MCCommonLiteralsVisitor2 {

  protected TypeCheckResult typeCheckResult;

  public void setTypeCheckResult(TypeCheckResult result) {
    this.typeCheckResult = result;
  }

  @Override
  public void visit(ASTNatLiteral lit){
    typeCheckResult.setCurrentResult(DefsTypeBasic._intSymType);
  }

  @Override
  public void visit(ASTCharLiteral lit){
    typeCheckResult.setCurrentResult(DefsTypeBasic._charSymType);
  }

  @Override
  public void visit(ASTBooleanLiteral lit){
    typeCheckResult.setCurrentResult(DefsTypeBasic._booleanSymType);
  }

  @Override
  public void visit(ASTBasicDoubleLiteral lit){
    typeCheckResult.setCurrentResult(DefsTypeBasic._doubleSymType);
  }

  @Override
  public void visit(ASTBasicFloatLiteral lit){
    typeCheckResult.setCurrentResult(DefsTypeBasic._floatSymType);
  }

  @Override
  public void visit(ASTBasicLongLiteral lit){
    typeCheckResult.setCurrentResult(DefsTypeBasic._longSymType);
  }

  @Override
  public void visit(ASTStringLiteral lit){
    typeCheckResult.setCurrentResult(DefsTypeBasic._StringSymType);
  }

  @Override
  public void visit(ASTSignedNatLiteral lit) {
    typeCheckResult.setCurrentResult(DefsTypeBasic._intSymType);
  }

  @Override
  public void visit(ASTSignedBasicDoubleLiteral lit) {
    typeCheckResult.setCurrentResult(DefsTypeBasic._doubleSymType);
  }

  @Override
  public void visit(ASTSignedBasicFloatLiteral lit) {
    typeCheckResult.setCurrentResult(DefsTypeBasic._floatSymType);
  }

  @Override
  public void visit(ASTSignedBasicLongLiteral lit) {
    typeCheckResult.setCurrentResult(DefsTypeBasic._longSymType);
  }

  /**
   * Literal "null" gets marked with implicit SymType _null
   */
  @Override
  public void visit(ASTNullLiteral lit){
    typeCheckResult.setCurrentResult(DefsTypeBasic._nullSymType);
  }
  
}
