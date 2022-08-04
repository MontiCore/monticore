/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Visitor for Derivation of SymType from Literals
 * (Function 2b)
 * i.e. for
 *    literals/MCLiteralsBasis.mc4
 */
public class DeriveSymTypeOfMCCommonLiterals extends DeriveSymTypeOfLiterals implements MCCommonLiteralsVisitor2 {

  protected TypeCheckResult typeCheckResult;

  public void setTypeCheckResult(TypeCheckResult result) {
    this.typeCheckResult = result;
  }

  public TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }

  protected SymTypePrimitive getSymType(String type) {
    return new SymTypePrimitive(BasicSymbolsMill.globalScope().resolveType(type).get());
  }

  @Override
  public void visit(ASTNatLiteral lit){
    getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.INT));
  }

  @Override
  public void visit(ASTCharLiteral lit){
    getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.CHAR));
  }

  @Override
  public void visit(ASTBooleanLiteral lit){
    getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.BOOLEAN));
  }

  @Override
  public void visit(ASTBasicDoubleLiteral lit){
    getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.DOUBLE));
  }

  @Override
  public void visit(ASTBasicFloatLiteral lit){
    getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.FLOAT));
  }

  @Override
  public void visit(ASTBasicLongLiteral lit){
    getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.LONG));
  }

  @Override
  public void visit(ASTStringLiteral lit){
    Optional<TypeSymbol> stringType = getScope(lit.getEnclosingScope()).resolveType("String");
    if(stringType.isPresent()){
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createTypeObject(stringType.get()));
    } else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0238 The type String could not be found");
    }
  }

  @Override
  public void visit(ASTSignedNatLiteral lit) {
    getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.INT));
  }

  @Override
  public void visit(ASTSignedBasicDoubleLiteral lit) {
    getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.DOUBLE));
  }

  @Override
  public void visit(ASTSignedBasicFloatLiteral lit) {
    getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.FLOAT));
  }

  @Override
  public void visit(ASTSignedBasicLongLiteral lit) {
    getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.LONG));
  }

  /**
   * Literal "null" gets marked with implicit SymType _null
   */
  @Override
  public void visit(ASTNullLiteral lit){
    getTypeCheckResult().setResult(new SymTypeOfNull());
  }
  
}
