// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.streams;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Factory for StreamTypes
 * (ONLY) convenience methods for
 * {@link SymTypeExpressionFactory}
 */
public class StreamSymTypeFactory {

  public static SymTypeOfGenerics createStream(SymTypeExpression innerType) {
    return createStreamType("Stream", innerType);
  }

  public static SymTypeOfGenerics createEventStream(SymTypeExpression innerType) {
    return createStreamType("EventStream", innerType);
  }

  public static SymTypeOfGenerics createSyncStream(SymTypeExpression innerType) {
    return createStreamType("SyncStream", innerType);
  }

  public static SymTypeOfGenerics createToptStream(SymTypeExpression innerType) {
    return createStreamType("ToptStream", innerType);
  }

  public static SymTypeOfGenerics createUntimedStream(SymTypeExpression innerType) {
    return createStreamType("UntimedStream", innerType);
  }

  // Helper

  protected static SymTypeOfGenerics createStreamType(
      String name,
      SymTypeExpression innerType
  ) {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    Optional<TypeSymbol> typeSymbol = gs.resolveType(name);
    if (typeSymbol.isPresent()) {
      return SymTypeExpressionFactory.createGenerics(typeSymbol.get(), innerType);
    }
    else {
      Log.error("0xFD296 unable to resolve type "
          + name + ", unable to create the stream type.");
      return null;
    }
  }

}
