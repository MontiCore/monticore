/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of (static) methods that support using DeSers in combination with Json.
 */
public class JsonUtil {

  /**
   * This method deserializes a list of import statements in the passed Json object
   *
   * @param scope
   * @return
   */
  public static List<ImportStatement> deserializeImports(JsonObject scope) {
    List<ImportStatement> result = new ArrayList<>();
    if (scope.hasMember(JsonConstants.IMPORTS)) {
      for (JsonElement e : scope.getArrayMember(JsonConstants.IMPORTS)) {
        String i = e.getAsJsonString().getValue();
        result.add(new ImportStatement(i, i.endsWith("*")));
      }
    }
    return result;
  }

  /**
   * Serializes a scope spanning symbol in a form as used for the attribute "spanning symbol".
   *
   * @param spanningSymbol
   * @return
   */
  public static JsonPrinter serializeScopeSpanningSymbol(IScopeSpanningSymbol spanningSymbol) {
    JsonPrinter spPrinter = new JsonPrinter();
    spPrinter.beginObject();
    spPrinter.member(JsonConstants.KIND, spanningSymbol.getClass().getName());
    spPrinter.member(JsonConstants.NAME, spanningSymbol.getName());
    spPrinter.endObject();
    return spPrinter;
  }

  /**
   * Returns true if the passed DeSer object is responsible to (de)serialize the passed JsonElement,
   * based on it being a serialized object with a member of the key "KIND" with the suitable value.
   * Returns false otherwise.
   *
   * @param deser
   * @param serializedObject
   * @return
   */
  public static boolean isCorrectDeSerForKind(IDeSer<?> deser, JsonElement serializedObject) {
    if (!serializedObject.isJsonObject()) {
      return false;
    }
    JsonObject o = serializedObject.getAsJsonObject();
    if (!o.hasMember(JsonConstants.KIND)) {
      return false;
    }
    return deser.getSerializedKind().equals(o.getStringMember(JsonConstants.KIND));
  }

}
