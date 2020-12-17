/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

public class SymTypeOfWildcardDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeOfWildcard";

  public String serialize(SymTypeOfWildcard toSerialize) {
    return toSerialize.printAsJson();
  }

  public SymTypeOfWildcard deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeOfWildcard deserialize(JsonObject serialized) {
    if (serialized.hasBooleanMember("isUpper")) {
      boolean isUpper = serialized.getBooleanMember("isUpper");
      JsonElement boundString = serialized.getMember("bound");
      if (boundString != null) {
        SymTypeExpression bound = SymTypeExpressionDeSer.getInstance().deserialize(boundString);
        return SymTypeExpressionFactory.createWildcard(isUpper, bound);
      }
      return SymTypeExpressionFactory.createWildcard();
    }
    Log.error("0x823F7 Internal error: Cannot load \"" + serialized + "\" as  SymTypeOfWildcard!");
    return null;
  }

}
