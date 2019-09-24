/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.*;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static de.monticore.types.check.SymTypeExpressionFactory.createTypeExpression;

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
      lastResult.setLastOpt(Optional.empty());
      Log.error("0xA0207 The resulting type cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTNameExpression expr){
    Optional<FieldSymbol> optVar = scope.resolveField(expr.getName());
    Optional<TypeSymbol> optType = scope.resolveType(expr.getName());
//    Collection<MethodSymbol> methods = scope.resolveMethodMany(expr.getName());
//   if(lastResult.isMethodpreferred()) {
//     //last ast node was call expression
//     //in this case only method is tested
//     lastResult.setMethodpreferred(false);
//     if(!methods.isEmpty()){
//       ArrayList<MethodSymbol> methodList = new ArrayList<>(methods);
//       SymTypeExpression retType = methodList.get(0).getReturnType();
//       for(MethodSymbol method: methodList){
//         if(!method.getReturnType().print().equals(retType.print())){
//           Log.error("More than one method with the same name and different return types found");
//         }
//       }
//       if (!"void".equals(retType.print())) {
//         SymTypeExpression type = retType;
//         this.result = type;
//         lastResult.setLast(retType);
//       }else {
//         SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeVoid();
//         this.result = wholeResult;
//         lastResult.setLast(wholeResult);
//       }
//     }else{
//      Log.error("No method with the name " +expr.getName()+" found");
//     }
//   }else
    if(optVar.isPresent()){
     //no method here, test variable first
      // durch AST-Umbau kann ASTNameExpression keine Methode sein
      FieldSymbol var = optVar.get();
      this.result=var.getType();
      lastResult.setLast(var.getType());
    }else if(optType.isPresent()) {
     //no variable found, test if name is type
      TypeSymbol type = optType.get();
      SymTypeExpression res = createTypeExpression(type);
      this.result = res;
      lastResult.setLast(res);

    }else{
     //name not found --> package or nothing
     lastResult.setLastOpt(Optional.empty());
      Log.info("package suspected","ExpressionBasisTypesCalculator");
    }
  }

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
