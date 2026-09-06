/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class FunctionSymbolDeSer extends FunctionSymbolDeSerTOP {
  
  @Override
  public SymTypeExpression deserializeType(JsonObject symbolJson) {
    // support deprecated behavior
    return deserializeType(null, symbolJson);
  }

  @Override
  public SymTypeExpression deserializeType(
      IBasicSymbolsScope scope, JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeMember("type", symbolJson, scope);
  }

}
