/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

public class SymTypeOfRegExDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeOfRegEx";
  protected static final String SERIALIZED_REGEX = "regex";

  public String serialize(SymTypeOfRegEx toSerialize) {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, SERIALIZED_KIND);
    jp.member(SERIALIZED_REGEX, toSerialize.getRegExString());
    jp.endObject();
    return jp.getContent();
  }

  public SymTypeOfRegEx deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeOfRegEx deserialize(JsonObject serialized) {
    if (serialized.hasMember(SERIALIZED_REGEX)) {
      String regex = serialized.getStringMember(SERIALIZED_REGEX);
      return SymTypeExpressionFactory.createTypeRegEx(regex);
    }
    Log.error(
        "0x9E2F9 Internal error: Loading ill-structured SymTab: missing "
            + SERIALIZED_REGEX
            + "of SymTypeOfRegEx "
            + serialized);
    return null;
  }
}
