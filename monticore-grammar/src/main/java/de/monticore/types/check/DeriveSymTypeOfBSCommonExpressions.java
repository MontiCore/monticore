/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.CommonExpressionsFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.TypeCheck.*;

public class DeriveSymTypeOfBSCommonExpressions extends AbstractDeriveFromExpression implements CommonExpressionsVisitor2, CommonExpressionsHandler {

  protected CommonExpressionsTraverser traverser;

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ASTPlusPrefixExpression expr) {
    Optional<SymTypeExpression> innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    Optional<SymTypeExpression> wholeResult = innerResult.isPresent() ?
      calculatePlusPrefixExpression(innerResult.get()) : Optional.empty();
    storeResultOrLogError(wholeResult, expr, "0xA0174");
  }

  protected Optional<SymTypeExpression> calculatePlusPrefixExpression(SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  @Override
  public void traverse(ASTMinusPrefixExpression expr) {
    Optional<SymTypeExpression> innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    Optional<SymTypeExpression> wholeResult = innerResult.isPresent() ?
      calculateMinusPrefixExpression(innerResult.get()) : Optional.empty();
    storeResultOrLogError(wholeResult, expr, "0xA0175");
  }

  protected Optional<SymTypeExpression> calculateMinusPrefixExpression(SymTypeExpression innerResult) {
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
    return getBinaryNumericPromotionWithString(expr.getRight(), expr.getLeft());
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
    Optional<SymTypeExpression> leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getLeft(), "0xA0221");
    Optional<SymTypeExpression> rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getRight(), "0xA0222");

    Optional<SymTypeExpression> wholeResult = (leftResult.isPresent() && rightResult.isPresent()) ?
      calculateBooleanAndOpExpression(leftResult.get(), rightResult.get()) : Optional.empty();
    storeResultOrLogError(wholeResult, expr, "0xA0223");
  }

  protected Optional<SymTypeExpression> calculateBooleanAndOpExpression(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if (isBoolean(leftResult) && isBoolean(rightResult)) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.BOOLEAN));
    }
    return wholeResult;
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression expr) {
    Optional<SymTypeExpression> leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getLeft(), "0xA0224");
    Optional<SymTypeExpression> rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getRight(), "0xA0225");

    Optional<SymTypeExpression> wholeResult = (leftResult.isPresent() && rightResult.isPresent()) ?
      calculateBooleanOrOpExpression(leftResult.get(), rightResult.get()) : Optional.empty();
    storeResultOrLogError(wholeResult, expr, "0xA0226");
  }

  protected Optional<SymTypeExpression> calculateBooleanOrOpExpression(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if (isBoolean(leftResult) && isBoolean(rightResult)) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.BOOLEAN));
    }
    return wholeResult;
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLogicalNotExpression expr) {
    Optional<SymTypeExpression> innerResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getExpression(), "0xA0227");
    Optional<SymTypeExpression> wholeResult = innerResult.isPresent() ?
      calculateLogicalNotExpression(innerResult.get()) : Optional.empty();
    storeResultOrLogError(wholeResult, expr, "0xA0228");
  }

  protected Optional<SymTypeExpression> calculateLogicalNotExpression(SymTypeExpression innerResult) {
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if (isBoolean(innerResult)) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.BOOLEAN));
    }
    return wholeResult;
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTBracketExpression expr) {
    Optional<SymTypeExpression> result = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getExpression(), "0xA0229");
    storeResultOrLogError(result, expr, "0xA0230");
  }

  /**
   * We use traverse to collect the results of the three parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTConditionalExpression expr) {
    Optional<SymTypeExpression> conditionResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getCondition(), "0xA0231");
    Optional<SymTypeExpression> trueResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getTrueExpression(), "0xA0232");
    Optional<SymTypeExpression> falseResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getFalseExpression(), "0xA0233");
    Optional<SymTypeExpression> wholeResult = (conditionResult.isPresent() && trueResult.isPresent() && falseResult.isPresent()) ?
      calculateConditionalExpressionType(conditionResult.get(), trueResult.get(), falseResult.get()) : Optional.empty();
    storeResultOrLogError(wholeResult, expr, "0xA0234");
  }

  protected Optional<SymTypeExpression> calculateConditionalExpressionType(SymTypeExpression conditionResult,
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
    Optional<SymTypeExpression> innerResult = acceptThisAndReturnSymTypeExpressionOrLogError(expr.getExpression(), "0xA0235");
    Optional<SymTypeExpression> wholeResult = innerResult.isPresent() ?
      calculateBooleanNotExpression(innerResult.get()) : Optional.empty();
    storeResultOrLogError(wholeResult, expr, "0xA0236");
  }

  protected Optional<SymTypeExpression> calculateBooleanNotExpression(SymTypeExpression innerResult) {
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
    CommonExpressionsFullPrettyPrinter printer = new CommonExpressionsFullPrettyPrinter(new IndentPrinter());
    SymTypeExpression innerResult;
    expr.getExpression().accept(getTraverser());
    if (typeCheckResult.isPresentCurrentResult()) {
      //store the type of the inner expression in a variable
      innerResult = typeCheckResult.getCurrentResult();
      //look for this type in our scope
      TypeSymbol innerResultType = innerResult.getTypeInfo();
      if (innerResultType instanceof TypeVarSymbol) {
        Log.error("0xA0321 The type " + innerResultType.getName() + " is a type variable and cannot have methods and attributes");
      }
      //search for a method, field or type in the scope of the type of the inner expression
      List<VariableSymbol> fieldSymbols = getCorrectFieldsFromInnerType(innerResult, expr);
      Optional<TypeSymbol> typeSymbolOpt = innerResultType.getSpannedScope().resolveType(expr.getName());
      Optional<TypeVarSymbol> typeVarOpt = innerResultType.getSpannedScope().resolveTypeVar(expr.getName());
      if (!fieldSymbols.isEmpty()) {
        //cannot be a method, test variable first
        //durch AST-Umbau kann ASTFieldAccessExpression keine Methode sein
        //if the last result is a type then filter for static field symbols
        if (typeCheckResult.isType()) {
          fieldSymbols = filterModifiersVariables(fieldSymbols);
        }
        if (fieldSymbols.size() != 1) {
          typeCheckResult.reset();
          logError("0xA1236", expr.get_SourcePositionStart());
        }
        if (!fieldSymbols.isEmpty()) {
          VariableSymbol var = fieldSymbols.get(0);
          SymTypeExpression type = var.getType();
          typeCheckResult.setField();
          typeCheckResult.setCurrentResult(type);
        }
      } else if (typeVarOpt.isPresent()) {
        //test for type var first
        TypeVarSymbol typeVar = typeVarOpt.get();
        if(checkModifierType(typeVar)){
          SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeVariable(typeVar);
          typeCheckResult.setType();
          typeCheckResult.setCurrentResult(wholeResult);
        }else{
          typeCheckResult.reset();
          logError("0xA1306", expr.get_SourcePositionStart());
        }
      } else if (typeSymbolOpt.isPresent()) {
        //no variable found, test type
        TypeSymbol typeSymbol = typeSymbolOpt.get();
        if (checkModifierType(typeSymbol)) {
          SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeExpression(typeSymbol);
          typeCheckResult.setType();
          typeCheckResult.setCurrentResult(wholeResult);
        } else {
          typeCheckResult.reset();
          logError("0xA1303", expr.get_SourcePositionStart());
        }
      } else {
        typeCheckResult.reset();
        logError("0xA1317", expr.get_SourcePositionStart());
      }
    } else {
      //inner type has no result --> try to resolve a type
      String toResolve = printer.prettyprint(expr);
      Optional<TypeVarSymbol> typeVarOpt = getScope(expr.getEnclosingScope()).resolveTypeVar(toResolve);
      Optional<TypeSymbol> typeSymbolOpt = getScope(expr.getEnclosingScope()).resolveType(toResolve);
      if (typeVarOpt.isPresent()) {
        TypeVarSymbol typeVar = typeVarOpt.get();
        SymTypeExpression type = SymTypeExpressionFactory.createTypeVariable(typeVar);
        typeCheckResult.setType();
        typeCheckResult.setCurrentResult(type);
      } else if (typeSymbolOpt.isPresent()) {
        TypeSymbol typeSymbol = typeSymbolOpt.get();
        SymTypeExpression type = SymTypeExpressionFactory.createTypeExpression(typeSymbol);
        typeCheckResult.setType();
        typeCheckResult.setCurrentResult(type);
      } else {
        //the inner type has no result and there is no type found
        typeCheckResult.reset();
        Log.info("package expected", "DeriveSymTypeOfCommonExpressions");
      }
    }
  }

  /**
   * Hookpoint for object oriented languages to get the correct variables/fields from a type based on their modifiers
   */
  protected List<VariableSymbol> getCorrectFieldsFromInnerType(SymTypeExpression innerResult, ASTFieldAccessExpression expr) {
    return innerResult.getFieldList(expr.getName(), typeCheckResult.isType(), true);
  }

  /**
   * Hookpoint for object oriented languages that offer modifiers like static, public, private, ...
   */
  protected boolean checkModifierType(TypeSymbol typeSymbol){
    return true;
  }

  /**
   * Hookpoint for object oriented languages that offer modifiers like static, public, private, ...
   */
  protected List<VariableSymbol> filterModifiersVariables(List<VariableSymbol> variableSymbols) {
    return variableSymbols;
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTCallExpression expr) {
    NameToCallExpressionVisitor visitor = new NameToCallExpressionVisitor();
    CommonExpressionsTraverser traverser = CommonExpressionsMill.traverser();
    traverser.setCommonExpressionsHandler(visitor);
    traverser.add4CommonExpressions(visitor);
    traverser.setExpressionsBasisHandler(visitor);
    traverser.add4ExpressionsBasis(visitor);
    expr.accept(traverser);
    SymTypeExpression innerResult;
    ASTExpression lastExpression = visitor.getLastExpression();
    if(lastExpression != null){
      lastExpression.accept(getTraverser());
    }else{
      expr.getExpression().accept(getTraverser());
    }
    if (typeCheckResult.isPresentCurrentResult()) {
      innerResult = typeCheckResult.getCurrentResult();
      //resolve methods with name of the inner expression
      List<FunctionSymbol> methodlist = getCorrectMethodsFromInnerType(innerResult, expr, visitor.getLastName());
      //count how many methods can be found with the correct arguments and return type
      List<FunctionSymbol> fittingMethods = getFittingMethods(methodlist, expr);
      //if the last result is static then filter for static methods
      if (typeCheckResult.isType()) {
        fittingMethods = filterModifiersFunctions(fittingMethods);
      }
      //there can only be one method with the correct arguments and return type
      if (!fittingMethods.isEmpty()) {
        if (fittingMethods.size() > 1) {
          checkForReturnType(expr, fittingMethods);
        }
        SymTypeExpression result = fittingMethods.get(0).getType();
        typeCheckResult.setMethod();
        typeCheckResult.setCurrentResult(result);
      } else {
        typeCheckResult.reset();
        logError("0xA2239", expr.get_SourcePositionStart());
      }
    } else {
      Collection<FunctionSymbol> methodcollection = getScope(expr.getEnclosingScope()).resolveFunctionMany(visitor.getLastName());
      List<FunctionSymbol> methodlist = new ArrayList<>(methodcollection);
      //count how many methods can be found with the correct arguments and return type
      List<FunctionSymbol> fittingMethods = getFittingMethods(methodlist, expr);
      //there can only be one method with the correct arguments and return type
      if (fittingMethods.size() == 1) {
        Optional<SymTypeExpression> wholeResult = Optional.of(fittingMethods.get(0).getType());
        typeCheckResult.setMethod();
        typeCheckResult.setCurrentResult(wholeResult.get());
      } else {
        typeCheckResult.reset();
        logError("0xA1242", expr.get_SourcePositionStart());
      }
    }
  }

  protected void checkForReturnType(ASTCallExpression expr, List<FunctionSymbol> fittingMethods){
    SymTypeExpression type = fittingMethods.get(0).getType();
    for (FunctionSymbol method : fittingMethods) {
      if (!type.deepEquals(method.getType())) {
        logError("0xA1239", expr.get_SourcePositionStart());
      }
    }
  }

  /**
   * Hookpoint for object oriented languages to get the correct functions/methods from a type based on their modifiers
   */
  protected List<FunctionSymbol> getCorrectMethodsFromInnerType(SymTypeExpression innerResult, ASTCallExpression expr, String name) {
    return innerResult.getMethodList(name, typeCheckResult.isType(), true);
  }

  protected List<FunctionSymbol> getFittingMethods(List<FunctionSymbol> methodlist, ASTCallExpression expr) {
    List<FunctionSymbol> fittingMethods = new ArrayList<>();
    for (FunctionSymbol method : methodlist) {
      //for every method found check if the arguments are correct
      if (expr.getArguments().getExpressionList().size() == method.getParameterList().size()) {
        boolean success = true;
        for (int i = 0; i < method.getParameterList().size(); i++) {
          expr.getArguments().getExpression(i).accept(getTraverser());
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

  /**
   * Hookpoint for object oriented languages that offer modifiers like static, public, private, ...
   */
  protected List<FunctionSymbol> filterModifiersFunctions(List<FunctionSymbol> functionSymbols) {
    return functionSymbols;
  }

  /**
   * helper method for <=, >=, <, > -> calculates the result of these expressions
   */
  protected Optional<SymTypeExpression> calculateTypeCompare(ASTInfixExpression expr, ASTExpression right, ASTExpression left) {
    Optional<SymTypeExpression> leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA2241");
    Optional<SymTypeExpression> rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA2244");
    if (leftResult.isPresent() && rightResult.isPresent()) {
      return calculateTypeCompare(expr, rightResult.get(), leftResult.get());
    } else {
      typeCheckResult.reset();
      return Optional.empty();
    }
  }

  /**
   * helper method for <=, >=, <, > -> calculates the result of these expressions
   */
  protected Optional<SymTypeExpression> calculateTypeCompare(ASTInfixExpression expr, SymTypeExpression rightResult, SymTypeExpression leftResult) {
    // if the left and the right part of the expression are numerics,
    // then the whole expression is a boolean
    if (isNumericType(leftResult) && isNumericType(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.BOOLEAN));
    }
    //should never happen, no valid result, error will be handled in traverse
    typeCheckResult.reset();
    return Optional.empty();
  }

  /**
   * helper method for the calculation of the ASTEqualsExpression and the ASTNotEqualsExpression
   */
  protected Optional<SymTypeExpression> calculateTypeLogical(ASTInfixExpression expr, ASTExpression right, ASTExpression left) {
    Optional<SymTypeExpression> leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA2247");
    Optional<SymTypeExpression> rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right, "0xA2248");
    if (leftResult.isPresent() && rightResult.isPresent()) {
      return calculateTypeLogical(expr, rightResult.get(), leftResult.get());
    } else {
      typeCheckResult.reset();
      return Optional.empty();
    }
  }

  /**
   * helper method for the calculation of the ASTEqualsExpression and the ASTNotEqualsExpression
   */
  protected Optional<SymTypeExpression> calculateTypeLogical(ASTInfixExpression expr, SymTypeExpression rightResult, SymTypeExpression leftResult) {
    //Option one: they are both numeric types
    if (isNumericType(leftResult) && isNumericType(rightResult)
        || isBoolean(leftResult) && isBoolean(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.BOOLEAN));
    }
    //Option two: none of them is a primitive type and they are either the same type or in a super/sub type relation
    if (!leftResult.isTypeConstant() && !rightResult.isTypeConstant() &&
        (compatible(leftResult, rightResult) || compatible(rightResult, leftResult))
    ) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(BasicSymbolsMill.BOOLEAN));
    }
    //should never happen, no valid result, error will be handled in traverse
    typeCheckResult.reset();
    return Optional.empty();
  }

  /**
   * return the result for the five basic arithmetic operations (+,-,*,/,%)
   */
  protected Optional<SymTypeExpression> getBinaryNumericPromotion(ASTExpression leftType,
                                                                  ASTExpression rightType) {
    Optional<SymTypeExpression> leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(leftType, "0xA0246");
    Optional<SymTypeExpression> rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(rightType, "0xA0247");
    if (leftResult.isPresent() && rightResult.isPresent()) {
      return getBinaryNumericPromotion(leftResult.get(), rightResult.get());
    } else {
      typeCheckResult.reset();
      return Optional.empty();
    }
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
    typeCheckResult.reset();
    return Optional.empty();
  }

  /**
   * return the result for the "+"-operation if Strings
   */
  protected Optional<SymTypeExpression> getBinaryNumericPromotionWithString(ASTExpression rightType, ASTExpression leftType) {
    Optional<SymTypeExpression> leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(leftType, "0xA0248");
    Optional<SymTypeExpression> rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(rightType, "0xA0249");
    if (leftResult.isPresent() && rightResult.isPresent()) {
      return getBinaryNumericPromotionWithString(rightResult.get(), leftResult.get());
    } else {
      typeCheckResult.reset();
      return Optional.empty();
    }
  }

  /**
   * return the result for the "+"-operation if Strings
   */
  protected Optional<SymTypeExpression> getBinaryNumericPromotionWithString(SymTypeExpression rightResult, SymTypeExpression leftResult) {
    //if one part of the expression is a String then the whole expression is a String
    if(isString(leftResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeObject(leftResult.getTypeInfo()));
    }
    if (isString(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeObject(rightResult.getTypeInfo()));
    }
    //no String in the expression -> use the normal calculation for the basic arithmetic operators
    return getBinaryNumericPromotion(leftResult, rightResult);
  }

  /**
   * helper method for the calculation of the ASTBooleanNotExpression
   */
  protected Optional<SymTypeExpression> getUnaryIntegralPromotionType(SymTypeExpression type) {
    if (!isLong(type) && type.isTypeConstant() && ((SymTypeConstant) type).isIntegralType()) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
    }
    return Optional.empty();
  }
}
