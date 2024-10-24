// (c) https://github.com/MontiCore/monticore
package de.monticore.types.mccollectiontypes.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Factory for MCCollectionTypes
 * (ONLY) convenience methods for
 * {@link de.monticore.types.check.SymTypeExpressionFactory}
 */
public class MCCollectionSymTypeFactory {

  public static SymTypeOfGenerics createList(SymTypeExpression innerType) {
    return createCollectionType("List", "java.util.List", innerType);
  }

  public static SymTypeOfGenerics createSet(SymTypeExpression innerType) {
    return createCollectionType("Set", "java.util.Set", innerType);
  }

  public static SymTypeOfGenerics createOptional(SymTypeExpression innerType) {
    return createCollectionType("Optional", "java.util.Optional", innerType);
  }

  public static SymTypeOfGenerics createMap(
      SymTypeExpression keyType,
      SymTypeExpression valueType
  ) {
    return createCollectionType("Map", "java.util.Map", keyType, valueType);
  }

  // Helper

  protected static SymTypeOfGenerics createCollectionType(
      String unboxedName,
      String boxedName,
      SymTypeExpression... innerTypes
  ) {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    Optional<TypeSymbol> typeSymbol = gs.resolveType(unboxedName);
    if (typeSymbol.isEmpty()) {
      typeSymbol = gs.resolveType(boxedName);
    }
    if (typeSymbol.isPresent()) {
      return SymTypeExpressionFactory.createGenerics(typeSymbol.get(), innerTypes);
    }
    else {
      Log.error("0xFD299 unable to resolve type "
          + unboxedName + ", unable to create the collection type.");
      return null;
    }
  }

}
