/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Collection of (static) methods and constants that support using DeSers in combination with Json.
 */
public class JsonDeSers {

  public static final String PACKAGE = "package";

  @Deprecated
  public static final String IMPORTS = "imports";

  @Deprecated
  public static final String SUBSCOPES = "subScopes";

  @Deprecated
  public static final String EXPORTS_SYMBOLS = "exportsSymbols";

  public static final String IS_SHADOWING_SCOPE = "isShadowingScope";

  public static final String NAME = "name";

  public static final String KIND = "kind";

  public static final String SYMBOLS = "symbols";

  public static final String SPANNED_SCOPE = "spannedScope";

  /**
   * This method checks, if a passed JsonElement is a Json object of a certain passed kind.
   * It is useful to check, whether a DeSer that can deserialize the passed deSerSymbolKind is
   * capable of deserializing the passed serializedElement.
   *
   * @param deSerSymbolKind
   * @param serializedElement
   * @return
   */
  public static boolean isCorrectDeSerForKind(String deSerSymbolKind, JsonElement serializedElement) {
    if (!serializedElement.isJsonObject()) {
      return false;
    }
    JsonObject o = serializedElement.getAsJsonObject();
    if (!o.hasMember(KIND)) {
      return false;
    }
    List<String> kinds = o.getArrayMember(KIND).stream()
        .map(m -> m.getAsJsonString().getValue())
        .collect(Collectors.toList());
    return kinds.contains(deSerSymbolKind);
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
    List<String> kinds = o.getArrayMember(KIND).stream()
        .map(m -> m.getAsJsonString().getValue())
        .collect(Collectors.toList());
    if (!kinds.contains(deSerKind)) {
      Log.error("0xA7225 DeSer for kind '" + deSerKind + "' cannot deserialize Json objects"
          + " of kind '" + o.getArrayMember(KIND).toString() + "'");
    }
  }

}
