/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.expressions.expressionsbasis._ast.*;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.typescalculator.TypesCalculatorHelper;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class DeriveSymTypeOfExpression implements ExpressionsBasisVisitor {

  protected IExpressionsBasisScope scope;

  protected SymTypeExpression result;

  protected LastResult lastResult;

  private ExpressionsBasisVisitor realThis;

  @Override
  public void setRealThis(ExpressionsBasisVisitor realThis){
    this.realThis=realThis;
  }

  @Override
  public ExpressionsBasisVisitor getRealThis(){
    return realThis;
  }

  public DeriveSymTypeOfExpression(){
    realThis=this;
  }

  @Override
  public void traverse(ASTLiteralExpression expr){
    SymTypeExpression result = null;
    //get the type of the literal
    expr.getLiteral().accept(getRealThis());
    if(lastResult.isPresentLast()) {
      result = lastResult.getLast();
    }else{
      result = null;
    }
    if(result!=null) {
      this.result=result;
      lastResult.setLastOpt(Optional.of(result));
    }else{
      //No type found --> error
      Log.error("0xA0207 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTNameExpression expr){
    Optional<FieldSymbol> optVar = scope.resolveField(expr.getName());
    Optional<TypeSymbol> optType = scope.resolveType(expr.getName());
    Collection<MethodSymbol> methods = scope.resolveMethodMany(expr.getName());
   if(lastResult.isMethodpreferred()) {
     //last ast node was call expression
     //in this case only method is tested
     lastResult.setMethodpreferred(false);
     if(!methods.isEmpty()){
       ArrayList<MethodSymbol> methodList = new ArrayList<>(methods);
       SymTypeExpression retType = methodList.get(0).getReturnType();
       for(MethodSymbol method: methodList){
         if(!method.getReturnType().print().equals(retType.print())){
           Log.error("More than one method with the same name and different return types found");
         }
       }
       if (!"void".equals(retType.print())) {
         SymTypeExpression type = retType;
         this.result = type;
         lastResult.setLast(retType);
       }else {
         SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeVoid();
         this.result = wholeResult;
         lastResult.setLast(wholeResult);
       }
     }else{
      Log.error("No method with the name " +expr.getName()+" found");
     }
   }else if(optVar.isPresent()){
     //no method here, test variable first
      FieldSymbol var = optVar.get();
      this.result=var.getType();
      lastResult.setLast(var.getType());
    }else if(optType.isPresent()) {
     //no variable found, test if name is type
      TypeSymbol type = optType.get();
      SymTypeExpression res = TypesCalculatorHelper.fromTypeSymbol(type);
      this.result = res;
      lastResult.setLast(res);

    }else{
     //name not found --> package or nothing
      Log.info("package suspected","ExpressionBasisTypesCalculator");
    }
  }

//  @Override
//  public void endVisit(ASTQualifiedNameExpression expr) {
//    String toResolve;
//    if(types.containsKey(expr)) {
//      result=types.get(expr);
//      return;
//    }else{
//      ExpressionsBasisPrettyPrinter printer = new ExpressionsBasisPrettyPrinter(new IndentPrinter());
//      toResolve = printer.prettyprint(expr);
//    }
//
//    // (statische innere) Klasse
//    Optional<TypeSymbol> typeSymbolopt = scope.resolveType(toResolve);
//
//    // statische variable
//    Optional<FieldSymbol> variableSymbolopt = scope.resolveField(toResolve);
//
//    // statische methode
//    Optional<MethodSymbol> methodSymbolopt = scope.resolveMethod(toResolve);
//
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
//  }
//
//  private void addToTypesMapQName (ASTExpression expr, List<TypeSymbol> superTypes, String fullName){
//    String[] parts = fullName.split("\\.");
//    ArrayList<String> nameList = new ArrayList<>();
//    Collections.addAll(nameList,parts);
//    SymTypeExpression res = new SymTypeOfObject();
//    res.setName(fullName);
//
//    List<SymTypeExpression> superTypesAsSymTypes = new ArrayList<>();
//    for(TypeSymbol ts : superTypes) {
//      superTypesAsSymTypes.add(ts.deepClone());
//    }
//
//
//    res.setSuperTypes(superTypesAsSymTypes);
//    this.result=res;
//    types.put(expr,unbox(res));
//  }
//
//  private void addToTypesMapQName (ASTExpression expr, String fullName, List<SymTypeExpression> superTypes){
//    String[] parts = fullName.split("\\.");
//    ArrayList<String> nameList = new ArrayList<>();
//    Collections.addAll(nameList,parts);
//    SymTypeExpression res = new SymTypeOfObject();
//    res.setName(fullName);
//
//    List<SymTypeExpression> superTypesAsSymTypes = new ArrayList<>();
//    for(SymTypeExpression ts : superTypes) {
//      superTypesAsSymTypes.add(ts.deepClone());
//    }
//
//
//    res.setSuperTypes(superTypesAsSymTypes);
//    this.result=res;
//    types.put(expr,unbox(res));
//  }
//
//
  public SymTypeExpression getResult() {
    return result;
  }

  protected IExpressionsBasisScope getScope() {
    return scope;
  }

  public void setScope(IExpressionsBasisScope scope) {
    this.scope = scope;
  }

  public Optional<SymTypeExpression> calculateType(ASTExpression expr){
    expr.accept(realThis);
    Optional<SymTypeExpression> result = lastResult.getLastOpt();
    lastResult.setLastOpt(Optional.empty());
    return result;
  }

  public void setLastResult(LastResult lastResult){
    this.lastResult = lastResult;
  }

}
