/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.combineexpressionswithliterals._symboltable;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;

import java.util.List;
import java.util.function.Predicate;

public class CombineExpressionsWithLiteralsScope extends CombineExpressionsWithLiteralsScopeTOP {

  public CombineExpressionsWithLiteralsScope() {
    super();
  }

  public CombineExpressionsWithLiteralsScope(boolean isShadowingScope) {
    super(isShadowingScope);
  }

  public CombineExpressionsWithLiteralsScope(ICombineExpressionsWithLiteralsScope enclosingScope) {
    this(enclosingScope, false);
  }

  public CombineExpressionsWithLiteralsScope(ICombineExpressionsWithLiteralsScope enclosingScope, boolean isShadowingScope) {
    super(enclosingScope,isShadowingScope);
  }

  /**
   * override method from ExpressionBasisScope to resolve all methods correctly
   * method needed to be overridden because of special cases: if the scope is spanned by a type symbol you have to look for fitting methods in its super types too because of inheritance
   * the method resolves the methods like the overridden method and if the spanning symbol is a type symbol it additionally looks for methods in its super types
   * it is used by the method getMethodList in SymTypeExpression
   */
  @Override
  public List<FunctionSymbol> resolveFunctionLocallyMany(boolean foundSymbols, String name, AccessModifier modifier,
                                                       Predicate<FunctionSymbol> predicate) {
    //resolve methods by using overridden method
    List<FunctionSymbol> set = super.resolveFunctionLocallyMany(foundSymbols,name,modifier,predicate);
    if(this.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = getSpanningSymbol();
      //if the methodsymbol is in the spanned scope of a typesymbol then look for method in super types too
      if(spanningSymbol instanceof TypeSymbol){
        TypeSymbol typeSymbol = ((TypeSymbol) spanningSymbol);
        for(SymTypeExpression t : typeSymbol.getSuperTypesList()){
          set.addAll(t.getMethodList(name, false));
        }
      }
    }
    return set;
  }

  /**
   * override method from ExpressionBasisScope to resolve all fields correctly
   * method needed to be overridden because of special cases: if the scope is spanned by a type symbol you have to look for fitting fields in its super types too because of inheritance
   * the method resolves the fields like the overridden method and if the spanning symbol is a type symbol it additionally looks for fields in its super types
   * it is used by the method getFieldList in SymTypeExpression
   */
  @Override
  public List<VariableSymbol> resolveVariableLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<VariableSymbol> predicate){
    //resolve methods by using overridden method
    List<VariableSymbol> result = super.resolveVariableLocallyMany(foundSymbols,name,modifier,predicate);
    if(this.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = getSpanningSymbol();
      //if the fieldsymbol is in the spanned scope of a typesymbol then look for method in super types too
      if(spanningSymbol instanceof TypeSymbol){
        TypeSymbol typeSymbol = (TypeSymbol) spanningSymbol;
        for(SymTypeExpression superType : typeSymbol.getSuperTypesList()){
          result.addAll(superType.getFieldList(name, false));
        }
      }
    }
    return result;
  }
}
