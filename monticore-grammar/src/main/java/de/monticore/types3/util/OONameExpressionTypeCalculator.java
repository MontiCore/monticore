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
public class OONameExpressionTypeCalculator
    extends NameExpressionTypeCalculator {

  public OONameExpressionTypeCalculator() {
    // default values
    super(
        new TypeContextCalculator(),
        new OOWithinTypeBasicSymbolsResolver()
    );
  }

  /**
   * filter out any constructors
   */
  @Override
  protected Predicate<FunctionSymbol> getFunctionPredicate() {
    return f -> {
      if (OOSymbolsMill.typeDispatcher().isMethod(f)) {
        MethodSymbol m = OOSymbolsMill.typeDispatcher().asMethod(f);
        if (m.isIsConstructor()) {
          return false;
        }
      }
      return true;
    };
  }

}
