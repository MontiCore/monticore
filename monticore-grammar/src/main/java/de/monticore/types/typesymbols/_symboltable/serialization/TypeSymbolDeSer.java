/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typesymbols._symboltable.serialization;

import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;

import java.util.ArrayList;
import java.util.List;

public class TypeSymbolDeSer extends TypeSymbolDeSerTOP {

  @Override
  protected List<SymTypeExpression> deserializeSuperTypes(JsonObject symbolJson, ITypeSymbolsScope enclosingScope) {
    List<SymTypeExpression> result = new ArrayList<>();
    for(JsonElement e : symbolJson.getMember("superTypes").getAsJsonArray().getValues()){
      result.add(SymTypeExpressionDeSer.getInstance().deserialize(e,enclosingScope));
    }
    return result;
  }
  
}
