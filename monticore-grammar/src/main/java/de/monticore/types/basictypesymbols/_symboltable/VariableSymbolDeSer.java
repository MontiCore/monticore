// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.basictypesymbols._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;

public class VariableSymbolDeSer extends VariableSymbolDeSerTOP {

  protected SymTypeExpression deserializeType(JsonObject symbolJson,
      ITypeSymbolsScope enclosingScope) {
    return SymTypeExpressionDeSer.deserializeMember("type", symbolJson, enclosingScope);
  }

}
