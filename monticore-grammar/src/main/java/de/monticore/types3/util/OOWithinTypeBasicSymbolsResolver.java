// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.StaticAccessModifier;
import de.monticore.types3.SymTypeRelations;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * resolves within a type,
 * unlike {@link WithinTypeBasicSymbolsResolver},
 * we further filter by "OO-rules",
 * e.g., a constructor cannot be called like other methods
 */
public class OOWithinTypeBasicSymbolsResolver
    extends WithinTypeBasicSymbolsResolver {

  public OOWithinTypeBasicSymbolsResolver(SymTypeRelations symTypeRelations) {
    super(symTypeRelations);
  }

  public OOWithinTypeBasicSymbolsResolver() {
    super();
  }

  // Helper

  /**
   * resolves locally, EXCLUDING supertypes
   * this filters out constructors
   */
  @Override
  protected List<FunctionSymbol> resolveFunctionLocally(
      IBasicSymbolsScope scope,
      String name,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate) {
    return super.resolveFunctionLocally(
        scope, name, accessModifier,
        predicate.and(Predicate.not(this::isConstructor))
    );
  }

  /**
   * same as {@link #resolveFunctionLocally}
   * but does only returns constructors
   */
  public List<FunctionSymbol> resolveConstructorLocally(
      IBasicSymbolsScope scope,
      String name,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate
  ) {
    return super.resolveFunctionLocally(
        scope, name,
        removeStaticness(accessModifier),
        predicate.and(this::isConstructor)
    );
  }

  // Helper

  protected boolean isConstructor(FunctionSymbol func) {
    if (OOSymbolsMill.typeDispatcher().isMethod(func)) {
      MethodSymbol method =
          OOSymbolsMill.typeDispatcher().asMethod(func);
      return method.isIsConstructor();
    }
    return false;
  }

  /**
   * replaces any static/non-static access with all access
   * this is done as we want to ignore isStatic in constructors
   * There are some languages, where this distinction is relevant (e.g., C#)
   */
  protected AccessModifier removeStaticness(AccessModifier accessModifier) {
    AccessModifier newModifier = accessModifier.shallowCopy();
    Map<String, AccessModifier> map = newModifier.getDimensionToModifierMap();
    map.remove(StaticAccessModifier.DIMENSION);
    return newModifier;
  }

}
