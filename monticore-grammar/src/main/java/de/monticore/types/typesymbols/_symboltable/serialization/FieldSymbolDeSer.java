/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typesymbols._symboltable.serialization;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class FieldSymbolDeSer extends FieldSymbolDeSerTOP {
  
  /**
   * @see de.monticore.types.typesymbols._symboltable.serialization.TypeSymbolDeSerTOP#deserializeSuperTypes(de.monticore.symboltable.serialization.json.JsonObject)
   */
  @Override
  protected SymTypeExpression deserializeType(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.getInstance().deserialize(symbolJson.getMember("type"));
  }
  
}
