/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typesymbols._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class MethodSymbolDeSer extends MethodSymbolDeSerTOP {

  @Override
  public SymTypeExpression deserializeReturnType(JsonObject symbolJson,
      ITypeSymbolsScope enclosingScope) {
    return SymTypeExpressionDeSer.deserializeMember("returnType", symbolJson, enclosingScope);
  }

}