/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import com.google.common.collect.Lists;
import de.monticore.symboltable.serialization.json.JsonArray;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.units.qual.K;

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
        if(e.isJsonObject()){
          symbols.add(e.getAsJsonObject());
        }
        else {
          Log.error("0xA1233 Serialized symbol is not a JSON object: '" + e + "'.");
        }
      }
    }
    return symbols;
  }

  public static String getKind(JsonObject symbol) {
    if(!symbol.hasStringMember(KIND)){
      Log.error("0xA1235 Serialized object does not have a kind attribute: '" + symbol + "'.");
      return "error";
    }
    return symbol.getStringMember(KIND);
  }
}
