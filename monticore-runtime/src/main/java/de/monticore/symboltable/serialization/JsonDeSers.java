/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of (static) methods and constants that support using DeSers in combination with Json.
 */
public class JsonDeSers {

  public static final String PACKAGE = "package";

  public static final String IMPORTS = "imports";

  public static final String SUBSCOPES = "subScopes";

  public static final String EXPORTS_SYMBOLS = "exportsSymbols";

  public static final String IS_SHADOWING_SCOPE = "isShadowingScope";

  public static final String NAME = "name";

  public static final String KIND = "kind";

  public static final String SCOPE_SPANNING_SYMBOL = "spanningSymbol";

  /**
   * This method deserializes a list of import statements in the passed Json object
   *
   * @param scope
   * @return
   */
  public static List<ImportStatement> deserializeImports(JsonObject scope) {
    List<ImportStatement> result = new ArrayList<>();
    if (scope.hasMember(IMPORTS)) {
      for (JsonElement e : scope.getArrayMember(IMPORTS)) {
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
  public static void serializeScopeSpanningSymbol(IScopeSpanningSymbol spanningSymbol,
      JsonPrinter printer) {
    if (null != spanningSymbol) {
      printer.beginObject();
      printer.member(KIND, spanningSymbol.getClass().getName());
      printer.member(NAME, spanningSymbol.getName());
      printer.endObject();
    }
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
  public static boolean isCorrectDeSerForKind(IDeSer<?, ?> deser, JsonElement serializedObject) {
    if (!serializedObject.isJsonObject()) {
      return false;
    }
    JsonObject o = serializedObject.getAsJsonObject();
    if (!o.hasMember(KIND)) {
      return false;
    }
    return deser.getSerializedKind().equals(o.getStringMember(KIND));
  }

}
