/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.*;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static de.monticore.types.check.SymTypeExpressionFactory.*;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in ExpressionsBasis
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfExpression implements ExpressionsBasisVisitor {
  
  public ITypeSymbolsScope getScope (IExpressionsBasisScope expressionsBasisScope){
    // is accepted only here, decided on 07.04.2020
    if(!(expressionsBasisScope instanceof ITypeSymbolsScope)){
      Log.error("0xA0307 the enclosing scope of the expression does not implement the interface ITypeSymbolsScope");
    }
    // is accepted only here, decided on 07.04.2020
    return (ITypeSymbolsScope) expressionsBasisScope;
  }

  protected TypeCheckResult typeCheckResult;

  private ExpressionsBasisVisitor realThis;

  protected static final String ERROR_MSG = " The expression at source position %s cannot be calculated.";

  @Override
  public void setRealThis(ExpressionsBasisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public ExpressionsBasisVisitor getRealThis() {
    return realThis;
  }

  public DeriveSymTypeOfExpression() {
    realThis = this;
  }

  @Override
  public void traverse(ASTLiteralExpression expr) {
    SymTypeExpression wholeResult = null;
    //get the type of the literal
    expr.getLiteral().accept(getRealThis());
    if (typeCheckResult.isPresentLast()) {
      wholeResult = typeCheckResult.getLast();
    }
    if (wholeResult != null) {
      typeCheckResult.setLast(wholeResult);
    } else {
      //No type found --> error
      typeCheckResult.reset();

      logError("0xA0250",expr.get_SourcePositionStart());
    }
  }

  @Override
  public void traverse(ASTNameExpression expr) {
    Optional<FieldSymbol> optVar = getScope(expr.getEnclosingScope()).resolveField(expr.getName());
    Optional<TypeSymbol> optType = getScope(expr.getEnclosingScope()).resolveType(expr.getName());
    if (optVar.isPresent()) {
      //no method here, test variable first
      // durch AST-Umbau kann ASTNameExpression keine Methode sein
      FieldSymbol var = optVar.get();
      SymTypeExpression res;
      if (var.getType() instanceof SymTypeOfGenerics) {
        res = createGenerics(((SymTypeOfGenerics) var.getType()).getTypeConstructorFullName(), var.getType().getTypeInfo().getEnclosingScope(),
            ((SymTypeOfGenerics) var.getType()).getArgumentList());
      } else if (var.getType() instanceof SymTypeArray) {
        res = createTypeArray(((SymTypeArray) var.getType()).getArgument().getTypeInfo().getName(), var.getType().getTypeInfo().getEnclosingScope(),
            ((SymTypeArray) var.getType()).getDim(),((SymTypeArray) var.getType()).getArgument());
      } else {
        res = createTypeExpression(var.getType().print(), var.getType().getTypeInfo().getEnclosingScope());
      }
      typeCheckResult.setField();
      typeCheckResult.setLast(res);
    } else if (optType.isPresent()) {
      //no variable found, test if name is type
      TypeSymbol type = optType.get();
      SymTypeExpression res = createTypeExpression(type.getName(), type.getEnclosingScope());
      typeCheckResult.setType();
      typeCheckResult.setLast(res);
    }else{
     //name not found --> package or nothing
     typeCheckResult.reset();
      Log.info("package suspected", "DeriveSymTypeOfExpression");
    }
  }

  public void setTypeCheckResult(TypeCheckResult typeCheckResult) {
    this.typeCheckResult = typeCheckResult;
  }

  protected void logError(String errorCode, SourcePosition start){
    Log.error(errorCode+String.format(ERROR_MSG, start));
  }
}
