package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ETypeSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.prettyprint2.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import javax.swing.text.html.Option;
import java.lang.reflect.Array;
import java.util.*;

public class CommonExpressionTypesCalculator extends ExpressionsBasisTypesCalculator implements CommonExpressionsVisitor {
  
  private final String errorCode="0xA0142 ";

  public CommonExpressionTypesCalculator(){
    types = new HashMap<>();
  }

  @Override
  public void endVisit(ASTPlusExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTMultExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTDivideExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTMinusExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTModuloExpression expr){
    ASTMCType result = calculateTypeArithmetic(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }
  
  @Override
  public void endVisit(ASTLessEqualExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTGreaterEqualExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTLessThanExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTGreaterThanExpression expr){
    ASTMCType result = calculateTypeCompare(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTEqualsExpression expr){
    ASTMCType result = calculateTypeLogical(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTNotEqualsExpression expr){
    ASTMCType result = calculateTypeLogical(expr.getLeft(),expr.getRight());
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanAndOpExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      if (types.get(expr.getLeft()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build()) && types.get(expr.getRight()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      if (types.get(expr.getLeft()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build()) && types.get(expr.getRight()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTLogicalNotExpression expr) {
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if (types.get(expr.getExpression()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBracketExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      result=types.get(expr.getExpression()).getASTMCType();
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTSimpleAssignmentExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())){
      if(types.get(expr.getLeft()).deepEquals(types.get(expr.getRight()))){
        result=types.get(expr.getRight()).getASTMCType();
      }else if(types.get(expr.getLeft()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())&&types.get(expr.getRight()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())){
        result=MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
      }
    }
    if(result!=null){
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr,sym);
      this.result=result;
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTConditionalExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getTrueExpression())&&types.containsKey(expr.getFalseExpression())){
      if(types.containsKey(expr.getCondition())&&types.get(expr.getCondition()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())){
        result = calculateTypeArithmetic(expr.getTrueExpression(),expr.getFalseExpression());
      }
    }
    if(result!=null){
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      this.result=result;
      types.put(expr,sym);
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanNotExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if(types.get(expr.getExpression()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())){
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build();
      }else if(types.get(expr.getExpression()).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build())){
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build();
      }
    }
    if(result!=null){
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      this.result=result;
      types.put(expr,sym);
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTCallExpression expr){
    Optional<EMethodSymbol> sym = scope.resolveEMethod(expr.geteMethodSymbol().getName());
    if(sym.isPresent()) {
      if(sym.get().getReturnType().isPresentMCType()) {
        ASTMCType ret = sym.get().getReturnType().getMCType();
        this.result = ret;
        MCTypeSymbol symbol = new MCTypeSymbol(sym.get().getName());
        symbol.setASTMCType(ret);
        types.put(expr, symbol);
      }else{
        List<String> name = new ArrayList<>();
        name.add("void");
        ASTMCType type=MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
        this.result=type;
        MCTypeSymbol symbol = new MCTypeSymbol("void");
        symbol.setASTMCType(type);
        types.put(expr,symbol);
      }
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }

  }

  private ASTMCType calculateTypeArithmetic(ASTExpression left, ASTExpression right){
    ASTMCType result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build();
      }else if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())){
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
      }else if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build())){
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build();
      }else if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build())){
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build();
      }else if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())
            || types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())){
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
      }
    }
    return result;
  }

  private ASTMCType calculateTypeCompare(ASTExpression left, ASTExpression right){
    ASTMCType result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())
            || types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    return result;
  }

  private ASTMCType calculateTypeLogical(ASTExpression left, ASTExpression right) {
    ASTMCType result = null;

    if(types.containsKey(left)&&types.containsKey(right)) {
      if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }else if(types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())
          || types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
      else if (types.get(left).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build()) && types.get(right).deepEqualsWithType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())) {
        result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      }
    }
    return result;
  }

  //TODO: es fehlen noch LiteralExpr, CallExpr, NameExpr und QualifiedNameExpr, bisher nur fuer double und int alles implementiert
}
