/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.*;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static de.monticore.types.check.SymTypeExpressionFactory.*;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in ExpressionsBasis
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 * @deprecated Use {@link de.monticore.types3.TypeCheck3} instead.
 */
@Deprecated(forRemoval = true)
public class DeriveSymTypeOfExpression extends AbstractDeriveFromExpression implements ExpressionsBasisVisitor2, ExpressionsBasisHandler {

  public IBasicSymbolsScope getScope (IExpressionsBasisScope expressionsBasisScope){
    // is accepted only here, decided on 07.04.2020
    if(!(expressionsBasisScope instanceof IBasicSymbolsScope)){
      Log.error("0xA2308 the enclosing scope of the expression does not implement the interface IBasicSymbolsScope");
    }
    // is accepted only here, decided on 07.04.2020
    return (IBasicSymbolsScope) expressionsBasisScope;
  }

  protected ExpressionsBasisTraverser traverser;

  @Override
  public ExpressionsBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(ExpressionsBasisTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ASTNameExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateNameExpression(expr);
    if(wholeResult.isPresent()){
      getTypeCheckResult().setResult(wholeResult.get());
    }else{
     getTypeCheckResult().reset();
     getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
     Log.error("0xA0240 Cannot find symbol " + expr.getName(), expr.get_SourcePositionStart());
    }
  }

  protected Optional<SymTypeExpression> calculateNameExpression(ASTNameExpression expr){
    Optional<VariableSymbol> optVar = getScope(expr.getEnclosingScope()).resolveVariable(expr.getName());
    Optional<TypeVarSymbol> optTypeVar = getScope(expr.getEnclosingScope()).resolveTypeVar(expr.getName());
    Optional<TypeSymbol> optType = getScope(expr.getEnclosingScope()).resolveType(expr.getName());
    if("null".equals(expr.getName())){
      SymTypeExpression res = createTypeOfNull();
      expr.setDefiningSymbol(res.getTypeInfo());
      return Optional.of(res);
    }else if (optVar.isPresent()) {
      //no method here, test variable first
      // durch AST-Umbau kann ASTNameExpression keine Methode sein
      VariableSymbol var = optVar.get();
      expr.setDefiningSymbol(var);
      SymTypeExpression res = var.getType().deepClone();
      getTypeCheckResult().setField();
      return Optional.of(res);
    } else if(optTypeVar.isPresent()) {
      TypeVarSymbol typeVar = optTypeVar.get();
      expr.setDefiningSymbol(typeVar);
      SymTypeExpression res = createTypeVariable(typeVar);
      getTypeCheckResult().setType();
      return Optional.of(res);
    } else if (optType.isPresent()) {
      //no variable found, test if name is type
      TypeSymbol type = optType.get();
      expr.setDefiningSymbol(type);
      SymTypeExpression res = createTypeExpression(type);
      getTypeCheckResult().setType();
      return Optional.of(res);
    }
    return Optional.empty();
  }

}
