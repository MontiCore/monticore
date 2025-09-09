/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

public class SymTypePrimitiveDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypePrimitive";
  protected static final String SERIALIZED_NAME = "primitiveName";

  public String serialize(SymTypePrimitive toSerialize) {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, SERIALIZED_KIND);
    jp.member(SERIALIZED_NAME, toSerialize.getPrimitiveName());
    jp.endObject();
    return jp.getContent();
  }

  public SymTypePrimitive deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypePrimitive deserialize(JsonObject serialized) {
    if (serialized.hasStringMember(SERIALIZED_NAME)) {
      String constName = serialized.getStringMember(SERIALIZED_NAME);
      return SymTypeExpressionFactory.createPrimitive(constName);
    }
    Log.error("0x823F1 Internal error: Cannot load \"" + serialized + "\" as  SymTypePrimitive!");
    return null;
  }
}
