/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class TypeVarSymbolDeSer extends TypeVarSymbolDeSerTOP {
  
  @Override
  public List<SymTypeExpression> deserializeSuperTypes(JsonObject symbolJson) {
    // support deprecated behavior
    return deserializeSuperTypes(null, symbolJson);
  }

  @Override
  public List<SymTypeExpression> deserializeSuperTypes(
      IBasicSymbolsScope scope, JsonObject symbolJson) {
    return SymTypeExpressionDeSer.deserializeListMember("superTypes", symbolJson, scope);
  }

}
