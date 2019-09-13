/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint2.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint2.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;
import de.monticore.types2.SymTypeExpressionFactory;
import de.monticore.types2.SymTypeOfObject;
import de.se_rwth.commons.logging.Log;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.*;

import static de.monticore.types2.SymTypeConstant.unbox;
import static de.monticore.typescalculator.TypesCalculator.isSubtypeOf;
import static de.monticore.typescalculator.TypesCalculatorHelper.getUnaryNumericPromotionType;


public class CommonExpressionTypesCalculator extends ExpressionsBasisTypesCalculator implements CommonExpressionsVisitor {

  private CommonExpressionsVisitor realThis;

  @Override
  public void setRealThis(CommonExpressionsVisitor realThis){
    this.realThis=realThis;
  }

  @Override
  public CommonExpressionsVisitor getRealThis(){
    return realThis;
  }

  public CommonExpressionTypesCalculator(){
    realThis=this;
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTPlusExpression expr){
    //the "+"-operator also allows String
    Optional<SymTypeExpression> wholeResult = getBinaryNumericPromotionWithString(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0188 The resulting type cannot be calculated");
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTMultExpression expr){
    Optional<SymTypeExpression> wholeResult = getBinaryNumericPromotion(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0189 The resulting type cannot be calculated");
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTDivideExpression expr){
    Optional<SymTypeExpression> wholeResult = getBinaryNumericPromotion(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0190 The resulting type cannot be calculated");
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void endVisit(ASTMinusExpression expr){
    Optional<SymTypeExpression> wholeResult = getBinaryNumericPromotion(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0191 The resulting type cannot be calculated");
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void endVisit(ASTModuloExpression expr){
    Optional<SymTypeExpression> wholeResult = getBinaryNumericPromotion(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0192 The resulting type cannot be calculated");
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLessEqualExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0193 The resulting type cannot be calculated");
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTGreaterEqualExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0194 The resulting type cannot be calculated");
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTLessThanExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0195 The resulting type cannot be calculated");
    }
  }

  /**
   * We use traverse to collect the results of the two parts of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTGreaterThanExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0196 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTEqualsExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeLogical(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0197 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTNotEqualsExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeLogical(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0198 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTBooleanAndOpExpression expr){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    if(expr.getLeft()!=null){
      expr.getLeft().accept(getRealThis());
    }
    if(lastResult.isPresentLast()) {
      //store result of the left part of the expression
      leftResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }
    if(expr.getRight()!=null){
      expr.getRight().accept(getRealThis());
    }
    if(lastResult.isPresentLast()){
      //store result of the right part of the expression
      rightResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }

    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if (leftResult.print().equals("boolean") && rightResult.print().equals("boolean")) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0199 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression expr){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    if(expr.getLeft()!=null){
      expr.getLeft().accept(getRealThis());
    }
    if(lastResult.isPresentLast()) {
      //store result of the left part of the expression
      leftResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }
    if(expr.getRight()!=null){
      expr.getRight().accept(getRealThis());
    }
    if(lastResult.isPresentLast()){
      //store result of the right part of the expression
      rightResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }

    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if (leftResult.print().equals("boolean") && rightResult.print().equals("boolean")) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0200 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTLogicalNotExpression expr) {
    SymTypeExpression innerResult = null;
    if(expr.getExpression()!=null){
      expr.getExpression().accept(getRealThis());
    }
    if(lastResult.isPresentLast()){
      //store result of the inner part of the expression
      innerResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if(innerResult.print().equals("boolean")){
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0201 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTBracketExpression expr){
    SymTypeExpression innerResult = null;
    if(expr.getExpression()!=null){
      expr.getExpression().accept(getRealThis());
    }
    if(lastResult.isPresentLast()){
      //store result of the inner part of the expression
      innerResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }
    Optional<SymTypeExpression> wholeResult = Optional.of(innerResult);
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0202 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTConditionalExpression expr){
    SymTypeExpression conditionResult = null;
    SymTypeExpression trueResult = null;
    SymTypeExpression falseResult = null;

    if(expr.getCondition()!=null){
      expr.getCondition().accept(getRealThis());
    }
    if(lastResult.isPresentLast()){
      conditionResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }
    if(expr.getTrueExpression()!=null){
      expr.getTrueExpression().accept(getRealThis());
    }
    if(lastResult.isPresentLast()){
      trueResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }
    if(expr.getFalseExpression()!=null){
      expr.getFalseExpression().accept(getRealThis());
    }
    if(lastResult.isPresentLast()){
      falseResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if(conditionResult.print().equals("boolean")){
      if(trueResult.print().equals(falseResult.print())){
        wholeResult = Optional.of(trueResult);
      }else if(isSubtypeOf(expr.getFalseExpression(),expr.getTrueExpression())){
        wholeResult = Optional.of(trueResult);
      }else if(isSubtypeOf(expr.getTrueExpression(),expr.getFalseExpression())){
        wholeResult = Optional.of(falseResult);
      }
      else{
        wholeResult = getBinaryNumericPromotion(expr.getTrueExpression(), expr.getFalseExpression());
      }
    }
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0204 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTBooleanNotExpression expr){
    SymTypeExpression innerResult = null;
    if(expr.getExpression()!=null){
      expr.getExpression().accept(getRealThis());
    }
    if(lastResult.isPresentLast()){
      //store result of the inner part of the expression
      innerResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if(innerResult instanceof SymTypeConstant && ((SymTypeConstant) innerResult).isIntegralType()){
      wholeResult = getUnaryNumericPromotionType(lastResult.getLast());
    }
    if(wholeResult.isPresent()) {
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0205 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTFieldAccessExpression expr) {
    CommonExpressionsPrettyPrinter printer = new CommonExpressionsPrettyPrinter(new IndentPrinter());
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()) {
      innerResult = lastResult.getLast();
      TypeSymbol innerResultType = innerResult.getTypeInfo();
      Collection<MethodSymbol> methodSymbols = innerResultType.getSpannedScope().resolveMethodMany(expr.getName());
      Optional<FieldSymbol> fieldSymbolOpt = innerResultType.getSpannedScope().resolveField(expr.getName());
      Optional<TypeSymbol> typeSymbolOpt = innerResultType.getSpannedScope().resolveType(expr.getName());
      if (lastResult.isMethodpreferred()) {
        //we were in callexpression, this nameexpression must be a method
        //TODO: was ist, wenn mehrere Methodsymbols gefunden werden koennen? Parameter?
        if (!methodSymbols.isEmpty()) {
          for (MethodSymbol method : methodSymbols) {
            //            if (!"void".equals(method.getReturnType().print())) {
            //              SymTypeExpression type = method.getReturnType();
            //              this.result = type;
            //              lastResult.setLast(type);
            //            }else {
            //              SymTypeExpression type = SymTypeExpressionFactory.createTypeVoid();
            //              this.result = type;
            //              lastResult.setLast(type);
            //            }
          }
        }
        else {
          //TODO: logs
          Log.error("");
        }
      }
      else if (fieldSymbolOpt.isPresent()) {
        //cannot be a method, test first variable
        FieldSymbol var = fieldSymbolOpt.get();
        innerResult.getTypeInfo().getFields().contains(var);
        SymTypeExpression type = var.getType();
        this.result = type;
        lastResult.setLast(type);
      }
      else if (typeSymbolOpt.isPresent()) {
        //no variable found, test type
      }
    }




//    //TODO RE Reihenfolge beachten var vor? Klasse
//    if(typeSymbolopt.isPresent()){
//      String fullName= typeSymbolopt.get().getFullName();
//      addToTypesMapQName(expr,fullName,typeSymbolopt.get().getSuperTypes());
//    }else if(variableSymbolopt.isPresent()) {
//      ExpressionsBasisPrettyPrinter printer = new ExpressionsBasisPrettyPrinter(new IndentPrinter());
//      String exprString = printer.prettyprint(expr);
//      String[] stringParts = exprString.split("\\.");
//      String beforeName="";
//      if(stringParts.length!=1){
//        for(int i=0;i<stringParts.length-1;i++){
//          beforeName+=stringParts[i]+".";
//        }
//        beforeName=beforeName.substring(0,beforeName.length()-1);
//        if(!scope.resolveType(beforeName).isPresent()&&scope.resolveMethodMany(beforeName).isEmpty()){
//          Log.info("package suspected","ExpressionsBasisTypesCalculator");
//        }else{
//          if(scope.resolveType(beforeName).isPresent()) {
//            Optional<TypeSymbol> typeSymbol = scope.resolveType(beforeName);
//            boolean test = false;
//            for(int i=0;i<typeSymbol.get().getFields().size();i++){
//              if(!test&&typeSymbol.get().getFields().get(i).getFullName().equals(variableSymbolopt.get().getFullName())){
//                test = true;
//              }
//            }
//            if(!test){
//              Log.error("0xA208 the resulting type cannot be calculated");
//            }
//          }else{
//            boolean success = true;
//            Collection<MethodSymbol> methodSymbols = scope.resolveMethodMany(beforeName);
//            for(MethodSymbol methodSymbol:methodSymbols){
//              if(methodSymbol.getReturnType().getName().equals("void")){
//                success = false;
//              }else{
//                SymTypeExpression returnType = methodSymbol.getReturnType();
//                String[] primitives = new String[]{"int","double","char","float","long","short","byte","boolean"};
//                for(String primitive: primitives){
//                  if(primitive.equals(returnType.getName())){
//                    success=false;
//                  }
//                  if(success) {
//                    if (!methodSymbol.getParameter().contains(variableSymbolopt.get())) {
//                      success = false;
//                    }
//                  }
//                }
//                if(!success){
//                  Log.error("0xA0208 the resulting type cannot be calculated");
//                }
//              }
//            }
//          }
//        }
//      }
//      String fullName= variableSymbolopt.get().getType().getName();
//      addToTypesMapQName(expr,fullName,variableSymbolopt.get().getType().getSuperTypes());
//    }else if(methodSymbolopt.isPresent()) {
//      String fullName = methodSymbolopt.get().getReturnType().getName();
//      addToTypesMapQName(expr,fullName,methodSymbolopt.get().getReturnType().getSuperTypes());
//    }else{
//      Log.info("package suspected","ExpressionsBasisTypesCalculator");
//    }
  }

//  @Override
//  public void traverse(ASTCallExpression expr){
//    if(types.containsKey(expr.getExpression())) {
//      CommonExpressionsPrettyPrinter printer = new CommonExpressionsPrettyPrinter(new IndentPrinter());
//      ExpressionsBasisPrettyPrinter prettyPrinter = new ExpressionsBasisPrettyPrinter(new IndentPrinter());
//      String exp = prettyPrinter.prettyprint(expr.getExpression());
//      Collection<MethodSymbol> methodcollection = scope.resolveMethodMany(exp);
//      List<MethodSymbol> methodlist = new ArrayList<>(methodcollection);
//      for (MethodSymbol method : methodlist) {
//        if (expr.getArguments().getExpressionList().size()==method.getParameter().size()){
//          boolean success = true;
//          for(int i=0;i<method.getParameter().size();i++){
//            if(!method.getParameter().get(i).getType().deepEquals(types.get(expr.getArguments().getExpressionList().get(i)))&&!isSubtypeOf(types.get(expr.getArguments().getExpressionList().get(i)),method.getParameter().get(i).getType())){
//              success = false;
//            }
//          }
//          if(success){
//            if(!"void".equals(method.getReturnType().getName())){
//              SymTypeExpression result=method.getReturnType();
//              this.result=result;
//              types.put(expr,result);
//            }else if("void".equals(method.getReturnType().getName())){
//              SymTypeExpression result = new SymTypeConstant();
//              result.setName("void");
//              types.put(expr, result);
//              this.result = result;
//            }else{
//              Log.error("0xA209 the resulting type cannot be resolved");
//            }
//          }else{
//            Log.error("0xA209 the resulting type cannot be resolved");
//          }
//        }
//      }
//    }else{
//      Log.info("package suspected","CommonExpressionTypesCalculator");
//    }
//  }

  /**
   * helper method for <=, >=, <, > -> calculate the result of these expressions
   */
  private Optional<SymTypeExpression> calculateTypeCompare(ASTExpression left, ASTExpression right){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    if(left!=null){
      left.accept(getRealThis());
    }
    if(lastResult.isPresentLast()) {
      //store result of the left part of the expression
      leftResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }
    if(right!=null){
      right.accept(getRealThis());
    }
    if(lastResult.isPresentLast()){
      //store result of the right part of the expression
      rightResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }

    //if the left and the right part of the expression are numerics, then the whole expression is a boolean
    if(isNumericType(leftResult)&&isNumericType(rightResult)){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    //should never happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }

  private Optional<SymTypeExpression> calculateTypeLogical(ASTExpression left, ASTExpression right) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    if(left!=null){
      left.accept(getRealThis());
    }
    if(lastResult.isPresentLast()) {
      //store result of the left part of the expression
      leftResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }
    if(right!=null){
      right.accept(getRealThis());
    }
    if(lastResult.isPresentLast()){
      //store result of the right part of the expression
      rightResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }

    SymTypeExpression result = null;
    if(isNumericType(leftResult)&&isNumericType(rightResult)
    ||leftResult.print().equals("boolean")&&rightResult.print().equals("boolean")){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    if(!isPrimitiveType(leftResult) && !isPrimitiveType(rightResult) &&
        (         leftResult.print().equals(rightResult.print())
            || isSubtypeOf(rightResult,leftResult)
            || isSubtypeOf(leftResult,rightResult)
        )){
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
    if(leftType!=null){
      leftType.accept(getRealThis());
    }
    if(lastResult.isPresentLast()) {
      //store result of the left part of the expression
      leftResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }
    if(rightType!=null){
      rightType.accept(getRealThis());
    }
    if(lastResult.isPresentLast()){
      //store result of the right part of the expression
      rightResult = lastResult.getLast();
    }else{
      //TODO: logs
      Log.error("");
    }

    //if one part of the expression is a double and the other is another numeric type then the result is a double
    if(("double".equals(unbox(leftResult.print()))&&isNumericType(rightResult))||("double".equals(unbox(rightResult.print()))&&isNumericType(leftResult))){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("double"));
    //no part of the expression is a double -> try again with float
    }else if(("float".equals(unbox(leftResult.print()))&&isNumericType(rightResult))||("float".equals(unbox(rightResult.print()))&&isNumericType(leftResult))){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("float"));
    //no part of the expression is a float -> try again with long
    }else if(("long".equals(unbox(leftResult.print()))&&isNumericType(rightResult))||("long".equals(unbox(rightResult.print()))&&isNumericType(leftResult))) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("long"));
    //no part of the expression is a long -> if both parts are numeric types then the result is a int
    }else{
      if (("int".equals(unbox(leftResult.print())) || "char".equals(unbox(leftResult.print())) || "short".equals(unbox(leftResult.print())) || "byte".equals(unbox(leftResult.print()))) && ("int".equals(unbox(rightResult.print())) || "char".equals(unbox(rightResult.print())) || "short".equals(unbox(rightResult.print())) || "byte".equals(unbox(rightResult.print())))) {
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

    if(leftType!=null){
      leftType.accept(getRealThis());
    }
    if(lastResult.isPresentLast()){
      //store result of the left part of the expression
      leftResult = lastResult.getLast();
    }else{

    }
    if(rightType!=null){
      rightType.accept(getRealThis());
    }
    if(lastResult.isPresentLast()){
      //store result of the right part of the expression
      rightResult = lastResult.getLast();
    }else{

    }
    //if one part of the expression is a String then the whole expression is a String
    if(unbox(leftResult.print()).equals("String")||unbox(rightResult.print()).equals("String")) {
      return Optional.of(SymTypeExpressionFactory.createTypeObject("String",null));
    }
    //no String in the expression -> use the normal calculation for the basic arithmetic operators
    return getBinaryNumericPromotion(leftType,rightType);
  }

  /**
   * test if the expression is of numeric type (double, float, long, int, char, short, byte)
   */
  private boolean isNumericType(SymTypeExpression ex){
    return (ex.print().equals("double") || ex.print().equals("float") || ex.print().equals("long") || ex.print().equals("int") || ex.print().equals("char") || ex.print().equals("short") || ex.print().equals("byte"));
  }

  private boolean isPrimitiveType(SymTypeExpression ex){
    return isNumericType(ex) || ex.print().equals("boolean");
  }

  public Optional<SymTypeExpression> calculateType(ASTExpression expr){
    expr.accept(realThis);
    return lastResult.getLastOpt();
  }

  public void setLastResult(LastResult lastResult){
    this.lastResult = lastResult;
  }

}
