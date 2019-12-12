/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.*;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static de.monticore.types.check.SymTypeExpressionFactory.*;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in ExpressionsBasis
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfExpression implements ExpressionsBasisVisitor {

  protected IExpressionsBasisScope scope;

  protected SymTypeExpression result;

  protected LastResult lastResult;

  private ExpressionsBasisVisitor realThis;

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
    SymTypeExpression result = null;
    //get the type of the literal
    expr.getLiteral().accept(getRealThis());
    if (lastResult.isPresentLast()) {
      result = lastResult.getLast();
    }
    if (result != null) {
      this.result = result;
      lastResult.setLast(result);
    } else {
      //No type found --> error
      lastResult.setLastAbsent();
      Log.error("0xA0207 The resulting type of the LiteralExpression cannot be calculated");
    }
  }

  @Override
  public void traverse(ASTNameExpression expr) {
    Optional<FieldSymbol> optVar = scope.resolveField(expr.getName());
    Optional<TypeSymbol> optType = scope.resolveType(expr.getName());
    if (optVar.isPresent()) {
      //no method here, test variable first
      // durch AST-Umbau kann ASTNameExpression keine Methode sein
      FieldSymbol var = optVar.get();
      SymTypeExpression res;
      if (var.getType() instanceof SymTypeOfGenerics) {
        res = createGenerics(((SymTypeOfGenerics) var.getType()).getTypeConstructorFullName(), var.getEnclosingScope(),
            ((SymTypeOfGenerics) var.getType()).getArgumentList());
      } else if (var.getType() instanceof SymTypeArray) {
        res = createTypeArray(((SymTypeArray) var.getType()).getArgument().getTypeInfo().getName(), var.getEnclosingScope(),
            ((SymTypeArray) var.getType()).getDim(),((SymTypeArray) var.getType()).getArgument());
      } else {
        res = createTypeExpression(var.getType().print(), var.getEnclosingScope());
      }
      this.result = res;
      lastResult.setLast(res);
    } else if (optType.isPresent()) {
      //no variable found, test if name is type
      TypeSymbol type = optType.get();
      SymTypeExpression res = createTypeExpression(type.getName(), expr.getEnclosingScope());
      this.result = res;
      lastResult.setLast(res);
    }else{
     //name not found --> package or nothing
     lastResult.setLastAbsent();
      Log.info("package suspected", "ExpressionBasisTypesCalculator");
    }
  }

  public void setScope(IExpressionsBasisScope scope) {
    this.scope = scope;
  }

  public void setLastResult(LastResult lastResult) {
    this.lastResult = lastResult;
  }
}
