/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Visitor for Derivation of SymType from Literals
 * (Function 2b)
 * i.e. for
 *    literals/MCLiteralsBasis.mc4
 * @deprecated Use {@link de.monticore.types3.TypeCheck3} instead.
 */
@Deprecated(forRemoval = true)
public class DeriveSymTypeOfMCCommonLiterals extends DeriveSymTypeOfLiterals implements MCCommonLiteralsVisitor2 {

  protected TypeCheckResult typeCheckResult;

  public void setTypeCheckResult(TypeCheckResult result) {
    this.typeCheckResult = result;
  }

  public TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }

  protected SymTypeExpression getSymType(String type, SourcePosition pos) {
    Optional<TypeSymbol> primitive = BasicSymbolsMill.globalScope().resolveType(type);
    if(primitive.isPresent()){
      return SymTypeExpressionFactory.createPrimitive(primitive.get());
    }else{
      Log.error("0xA0207 The primitive type " + type + " could not be resolved." +
        "Did you add primitive types to your language?", pos);
      return SymTypeExpressionFactory.createObscureType();
    }
  }

  @Override
  public void visit(ASTNatLiteral lit){
    derivePrimitive(lit, BasicSymbolsMill.INT);
  }

  @Override
  public void visit(ASTCharLiteral lit){
    derivePrimitive((ASTLiteral) lit, BasicSymbolsMill.CHAR);
  }

  @Override
  public void visit(ASTBooleanLiteral lit){
    derivePrimitive((ASTLiteral) lit, BasicSymbolsMill.BOOLEAN);
  }

  @Override
  public void visit(ASTBasicDoubleLiteral lit){
    derivePrimitive(lit, BasicSymbolsMill.DOUBLE);
  }

  @Override
  public void visit(ASTBasicFloatLiteral lit){
    derivePrimitive(lit, BasicSymbolsMill.FLOAT);
  }

  @Override
  public void visit(ASTBasicLongLiteral lit){
    derivePrimitive(lit, BasicSymbolsMill.LONG);
  }

  @Override
  public void visit(ASTStringLiteral lit){
    Optional<TypeSymbol> stringType = getScope(lit.getEnclosingScope()).resolveType("String");
    if(stringType.isPresent()){
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createTypeObject(stringType.get()));
    } else {
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0238 The type String could not be resolved.");
    }
  }

  @Override
  public void visit(ASTSignedNatLiteral lit) {
    derivePrimitive(lit, BasicSymbolsMill.INT);
  }

  @Override
  public void visit(ASTSignedBasicDoubleLiteral lit) {
    derivePrimitive(lit, BasicSymbolsMill.DOUBLE);
  }

  @Override
  public void visit(ASTSignedBasicFloatLiteral lit) {
    derivePrimitive(lit, BasicSymbolsMill.FLOAT);
  }

  @Override
  public void visit(ASTSignedBasicLongLiteral lit) {
    derivePrimitive(lit, BasicSymbolsMill.LONG);
  }

  protected void derivePrimitive(ASTLiteral lit, String primitive){
    getTypeCheckResult().setResult(getSymType(primitive, lit.get_SourcePositionStart()));
  }

  protected void derivePrimitive(ASTSignedLiteral lit, String primitive) {
    getTypeCheckResult().setResult(getSymType(primitive, lit.get_SourcePositionStart()));
  }

  /**
   * Literal "null" gets marked with implicit SymType _null
   */
  @Override
  public void visit(ASTNullLiteral lit){
    getTypeCheckResult().setResult(new SymTypeOfNull());
  }
  
}
