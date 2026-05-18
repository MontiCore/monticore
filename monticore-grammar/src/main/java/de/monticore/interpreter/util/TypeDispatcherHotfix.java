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
 * https://git.rwth-aachen.de/monticore/monticore/-/work_items/5031
 */
public class TypeDispatcherHotfix {

  // ugly, but only a hotfix, to be removed

  protected static final String J_METHOD_ADAPTER_NAME =
      "de.monticore.class2mc.adapters.JMethod2FunctionSymbolAdapter";

  protected static final String J_FIELD_VARIABLE_ADAPTER_NAME =
      "de.monticore.class2mc.adapters.JField2VariableSymbolAdapter";

  protected static final String J_FIELD_FIELD_ADAPTER_NAME =
      "de.monticore.class2mc.adapters.JField2FieldSymbolAdapter";

  public static boolean isFunctionSymbol(ISymbol symbol) {
    return BasicSymbolsMill.typeDispatcher().isBasicSymbolsFunction(symbol)
        || symbol instanceof FunctionSymbol
        || isAdapterInstance(symbol, J_METHOD_ADAPTER_NAME)
        || isMethodSymbol(symbol);
  }

  public static boolean isMethodSymbol(ISymbol symbol) {
    return OOSymbolsMill.typeDispatcher().isOOSymbolsMethod(symbol)
        || symbol instanceof MethodSymbol
        || isAdapterInstance(symbol, J_METHOD_ADAPTER_NAME);
  }

  public static boolean isVariableSymbol(ISymbol symbol) {
    return BasicSymbolsMill.typeDispatcher().isBasicSymbolsVariable(symbol)
        || symbol instanceof VariableSymbol
        || isAdapterInstance(symbol, J_FIELD_VARIABLE_ADAPTER_NAME)
        || isFieldSymbol(symbol);
  }

  public static boolean isFieldSymbol(ISymbol symbol) {
    return OOSymbolsMill.typeDispatcher().isOOSymbolsField(symbol)
        || symbol instanceof FieldSymbol
        || isAdapterInstance(symbol, J_FIELD_FIELD_ADAPTER_NAME);
  }

  // remove dependency to class2mc by lazy loading the classes
  protected static boolean isAdapterInstance(
      ISymbol symbol,
      String adapterClassName
  ) {
    try {
      Class<?> adapterClass = Class.forName(adapterClassName);
      return adapterClass.isInstance(symbol);
    }
    catch (ClassNotFoundException e) {
      return false;
    }
  }

}
