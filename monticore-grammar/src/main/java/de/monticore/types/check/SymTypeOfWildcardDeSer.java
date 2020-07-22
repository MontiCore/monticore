/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.se_rwth.commons.logging.Log;

public class SymTypeOfWildcardDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeOfWildcard";

  public String serialize(SymTypeOfWildcard toSerialize) {
    return toSerialize.printAsJson();
  }

  public SymTypeOfWildcard deserialize(String serialized, IOOSymbolsScope enclosingScope) {
    return deserialize(JsonParser.parse(serialized), enclosingScope);
  }

  public SymTypeOfWildcard deserialize(JsonElement serialized, IOOSymbolsScope enclosingScope) {
    if (JsonDeSers.isCorrectDeSerForKind(SERIALIZED_KIND, serialized)) {
      JsonObject o = serialized.getAsJsonObject();  //if it has a kind, it is an object
      boolean isUpper = o.getBooleanMember("isUpper");
      JsonElement boundString = o.getMember("bound");
      if(boundString!=null){
        SymTypeExpression bound = SymTypeExpressionDeSer.getInstance()
            .deserialize(boundString, enclosingScope);
        return SymTypeExpressionFactory.createWildcard(isUpper,bound);
      }
      return SymTypeExpressionFactory.createWildcard();
    }
    Log.error("0x823F7 Internal error: Cannot load \"" + serialized + "\" as  SymTypeOfWildcard!");
    return null;
  }

}
