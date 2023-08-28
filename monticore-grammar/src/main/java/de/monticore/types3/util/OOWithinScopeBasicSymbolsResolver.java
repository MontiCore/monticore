// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;

import java.util.function.Predicate;

/**
 * contains the code to derive / synthesize the type of a single name,
 * but is "OO-aware" e.g. constructors are filtered out.
 */
public class OOWithinScopeBasicSymbolsResolver
    extends WithinScopeBasicSymbolsResolver {

  protected OOWithinScopeBasicSymbolsResolver(
      TypeContextCalculator typeContextCalculator,
      WithinTypeBasicSymbolsResolver withinTypeBasicSymbolsResolver
  ) {
    super(
        typeContextCalculator,
        withinTypeBasicSymbolsResolver
    );
  }

  public OOWithinScopeBasicSymbolsResolver() {
    // default values
    this(
        new TypeContextCalculator(),
        new OOWithinTypeBasicSymbolsResolver()
    );
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
      if (OOSymbolsMill.typeDispatcher().isMethod(f)) {
        MethodSymbol m = OOSymbolsMill.typeDispatcher().asMethod(f);
        if (m.isIsConstructor()) {
          return true;
        }
      }
      return false;
    };
  }

}
