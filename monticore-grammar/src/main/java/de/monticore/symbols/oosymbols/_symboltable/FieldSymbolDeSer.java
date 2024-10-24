/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class FieldSymbolDeSer extends FieldSymbolDeSerTOP {

  @Override
  protected void serializeType(SymTypeExpression type, OOSymbolsSymbols2Json s2j) {
    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), "type", type);
  }

  @Override
  public SymTypeExpression deserializeType(JsonObject symbolJson) {
    // support deprecated behavior
    return deserializeType(null, symbolJson);
  }

  @Override
  public SymTypeExpression deserializeType(IOOSymbolsScope enclosingScope, JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeMember("type", symbolJson, enclosingScope);
  }

}
