/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import com.google.common.collect.Lists;
import de.monticore.symboltable.serialization.json.JsonArray;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Collection of (static) methods and constants that support using DeSers in combination with Json.
 */
public class JsonDeSers {

  public static final String PACKAGE = "package";

  public static final String IS_SHADOWING_SCOPE = "isShadowingScope";

  public static final String NAME = "name";

  public static final String KIND = "kind";

  @Deprecated
  public static final String KIND_HIERARCHY = "kindHierarchy";

  public static final String SYMBOLS = "symbols";

  public static final String SPANNED_SCOPE = "spannedScope";

  /**
   * This method deserializes a stored package. If no package is stored, the default
   * empty package ("") is returned.
   *
   * @param scopeJson
   * @return
   */
  public static String getPackage(JsonObject scopeJson) {
    return scopeJson.getStringMemberOpt(PACKAGE).orElse("");
  }

  /**
   * This method returns a list of JsonObjects from a passed serialized scope.
   * @param scopeJson
   * @return
   */
  public static List<JsonObject> getSymbols(JsonObject scopeJson) {
    List<JsonObject> symbols = new ArrayList<>();
    if (scopeJson.hasArrayMember(SYMBOLS)) {
      for (JsonElement e : scopeJson.getArrayMember(SYMBOLS)) {
        symbols.add(e.getAsJsonObject());
      }
    }
    return symbols;
  }

  /**
   * This method deserializes a stored map with kind hierarchies from the passed serialized scope.
   *
   * @param scopeJson
   * @return
   */
  @Deprecated
  public static Map<String, String> deserializeKindHierarchy(JsonObject scopeJson) {
    Map<String, String> kindHierarchy = new HashMap<>();
    if (scopeJson.hasArrayMember(KIND_HIERARCHY)) {
      for (JsonElement e : scopeJson.getArrayMember(KIND_HIERARCHY)) {
        JsonArray entry = e.getAsJsonArray();
        if (2 == entry.size()) {
          String kind = entry.get(0).getAsJsonString().getValue();
          String superKind = entry.get(1).getAsJsonString().getValue();
          kindHierarchy.put(kind, superKind);
        }
        else {
          Log.error(
              "0xA7434 An entry in the kind hierarchy map has to consist of a kind and a superkind. '"
                  + entry + "' does not conform to this!");
        }
      }
    }
    return kindHierarchy;
  }

  @Deprecated
  public static void printKindHierarchyEntry(JsonPrinter printer, String kind, String superKind){
    printer.array(Lists.newArrayList(kind, superKind), s -> "\""+s+"\"");
  }

  @Deprecated
  public static String getParentKind(String kind, Map<String, String> kindHierarchy) {
    if (null != kind && kindHierarchy.containsKey(kind)) {
      return kindHierarchy.get(kind);
    }
    return null;
  }

  /**
   * This method checks, if a passed JsonElement is a Json object of a certain passed kind.
   * It is useful to check, whether a DeSer that can deserialize the passed deSerSymbolKind is
   * capable of deserializing the passed serializedElement.
   *
   * @param deSerSymbolKind
   * @param serializedElement
   * @return
   */
  @Deprecated
  public static boolean isCorrectDeSerForKind(String deSerSymbolKind,
      JsonElement serializedElement) {
    if (!serializedElement.isJsonObject()) {
      return false;
    }
    JsonObject o = serializedElement.getAsJsonObject();
    if (!o.hasMember(KIND)) {
      return false;
    }
    return deSerSymbolKind.equals(o.getStringMember(KIND));
  }

  /**
   * This method checks, if a passed JsonElement is a Json object of a certain passed kind.
   * It is useful to check, whether a DeSer that can deserialize the passed deSerSymbolKind is
   * capable of deserializing the passed serializedElement.
   *
   * @param deSerKind
   * @param serializedElement
   * @return
   */
  public static void checkCorrectDeSerForKind(String deSerKind, JsonElement serializedElement) {
    if (!serializedElement.isJsonObject()) {
      Log.error("0xA7223 DeSer for kind '" + deSerKind + "' can only deserialize Json objects! '"
          + serializedElement + "' is not a Json object.");
      return; //return here to avoid consecutive errors in this method
    }
    JsonObject o = serializedElement.getAsJsonObject();
    if (!o.hasMember(KIND)) {
      Log.error("0xA7224 Serialized symbol table classes must have a member describing their "
          + "kind. The Json object '" + serializedElement
          + "' does not have a member describing the kind.");
      return; //return here to avoid consecutive errors in this method
    }
    if (!deSerKind.equals(o.getStringMember(KIND))) {
      Log.error("0xA7225 DeSer for kind '" + deSerKind + "' cannot deserialize Json objects"
          + " of kind '" + o.getStringMember(KIND) + "'");
    }
  }

}
