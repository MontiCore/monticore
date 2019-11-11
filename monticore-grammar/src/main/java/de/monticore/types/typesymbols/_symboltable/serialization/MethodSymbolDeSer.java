/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typesymbols._symboltable.serialization;

import de.monticore.symboltable.serialization.ListDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class MethodSymbolDeSer extends MethodSymbolDeSerTOP {
  
  /**
   * @see de.monticore.types.typesymbols._symboltable.serialization.TypeSymbolDeSerTOP#deserializeSuperTypes(de.monticore.symboltable.serialization.json.JsonObject)
   */
  @Override
  protected SymTypeExpression deserializeReturnType(JsonObject symbolJson) {
    return SymTypeExpressionDeSer.getInstance().deserialize(symbolJson.getMember("returnType"));
  }
  
  @Override
  protected java.util.List<de.monticore.types.typesymbols._symboltable.FieldSymbol> deserializeParameter(JsonObject symbolJson) {
    return ListDeSer.of(new FieldSymbolDeSer()).deserialize(symbolJson.getMember("parameter"));
  }
  
}
