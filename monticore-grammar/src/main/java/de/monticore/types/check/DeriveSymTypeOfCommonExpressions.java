/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.typesymbols._symboltable.*;
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

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTPlusExpression expr) {
    //the "+"-operator also allows String
    Optional<SymTypeExpression> wholeResult = getBinaryNumericPromotionWithString(expr, expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0210", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTMultExpression expr) {
    Optional<SymTypeExpression> wholeResult = getBinaryNumericPromotion(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0211", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTDivideExpression expr) {
    Optional<SymTypeExpression> wholeResult = getBinaryNumericPromotion(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0212", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void endVisit(ASTMinusExpression expr) {
    Optional<SymTypeExpression> wholeResult = getBinaryNumericPromotion(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0213", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void endVisit(ASTModuloExpression expr) {
    Optional<SymTypeExpression> wholeResult = getBinaryNumericPromotion(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0214", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLessEqualExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeCompare(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0215", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTGreaterEqualExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeCompare(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0216", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLessThanExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeCompare(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0217", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTGreaterThanExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeCompare(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0218", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTEqualsExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeLogical(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0219", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTNotEqualsExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateTypeLogical(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0220", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTBooleanAndOpExpression expr) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    if (expr.getLeft() != null) {
      expr.getLeft().accept(getRealThis());
    }
    if (typeCheckResult.isPresentLast()) {
      //store result of the left part of the expression
      leftResult = typeCheckResult.getLast();
    } else {
      logError("0xA0221", expr.getLeft().get_SourcePositionStart());
    }
    if (expr.getRight() != null) {
      expr.getRight().accept(getRealThis());
    }
    if (typeCheckResult.isPresentLast()) {
      //store result of the right part of the expression
      rightResult = typeCheckResult.getLast();
    } else {
      logError("0xA0222", expr.getRight().get_SourcePositionStart());
    }

    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if (isBoolean(leftResult) && isBoolean(rightResult)) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0223", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void endVisit(ASTBooleanOrOpExpression expr) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    if (expr.getLeft() != null) {
      expr.getLeft().accept(getRealThis());
    }
    if (typeCheckResult.isPresentLast()) {
      //store result of the left part of the expression
      leftResult = typeCheckResult.getLast();
    } else {
      logError("0xA0224", expr.getLeft().get_SourcePositionStart());
    }
    if (expr.getRight() != null) {
      expr.getRight().accept(getRealThis());
    }
    if (typeCheckResult.isPresentLast()) {
      //store result of the right part of the expression
      rightResult = typeCheckResult.getLast();
    } else {
      logError("0xA0225", expr.getRight().get_SourcePositionStart());
    }

    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if (isBoolean(leftResult) && isBoolean(rightResult)) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0226", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLogicalNotExpression expr) {
    SymTypeExpression innerResult = null;
    if (expr.getExpression() != null) {
      expr.getExpression().accept(getRealThis());
    }
    if (typeCheckResult.isPresentLast()) {
      //store result of the inner part of the expression
      innerResult = typeCheckResult.getLast();
    } else {
      logError("0xA0227", expr.getExpression().get_SourcePositionStart());
    }
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if (isBoolean(innerResult)) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0228", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTBracketExpression expr) {
    SymTypeExpression innerResult = null;
    if (expr.getExpression() != null) {
      expr.getExpression().accept(getRealThis());
    }
    if (typeCheckResult.isPresentLast()) {
      //store result of the inner part of the expression
      innerResult = typeCheckResult.getLast();
    } else {
      logError("0xA0229", expr.getExpression().get_SourcePositionStart());
    }
    Optional<SymTypeExpression> wholeResult = Optional.of(innerResult);
    if (wholeResult.isPresent()) {
      //store the result of the whole expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0230", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the results of the three parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTConditionalExpression expr) {
    SymTypeExpression conditionResult = null;
    SymTypeExpression trueResult = null;
    SymTypeExpression falseResult = null;

    if (expr.getCondition() != null) {
      expr.getCondition().accept(getRealThis());
    }
    if (typeCheckResult.isPresentLast()) {
      //store the type of the "if" in a variable
      conditionResult = typeCheckResult.getLast();
    } else {
      logError("0xA0231", expr.getCondition().get_SourcePositionStart());
    }
    if (expr.getTrueExpression() != null) {
      expr.getTrueExpression().accept(getRealThis());
    }
    if (typeCheckResult.isPresentLast()) {
      //store the type of the "then" in a variable
      trueResult = typeCheckResult.getLast();
    } else {
      logError("0xA0232", expr.getTrueExpression().get_SourcePositionStart());
    }
    if (expr.getFalseExpression() != null) {
      expr.getFalseExpression().accept(getRealThis());
    }
    if (typeCheckResult.isPresentLast()) {
      //store the type of the "else" in a variable
      falseResult = typeCheckResult.getLast();
    } else {
      logError("0xA0233", expr.getFalseExpression().get_SourcePositionStart());
    }
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    //condition has to be boolean
    if (isBoolean(conditionResult)) {
      //check if "then" and "else" are either from the same type or are in sub-supertype relation
      if (trueResult.print().equals(falseResult.print())) {
        wholeResult = Optional.of(trueResult);
      } else if (isSubtypeOf(falseResult, trueResult)) {
        wholeResult = Optional.of(trueResult);
      } else if (isSubtypeOf(trueResult, falseResult)) {
        wholeResult = Optional.of(falseResult);
      } else {
        wholeResult = getBinaryNumericPromotion(expr.getTrueExpression(), expr.getFalseExpression());
      }
    }
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0234", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTBooleanNotExpression expr) {
    SymTypeExpression innerResult = null;
    if (expr.getExpression() != null) {
      expr.getExpression().accept(getRealThis());
    }
    if (typeCheckResult.isPresentLast()) {
      //store result of the inner part of the expression
      innerResult = typeCheckResult.getLast();
    } else {
      logError("0xA0235", expr.getExpression().get_SourcePositionStart());
    }
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    //the inner result has to be an integral type
    if (innerResult instanceof SymTypeConstant && ((SymTypeConstant) innerResult).isIntegralType()) {
      wholeResult = getUnaryIntegralPromotionType(typeCheckResult.getLast());
    }
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      typeCheckResult.setLast(wholeResult.get());
    } else {
      typeCheckResult.reset();
      logError("0xA0236", expr.get_SourcePositionStart());
    }
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTFieldAccessExpression expr) {
    CommonExpressionsPrettyPrinter printer = new CommonExpressionsPrettyPrinter(new IndentPrinter());
    SymTypeExpression innerResult;
    expr.getExpression().accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      //store the type of the inner expression in a variable
      innerResult = typeCheckResult.getLast();
      //look for this type in our scope
      TypeSymbol innerResultType = innerResult.getTypeInfo();
      //search for a method, field or type in the scope of the type of the inner expression
      List<FieldSymbol> fieldSymbols = innerResult.getFieldList(expr.getName(), typeCheckResult.isType());
      Optional<TypeSymbol> typeSymbolOpt = innerResultType.getSpannedScope().resolveType(expr.getName());
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
          typeCheckResult.setLast(type);
        }
      } else if (typeSymbolOpt.isPresent()) {
        //no variable found, test type
        TypeSymbol typeSymbol = typeSymbolOpt.get();
        boolean match = true;
        //if the last result is a type and the type is not static then it is not accessible
        if(typeCheckResult.isType()&&!typeSymbol.isIsStatic()){
          match = false;
        }
        if(match){
          SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeExpression(typeSymbol.getName(), typeSymbol.getEnclosingScope());
          typeCheckResult.setType();
          typeCheckResult.setLast(wholeResult);
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
      Optional<TypeSymbol> typeSymbolOpt = getScope(expr.getEnclosingScope()).resolveType(toResolve);
      if (typeSymbolOpt.isPresent()) {
        TypeSymbol typeSymbol = typeSymbolOpt.get();
        SymTypeExpression type = SymTypeExpressionFactory.createTypeExpression(typeSymbol.getName(), typeSymbol.getEnclosingScope());
        typeCheckResult.setType();
        typeCheckResult.setLast(type);
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
    if (typeCheckResult.isPresentLast()) {
      innerResult = typeCheckResult.getLast();
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
            if (!returnType.print().equals(method.getReturnType().print())) {
              logError("0xA0238", expr.get_SourcePositionStart());
            }
          }
        }
        SymTypeExpression result = fittingMethods.get(0).getReturnType();
        typeCheckResult.setMethod();
        typeCheckResult.setLast(result);
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
        typeCheckResult.setLast(wholeResult.get());
      } else {
        typeCheckResult.reset();
        logError("0xA0240", expr.get_SourcePositionStart());
      }
    }
  }

  List<MethodSymbol> getFittingMethods(List<MethodSymbol> methodlist, ASTCallExpression expr){
    List<MethodSymbol> fittingMethods = new ArrayList<>();
    for (MethodSymbol method : methodlist) {
      //for every method found check if the arguments are correct
      if (expr.getArguments().getExpressionList().size() == method.getParameterList().size()) {
        boolean success = true;
        for (int i = 0; i < method.getParameterList().size(); i++) {
          expr.getArguments().getExpression(i).accept(getRealThis());
          //test if every single argument is correct
          if (!method.getParameterList().get(i).getType().print().equals(typeCheckResult.getLast().print()) &&
              !compatible(typeCheckResult.getLast(), method.getParameterList().get(i).getType())) {
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
  private Optional<SymTypeExpression> calculateTypeCompare(ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    if (left != null) {
      left.accept(getRealThis());
      if (typeCheckResult.isPresentLast()) {
        //store result of the left part of the expression
        leftResult = typeCheckResult.getLast();
      } else {
        logError("0xA0241", left.get_SourcePositionStart());
      }
    }
    if (right != null) {
      right.accept(getRealThis());
      if (typeCheckResult.isPresentLast()) {
        //store result of the right part of the expression
        rightResult = typeCheckResult.getLast();
      } else {
        logError("0xA0242", right.get_SourcePositionStart());
      }
    }
    //if the left and the right part of the expression are numerics, then the whole expression is a boolean
    if (isNumericType(leftResult) && isNumericType(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    //should never happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }

  /**
   * helper method for the calculation of the ASTEqualsExpression and the ASTNotEqualsExpression
   */
  private Optional<SymTypeExpression> calculateTypeLogical(ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    if (left != null) {
      left.accept(getRealThis());
      if (typeCheckResult.isPresentLast()) {
        //store result of the left part of the expression
        leftResult = typeCheckResult.getLast();
      } else {
        logError("0xA0244", left.get_SourcePositionStart());
      }
    }
    if (right != null) {
      right.accept(getRealThis());
      if (typeCheckResult.isPresentLast()) {
        //store result of the right part of the expression
        rightResult = typeCheckResult.getLast();
      } else {
        logError("0xA0245", right.get_SourcePositionStart());
      }
    }

    //Option one: they are both numeric types or they are both booleans
    if (isNumericType(leftResult) && isNumericType(rightResult)
        || isBoolean(leftResult) && isBoolean(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    //Option two: none of them is a primitive type and they are either the same type or in a super/sub type relation
    if (!isPrimitiveType(leftResult) && !isPrimitiveType(rightResult) &&
        (leftResult.print().equals(rightResult.print())
            || isSubtypeOf(rightResult, leftResult)
            || isSubtypeOf(leftResult, rightResult)
        )) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    //should never happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }

  /**
   * return the result for the five basic arithmetic operations (+,-,*,/,%)
   */
  protected Optional<SymTypeExpression> getBinaryNumericPromotion(ASTExpression leftType,
                                                                  ASTExpression rightType) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    if (leftType != null) {
      leftType.accept(getRealThis());
      if (typeCheckResult.isPresentLast()) {
        //store result of the left part of the expression
        leftResult = typeCheckResult.getLast();
      } else {
        logError("0xA0246", leftType.get_SourcePositionStart());
      }
    }
    if (rightType != null) {
      rightType.accept(getRealThis());
      if (typeCheckResult.isPresentLast()) {
        //store result of the right part of the expression
        rightResult = typeCheckResult.getLast();
      } else {
        logError("0xA0247", rightType.get_SourcePositionStart());
      }
    }

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
    } else {
      if (leftResult.isPrimitive()&&((SymTypeConstant)leftResult).isIntegralType()
          &&rightResult.isPrimitive()&&((SymTypeConstant)rightResult).isIntegralType()
      ) {
        return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
      }
    }
    //should never happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }

  /**
   * return the result for the "+"-operation if Strings
   */
  public Optional<SymTypeExpression> getBinaryNumericPromotionWithString(ASTExpression expr, ASTExpression leftType,
                                                                         ASTExpression rightType) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;

    if (leftType != null) {
      leftType.accept(getRealThis());
      if (typeCheckResult.isPresentLast()) {
        //store result of the left part of the expression
        leftResult = typeCheckResult.getLast();
      } else {
        logError("0xA0248", leftType.get_SourcePositionStart());
      }
    }
    if (rightType != null) {
      rightType.accept(getRealThis());
      if (typeCheckResult.isPresentLast()) {
        //store result of the right part of the expression
        rightResult = typeCheckResult.getLast();
      } else {
        logError("0xA0249", rightType.get_SourcePositionStart());
      }
    }
    //if one part of the expression is a String then the whole expression is a String
    if (isString(leftResult) || isString(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeObject("String", getScope(expr.getEnclosingScope())));
    }
    //no String in the expression -> use the normal calculation for the basic arithmetic operators
    return getBinaryNumericPromotion(leftType, rightType);
  }

  /**
   * test if the expression is of numeric type (double, float, long, int, char, short, byte)
   */
  private boolean isNumericType(SymTypeExpression ex) {
    return (isDouble(ex) || isFloat(ex) ||
        isLong(ex) || isInt(ex) ||
        isChar(ex) || isShort(ex) ||
        isByte(ex)
    );
  }

  /**
   * test if the expression is a primitive type
   */
  private boolean isPrimitiveType(SymTypeExpression ex) {
    return isNumericType(ex) || isBoolean(ex);
  }

  /**
   * helper method for the calculation of the ASTBooleanNotExpression
   */
  public static Optional<SymTypeExpression> getUnaryIntegralPromotionType(SymTypeExpression type) {
    if(!isLong(type)&&type.isPrimitive()&&((SymTypeConstant)type).isIntegralType()){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
    }
    return Optional.empty();
  }

}
