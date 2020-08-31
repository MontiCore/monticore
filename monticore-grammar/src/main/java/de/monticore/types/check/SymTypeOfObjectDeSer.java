/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

public class SymTypeOfObjectDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeOfObject";

  public String serialize(SymTypeOfObject toSerialize) {
    return toSerialize.printAsJson();
  }

  public SymTypeOfObject deserialize(String serialized, IOOSymbolsScope enclosingScope) {
    return deserialize(JsonParser.parseJsonObject(serialized), enclosingScope);
  }

  public SymTypeOfObject deserialize(JsonObject serialized, IOOSymbolsScope enclosingScope) {
    if (serialized.hasStringMember("objName")) {
      String objName = serialized.getStringMember("objName");
      return SymTypeExpressionFactory.createTypeObject(objName, enclosingScope);
    }
    Log.error("0x823F4 Internal error: Cannot load \""
        + serialized + "\" as  SymTypeOfObject!");
    return null;
  }
}
