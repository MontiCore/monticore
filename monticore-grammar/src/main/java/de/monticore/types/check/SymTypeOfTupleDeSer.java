/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class SymTypeOfTupleDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeOfTuple";
  protected static final String SERIALIZED_TYPES = "listedTypes";

  public String serialize(SymTypeOfTuple toSerialize) {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, SERIALIZED_KIND);

    jp.beginArray(SERIALIZED_TYPES);
    toSerialize.streamTypes()
        .map(SymTypeExpression::printAsJson)
        .forEach(jp::valueJson);
    jp.endArray();

    jp.endObject();
    return jp.getContent();
  }

  public SymTypeOfTuple deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeOfTuple deserialize(JsonObject serialized) {
    if (serialized.hasMember(SERIALIZED_TYPES)) {
      List<SymTypeExpression> typesList =
          SymTypeExpressionDeSer.deserializeListMember(SERIALIZED_TYPES, serialized);
      return SymTypeExpressionFactory.createTuple(typesList);
    }
    Log.error(
        "0x9E2F8 internal error: Loading ill-structured SymTab: missing "
            + SERIALIZED_TYPES
            + "of SymTypeOfTuple "
            + serialized);
    return null;
  }
}
