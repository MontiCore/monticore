// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.ISymbol;

/**
 * todo remove this after
 * <a href="https://git.rwth-aachen.de/monticore/monticore/-/work_items/5031">...</a>
 */
public class TypeDispatcherHotfix {

  public static boolean isFunctionSymbol(ISymbol symbol) {
    return BasicSymbolsMill.typeDispatcher().isBasicSymbolsFunction(symbol)
        || symbol instanceof FunctionSymbol
        || isMethodSymbol(symbol);
  }

  public static boolean isMethodSymbol(ISymbol symbol) {
    return OOSymbolsMill.typeDispatcher().isOOSymbolsMethod(symbol)
        || symbol instanceof MethodSymbol;
  }

  public static boolean isVariableSymbol(ISymbol symbol) {
    return BasicSymbolsMill.typeDispatcher().isBasicSymbolsVariable(symbol)
        || symbol instanceof VariableSymbol
        || isFieldSymbol(symbol);
  }

  public static boolean isFieldSymbol(ISymbol symbol) {
    return OOSymbolsMill.typeDispatcher().isOOSymbolsField(symbol)
        || symbol instanceof FieldSymbol;
  }

}
