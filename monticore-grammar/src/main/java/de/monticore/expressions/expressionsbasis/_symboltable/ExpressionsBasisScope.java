package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

public class ExpressionsBasisScope extends ExpressionsBasisScopeTOP {

  public ExpressionsBasisScope() {
    super();
  }

  public ExpressionsBasisScope(boolean isShadowingScope) {
    super(isShadowingScope);
  }

  public ExpressionsBasisScope(IExpressionsBasisScope enclosingScope) {
    this(enclosingScope, false);
  }

  public ExpressionsBasisScope(IExpressionsBasisScope enclosingScope, boolean isShadowingScope) {
    super(enclosingScope,isShadowingScope);
  }

  public Set<MethodSymbol> resolveMethodLocallyMany(boolean foundSymbols, String name, AccessModifier modifier,
                                                     Predicate<MethodSymbol> predicate) {
    Set<MethodSymbol> set = super.resolveMethodLocallyMany(foundSymbols,name,modifier,predicate);
    if(this.isSpannedBySymbol()){
      IScopeSpanningSymbol spanningSymbol = getSpanningSymbol().get();
      if(spanningSymbol instanceof TypeSymbol){
        TypeSymbol typeSymbol = (TypeSymbol) spanningSymbol;
        for(SymTypeExpression t : typeSymbol.getSuperTypes()){
          //TODO: for every SymTypeExpression
          set.addAll(t.getTypeInfo().getSpannedScope().resolveMethodMany(foundSymbols, name, modifier, predicate));
        }
      }else if(spanningSymbol instanceof FieldSymbol){
        FieldSymbol fieldSymbol = (FieldSymbol) spanningSymbol;
        for(SymTypeExpression t: fieldSymbol.getType().getTypeInfo().getSuperTypes()){
          //TODO: for every SymTypeExpression
          set.addAll(t.getTypeInfo().getSpannedScope().resolveMethodMany(foundSymbols, name, modifier, predicate));
        }
      }else if(spanningSymbol instanceof MethodSymbol){
        MethodSymbol methodSymbol = (MethodSymbol) spanningSymbol;
        for(SymTypeExpression t: methodSymbol.getReturnType().getTypeInfo().getSuperTypes()){
          //TODO: for every SymTypeExpression
          set.addAll(t.getTypeInfo().getSpannedScope().resolveMethodMany(foundSymbols, name, modifier, predicate));
        }
      }
    }

  }

}
