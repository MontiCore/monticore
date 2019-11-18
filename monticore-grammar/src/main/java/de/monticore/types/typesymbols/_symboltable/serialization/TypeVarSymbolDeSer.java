/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typesymbols._symboltable.serialization;

import de.monticore.symboltable.serialization.ListDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;

public class TypeVarSymbolDeSer extends TypeVarSymbolDeSerTOP {

  @Override
  protected java.util.List<SymTypeExpression> deserializeUpperBound(JsonObject symbolJson, ITypeSymbolsScope enclosingScope) {
    return ListDeSer.of(SymTypeExpressionDeSer.getInstance()).deserialize(symbolJson.getMember("upperBound"), enclosingScope);
  }
  
}
