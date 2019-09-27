/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

/**
 * Collection of (static) methods that support using DeSers in combination with Json.
 * 
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
    if (scope.containsKey(JsonConstants.IMPORTS)) {
      for (JsonElement e : scope.get(JsonConstants.IMPORTS).getAsJsonArray().getValues()) {
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
    return deser.getSerializedKind()
        .equals(getOptStringMember(serializedObject, JsonConstants.KIND).orElse(null));
  }
  
  /**
   * Returns the member with the passed key of the passed JsonElement as String, if it exists.
   * Otherwise, returns empty()
   * 
   * @param json
   * @param key
   * @return
   */
  public static Optional<String> getOptStringMember(JsonElement json, String key) {
    if (json.isJsonObject()) {
      if (json.getAsJsonObject().containsKey(key)) {
        JsonElement jsonMember = json.getAsJsonObject().get(key);
        if (jsonMember.isJsonString()) {
          return Optional.ofNullable(jsonMember.getAsJsonString().getValue());
        }
      }
    }
    return Optional.empty();
  }
  
  /**
   * Returns the member with the passed key of the passed JsonElement as integer, if it exists.
   * Otherwise, returns empty()
   * 
   * @param json
   * @param key
   * @return
   */
  public static Optional<Integer> getOptIntMember(JsonElement json, String key) {
    if (json.isJsonObject()) {
      if (json.getAsJsonObject().containsKey(key)) {
        JsonElement jsonMember = json.getAsJsonObject().get(key);
        if (jsonMember.isJsonNumber()) {
          return Optional.ofNullable(jsonMember.getAsJsonNumber().getNumberAsInt());
        }
      }
    }
    return Optional.empty();
  }
  
}
