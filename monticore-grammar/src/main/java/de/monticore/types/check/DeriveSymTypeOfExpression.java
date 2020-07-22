/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.*;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static de.monticore.types.check.SymTypeConstant.unbox;
import static de.monticore.types.check.SymTypeExpressionFactory.*;
import static de.monticore.types.check.TypeCheck.*;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in ExpressionsBasis
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfExpression implements ExpressionsBasisVisitor {

  public IOOSymbolsScope getScope (IExpressionsBasisScope expressionsBasisScope){
    // is accepted only here, decided on 07.04.2020
    if(!(expressionsBasisScope instanceof IOOSymbolsScope)){
      Log.error("0xA0307 the enclosing scope of the expression does not implement the interface IOOSymbolsScope");
    }
    // is accepted only here, decided on 07.04.2020
    return (IOOSymbolsScope) expressionsBasisScope;
  }

  protected TypeCheckResult typeCheckResult;

  private ExpressionsBasisVisitor realThis;

  protected static final String ERROR_MSG = " The expression at source position %s cannot be calculated.";

  @Override
  public void setRealThis(ExpressionsBasisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public ExpressionsBasisVisitor getRealThis() {
    return realThis;
  }

  public DeriveSymTypeOfExpression() {
    realThis = this;
  }

  @Override
  public void traverse(ASTLiteralExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateLiteralExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0250");
  }

  protected Optional<SymTypeExpression> calculateLiteralExpression(ASTLiteralExpression expr){
    //get the type of the literal
    SymTypeExpression wholeResult = acceptThisAndReturnSymTypeExpression(expr.getLiteral());
    return Optional.of(wholeResult);
  }

  @Override
  public void traverse(ASTNameExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateNameExpression(expr);
    if(wholeResult.isPresent()){
      typeCheckResult.setCurrentResult(wholeResult.get());
    }else{
     //name not found --> package or nothing
     typeCheckResult.reset();
     Log.info("package suspected", "DeriveSymTypeOfExpression");
    }
  }

  protected Optional<SymTypeExpression> calculateNameExpression(ASTNameExpression expr){
    Optional<FieldSymbol> optVar = getScope(expr.getEnclosingScope()).resolveField(expr.getName());
    Optional<OOTypeSymbol> optType = getScope(expr.getEnclosingScope()).resolveOOType(expr.getName());
    if (optVar.isPresent()) {
      //no method here, test variable first
      // durch AST-Umbau kann ASTNameExpression keine Methode sein
      FieldSymbol var = optVar.get();
      SymTypeExpression res = var.getType().deepClone();
      typeCheckResult.setField();
      return Optional.of(res);
    } else if (optType.isPresent()) {
      //no variable found, test if name is type
      OOTypeSymbol type = optType.get();
      SymTypeExpression res = createTypeExpression(type.getName(), type.getEnclosingScope());
      typeCheckResult.setType();
      return Optional.of(res);
    }
    return Optional.empty();
  }

  public void setTypeCheckResult(TypeCheckResult typeCheckResult) {
    this.typeCheckResult = typeCheckResult;
  }

  protected void logError(String errorCode, SourcePosition start){
    typeCheckResult.reset();
    Log.error(errorCode+String.format(ERROR_MSG, start));
  }

  /**
   * Helper method to store the calculated result or log an error if it is not present
   * @param result the calculated result
   * @param expression the expression the SymTypeExpressions is calculated for
   * @param errorCode the code which is logged in case of an error
   */
  protected void storeResultOrLogError(Optional<SymTypeExpression> result, ASTExpression expression, String errorCode){
    if(result.isPresent()){
      //store the result of the expression in the last result
      typeCheckResult.setCurrentResult(result.get());
    }else{
      typeCheckResult.reset();
      logError(errorCode, expression.get_SourcePositionStart());
    }
  }

  /**
   * Helper method to calculate the SymTypeExpression of a subexpression in a traverse method
   * @param expression the expression the SymTypeExpressions is calculated for
   * @return the SymTypeExpression of the expression
   */
  protected SymTypeExpression acceptThisAndReturnSymTypeExpression(ASTExpression expression){
    SymTypeExpression result = null;
    expression.accept(getRealThis());
    if(typeCheckResult.isPresentCurrentResult()){
      result = typeCheckResult.getCurrentResult();
    }
    return result;
  }

  /**
   * Helper method to calculate the SymTypeExpression of a subliteral in a traverse method
   * @param literal the literal the SymTypeExpressions is calculated for
   * @return the SymTypeExpression of the literal
   */
  protected SymTypeExpression acceptThisAndReturnSymTypeExpression(ASTLiteral literal){
    SymTypeExpression result = null;
    literal.accept(getRealThis());
    if(typeCheckResult.isPresentCurrentResult()){
      result = typeCheckResult.getCurrentResult();
    }
    return result;
  }

  /**
   * Helper method to calculate the SymTypeExpression of a subexpression in a traverse method or log
   *  an error if it could not be calculated
   * @param expression the expression the SymTypeExpressions is calculated for
   * @param errorCode the code which is logged in case of an error
   * @return the SymTypeExpression of the expression
   */
  protected SymTypeExpression acceptThisAndReturnSymTypeExpressionOrLogError(ASTExpression expression, String errorCode) {
    SymTypeExpression result = null;
    if (expression != null) {
      result = acceptThisAndReturnSymTypeExpression(expression);
    }
    if (result == null) {
      logError(errorCode, expression.get_SourcePositionStart());
    }
    return result;
  }

  /**
   * test if the expression is of numeric type (double, float, long, int, char, short, byte)
   */
  public boolean isNumericType(SymTypeExpression type) {
    return (isDouble(type) || isFloat(type) ||
       isIntegralType(type));
  }

  /**
   * test if the expression is of integral type (long, int, char, short, byte)
   */
  public boolean isIntegralType(SymTypeExpression type) {
    return (isLong(type) || isInt(type) ||
        isChar(type) || isShort(type) ||
        isByte(type));
  }

  /**
   * helper method for the calculation of the ASTBooleanNotExpression
   */
  protected Optional<SymTypeExpression> getUnaryNumericPromotionType(SymTypeExpression type) {
    if (isByte(type) || isShort(type) || isChar(type) || isInt(type)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
    }
    if (isLong(type) || isDouble(type) || isFloat(type)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(unbox(type.print())));
    }
    return Optional.empty();
  }

}
