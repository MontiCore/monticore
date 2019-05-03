package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
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
import sun.security.krb5.internal.crypto.EType;

import javax.swing.text.html.Option;
import java.lang.reflect.Array;
import java.util.*;

public class CommonExpressionTypesCalculator implements CommonExpressionsVisitor {

  private Map<ASTNode, MCTypeSymbol> types;

  private ASTMCType result;

  private ExpressionsBasisScope scope;

  private LiteralTypeCalculator literalsVisitor;

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
      throw new RuntimeException("The resulting type cannot be calculated");
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
      throw new RuntimeException("The resulting type cannot be calculated");
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
      throw new RuntimeException("The resulting type cannot be calculated");
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
      throw new RuntimeException("The resulting type cannot be calculated");
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
      throw new RuntimeException("The resulting type cannot be calculated");
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
      throw new RuntimeException("The resulting type cannot be calculated");
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
      throw new RuntimeException("The resulting type cannot be calculated");
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
      throw new RuntimeException("The resulting type cannot be calculated");
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
      throw new RuntimeException("The resulting type cannot be calculated");
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
      throw new RuntimeException("The resulting type cannot be calculated");
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
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanAndOpExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      if (types.get(expr.getLeft()).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN)) && types.get(expr.getRight()).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanOrOpExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())) {
      if (types.get(expr.getLeft()).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN)) && types.get(expr.getRight()).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTLogicalNotExpression expr) {
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if (types.get(expr.getExpression()).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
    }
    if(result!=null) {
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
      this.result = result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
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
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTSimpleAssignmentExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getLeft())&&types.containsKey(expr.getRight())){
      if(types.get(expr.getLeft()).deepEquals(types.get(expr.getRight()))){
        result=types.get(expr.getRight()).getASTMCType();
      }else if(types.get(expr.getLeft()).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE))&&types.get(expr.getRight()).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))){
        result=new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE);
      }
    }
    if(result!=null){
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr,sym);
      this.result=result;
    }else{
      throw new RuntimeException("The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTConditionalExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getTrueExpression())&&types.containsKey(expr.getFalseExpression())){
      if(types.containsKey(expr.getCondition())&&types.get(expr.getCondition()).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN))){
        result = calculateTypeArithmetic(expr.getTrueExpression(),expr.getFalseExpression());
      }
    }
    if(result!=null){
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      this.result=result;
      types.put(expr,sym);
    }else{
      throw new RuntimeException("the resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTBooleanNotExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExpression())){
      if(types.get(expr.getExpression()).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))){
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT);
      }else if(types.get(expr.getExpression()).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.LONG))){
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.LONG);
      }
    }
    if(result!=null){
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      this.result=result;
      types.put(expr,sym);
    }else{
      throw new RuntimeException("the resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTLiteralExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExtLiteral())){
      result = types.get(expr.getExtLiteral()).getASTMCType();
    }
    if(result!=null) {
      this.result=result;
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
    }else{
      throw new RuntimeException("the resulting type cannot be calculated");
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
        ASTMCType type=new ASTMCQualifiedType(new ASTMCQualifiedName(name));
        this.result=type;
        MCTypeSymbol symbol = new MCTypeSymbol("void");
        symbol.setASTMCType(type);
        types.put(expr,symbol);
      }
    }else{
      throw new RuntimeException("the resulting type cannot be calculated");
    }

  }

  @Override
  public void endVisit(ASTNameExpression expr){
    Optional<EVariableSymbol> optVar = scope.resolveEVariable(expr.getName());
    Optional<ETypeSymbol> optType = scope.resolveEType(expr.getName());
    if(optVar.isPresent()){
      EVariableSymbol var = optVar.get();
      this.result=var.getMCTypeSymbol().getASTMCType();
      types.put(expr,var.getMCTypeSymbol());
    }else if(optType.isPresent()) {
      ASTMCType type = null;
      type = optType.get().getType();
      this.result=type;
      MCTypeSymbol sym = new MCTypeSymbol(type.getBaseName());
      sym.setASTMCType(type);
      types.put(expr,sym);
    }else{
      Log.info("package oder methode vermutet","CommonExpressionTypesCalculator");
    }
  }

  @Override
  public void endVisit(ASTQualifiedNameExpression expr) {
    ASTMCType result = null;
    if(types.containsKey(expr)){
      MCFullGenericTypesPrettyPrinter printer = new MCFullGenericTypesPrettyPrinter(new IndentPrinter());
      String toResolve = printer.prettyprint(types.get(expr).getASTMCType())+expr.getName();
      Optional<ETypeSymbol> typeSymbolopt = scope.resolveETypeDown(toResolve);//TODO: resolve statt resolveDown
      Optional<EVariableSymbol> variableSymbolopt = scope.resolveEVariableDown(toResolve);//TODO: resolve statt resolveDown
      if(typeSymbolopt.isPresent()){
        String fullName= typeSymbolopt.get().getFullName();
        String[] parts = fullName.split(".");
        ArrayList<String> nameList = new ArrayList<>();
        for(String s: parts){
          nameList.add(s);
        }
        result= new ASTMCQualifiedType(new ASTMCQualifiedName(nameList));
        this.result=result;
      }else if(variableSymbolopt.isPresent()){
        String fullName= variableSymbolopt.get().getFullName();
        String[] parts = fullName.split(".");
        ArrayList<String> nameList = new ArrayList<>();
        for(String s: parts){
          nameList.add(s);
        }
        result= new ASTMCQualifiedType(new ASTMCQualifiedName(nameList));
        this.result=result;
      }else{
        Log.info("package oder methode vermutet","CommonExpressionTypesCalculator");
      }
    }else{
      CommonExpressionsPrettyPrinter printer = new CommonExpressionsPrettyPrinter(new IndentPrinter());
      String toResolve = printer.prettyprint(expr);
      Optional<ETypeSymbol> typeSymbolopt = scope.resolveETypeDown(toResolve);//TODO: resolve statt resolveDown
      Optional<EVariableSymbol> variableSymbolopt = scope.resolveEVariableDown(toResolve);//TODO: resolve statt resolveDown
      if(typeSymbolopt.isPresent()){
        String fullName= typeSymbolopt.get().getFullName();
        String[] parts = fullName.split(".");
        ArrayList<String> nameList = new ArrayList<>();
        for(String s: parts){
          nameList.add(s);
        }
        result= new ASTMCQualifiedType(new ASTMCQualifiedName(nameList));
        this.result=result;
      }else if(variableSymbolopt.isPresent()){
        String fullName= variableSymbolopt.get().getFullName();
        String[] parts = fullName.split(".");
        ArrayList<String> nameList = new ArrayList<>();
        for(String s: parts){
          nameList.add(s);
        }
        result= new ASTMCQualifiedType(new ASTMCQualifiedName(nameList));
        this.result=result;
      }else{
        Log.info("package oder methode vermutet", "CommonExpressionTypesCalculator");
      }
    }
  }


  public ASTMCType getResult() {
    return result;
  }

  public Map<ASTNode, MCTypeSymbol> getTypes() {
    return types;
  }

  public void setTypes(Map<ASTNode, MCTypeSymbol> types){
    this.types = types;
  }

  public void setResult(ASTMCType result) {
    this.result = result;
  }

  private ASTMCType calculateTypeArithmetic(ASTExpression left, ASTExpression right){
    ASTMCType result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if (types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT);
      }else if(types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE))){
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE);
      }else if(types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.FLOAT)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.FLOAT))){
        result = new ASTMCPrimitiveType(5);
      }else if(types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.LONG)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.LONG))){
        result = new ASTMCPrimitiveType(7);
      }else if(types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) || types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))){
        result = new ASTMCPrimitiveType(4);
      }
    }
    return result;
  }

  private ASTMCType calculateTypeCompare(ASTExpression left, ASTExpression right){
    ASTMCType result = null;
    if(types.containsKey(left)&&types.containsKey(right)) {
      if (types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }else if(types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE))){
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }else if(types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.FLOAT)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.FLOAT))){
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }else if(types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.LONG)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.LONG))){
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }else if(types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) || types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
    }
    return result;
  }

  private ASTMCType calculateTypeLogical(ASTExpression left, ASTExpression right) {
    ASTMCType result = null;
    if (types.containsKey(left) && types.containsKey(right)) {
      if (types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
      else if (types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
      else if (types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.FLOAT)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.FLOAT))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
      else if (types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.LONG)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.LONG))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
      else if (types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) || types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
      else if (types.get(left).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN)) && types.get(right).deepEqualsWithType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN))) {
        result = new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
      }
    }
    return result;
  }

  public void setScope(ExpressionsBasisScope scope){
    this.scope=scope;
  }

  public ExpressionsBasisScope getScope(){
    return scope;
  }

  public void setLiteralsVisitor(LiteralTypeCalculator literalsVisitor){
    this.literalsVisitor = literalsVisitor;
  }

  public LiteralTypeCalculator getLiteralsVisitor(){
    return this.literalsVisitor;
  }

  //TODO: es fehlen noch LiteralExpr, CallExpr, NameExpr und QualifiedNameExpr, bisher nur fuer double und int alles implementiert
}
