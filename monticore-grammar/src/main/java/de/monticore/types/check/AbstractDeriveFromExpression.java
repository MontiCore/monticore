/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types.check.SymTypeConstant.unbox;
import static de.monticore.types.check.TypeCheck.isFloat;

public abstract class AbstractDeriveFromExpression {

  public abstract ExpressionsBasisTraverser getTraverser();

  public IBasicSymbolsScope getScope (IExpressionsBasisScope expressionsBasisScope){
    // is accepted only here, decided on 07.04.2020
    if(!(expressionsBasisScope instanceof IBasicSymbolsScope)){
      Log.error("0xA2307 the enclosing scope of the expression does not implement the interface IBasicSymbolsScope");
    }
    // is accepted only here, decided on 07.04.2020
    return (IBasicSymbolsScope) expressionsBasisScope;
  }

  protected TypeCheckResult typeCheckResult;

  protected static final String ERROR_MSG = " The expression at source position %s cannot be calculated.";

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
  protected Optional<SymTypeExpression> acceptThisAndReturnSymTypeExpression(ASTExpression expression){
    Optional<SymTypeExpression> result = Optional.empty();
    expression.accept(getTraverser());
    if(typeCheckResult.isPresentCurrentResult()){
      result = Optional.of(typeCheckResult.getCurrentResult());
    }
    return result;
  }

  /**
   * Helper method to calculate the SymTypeExpression of a subliteral in a traverse method
   * @param literal the literal the SymTypeExpressions is calculated for
   * @return the SymTypeExpression of the literal
   */
  protected Optional<SymTypeExpression> acceptThisAndReturnSymTypeExpression(ASTLiteral literal){
    Optional<SymTypeExpression> result = Optional.empty();
    literal.accept(getTraverser());
    if(typeCheckResult.isPresentCurrentResult()){
      result = Optional.of(typeCheckResult.getCurrentResult());
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
  protected Optional<SymTypeExpression> acceptThisAndReturnSymTypeExpressionOrLogError(ASTExpression expression, String errorCode) {
    Optional<SymTypeExpression> result = Optional.empty();
    if (expression != null) {
      result = acceptThisAndReturnSymTypeExpression(expression);
    }
    if (!result.isPresent()) {
      logError(errorCode, expression.get_SourcePositionStart());
    }
    return result;
  }

  /**
   * test if the expression is of numeric type (double, float, long, int, char, short, byte)
   */
  public boolean isNumericType(SymTypeExpression type) {
    return (TypeCheck.isDouble(type) || isFloat(type) ||
        isIntegralType(type));
  }

  /**
   * test if the expression is of integral type (long, int, char, short, byte)
   */
  public boolean isIntegralType(SymTypeExpression type) {
    return (TypeCheck.isLong(type) || TypeCheck.isInt(type) ||
        TypeCheck.isChar(type) || TypeCheck.isShort(type) ||
        TypeCheck.isByte(type));
  }

  /**
   * helper method for the calculation of the ASTBooleanNotExpression
   */
  protected Optional<SymTypeExpression> getUnaryNumericPromotionType(SymTypeExpression type) {
    if (TypeCheck.isByte(type) || TypeCheck.isShort(type) || TypeCheck.isChar(type) || TypeCheck.isInt(type)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
    }
    if (TypeCheck.isLong(type) || TypeCheck.isDouble(type) || isFloat(type)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(unbox(type.print())));
    }
    return Optional.empty();
  }


}
