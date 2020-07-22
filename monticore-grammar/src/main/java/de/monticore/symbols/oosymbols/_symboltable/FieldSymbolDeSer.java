// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class FieldSymbolDeSer extends FieldSymbolDeSerTOP {

  @Override
  public SymTypeExpression deserializeType(JsonObject symbolJson,
      IOOSymbolsScope enclosingScope) {
    return SymTypeExpressionDeSer.deserializeMember("type", symbolJson, enclosingScope);
  }

}
