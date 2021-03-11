/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;

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

  private SymTypeConstant getSymType(String type) {
    return new SymTypeConstant(BasicSymbolsMill.globalScope().resolveType(type).get());
  }

  @Override
  public void visit(ASTNatLiteral lit){
    typeCheckResult.setCurrentResult(getSymType(BasicSymbolsMill.INT));
  }

  @Override
  public void visit(ASTCharLiteral lit){
    typeCheckResult.setCurrentResult(getSymType(BasicSymbolsMill.CHAR));
  }

  @Override
  public void visit(ASTBooleanLiteral lit){
    typeCheckResult.setCurrentResult(getSymType(BasicSymbolsMill.BOOLEAN));
  }

  @Override
  public void visit(ASTBasicDoubleLiteral lit){
    typeCheckResult.setCurrentResult(getSymType(BasicSymbolsMill.DOUBLE));
  }

  @Override
  public void visit(ASTBasicFloatLiteral lit){
    typeCheckResult.setCurrentResult(getSymType(BasicSymbolsMill.FLOAT));
  }

  @Override
  public void visit(ASTBasicLongLiteral lit){
    typeCheckResult.setCurrentResult(getSymType(BasicSymbolsMill.LONG));
  }

  @Override
  public void visit(ASTStringLiteral lit){
    OOTypeSymbolSurrogate oo = new OOTypeSymbolSurrogate("String");
    oo.setEnclosingScope(OOSymbolsMill.globalScope());
    typeCheckResult.setCurrentResult(new SymTypeOfObject(oo));
  }

  @Override
  public void visit(ASTSignedNatLiteral lit) {
    typeCheckResult.setCurrentResult(getSymType(BasicSymbolsMill.INT));
  }

  @Override
  public void visit(ASTSignedBasicDoubleLiteral lit) {
    typeCheckResult.setCurrentResult(getSymType(BasicSymbolsMill.DOUBLE));
  }

  @Override
  public void visit(ASTSignedBasicFloatLiteral lit) {
    typeCheckResult.setCurrentResult(getSymType(BasicSymbolsMill.FLOAT));
  }

  @Override
  public void visit(ASTSignedBasicLongLiteral lit) {
    typeCheckResult.setCurrentResult(getSymType(BasicSymbolsMill.LONG));
  }

  /**
   * Literal "null" gets marked with implicit SymType _null
   */
  @Override
  public void visit(ASTNullLiteral lit){
    typeCheckResult.setCurrentResult(new SymTypeOfNull());
  }
  
}
