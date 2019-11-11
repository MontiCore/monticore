/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typesymbols._symboltable.serialization;

import de.monticore.symboltable.serialization.ListDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class TypeVarSymbolDeSer extends TypeVarSymbolDeSerTOP {
  
  /**
   * @see de.monticore.types.typesymbols._symboltable.serialization.TypeSymbolDeSerTOP#deserializeSuperTypes(de.monticore.symboltable.serialization.json.JsonObject)
   */
  @Override
  protected java.util.List<SymTypeExpression> deserializeUpperBound(JsonObject symbolJson) {
    return ListDeSer.of(SymTypeExpressionDeSer.getInstance()).deserialize(symbolJson.getMember("upperBound"));
  }
  
}
