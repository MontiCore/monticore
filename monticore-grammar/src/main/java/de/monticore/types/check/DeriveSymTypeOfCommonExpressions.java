/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.types.check.TypeCheck.*;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in CommonExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfCommonExpressions extends DeriveSymTypeOfExpression implements CommonExpressionsVisitor {

  private CommonExpressionsVisitor realThis;

  @Override
  public void setRealThis(CommonExpressionsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public CommonExpressionsVisitor getRealThis() {
    return realThis;
  }

  public DeriveSymTypeOfCommonExpressions() {
    realThis = this;
  }

  @Override
  public void traverse(ASTPlusPrefixExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    Optional<SymTypeExpression> wholeResult = calculatePlusPrefixExpression(expr, innerResult);
    storeResultOrLogError(wholeResult, expr, "0xA0174");
  }

  protected Optional<SymTypeExpression> calculatePlusPrefixExpression(ASTPlusPrefixExpression expr, SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  @Override
  public void traverse(ASTMinusPrefixExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    Optional<SymTypeExpression> wholeResult = calculateMinusPrefixExpression(expr, innerResult);
    storeResultOrLogError(wholeResult, expr, "0xA0175");
  }

  protected Optional<SymTypeExpression> calculateMinusPrefixExpression(ASTMinusPrefixExpression expr, SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }


  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTPlusExpression expr) {
    //the "+"-operator also allows String
    Optional<SymTypeExpression> wholeResult = calculatePlusExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0210");
  }

  protected Optional<SymTypeExpression> calculatePlusExpression(ASTPlusExpression expr) {
    return getBinaryNumericPromotionWithString(expr, expr.getRight(), expr.getLeft());
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTMultExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateMultExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0211");
  }

  protected Optional<SymTypeExpression> calculateMultExpression(ASTMultExpression expr) {
    return getBinaryNumericPromotion(expr.getRight(), expr.getLeft());
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTDivideExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateDivideExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0212");
  }

  protected Optional<SymTypeExpression> calculateDivideExpression(ASTDivideExpression expr) {
    return getBinaryNumericPromotion(expr.getRight(), expr.getLeft());
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void endVisit(ASTMinusExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateMinusExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0213");
  }

  protected Optional<SymTypeExpression> calculateMinusExpression(ASTMinusExpression expr) {
    return getBinaryNumericPromotion(expr.getRight(), expr.getLeft());
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void endVisit(ASTModuloExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateModuloExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0214");
  }

  protected Optional<SymTypeExpression> calculateModuloExpression(ASTModuloExpression expr) {
    return getBinaryNumericPromotion(expr.getRight(), expr.getLeft());
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLessEqualExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateLessEqualExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0215");
  }

  protected Optional<SymTypeExpression> calculateLessEqualExpression(ASTLessEqualExpression expr) {
    return calculateTypeCompare(expr, expr.getRight(), expr.getLeft());
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTGreaterEqualExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateGreaterEqualExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0216");
  }

  protected Optional<SymTypeExpression> calculateGreaterEqualExpression(ASTGreaterEqualExpression expr) {
    return calculateTypeCompare(expr, expr.getRight(), expr.getLeft());
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLessThanExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateLessThanExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0217");
  }

  protected Optional<SymTypeExpression> calculateLessThanExpression(ASTLessThanExpression expr) {
    return calculateTypeCompare(expr, expr.getRight(), expr.getLeft());
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTGreaterThanExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateGreaterThanExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0218");
  }

  protected Optional<SymTypeExpression> calculateGreaterThanExpression(ASTGreaterThanExpression expr) {
    return calculateTypeCompare(expr, expr.getRight(), expr.getLeft());
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTEqualsExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateEqualsExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0219");
  }

  protected Optional<SymTypeExpression> calculateEqualsExpression(ASTEqualsExpression expr) {
    return calculateTypeLogical(expr, expr.getRight(), expr.getLeft());
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTNotEqualsExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateNotEqualsExpression(expr);
    storeResultOrLogError(wholeResult, expr, "0xA0220");
  }

  protected Optional<SymTypeExpression> calculateNotEqualsExpression(ASTNotEqualsExpression expr) {
    return calculateTypeLogical(expr, expr.getRight(), expr.getLeft());
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTBooleanAndOpExpression expr) {
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getLeft(), "0xA0221");
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getRight(), "0xA0222");

    Optional<SymTypeExpression> wholeResult = calculateBooleanAndOpExpression(expr, leftResult, rightResult);
    storeResultOrLogError(wholeResult, expr, "0xA0223");
  }

  protected Optional<SymTypeExpression> calculateBooleanAndOpExpression(ASTBooleanAndOpExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if (isBoolean(leftResult) && isBoolean(rightResult)) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    return wholeResult;
  }


  @Override
  public void endVisit(ASTBooleanOrOpExpression expr) {
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getLeft(), "0xA0224");
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getRight(), "0xA0225");

    Optional<SymTypeExpression> wholeResult = calculateBooleanOrOpExpression(expr, leftResult, rightResult);
    storeResultOrLogError(wholeResult, expr, "0xA0226");
  }

  protected Optional<SymTypeExpression> calculateBooleanOrOpExpression(ASTBooleanOrOpExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if (isBoolean(leftResult) && isBoolean(rightResult)) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    return wholeResult;
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLogicalNotExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getExpression(), "0xA0227");
    Optional<SymTypeExpression> wholeResult = calculateLogicalNotExpression(expr, innerResult);
    storeResultOrLogError(wholeResult, expr, "0xA0228");
  }

  protected Optional<SymTypeExpression> calculateLogicalNotExpression(ASTLogicalNotExpression expr, SymTypeExpression innerResult) {
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if (isBoolean(innerResult)) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    return wholeResult;
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTBracketExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getExpression(), "0xA0229");
    Optional<SymTypeExpression> wholeResult = calculateBracketExpression(expr, innerResult);
    storeResultOrLogError(wholeResult, expr, "0xA0230");
  }

  protected Optional<SymTypeExpression> calculateBracketExpression(ASTBracketExpression expr, SymTypeExpression innerResult) {
    Optional<SymTypeExpression> wholeResult = Optional.of(innerResult);
    return wholeResult;
  }

  /**
   * We use traverse to collect the results of the three parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTConditionalExpression expr) {
    SymTypeExpression conditionResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getCondition(), "0xA0231");
    SymTypeExpression trueResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getTrueExpression(), "0xA0232");
    SymTypeExpression falseResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getFalseExpression(), "0xA0233");
    Optional<SymTypeExpression> wholeResult = calculateConditionalExpressionType(expr, conditionResult, trueResult, falseResult);
    storeResultOrLogError(wholeResult, expr, "0xA0234");
  }

  protected Optional<SymTypeExpression> calculateConditionalExpressionType(ASTConditionalExpression expr,
                                                                           SymTypeExpression conditionResult,
                                                                           SymTypeExpression trueResult,
                                                                           SymTypeExpression falseResult) {
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    //condition has to be boolean
    if (isBoolean(conditionResult)) {
      //check if "then" and "else" are either from the same type or are in sub-supertype relation
      if (compatible(trueResult, falseResult)) {
        wholeResult = Optional.of(trueResult);
      } else if (compatible(falseResult, trueResult)) {
        wholeResult = Optional.of(falseResult);
      } else {
        // first argument can be null since it should not be relevant to the type calculation
        wholeResult = getBinaryNumericPromotion(trueResult, falseResult);
      }
    }
    return wholeResult;
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTBooleanNotExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getExpression(), "0xA0235");
    Optional<SymTypeExpression> wholeResult = calculateBooleanNotExpression(expr, innerResult);
    storeResultOrLogError(wholeResult, expr, "0xA0236");
  }

  protected Optional<SymTypeExpression> calculateBooleanNotExpression(ASTBooleanNotExpression expr, SymTypeExpression innerResult) {
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    //the inner result has to be an integral type
    if (isIntegralType(innerResult)) {
      wholeResult = getUnaryIntegralPromotionType(typeCheckResult.getCurrentResult());
    }
    return wholeResult;
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTFieldAccessExpression expr) {
    CommonExpressionsPrettyPrinter printer = new CommonExpressionsPrettyPrinter(new IndentPrinter());
    SymTypeExpression innerResult;
    expr.getExpression().accept(getRealThis());
    if (typeCheckResult.isPresentCurrentResult()) {
      //store the type of the inner expression in a variable
      innerResult = typeCheckResult.getCurrentResult();
      //look for this type in our scope
      OOTypeSymbol innerResultType = innerResult.getTypeInfo();
      //search for a method, field or type in the scope of the type of the inner expression
      List<FieldSymbol> fieldSymbols = innerResult.getFieldList(expr.getName(), typeCheckResult.isType());
      Optional<OOTypeSymbol> typeSymbolOpt = innerResultType.getSpannedScope().resolveOOType(expr.getName());
      if (!fieldSymbols.isEmpty()) {
        //cannot be a method, test variable first
        //durch AST-Umbau kann ASTFieldAccessExpression keine Methode sein
        //if the last result is a type then filter for static field symbols
        if(typeCheckResult.isType()){
          fieldSymbols = filterStaticFieldSymbols(fieldSymbols);
        }
        if (fieldSymbols.size() != 1) {
          typeCheckResult.reset();
          logError("0xA0237", expr.get_SourcePositionStart());
        }
        if(!fieldSymbols.isEmpty()) {
          FieldSymbol var = fieldSymbols.get(0);
          SymTypeExpression type = var.getType();
          typeCheckResult.setField();
          typeCheckResult.setCurrentResult(type);
        }
      } else if (typeSymbolOpt.isPresent()) {
        //no variable found, test type
        OOTypeSymbol typeSymbol = typeSymbolOpt.get();
        boolean match = true;
        //if the last result is a type and the type is not static then it is not accessible
        if(typeCheckResult.isType()&&!typeSymbol.isIsStatic()){
          match = false;
        }
        if(match){
          SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeExpression(typeSymbol.getName(), typeSymbol.getEnclosingScope());
          typeCheckResult.setType();
          typeCheckResult.setCurrentResult(wholeResult);
        }else{
          typeCheckResult.reset();
          logError("0xA0303", expr.get_SourcePositionStart());
        }
      }else{
        typeCheckResult.reset();
        logError("0xA0306", expr.get_SourcePositionStart());
      }
    } else {
      //inner type has no result --> try to resolve a type
      String toResolve = printer.prettyprint(expr);
      Optional<OOTypeSymbol> typeSymbolOpt = getScope(expr.getEnclosingScope()).resolveOOType(toResolve);
      if (typeSymbolOpt.isPresent()) {
        OOTypeSymbol typeSymbol = typeSymbolOpt.get();
        SymTypeExpression type = SymTypeExpressionFactory.createTypeExpression(typeSymbol.getName(), typeSymbol.getEnclosingScope());
        typeCheckResult.setType();
        typeCheckResult.setCurrentResult(type);
      } else {
        //the inner type has no result and there is no type found
        typeCheckResult.reset();
        Log.info("package suspected", "DeriveSymTypeOfCommonExpressions");
      }
    }
  }

  private List<FieldSymbol> filterStaticFieldSymbols(List<FieldSymbol> fieldSymbols) {
    return fieldSymbols.stream().filter(FieldSymbol::isIsStatic).collect(Collectors.toList());
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTCallExpression expr) {
    NameToCallExpressionVisitor visitor = new NameToCallExpressionVisitor();
    expr.accept(visitor);
    SymTypeExpression innerResult;
    expr.getExpression().accept(getRealThis());
    if (typeCheckResult.isPresentCurrentResult()) {
      innerResult = typeCheckResult.getCurrentResult();
      //resolve methods with name of the inner expression
      List<MethodSymbol> methodlist = innerResult.getMethodList(expr.getName(), typeCheckResult.isType());
      //count how many methods can be found with the correct arguments and return type
      List<MethodSymbol> fittingMethods = getFittingMethods(methodlist,expr);
      //if the last result is static then filter for static methods
      if(typeCheckResult.isType()){
        fittingMethods = filterStaticMethodSymbols(fittingMethods);
      }
      //there can only be one method with the correct arguments and return type
      if (!fittingMethods.isEmpty()) {
        if (fittingMethods.size() > 1) {
          SymTypeExpression returnType = fittingMethods.get(0).getReturnType();
          for (MethodSymbol method : fittingMethods) {
            if (!returnType.deepEquals(method.getReturnType())) {
              logError("0xA0238", expr.get_SourcePositionStart());
            }
          }
        }
        SymTypeExpression result = fittingMethods.get(0).getReturnType();
        typeCheckResult.setMethod();
        typeCheckResult.setCurrentResult(result);
      } else {
        typeCheckResult.reset();
        logError("0xA0239", expr.get_SourcePositionStart());
      }
    } else {
      Collection<MethodSymbol> methodcollection = getScope(expr.getEnclosingScope()).resolveMethodMany(expr.getName());
      List<MethodSymbol> methodlist = new ArrayList<>(methodcollection);
      //count how many methods can be found with the correct arguments and return type
      List<MethodSymbol> fittingMethods = getFittingMethods(methodlist,expr);
      //there can only be one method with the correct arguments and return type
      if (fittingMethods.size() == 1) {
        Optional<SymTypeExpression> wholeResult = Optional.of(fittingMethods.get(0).getReturnType());
        typeCheckResult.setMethod();
        typeCheckResult.setCurrentResult(wholeResult.get());
      } else {
        typeCheckResult.reset();
        logError("0xA0240", expr.get_SourcePositionStart());
      }
    }
  }

  private List<MethodSymbol> getFittingMethods(List<MethodSymbol> methodlist, ASTCallExpression expr){
    List<MethodSymbol> fittingMethods = new ArrayList<>();
    for (MethodSymbol method : methodlist) {
      //for every method found check if the arguments are correct
      if (expr.getArguments().getExpressionsList().size() == method.getParameterList().size()) {
        boolean success = true;
        for (int i = 0; i < method.getParameterList().size(); i++) {
          expr.getArguments().getExpressions(i).accept(getRealThis());
          //test if every single argument is correct
          if (!method.getParameterList().get(i).getType().deepEquals(typeCheckResult.getCurrentResult()) &&
              !compatible(method.getParameterList().get(i).getType(), typeCheckResult.getCurrentResult())) {
            success = false;
          }
        }
        if (success) {
          //method has the correct arguments and return type
          fittingMethods.add(method);
        }
      }
    }
    return fittingMethods;
  }

  private List<MethodSymbol> filterStaticMethodSymbols(List<MethodSymbol> fittingMethods) {
      return fittingMethods.stream().filter(MethodSymbol::isIsStatic).collect(Collectors.toList());
  }

  /**
   * helper method for <=, >=, <, > -> calculates the result of these expressions
   */
  private Optional<SymTypeExpression> calculateTypeCompare(ASTInfixExpression expr, ASTExpression right, ASTExpression left) {
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0241");
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA0242");
    return calculateTypeCompare(expr, rightResult, leftResult);
  }

  /**
   * helper method for <=, >=, <, > -> calculates the result of these expressions
   */
  protected Optional<SymTypeExpression> calculateTypeCompare(ASTInfixExpression expr, SymTypeExpression rightResult, SymTypeExpression leftResult) {
    // if the left and the right part of the expression are numerics,
    // then the whole expression is a boolean
    if (isNumericType(leftResult) && isNumericType(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    //should never happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }

  /**
   * helper method for the calculation of the ASTEqualsExpression and the ASTNotEqualsExpression
   */
  private Optional<SymTypeExpression> calculateTypeLogical(ASTInfixExpression expr, ASTExpression right, ASTExpression left) {
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0244");
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA0245");
    return calculateTypeLogical(expr, rightResult, leftResult);
  }

  /**
   * helper method for the calculation of the ASTEqualsExpression and the ASTNotEqualsExpression
   */
  protected Optional<SymTypeExpression> calculateTypeLogical(ASTInfixExpression expr, SymTypeExpression rightResult, SymTypeExpression leftResult) {
    //Option one: they are both numeric types
    if (isNumericType(leftResult) && isNumericType(rightResult)
        || isBoolean(leftResult) && isBoolean(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    //Option two: none of them is a primitive type and they are either the same type or in a super/sub type relation
    if (!leftResult.isTypeConstant() && !rightResult.isTypeConstant() &&
        (compatible(leftResult, rightResult) || compatible(rightResult, leftResult))
    ) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    //should never happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }

  /**
   * return the result for the five basic arithmetic operations (+,-,*,/,%)
   */
  private Optional<SymTypeExpression> getBinaryNumericPromotion(ASTExpression leftType,
                                                                ASTExpression rightType) {
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(leftType, "0xA0246");
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(rightType, "0xA0247");

    return getBinaryNumericPromotion(leftResult, rightResult);
  }

  /**
   * return the result for the five basic arithmetic operations (+,-,*,/,%)
   */
  protected Optional<SymTypeExpression> getBinaryNumericPromotion(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //if one part of the expression is a double and the other is another numeric type then the result is a double
    if ((isDouble(leftResult) && isNumericType(rightResult)) ||
        (isDouble(rightResult) && isNumericType(leftResult))) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("double"));
      //no part of the expression is a double -> try again with float
    } else if ((isFloat(leftResult) && isNumericType(rightResult)) ||
        (isFloat(rightResult) && isNumericType(leftResult))) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("float"));
      //no part of the expression is a float -> try again with long
    } else if ((isLong(leftResult) && isNumericType(rightResult)) ||
        (isLong(rightResult) && isNumericType(leftResult))) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("long"));
      //no part of the expression is a long -> if both parts are numeric types then the result is a int
    } else if (leftResult.isTypeConstant() && isIntegralType(leftResult)
        && rightResult.isTypeConstant() && isIntegralType(rightResult)
    ) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
    }
    //should never happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }

  /**
   * return the result for the "+"-operation if Strings
   */
  private Optional<SymTypeExpression> getBinaryNumericPromotionWithString(ASTExpression expr, ASTExpression rightType, ASTExpression leftType) {
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(leftType, "0xA0248");
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(rightType, "0xA0249");
    return getBinaryNumericPromotionWithString(expr, rightResult, leftResult);
  }

  /**
   * return the result for the "+"-operation if Strings
   */
  protected Optional<SymTypeExpression> getBinaryNumericPromotionWithString(ASTExpression expr, SymTypeExpression rightResult, SymTypeExpression leftResult) {

    //if one part of the expression is a String then the whole expression is a String
    if (isString(leftResult) || isString(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeObject("String", getScope(expr.getEnclosingScope())));
    }
    //no String in the expression -> use the normal calculation for the basic arithmetic operators
    return getBinaryNumericPromotion(leftResult, rightResult);
  }

  /**
   * helper method for the calculation of the ASTBooleanNotExpression
   */
  protected Optional<SymTypeExpression> getUnaryIntegralPromotionType(SymTypeExpression type) {
    if(!isLong(type)&&type.isTypeConstant()&&((SymTypeConstant)type).isIntegralType()){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
    }
    return Optional.empty();
  }

}
