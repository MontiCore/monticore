/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint2.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;
import de.monticore.types2.SymTypeExpressionFactory;
import de.monticore.types2.SymTypeOfObject;
import de.se_rwth.commons.logging.Log;

import java.util.Map;
import java.util.Optional;

import static de.monticore.types2.SymTypeConstant.unbox;
import static de.monticore.typescalculator.TypesCalculator.isAssignableFrom;
import static de.monticore.typescalculator.TypesCalculator.isSubtypeOf;
import static de.monticore.typescalculator.TypesCalculatorHelper.getUnaryNumericPromotionType;

//import static de.monticore.typescalculator.TypesCalculator.isSubtypeOf;
//
public class AssignmentExpressionTypesCalculator extends ExpressionsBasisTypesCalculator implements AssignmentExpressionsVisitor {

  private AssignmentExpressionsVisitor realThis;

  @Override
  public void setRealThis(AssignmentExpressionsVisitor realThis){
    this.realThis=realThis;
  }

  @Override
  public AssignmentExpressionsVisitor getRealThis(){
    return realThis;
  }

  public AssignmentExpressionTypesCalculator(){
    realThis=this;
  }

  @Override
  public void traverse(ASTIncSuffixExpression expr){
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      innerResult = lastResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0170 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTDecSuffixExpression expr){
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      innerResult = lastResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0171 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTIncPrefixExpression expr){
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      innerResult = lastResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0172 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTDecPrefixExpression expr){
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      innerResult = lastResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0173 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTPlusPrefixExpression expr){
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      innerResult = lastResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0174 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTMinusPrefixExpression expr){
    SymTypeExpression innerResult = null;
    expr.getExpression().accept(getRealThis());
    if(lastResult.isPresentLast()){
      innerResult = lastResult.getLast();
    }
    Optional<SymTypeExpression> wholeResult = getUnaryNumericPromotionType(innerResult);
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0175 The resulting type cannot be calculated");
    }
  }

  private void calculatePlusAssignment(ASTRegularAssignmentExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmeticWithString(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0176 The resulting type cannot be calculated");
    }
  }

  private void calculateMinusAssignment(ASTRegularAssignmentExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0177 The resulting type cannot be calculated");
    }
  }

  private void calculateMultAssignment(ASTRegularAssignmentExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0178 The resulting type cannot be calculated");
    }
  }

  private void calculateDivideAssignment(ASTRegularAssignmentExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0179 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTRegularAssignmentExpression expr){
    Optional<FieldSymbol> leftEx = scope.resolveField(new ExpressionsBasisPrettyPrinter(new IndentPrinter()).prettyprint(expr.getLeft()));
    if(!leftEx.isPresent()){
      Log.error("0xA0180 The resulting type cannot be calculated");
    }
    if(expr.getOperator()==ASTConstantsAssignmentExpressions.PLUSEQUALS){
      calculatePlusAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.MINUSEQUALS){
      calculateMinusAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.STAREQUALS){
      calculateMultAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.SLASHEQUALS){
      calculateDivideAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.ANDEQUALS){
      calculateAndAssigment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.PIPEEQUALS){
      calculateOrAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.GTGTEQUALS){
      calculateDoubleRightAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.LTLTEQUALS){
      calculateDoubleLeftAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.GTGTGTEQUALS){
      calculateLogicalRightAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.ROOFEQUALS){
      calculateBinaryXorAssignment(expr);
    }else if(expr.getOperator()==ASTConstantsAssignmentExpressions.PERCENTEQUALS){
      calculateModuloAssignment(expr);
    }else {
      Optional<SymTypeExpression> wholeResult = calculateRegularAssignment(expr.getLeft(),expr.getRight());
      if(wholeResult.isPresent()){
        //store the result of the expression in the last result
        Optional<SymTypeExpression> sym = wholeResult;
        lastResult.setLastOpt(sym);
        this.result = sym.get();
      }
      else {
        Log.error("0xA0180 The resulting type cannot be calculated");
      }
    }
  }

  private void calculateAndAssigment(ASTRegularAssignmentExpression expr){
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0181 The resulting type cannot be calculated");
    }
  }

  private void calculateOrAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0182 The resulting type cannot be calculated");
    }
  }

  private void calculateBinaryXorAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf boolean - boolean und ganzzahl - ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBinaryOperations(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0183 The resulting type cannot be calculated");
    }
  }

  private void calculateDoubleRightAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0184 The resulting type cannot be calculated");
    }
  }

  private void calculateDoubleLeftAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0185 The resulting type cannot be calculated");
    }
  }

  private void calculateLogicalRightAssignment(ASTRegularAssignmentExpression expr){
    //definiert auf Ganzzahl - Ganzzahl
    Optional<SymTypeExpression> wholeResult = calculateTypeBitOperation(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0186 The resulting type cannot be calculated");
    }
  }

  private void calculateModuloAssignment(ASTRegularAssignmentExpression expr){
    Optional<SymTypeExpression> wholeResult = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(wholeResult.isPresent()){
      //store the result of the expression in the last result
      Optional<SymTypeExpression> sym = wholeResult;
      lastResult.setLastOpt(sym);
      this.result = sym.get();
    }else{
      Log.error("0xA0187 The resulting type cannot be calculated");
    }
  }

  private Optional<SymTypeExpression> calculateTypeArithmetic(ASTExpression left, ASTExpression right){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    left.accept(getRealThis());
    if(lastResult.isPresentLast()){
      leftResult = lastResult.getLast();
    }else{
      //TODO:logs
      Log.error("");
    }
    right.accept(getRealThis());
    if(lastResult.isPresentLast()){
      rightResult = lastResult.getLast();
    }else{
      //TODO:logs
      Log.error("");
    }
    if(leftResult.isPrimitiveType()&&((SymTypeConstant)leftResult).isNumericType()&&rightResult.isPrimitiveType()&&((SymTypeConstant)rightResult).isNumericType()){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(leftResult.print()));
    }
    //should not happen, not valid, will be handled in traverse
    return Optional.empty();
  }

  private Optional<SymTypeExpression> calculateTypeArithmeticWithString(ASTExpression left, ASTExpression right){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    left.accept(getRealThis());
    if(lastResult.isPresentLast()){
      leftResult = lastResult.getLast();
    }else{
      //TODO:logs
      Log.error("");
    }
    right.accept(getRealThis());
    if(lastResult.isPresentLast()){
      rightResult = lastResult.getLast();
    }else{
      //TODO:logs
      Log.error("");
    }
    if("String".equals(leftResult.print())){
      return Optional.of(SymTypeExpressionFactory.createTypeObject("String",null));
    }
    return calculateTypeArithmetic(left,right);
  }

  private Optional<SymTypeExpression> calculateTypeBitOperation(ASTExpression left, ASTExpression right){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    left.accept(getRealThis());
    if(lastResult.isPresentLast()){
      leftResult = lastResult.getLast();
    }else{
      //TODO:logs
      Log.error("");
    }
    right.accept(getRealThis());
    if(lastResult.isPresentLast()){
      rightResult = lastResult.getLast();
    }else{
      //TODO:logs
      Log.error("");
    }
    if(leftResult.isPrimitiveType()&&((SymTypeConstant)leftResult).isIntegralType()&&rightResult.isPrimitiveType()&&((SymTypeConstant)rightResult).isIntegralType()){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(leftResult.print()));
    }
    //should not happen, not valid, will be handled in traverse
    return Optional.empty();
  }

  private Optional<SymTypeExpression> calculateTypeBinaryOperations(ASTExpression left, ASTExpression right){
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    left.accept(getRealThis());
    if(lastResult.isPresentLast()){
      leftResult = lastResult.getLast();
    }else{
      //TODO:logs
      Log.error("");
    }
    right.accept(getRealThis());
    if(lastResult.isPresentLast()){
      rightResult = lastResult.getLast();
    }else{
      //TODO:logs
      Log.error("");
    }
    if(leftResult.isPrimitiveType()&&((SymTypeConstant)leftResult).isIntegralType()&&rightResult.isPrimitiveType()&&((SymTypeConstant)rightResult).isIntegralType()){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(leftResult.print()));
    }else if("boolean".equals(unbox(leftResult.print()))&&"boolean".equals(unbox(rightResult.print()))) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    //should not happen, not valid, will be handled in traverse
    return Optional.empty();
  }

  public Optional<SymTypeExpression> calculateRegularAssignment(ASTExpression left,
                                                      ASTExpression right) {
    SymTypeExpression leftResult = null;
    SymTypeExpression rightResult = null;
    left.accept(getRealThis());
    if(lastResult.isPresentLast()){
      leftResult = lastResult.getLast();
    }else{
      //TODO:logs
      Log.error("");
    }
    right.accept(getRealThis());
    if(lastResult.isPresentLast()){
      rightResult = lastResult.getLast();
    }else{
      //TODO:logs
      Log.error("");
    }
    if(leftResult.isPrimitiveType()&&((SymTypeConstant)leftResult).isNumericType()&&rightResult.isPrimitiveType()&&((SymTypeConstant)rightResult).isNumericType()&&isAssignableFrom(left,right)){
      return Optional.of(SymTypeExpressionFactory.createTypeConstant(leftResult.print()));
    }else if (isSubtypeOf(rightResult,leftResult)||rightResult.print().equals(leftResult.print())) {
      return Optional.of(leftResult);
    }
    //should not happen, not valid, will be handled in traverse
    return Optional.empty();
  }

  public Optional<SymTypeExpression> calculateType(ASTExpression expr){
    expr.accept(realThis);
    return lastResult.getLastOpt();
  }

  public void setLastResult(LastResult lastResult){
    this.lastResult = lastResult;
  }

}
