// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;

import java.util.List;
import java.util.function.Predicate;

public class OOSymbolsScope extends OOSymbolsScopeTOP {

  public OOSymbolsScope() {
    super();
  }

  public OOSymbolsScope(boolean isShadowingScope) {
    super(isShadowingScope);
  }

  public OOSymbolsScope(IOOSymbolsScope enclosingScope) {
    this(enclosingScope, false);
  }

  public OOSymbolsScope(IOOSymbolsScope enclosingScope, boolean isShadowingScope) {
    super(enclosingScope,isShadowingScope);
  }

  /**
   * override method from ExpressionBasisScope to resolve all methods correctly
   * method needed to be overridden because of special cases: if the scope is spanned by a type symbol you have to look for fitting methods in its super types too because of inheritance
   * the method resolves the methods like the overridden method and if the spanning symbol is a type symbol it additionally looks for methods in its super types
   * it is used by the method getMethodList in SymTypeExpression
   */
  @Override
  public List<MethodSymbol> resolveMethodLocallyMany(boolean foundSymbols, String name, AccessModifier modifier,
                                                     Predicate<MethodSymbol> predicate) {
    //resolve methods by using overridden method
    List<MethodSymbol> set = super.resolveMethodLocallyMany(foundSymbols,name,modifier,predicate);
    if(this.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = getSpanningSymbol();
      //if the methodsymbol is in the spanned scope of a typesymbol then look for method in super types too
      if(spanningSymbol instanceof OOTypeSymbol){
        OOTypeSymbol typeSymbol = ((OOTypeSymbol) spanningSymbol);
        for(SymTypeExpression t : typeSymbol.getSuperTypesList()){
          set.addAll(t.getMethodList(name));
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
  public List<FieldSymbol> resolveFieldLocallyMany(boolean foundSymbols,String name,AccessModifier modifier,Predicate predicate){
    //resolve methods by using overridden method
    List<FieldSymbol> result = super.resolveFieldLocallyMany(foundSymbols,name,modifier,predicate);
    if(this.isPresentSpanningSymbol()){
      IScopeSpanningSymbol spanningSymbol = getSpanningSymbol();
      //if the fieldsymbol is in the spanned scope of a typesymbol then look for method in super types too
      if(spanningSymbol instanceof OOTypeSymbol){
        OOTypeSymbol typeSymbol = (OOTypeSymbol) spanningSymbol;
        for(SymTypeExpression superType : typeSymbol.getSuperTypesList()){
          result.addAll(superType.getFieldList(name));
        }
      }
    }
    return result;
  }

  @Override
  public List<OOTypeSymbol> resolveOOTypeLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate predicate){
    List<OOTypeSymbol> result = super.resolveOOTypeLocallyMany(foundSymbols,name,modifier,predicate);
    //    TODO ND: uncomment when adding inner types
    //    if(this.isPresentSpanningSymbol()){
    //      IScopeSpanningSymbol spanningSymbol = getSpanningSymbol();
    //      if(spanningSymbol instanceof TypeSymbol){
    //        TypeSymbol typeSymbol = (TypeSymbol) spanningSymbol;
    //        for(SymTypeExpression superType : typeSymbol.getSuperTypeList()){
    //          result.addAll(superType.getInnerTypeList(name));
    //        }
    //      }
    //    }
    return result;
  }
}
