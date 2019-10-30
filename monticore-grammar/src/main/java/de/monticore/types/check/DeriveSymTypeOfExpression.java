/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.*;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static de.monticore.types.check.SymTypeExpressionFactory.createTypeExpression;

/**
 * Visitor for ExpressionsBasis
 */
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

  public void setScope(IExpressionsBasisScope scope) {
    this.scope = scope;
  }

  public void setLastResult(LastResult lastResult){
    this.lastResult = lastResult;
  }
}
