/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    if(checkInt(lit.getSource(), lit.get_SourcePositionStart())){
      getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.INT));
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
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
    if(checkDouble(lit.getSource(), lit.get_SourcePositionStart())){
      getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.DOUBLE));
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  @Override
  public void visit(ASTBasicFloatLiteral lit){
    if(checkFloat(lit.getSource().substring(0, lit.getSource().length()-1), lit.get_SourcePositionStart())){
      getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.FLOAT));
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  @Override
  public void visit(ASTBasicLongLiteral lit){
    if(checkLong(lit.getSource().substring(0, lit.getSource().length()-1), lit.get_SourcePositionStart())){
      getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.LONG));
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
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
    if(checkInt(lit.getSource(), lit.get_SourcePositionStart())){
      getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.INT));
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  @Override
  public void visit(ASTSignedBasicDoubleLiteral lit) {
    if(checkDouble(lit.getSource(), lit.get_SourcePositionStart())){
      getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.DOUBLE));
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  @Override
  public void visit(ASTSignedBasicFloatLiteral lit) {
    if(checkFloat(lit.getSource().substring(0, lit.getSource().length()-1), lit.get_SourcePositionStart())){
      getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.FLOAT));
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  @Override
  public void visit(ASTSignedBasicLongLiteral lit) {
    if(checkLong(lit.getSource().substring(0, lit.getSource().length()-1), lit.get_SourcePositionStart())){
      getTypeCheckResult().setResult(getSymType(BasicSymbolsMill.LONG));
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  /**
   * Literal "null" gets marked with implicit SymType _null
   */
  @Override
  public void visit(ASTNullLiteral lit){
    getTypeCheckResult().setResult(new SymTypeOfNull());
  }

  protected boolean checkLong(String source, SourcePosition pos){
    BigInteger max_long = BasicSymbolsMill.getTypeCheckSettings().getMAX_LONG();
    BigInteger min_long = BasicSymbolsMill.getTypeCheckSettings().getMIN_LONG();
    BigInteger litValue = new BigInteger(source);

    if(litValue.compareTo(min_long) < 0 || litValue.compareTo(max_long) > 0) {
      Log.error(String.format("0xA0209 number %s not in long range [%s,%s]", litValue, min_long, max_long),
        pos);
      return false;
    }
    return true;
  }

  protected boolean checkInt(String source, SourcePosition pos){
    BigInteger max_int = BasicSymbolsMill.getTypeCheckSettings().getMAX_INT();
    BigInteger min_int = BasicSymbolsMill.getTypeCheckSettings().getMIN_INT();
    BigInteger litValue = new BigInteger(source);

    if(litValue.compareTo(min_int) < 0 || litValue.compareTo(max_int) > 0) {
      Log.error(String.format("0xA0208 number %s not in int range [%s,%s]", litValue, min_int, max_int),
        pos);
      return false;
    }
    return true;
  }

  protected boolean checkDouble(String source, SourcePosition pos){
    BigDecimal max_double = BasicSymbolsMill.getTypeCheckSettings().getMAX_DOUBLE();
    BigDecimal min_double = BasicSymbolsMill.getTypeCheckSettings().getMIN_DOUBLE();
    BigDecimal litValue = new BigDecimal(source);

    if(litValue.compareTo(min_double) < 0 || litValue.compareTo(max_double) > 0) {
      Log.error(String.format("0xA0211 number %s not in double range [%s,%s]", litValue, min_double, max_double),
        pos);
      return false;
    }
    return true;
  }

  protected boolean checkFloat(String source, SourcePosition pos){
    BigDecimal max_float = BasicSymbolsMill.getTypeCheckSettings().getMAX_FLOAT();
    BigDecimal min_float = BasicSymbolsMill.getTypeCheckSettings().getMIN_FLOAT();
    BigDecimal litValue = new BigDecimal(source);

    if(litValue.compareTo(min_float) < 0 || litValue.compareTo(max_float) > 0) {
      Log.error(String.format("0xA0210 number %s not in float range [%s,%s]", litValue, min_float, max_float),
        pos);
      return false;
    }
    return true;
  }
  
}
