/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typesymbols._symboltable.serialization;

import de.monticore.symboltable.serialization.ListDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;

public class MethodSymbolDeSer extends MethodSymbolDeSerTOP {
  
  @Override
  protected SymTypeExpression deserializeReturnType(JsonObject symbolJson, ITypeSymbolsScope enclosingScope) {
    return SymTypeExpressionDeSer.getInstance().deserialize(symbolJson.getMember("returnType"), enclosingScope);
  }
  
  @Override
  protected java.util.List<de.monticore.types.typesymbols._symboltable.FieldSymbol> deserializeParameter(JsonObject symbolJson, ITypeSymbolsScope enclosingScope) {
    return ListDeSer.of(new FieldSymbolDeSer()).deserialize(symbolJson.getMember("parameter"), enclosingScope);
  }
  
}
