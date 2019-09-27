package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;

import java.util.List;
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

  public List<MethodSymbol> resolveMethodLocallyMany(boolean foundSymbols, String name, AccessModifier modifier,
                                                     Predicate<MethodSymbol> predicate) {
    List<MethodSymbol> set = super.resolveMethodLocallyMany(foundSymbols,name,modifier,predicate);
    if(this.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = getSpanningSymbol();
      if(spanningSymbol instanceof TypeSymbol){
        TypeSymbol typeSymbol = (TypeSymbol) spanningSymbol;
        for(SymTypeExpression t : typeSymbol.getSuperTypeList()){
          //TODO: for every SymTypeExpression
          set.addAll(t.getMethodList(name));
        }
      }
    }
    return set;
  }

}
