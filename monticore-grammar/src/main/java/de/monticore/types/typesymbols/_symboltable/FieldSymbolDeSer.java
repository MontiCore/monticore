/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typesymbols._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;

public class FieldSymbolDeSer extends FieldSymbolDeSerTOP {
  
  @Override
  protected SymTypeExpression deserializeType(JsonObject symbolJson, ITypeSymbolsScope enclosingScope) {
    return SymTypeExpressionDeSer.getInstance().deserialize(symbolJson.getMember("type"), enclosingScope);
  }
  
}
