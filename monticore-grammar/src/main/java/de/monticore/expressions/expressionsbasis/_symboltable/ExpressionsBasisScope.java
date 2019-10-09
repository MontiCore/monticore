package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;

import java.util.List;
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

  /**
   * override method from ExpressionBasisScope to resolve methods correctly
   */
  @Override
  public List<MethodSymbol> resolveMethodLocallyMany(boolean foundSymbols, String name, AccessModifier modifier,
                                                     Predicate<MethodSymbol> predicate) {
    //resolve methods by using overridden method
    List<MethodSymbol> set = super.resolveMethodLocallyMany(foundSymbols,name,modifier,predicate);
    if(this.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = getSpanningSymbol();
      //if the methodsymbol is in the spanned scope of a typesymbol then look for method in super types too
      if(spanningSymbol instanceof TypeSymbol){
        TypeSymbol typeSymbol = (TypeSymbol) spanningSymbol;
        for(SymTypeExpression t : typeSymbol.getSuperTypeList()){
          set.addAll(t.getMethodList(name));
        }
      }
    }
    return set;
  }

  /**
   * override method from ExpressionBasisScope to resolve fields correctly
   */
  @Override
  public List<FieldSymbol> resolveFieldLocallyMany(boolean foundSymbols,String name,AccessModifier modifier,Predicate predicate){
    //resolve methods by using overridden method
    List<FieldSymbol> result = super.resolveFieldLocallyMany(foundSymbols,name,modifier,predicate);
    if(this.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = getSpanningSymbol();
      //if the fieldsymbol is in the spanned scope of a typesymbol then look for method in super types too
      if(spanningSymbol instanceof TypeSymbol){
        TypeSymbol typeSymbol = (TypeSymbol) spanningSymbol;
        for(SymTypeExpression superType : typeSymbol.getSuperTypeList()){
          result.addAll(superType.getFieldList(name));
        }
      }
    }
    return result;
  }

  //TODO: resolve inner types
  @Override
  public List<TypeSymbol> resolveTypeLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate predicate){
    List<TypeSymbol> result = super.resolveTypeLocallyMany(foundSymbols,name,modifier,predicate);
    if(this.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = getSpanningSymbol();
      if(spanningSymbol instanceof TypeSymbol){

      }
    }

    return result;
  }

}
