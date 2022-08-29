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
import de.se_rwth.commons.Joiners;
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
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    if(!innerResult.isObscureType()){
      SymTypeExpression wholeResult = calculatePlusPrefixExpression(innerResult);
      storeResultOrLogError(wholeResult, expr, "0xA0174");
    }
  }

  protected SymTypeExpression calculatePlusPrefixExpression(SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  @Override
  public void traverse(ASTMinusPrefixExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    if(!innerResult.isObscureType()){
      SymTypeExpression wholeResult = calculateMinusPrefixExpression(innerResult);
      storeResultOrLogError(wholeResult, expr, "0xA0175");
    }
  }

  protected SymTypeExpression calculateMinusPrefixExpression(SymTypeExpression innerResult) {
    return getUnaryNumericPromotionType(innerResult);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTPlusExpression expr) {
    //the "+"-operator also allows String
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculatePlusExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0210");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculatePlusExpression(ASTPlusExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return getBinaryNumericPromotionWithString(right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTMultExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateMultExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0211");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateMultExpression(ASTMultExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return getBinaryNumericPromotion(right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTDivideExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateDivideExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0212");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateDivideExpression(ASTDivideExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return getBinaryNumericPromotion(right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void endVisit(ASTMinusExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateMinusExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0213");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateMinusExpression(ASTMinusExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return getBinaryNumericPromotion(right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void endVisit(ASTModuloExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateModuloExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0214");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateModuloExpression(ASTModuloExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return getBinaryNumericPromotion(right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLessEqualExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateLessEqualExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0215");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateLessEqualExpression(ASTLessEqualExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeCompare(expr, right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTGreaterEqualExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateGreaterEqualExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0216");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateGreaterEqualExpression(ASTGreaterEqualExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeCompare(expr, right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLessThanExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateLessThanExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0217");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateLessThanExpression(ASTLessThanExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeCompare(expr, right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTGreaterThanExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateGreaterThanExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0218");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateGreaterThanExpression(ASTGreaterThanExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeCompare(expr, right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTEqualsExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateEqualsExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0219");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateEqualsExpression(ASTEqualsExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeLogical(expr, right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTNotEqualsExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateNotEqualsExpression(expr, innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0220");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateNotEqualsExpression(ASTNotEqualsExpression expr, SymTypeExpression left, SymTypeExpression right) {
    return calculateTypeLogical(expr, right, left);
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTBooleanAndOpExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateBooleanAndOpExpression(innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0223");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateBooleanAndOpExpression(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    return calculateLogicalOrOpAndOp(leftResult, rightResult);
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getLeft(), expr.getRight());
    if(checkNotObscure(innerTypes)){
      //calculate
      SymTypeExpression wholeResult = calculateBooleanOrOpExpression(innerTypes.get(0), innerTypes.get(1));
      storeResultOrLogError(wholeResult, expr, "0xA0226");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateBooleanOrOpExpression(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    return calculateLogicalOrOpAndOp(leftResult, rightResult);
  }

  protected SymTypeExpression calculateLogicalOrOpAndOp(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    if (isBoolean(leftResult) && isBoolean(rightResult)) {
      wholeResult = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    return wholeResult;
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLogicalNotExpression expr) {
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    if(!innerResult.isObscureType()) {
      SymTypeExpression wholeResult = calculateLogicalNotExpression(innerResult);
      storeResultOrLogError(wholeResult, expr, "0xA0228");
    }
  }

  protected SymTypeExpression calculateLogicalNotExpression(SymTypeExpression innerResult) {
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    if (isBoolean(innerResult)) {
      wholeResult = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    return wholeResult;
  }

  /**
   * We use traverse to collect the results of the three parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTConditionalExpression expr) {
    List<SymTypeExpression> innerTypes = calculateInnerTypes(expr.getCondition(), expr.getTrueExpression(), expr.getFalseExpression());
    if(checkNotObscure(innerTypes)){
      SymTypeExpression wholeResult = calculateConditionalExpressionType(innerTypes.get(0), innerTypes.get(1), innerTypes.get(2));
      storeResultOrLogError(wholeResult, expr, "0xA0234");
    }else{
      getTypeCheckResult().reset();
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }

  protected SymTypeExpression calculateConditionalExpressionType(SymTypeExpression conditionResult,
                                                                           SymTypeExpression trueResult,
                                                                           SymTypeExpression falseResult) {
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    //condition has to be boolean
    if (isBoolean(conditionResult)) {
      //check if "then" and "else" are either from the same type or are in sub-supertype relation
      if (compatible(trueResult, falseResult)) {
        wholeResult = trueResult;
      } else if (compatible(falseResult, trueResult)) {
        wholeResult = falseResult;
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
    SymTypeExpression innerResult = acceptThisAndReturnSymTypeExpression(expr.getExpression());
    if(!innerResult.isObscureType()) {
      SymTypeExpression wholeResult = calculateBooleanNotExpression(innerResult);
      storeResultOrLogError(wholeResult, expr, "0xA0236");
    }
  }

  protected SymTypeExpression calculateBooleanNotExpression(SymTypeExpression innerResult) {
    SymTypeExpression wholeResult = SymTypeExpressionFactory.createObscureType();
    //the inner result has to be an integral type
    if (isIntegralType(innerResult)) {
      wholeResult = getUnaryIntegralPromotionType(getTypeCheckResult().getResult());
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
    if (getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType()) {
      //store the type of the inner expression in a variable
      innerResult = getTypeCheckResult().getResult();
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
        if (getTypeCheckResult().isType()) {
          fieldSymbols = filterModifiersVariables(fieldSymbols);
        }
        if (fieldSymbols.size() != 1) {
          getTypeCheckResult().reset();
          getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
          logError("0xA1236", expr.get_SourcePositionStart());
        }
        if (!fieldSymbols.isEmpty()) {
          VariableSymbol var = fieldSymbols.get(0);
          expr.setDefiningSymbol(var);
          SymTypeExpression type = var.getType();
          getTypeCheckResult().setField();
          getTypeCheckResult().setResult(type);
        }
      } else if (typeVarOpt.isPresent()) {
        //test for type var first
        TypeVarSymbol typeVar = typeVarOpt.get();
        if(checkModifierType(typeVar)){
          SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeVariable(typeVar);
          expr.setDefiningSymbol(typeVar);
          getTypeCheckResult().setType();
          getTypeCheckResult().setResult(wholeResult);
        }else{
          getTypeCheckResult().reset();
          getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
          logError("0xA1306", expr.get_SourcePositionStart());
        }
      } else if (typeSymbolOpt.isPresent()) {
        //no variable found, test type
        TypeSymbol typeSymbol = typeSymbolOpt.get();
        if (checkModifierType(typeSymbol)) {
          SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeExpression(typeSymbol);
          expr.setDefiningSymbol(typeSymbol);
          getTypeCheckResult().setType();
          getTypeCheckResult().setResult(wholeResult);
        } else {
          getTypeCheckResult().reset();
          getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
          logError("0xA1303", expr.get_SourcePositionStart());
        }
      } else {
        getTypeCheckResult().reset();
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        logError("0xA1317", expr.get_SourcePositionStart());
      }
    } else if (!getTypeCheckResult().isPresentResult()) {
      //inner type has no result --> try to resolve a type
      String toResolve = printer.prettyprint(expr);
      Optional<TypeVarSymbol> typeVarOpt = getScope(expr.getEnclosingScope()).resolveTypeVar(toResolve);
      Optional<TypeSymbol> typeSymbolOpt = getScope(expr.getEnclosingScope()).resolveType(toResolve);
      if (typeVarOpt.isPresent()) {
        TypeVarSymbol typeVar = typeVarOpt.get();
        SymTypeExpression type = SymTypeExpressionFactory.createTypeVariable(typeVar);
        expr.setDefiningSymbol(typeVar);
        getTypeCheckResult().setType();
        getTypeCheckResult().setResult(type);
      } else if (typeSymbolOpt.isPresent()) {
        TypeSymbol typeSymbol = typeSymbolOpt.get();
        SymTypeExpression type = SymTypeExpressionFactory.createTypeExpression(typeSymbol);
        expr.setDefiningSymbol(typeSymbol);
        getTypeCheckResult().setType();
        getTypeCheckResult().setResult(type);
      } else {
        //the inner type has no result and there is no type found
        getTypeCheckResult().reset();
        Log.info("package expected", "DeriveSymTypeOfCommonExpressions");
      }
    }
  }

  /**
   * Hookpoint for object oriented languages to get the correct variables/fields from a type based on their modifiers
   */
  protected List<VariableSymbol> getCorrectFieldsFromInnerType(SymTypeExpression innerResult, ASTFieldAccessExpression expr) {
    return innerResult.getFieldList(expr.getName(), getTypeCheckResult().isType(), true);
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
    // pass our traverser/typecheckresult such that the visitor can derive types
    visitor.setTypeCheckTraverser(getTraverser());
    visitor.setTypeCheckResult(getTypeCheckResult());
    CommonExpressionsTraverser traverser = CommonExpressionsMill.traverser();
    traverser.setCommonExpressionsHandler(visitor);
    traverser.add4CommonExpressions(visitor);
    traverser.setExpressionsBasisHandler(visitor);
    traverser.add4ExpressionsBasis(visitor);
    expr.accept(traverser);
    SymTypeExpression innerResult;
    List<SymTypeExpression> args = calculateArguments(expr, visitor.getLastName());
    //make sure that the type of the last argument is not stored in the TypeCheckResult anymore
    getTypeCheckResult().reset();
    ASTExpression lastExpression = visitor.getLastExpression();
    if(lastExpression != null){
      lastExpression.accept(getTraverser());
    }else{
      expr.getExpression().accept(getTraverser());
    }
    if (getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType() && checkNotObscure(args)) {
      innerResult = getTypeCheckResult().getResult();
      //resolve methods with name of the inner expression
      List<FunctionSymbol> methodlist = getCorrectMethodsFromInnerType(innerResult, expr,
          Joiners.DOT.join(visitor.getName()));
      //count how many methods can be found with the correct arguments and return type
      List<FunctionSymbol> fittingMethods = getFittingMethods(methodlist, expr, args);
      //if the last result is static then filter for static methods
      if (getTypeCheckResult().isType()) {
        fittingMethods = filterModifiersFunctions(fittingMethods);
      }
      //there can only be one method with the correct arguments and return type
      if (!fittingMethods.isEmpty()) {
        if (fittingMethods.size() > 1) {
          checkForReturnType(expr, fittingMethods);
        }
        expr.setDefiningSymbol(fittingMethods.get(0));
        SymTypeExpression result = fittingMethods.get(0).getType();
        getTypeCheckResult().setMethod();
        getTypeCheckResult().setResult(result);
      } else {
        getTypeCheckResult().reset();
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        logError("0xA2239", expr.get_SourcePositionStart());
      }
    } else if(!getTypeCheckResult().isPresentResult()) {
      Collection<FunctionSymbol> methodcollection = getScope(expr.getEnclosingScope())
          .resolveFunctionMany(Joiners.DOT.join(visitor.getName()));
      List<FunctionSymbol> methodlist = new ArrayList<>(methodcollection);
      //count how many methods can be found with the correct arguments and return type
      List<FunctionSymbol> fittingMethods = getFittingMethods(methodlist, expr, args);
      //there can only be one method with the correct arguments and return type
      if (fittingMethods.size() == 1 && checkNotObscure(args)) {
        expr.setDefiningSymbol(fittingMethods.get(0));
        Optional<SymTypeExpression> wholeResult = Optional.of(fittingMethods.get(0).getType());
        getTypeCheckResult().setMethod();
        getTypeCheckResult().setResult(wholeResult.get());
      } else {
        getTypeCheckResult().reset();
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        logError("0xA1242", expr.get_SourcePositionStart());
      }
    }
  }

  protected List<SymTypeExpression> calculateArguments(ASTCallExpression expr, String methodName){
    List<SymTypeExpression> returnList = new ArrayList<>();
    for(int i = 0; i < expr.getArguments().sizeExpressions(); i++){
      getTypeCheckResult().reset();
      expr.getArguments().getExpression(i).accept(getTraverser());
      if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().isType()){
        returnList.add(getTypeCheckResult().getResult());
      }else{
        //Placeholder as no function can have a parameter of type void and so that the correct number of
        //SymTypeExpressions is in the list
        returnList.add(SymTypeExpressionFactory.createObscureType());
      }
    }
    return returnList;
  }

  protected void checkForReturnType(ASTCallExpression expr, List<FunctionSymbol> fittingMethods){
    SymTypeExpression returnType = fittingMethods.get(0).getType();
    for (FunctionSymbol method : fittingMethods) {
      if (!returnType.deepEquals(method.getType())) {
        getTypeCheckResult().reset();
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        logError("0xA1239", expr.get_SourcePositionStart());
      }
    }
  }

  /**
   * Hookpoint for object oriented languages to get the correct functions/methods from a type based on their modifiers
   */
  protected List<FunctionSymbol> getCorrectMethodsFromInnerType(SymTypeExpression innerResult, ASTCallExpression expr, String name) {
    return innerResult.getMethodList(name, getTypeCheckResult().isType(), true);
  }

  protected List<FunctionSymbol> getFittingMethods(List<FunctionSymbol> methodlist, ASTCallExpression expr, List<SymTypeExpression> args) {
    List<FunctionSymbol> fittingMethods = new ArrayList<>();
    for (FunctionSymbol method : methodlist) {
      //for every method found check if the arguments are correct
      if ((!method.isIsElliptic() &&
          args.size() == method.getParameterList().size())
          || (method.isIsElliptic() &&
          args.size() >= method.getParameterList().size() - 1)) {
        boolean success = true;
        for (int i = 0; i < args.size(); i++) {
          //test if every single argument is correct
          //if an argument is void type then it could not be calculated correctly -> see calculateArguments
          SymTypeExpression paramType = method.getParameterList().get(Math.min(i, method.getParameterList().size() - 1)).getType();
          if (!paramType.deepEquals(args.get(i)) &&
            !compatible(paramType, args.get(i)) || args.get(i).isVoidType()) {
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
  protected SymTypeExpression calculateTypeCompare(ASTInfixExpression expr, SymTypeExpression rightResult, SymTypeExpression leftResult) {
    // if the left and the right part of the expression are numerics,
    // then the whole expression is a boolean
    if (isNumericType(leftResult) && isNumericType(rightResult)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    //should never happen, no valid result, error will be handled in traverse
    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * helper method for the calculation of the ASTEqualsExpression and the ASTNotEqualsExpression
   */
  protected SymTypeExpression calculateTypeLogical(ASTInfixExpression expr, SymTypeExpression rightResult, SymTypeExpression leftResult) {
    //Option one: they are both numeric types
    if (isNumericType(leftResult) && isNumericType(rightResult)
        || isBoolean(leftResult) && isBoolean(rightResult)) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    //Option two: none of them is a primitive type and they are either the same type or in a super/sub type relation
    if (!leftResult.isPrimitive() && !rightResult.isPrimitive() &&
        (compatible(leftResult, rightResult) || compatible(rightResult, leftResult))
    ) {
      return SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    }
    //should never happen, no valid result, error will be handled in traverse
    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * return the result for the five basic arithmetic operations (+,-,*,/,%)
   */
  protected SymTypeExpression getBinaryNumericPromotion(SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //if one part of the expression is a double and the other is another numeric type then the result is a double
    if ((isDouble(leftResult) && isNumericType(rightResult)) ||
        (isDouble(rightResult) && isNumericType(leftResult))) {
      return SymTypeExpressionFactory.createPrimitive("double");
      //no part of the expression is a double -> try again with float
    } else if ((isFloat(leftResult) && isNumericType(rightResult)) ||
        (isFloat(rightResult) && isNumericType(leftResult))) {
      return SymTypeExpressionFactory.createPrimitive("float");
      //no part of the expression is a float -> try again with long
    } else if ((isLong(leftResult) && isNumericType(rightResult)) ||
        (isLong(rightResult) && isNumericType(leftResult))) {
      return SymTypeExpressionFactory.createPrimitive("long");
      //no part of the expression is a long -> if both parts are numeric types then the result is a int
    } else if (isIntegralType(leftResult) && isIntegralType(rightResult)
    ) {
      return SymTypeExpressionFactory.createPrimitive("int");
    }
    //should never happen, no valid result, error will be handled in traverse
    return SymTypeExpressionFactory.createObscureType();
  }

  /**
   * return the result for the "+"-operation if Strings
   */
  protected SymTypeExpression getBinaryNumericPromotionWithString(SymTypeExpression rightResult, SymTypeExpression leftResult) {
    //if one part of the expression is a String then the whole expression is a String
    if(isString(leftResult)) {
      return SymTypeExpressionFactory.createTypeObject(leftResult.getTypeInfo());
    }
    if (isString(rightResult)) {
      return SymTypeExpressionFactory.createTypeObject(rightResult.getTypeInfo());
    }
    //no String in the expression -> use the normal calculation for the basic arithmetic operators
    return getBinaryNumericPromotion(leftResult, rightResult);
  }

  /**
   * helper method for the calculation of the ASTBooleanNotExpression
   */
  protected SymTypeExpression getUnaryIntegralPromotionType(SymTypeExpression type) {
    if (!isLong(type) && type.isPrimitive() && ((SymTypePrimitive) type).isIntegralType()) {
      return SymTypeExpressionFactory.createPrimitive("int");
    }
    return SymTypeExpressionFactory.createObscureType();
  }
}
