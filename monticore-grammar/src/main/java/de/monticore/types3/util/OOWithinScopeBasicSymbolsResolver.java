// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.function.Predicate;

/**
 * contains the code to derive / synthesize the type of a single name,
 * but is "OO-aware", e.g., constructors are filtered out.
 */
public class OOWithinScopeBasicSymbolsResolver
    extends WithinScopeBasicSymbolsResolver {

  public static void init() {
    Log.trace("init OOWithinScopeBasicSymbolsResolver", "TypeCheck setup");
    setDelegate(new OOWithinScopeBasicSymbolsResolver());
  }

  public static void reset() {
    WithinScopeBasicSymbolsResolver.reset();
  }

  /**
   * filter out any constructors
   */
  @Override
  protected Predicate<FunctionSymbol> getFunctionPredicate() {
    return Predicate.not(getIsConstructorPredicate());
  }

  // Helper

  protected Predicate<FunctionSymbol> getIsConstructorPredicate() {
    return f -> {
      if (OOSymbolsMill.typeDispatcher().isOOSymbolsMethod(f)) {
        MethodSymbol m = OOSymbolsMill.typeDispatcher().asOOSymbolsMethod(f);
        if (m.isIsConstructor()) {
          return true;
        }
      }
      return false;
    };
  }

}
