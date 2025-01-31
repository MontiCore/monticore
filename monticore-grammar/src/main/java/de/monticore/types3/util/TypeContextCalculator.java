// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._util.IBasicSymbolsTypeDispatcher;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.modifiers.CompoundAccessModifier;
import de.monticore.symboltable.modifiers.StaticAccessModifier;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * provides context information for an expression.
 * first and foremost, whether the expression is
 * within the context of a type (e.g., in a method)
 */
public class TypeContextCalculator {

  // static delegate

  protected static TypeContextCalculator delegate;

  public static void init() {
    Log.trace("init default TypeContextCalculator", "TypeCheck setup");
    setDelegate(new TypeContextCalculator());
  }

  public static void reset() {
    TypeContextCalculator.delegate = null;
  }

  protected static void setDelegate(TypeContextCalculator newDelegate) {
    TypeContextCalculator.delegate = Log.errorIfNull(newDelegate);
  }

  protected static TypeContextCalculator getDelegate() {
    if (TypeContextCalculator.delegate == null) {
      init();
    }
    return TypeContextCalculator.delegate;
  }

  // methods

  /**
   * given an expression's enclosing scope, returns the enclosing type symbol
   */
  public static Optional<TypeSymbol> getEnclosingType(IScope enclosingScope) {
    return getDelegate()._getEnclosingType(enclosingScope);
  }

  protected Optional<TypeSymbol> _getEnclosingType(IScope enclosingScope) {
    Optional<TypeSymbol> enclosingType = Optional.empty();
    for (IScope scope = enclosingScope;
         scope != null && enclosingType.isEmpty();
         scope = scope.getEnclosingScope()) {
      if (scope.isPresentSpanningSymbol() &&
          getTypeDispatcher().isBasicSymbolsType(scope.getSpanningSymbol())) {
        enclosingType = Optional.of(
            getTypeDispatcher().asBasicSymbolsType(scope.getSpanningSymbol())
        );
      }
    }
    return enclosingType;
  }

  /**
   * given an expression's scope that accesses the type,
   * return the appropriate AccessModifier
   * e.g., an expression in a method can access private members,
   * if the method is static, only static members can be accessed
   * supported: visibility (private and protected), staticness,
   * missing: package_local
   * this function does NOT handle supertypes
   * ({@link WithinTypeBasicSymbolsResolver} handles supertypes)
   * for access via typeID, static access can be forced on (e.g., C.v)
   */
  public static AccessModifier getAccessModifier(
      TypeSymbol type,
      IScope enclosingScope,
      boolean forceStatic
  ) {
    return getDelegate()._getAccessModifier(type, enclosingScope, forceStatic);
  }

  public static AccessModifier getAccessModifier(
      TypeSymbol type,
      IScope enclosingScope) {
    return getAccessModifier(type, enclosingScope, false);
  }

  protected AccessModifier _getAccessModifier(
      TypeSymbol type,
      IScope enclosingScope,
      boolean forceStatic
  ) {
    boolean accessIsStatic = forceStatic;
    boolean accessIsStaticIfInType = false;
    boolean exprIsInType = false;
    for (IScope scope = enclosingScope;
         scope != null && !exprIsInType;
         scope = scope.getEnclosingScope()) {
      if (scope.isPresentSpanningSymbol()) {
        ISymbol spanningSymbol = scope.getSpanningSymbol();
        // static function?
        if (getTypeDispatcher().isBasicSymbolsFunction(spanningSymbol) &&
            getTypeDispatcher().asBasicSymbolsFunction(spanningSymbol)
                .getAccessModifier().getDimensionToModifierMap()
                .getOrDefault(StaticAccessModifier.DIMENSION, null)
                == StaticAccessModifier.STATIC) {
          accessIsStaticIfInType = true;
        }
        // todo reactivate after typedispatcher fix
        //else if (getTypeDispatcher().isType(spanningSymbol)) {
        //  TypeSymbol typeSymbol = getTypeDispatcher().asType(spanningSymbol);
        else if (spanningSymbol instanceof TypeSymbol) {
          TypeSymbol typeSymbol = (TypeSymbol) spanningSymbol;
          if (typeSymbol == type) {
            exprIsInType = true;
          }
          // static inner type
          if (!exprIsInType &&
              type.getAccessModifier().getDimensionToModifierMap()
                  .getOrDefault(StaticAccessModifier.DIMENSION, null)
                  == StaticAccessModifier.STATIC) {
            accessIsStaticIfInType = true;
          }
        }
      }
    }
    if (exprIsInType) {
      accessIsStatic |= accessIsStaticIfInType;
    }
    List<AccessModifier> modifiers = new ArrayList<>();
    if (exprIsInType) {
      modifiers.add(BasicAccessModifier.PRIVATE);
    }
    else {
      modifiers.add(BasicAccessModifier.PUBLIC);
    }
    if (accessIsStatic) {
      modifiers.add(StaticAccessModifier.STATIC);
    }
    return new CompoundAccessModifier(modifiers);
  }

  // Helper

  protected IBasicSymbolsTypeDispatcher getTypeDispatcher() {
    return BasicSymbolsMill.typeDispatcher();
  }

}
