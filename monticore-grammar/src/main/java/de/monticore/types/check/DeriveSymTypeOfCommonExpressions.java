/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
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

import static de.monticore.types.check.SymTypeConstant.unbox;
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
    Optional<SymTypeExpression> wholeResult = getBinaryNumericPromotionWithString(expr.getLeft(), expr.getRight());
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0210 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0211 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0212 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0213 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0214 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0215 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0216 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0217 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0218 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0219 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0220 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
    if (lastResult.isPresentLast()) {
      //store result of the left part of the expression
      leftResult = lastResult.getLast();
    } else {
      Log.error("0xA0221 The resulting type of "+prettyPrinter.prettyprint(expr.getLeft())+" cannot be calculated");

    }
    if (expr.getRight() != null) {
      expr.getRight().accept(getRealThis());
    }
    if (lastResult.isPresentLast()) {
      //store result of the right part of the expression
      rightResult = lastResult.getLast();
    } else {
      Log.error("0xA0222 The resulting type of "+prettyPrinter.prettyprint(expr.getRight())+" cannot be calculated");
    }

    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if ("boolean".equals(leftResult.print()) && "boolean".equals(rightResult.print())) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0223 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
    if (lastResult.isPresentLast()) {
      //store result of the left part of the expression
      leftResult = lastResult.getLast();
    } else {
      Log.error("0xA0224 The resulting type of "+prettyPrinter.prettyprint(expr.getLeft())+" cannot be calculated");
    }
    if (expr.getRight() != null) {
      expr.getRight().accept(getRealThis());
    }
    if (lastResult.isPresentLast()) {
      //store result of the right part of the expression
      rightResult = lastResult.getLast();
    } else {
      Log.error("0xA0225 The resulting type of "+prettyPrinter.prettyprint(expr.getRight())+" cannot be calculated");
    }

    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if ("boolean".equals(leftResult.print()) && "boolean".equals(rightResult.print())) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0226 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
    if (lastResult.isPresentLast()) {
      //store result of the inner part of the expression
      innerResult = lastResult.getLast();
    } else {
      Log.error("0xA0227 The resulting type of "+prettyPrinter.prettyprint(expr.getExpression())+" cannot be calculated");
    }
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if ("boolean".equals(innerResult.print())) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0228 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
    if (lastResult.isPresentLast()) {
      //store result of the inner part of the expression
      innerResult = lastResult.getLast();
    } else {
      Log.error("0xA0229 The resulting type of "+prettyPrinter.prettyprint(expr.getExpression())+" cannot be calculated");
    }
    Optional<SymTypeExpression> wholeResult = Optional.of(innerResult);
    if (wholeResult.isPresent()) {
      //store the result of the whole expression in the last result
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0230 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
    if (lastResult.isPresentLast()) {
      //store the type of the "if" in a variable
      conditionResult = lastResult.getLast();
    } else {
      Log.error("0xA0231 The resulting type of "+prettyPrinter.prettyprint(expr.getCondition())+" cannot be calculated");
    }
    if (expr.getTrueExpression() != null) {
      expr.getTrueExpression().accept(getRealThis());
    }
    if (lastResult.isPresentLast()) {
      //store the type of the "then" in a variable
      trueResult = lastResult.getLast();
    } else {
      Log.error("0xA0232 The resulting type of "+prettyPrinter.prettyprint(expr.getTrueExpression())+" cannot be calculated");
    }
    if (expr.getFalseExpression() != null) {
      expr.getFalseExpression().accept(getRealThis());
    }
    if (lastResult.isPresentLast()) {
      //store the type of the "else" in a variable
      falseResult = lastResult.getLast();
    } else {
      Log.error("0xA0233 The resulting type of "+prettyPrinter.prettyprint(expr.getFalseExpression())+" cannot be calculated");
    }
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    //condition has to be boolean
    if ("boolean".equals(conditionResult.print())) {
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
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0234 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
    if (lastResult.isPresentLast()) {
      //store result of the inner part of the expression
      innerResult = lastResult.getLast();
    } else {
      Log.error("0xA0235 The resulting type of "+prettyPrinter.prettyprint(expr.getExpression())+" cannot be calculated");
    }
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    //the inner result has to be an integral type
    if (innerResult instanceof SymTypeConstant && ((SymTypeConstant) innerResult).isIntegralType()) {
      wholeResult = getUnaryIntegralPromotionType(lastResult.getLast());
    }
    if (wholeResult.isPresent()) {
      //store the result of the expression in the last result
      lastResult.setLast(wholeResult.get());
      this.result = wholeResult.get();
    } else {
      lastResult.reset();
      Log.error("0xA0236 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
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
    if (lastResult.isPresentLast()) {
      //store the type of the inner expression in a variable
      innerResult = lastResult.getLast();
      //look for this type in our scope
      TypeSymbol innerResultType = innerResult.getTypeInfo();
      //search for a method, field or type in the scope of the type of the inner expression
      List<FieldSymbol> fieldSymbols = innerResult.getFieldList(expr.getName());
      Optional<TypeSymbol> typeSymbolOpt = innerResultType.getSpannedScope().resolveType(expr.getName());
      if (!fieldSymbols.isEmpty()) {
        //cannot be a method, test variable first
        //durch AST-Umbau kann ASTFieldAccessExpression keine Methode sein
        //if the last result is a type then filter for static field symbols
        if(lastResult.isType()){
          fieldSymbols = filterStaticFieldSymbols(fieldSymbols);
        }
        if (fieldSymbols.size() != 1) {
          Log.error("0xA0237 There cannot be more than one attribute with the same name in your field");
        }
        FieldSymbol var = fieldSymbols.get(0);
        SymTypeExpression type = var.getType();
        this.result = type;
        lastResult.setField();
        lastResult.setLast(type);
      } else if (typeSymbolOpt.isPresent()) {
        //no variable found, test type
        TypeSymbol typeSymbol = typeSymbolOpt.get();
        boolean match = true;
        //if the last result is a type and the type is not static then it is not accessible
        if(lastResult.isType()&&!typeSymbol.isIsStatic()){
          match = false;
        }
        if(match){
          SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeExpression(typeSymbol.getFullName(), expr.getEnclosingScope());
          this.result = wholeResult;
          lastResult.setType();
          lastResult.setLast(wholeResult);
        }else{
          lastResult.reset();
          Log.error("0xA0303 the type "+typeSymbol.getName()+" must be static to be accessible in a type");
        }
      }
    } else {
      //inner type has no result --> try to resolve a type
      String toResolve = printer.prettyprint(expr);
      Optional<TypeSymbol> typeSymbolOpt = scope.resolveType(toResolve);
      if (typeSymbolOpt.isPresent()) {
        TypeSymbol typeSymbol = typeSymbolOpt.get();
        SymTypeExpression type = SymTypeExpressionFactory.createTypeExpression(typeSymbol.getFullName(), typeSymbol.getEnclosingScope());
        this.result = type;
        lastResult.setMethod();
        lastResult.setLast(type);
      } else {
        //the inner type has no result and there is no type found
        lastResult.reset();
        Log.info("package suspected", "CommonExpressionsTypesCalculator");
      }
    }
  }

  private List<FieldSymbol> filterStaticFieldSymbols(List<FieldSymbol> fieldSymbols) {
    return fieldSymbols.stream().filter(FieldSymbolTOP::isIsStatic).collect(Collectors.toList());
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
    if (lastResult.isPresentLast()) {
      innerResult = lastResult.getLast();
      //resolve methods with name of the inner expression
      List<MethodSymbol> methodlist = innerResult.getMethodList(expr.getName());
      //count how many methods can be found with the correct arguments and return type
      List<MethodSymbol> fittingMethods = new ArrayList<>();
      for (MethodSymbol method : methodlist) {
        //for every method found check if the arguments are correct
        if (expr.getArguments().getExpressionList().size() == method.getParameterList().size()) {
          boolean success = true;
          for (int i = 0; i < method.getParameterList().size(); i++) {
            expr.getArguments().getExpression(i).accept(getRealThis());
            //test if every single argument is correct
            if (!method.getParameterList().get(i).getType().print().equals(lastResult.getLast().print()) &&
                !compatible(lastResult.getLast(), method.getParameterList().get(i).getType())) {
              success = false;
            }
          }
          if (success) {
            //method has the correct arguments and return type
            fittingMethods.add(method);
          }
        }
      }
      //if the last result is static then filter for static methods
      if(lastResult.isType()){
        fittingMethods = filterStaticMethodSymbols(fittingMethods);
      }
      //there can only be one method with the correct arguments and return type
      if (!fittingMethods.isEmpty()) {
        if (fittingMethods.size() > 1) {
          SymTypeExpression returnType = fittingMethods.get(0).getReturnType();
          for (MethodSymbol method : fittingMethods) {
            if (!returnType.print().equals(method.getReturnType().print())) {
              Log.error("0xA0238 The fitting methods need to have the same return type");
            }
          }
        }
        SymTypeExpression result = fittingMethods.get(0).getReturnType();
        this.result = result;
        lastResult.setMethod();
        lastResult.setLast(result);
      } else {
        lastResult.reset();
        Log.error("0xA0239 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
      }
    } else {
      Collection<MethodSymbol> methodcollection = scope.resolveMethodMany(expr.getName());
      List<MethodSymbol> methodlist = new ArrayList<>(methodcollection);
      //count how many methods can be found with the correct arguments and return type
      List<MethodSymbol> fittingMethods = new ArrayList<>();
      for (MethodSymbol method : methodlist) {
        //for every method found check if the arguments are correct
        if (expr.getArguments().getExpressionList().size() == method.getParameterList().size()) {
          boolean success = true;
          for (int i = 0; i < method.getParameterList().size(); i++) {
            expr.getArguments().getExpression(i).accept(getRealThis());
            //test if every single argument is correct
            if (!method.getParameterList().get(i).getType().print().equals(lastResult.getLast().print()) &&
                !compatible(lastResult.getLast(), method.getParameterList().get(i).getType())) {
              success = false;
            }
          }
          if (success) {
            //method has the correct arguments and return type
            fittingMethods.add(method);
          }
        }
      }
      //there can only be one method with the correct arguments and return type
      if (fittingMethods.size() == 1) {
        if (!"void".equals(fittingMethods.get(0).getReturnType().print())) {
          SymTypeExpression result = fittingMethods.get(0).getReturnType();
          this.result = result;
          lastResult.setMethod();
          lastResult.setLast(result);
        } else {
          Optional<SymTypeExpression> wholeResult = Optional.of(SymTypeExpressionFactory.createTypeVoid());
          this.result = wholeResult.get();
          lastResult.setLast(wholeResult.get());
        }
      } else {
        lastResult.reset();
        Log.error("0xA0240 The resulting type of "+prettyPrinter.prettyprint(expr)+" cannot be calculated");
      }
    }
  }

  private List<MethodSymbol> filterStaticMethodSymbols(List<MethodSymbol> fittingMethods) {
      return fittingMethods.stream().filter(MethodSymbolTOP::isIsStatic).collect(Collectors.toList());
  }

  /**
   * helper method for <=, >=, <, > -> calculates the result of these expressions
   */
  private Optional<SymTypeExpression> calculateTypeCompare(ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    if (left != null) {
      left.accept(getRealThis());
    }
    if (lastResult.isPresentLast()) {
      //store result of the left part of the expression
      leftResult = lastResult.getLast();
    } else {
      Log.error("0xA0241 The resulting type of "+prettyPrinter.prettyprint(left)+" cannot be calculated");
    }
    if (right != null) {
      right.accept(getRealThis());
    }
    if (lastResult.isPresentLast()) {
      //store result of the right part of the expression
      rightResult = lastResult.getLast();
    } else {
      Log.error("0xA0242 The resulting type of "+prettyPrinter.prettyprint(right)+" cannot be calculated");
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
    }
    if (lastResult.isPresentLast()) {
      //store result of the left part of the expression
      leftResult = lastResult.getLast();
    } else {
      Log.error("0xA0244 The resulting type of "+prettyPrinter.prettyprint(left)+" cannot be calculated");
    }
    if (right != null) {
      right.accept(getRealThis());
    }
    if (lastResult.isPresentLast()) {
      //store result of the right part of the expression
      rightResult = lastResult.getLast();
    } else {
      Log.error("0xA0245 The resulting type of "+prettyPrinter.prettyprint(right)+" cannot be calculated");
    }
    //Option one: they are both numeric types or they are both booleans
    if (isNumericType(leftResult) && isNumericType(rightResult)
        || "boolean".equals(leftResult.print()) && "boolean".equals(rightResult.print())) {
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
    }
    if (lastResult.isPresentLast()) {
      //store result of the left part of the expression
      leftResult = lastResult.getLast();
    } else {
      Log.error("0xA0246 The resulting type of "+prettyPrinter.prettyprint(leftType)+" cannot be calculated");
    }
    if (rightType != null) {
      rightType.accept(getRealThis());
    }
    if (lastResult.isPresentLast()) {
      //store result of the right part of the expression
      rightResult = lastResult.getLast();
    } else {
      Log.error("0xA0247 The resulting type of "+prettyPrinter.prettyprint(rightType)+" cannot be calculated");
    }

    //if one part of the expression is a double and the other is another numeric type then the result is a double
    if (("double".equals(unbox(leftResult.print())) && isNumericType(rightResult)) ||
        ("double".equals(unbox(rightResult.print())) && isNumericType(leftResult))) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("double"));
      //no part of the expression is a double -> try again with float
    } else if (("float".equals(unbox(leftResult.print())) && isNumericType(rightResult)) ||
        ("float".equals(unbox(rightResult.print())) && isNumericType(leftResult))) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("float"));
      //no part of the expression is a float -> try again with long
    } else if (("long".equals(unbox(leftResult.print())) && isNumericType(rightResult)) ||
        ("long".equals(unbox(rightResult.print())) && isNumericType(leftResult))) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("long"));
      //no part of the expression is a long -> if both parts are numeric types then the result is a int
    } else {
      if (
          ("int".equals(unbox(leftResult.print())) || "char".equals(unbox(leftResult.print())) ||
              "short".equals(unbox(leftResult.print())) || "byte".equals(unbox(leftResult.print()))
          ) && ("int".equals(unbox(rightResult.print())) || "char".equals(unbox(rightResult.print())) ||
              "short".equals(unbox(rightResult.print())) || "byte".equals(unbox(rightResult.print()))
          )
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
  public Optional<SymTypeExpression> getBinaryNumericPromotionWithString(ASTExpression leftType,
                                                                         ASTExpression rightType) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;

    if (leftType != null) {
      leftType.accept(getRealThis());
    }
    if (lastResult.isPresentLast()) {
      //store result of the left part of the expression
      leftResult = lastResult.getLast();
    } else {
      Log.error("0xA0248 The resulting type of "+prettyPrinter.prettyprint(leftType)+" cannot be calculated");
    }
    if (rightType != null) {
      rightType.accept(getRealThis());
    }
    if (lastResult.isPresentLast()) {
      //store result of the right part of the expression
      rightResult = lastResult.getLast();
    } else {
      Log.error("0xA0249 The resulting type of "+prettyPrinter.prettyprint(rightType)+" cannot be calculated");
    }
    //if one part of the expression is a String then the whole expression is a String
    if ("String".equals(unbox(leftResult.print())) || "String".equals(unbox(rightResult.print()))) {
      return Optional.of(SymTypeExpressionFactory.createTypeObject("String", leftType.getEnclosingScope()));
    }
    //no String in the expression -> use the normal calculation for the basic arithmetic operators
    return getBinaryNumericPromotion(leftType, rightType);
  }

  /**
   * test if the expression is of numeric type (double, float, long, int, char, short, byte)
   */
  private boolean isNumericType(SymTypeExpression ex) {
    return ("double".equals(unbox(ex.print())) || "float".equals(unbox(ex.print())) ||
        "long".equals(unbox(ex.print())) || "int".equals(unbox(ex.print())) ||
        "char".equals(unbox(ex.print())) || "short".equals(unbox(ex.print())) ||
        "byte".equals(unbox(ex.print()))
    );
  }

  /**
   * test if the expression is a primitive type
   */
  private boolean isPrimitiveType(SymTypeExpression ex) {
    return isNumericType(ex) || "boolean".equals(unbox(ex.print()));
  }

  /**
   * helper method for the calculation of the ASTBooleanNotExpression
   */
  public static Optional<SymTypeExpression> getUnaryIntegralPromotionType(SymTypeExpression type) {
    if ("byte".equals(SymTypeConstant.unbox(type.print())) ||
        "short".equals(SymTypeConstant.unbox(type.print())) ||
        "char".equals(SymTypeConstant.unbox(type.print())) ||
        "int".equals(SymTypeConstant.unbox(type.print()))
    ) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("int"));
    }
    return Optional.empty();
  }

  public void setLastResult(LastResult lastResult) {
    this.lastResult = lastResult;
  }
}
