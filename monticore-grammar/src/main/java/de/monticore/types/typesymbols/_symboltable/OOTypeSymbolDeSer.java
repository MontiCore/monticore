/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typesymbols._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.List;

public class OOTypeSymbolDeSer extends OOTypeSymbolDeSerTOP {

  @Override
  public List<SymTypeExpression> deserializeSuperTypes(JsonObject symbolJson,
      ITypeSymbolsScope enclosingScope) {
    return SymTypeExpressionDeSer.deserializeListMember("superTypes", symbolJson, enclosingScope);
  }

}
