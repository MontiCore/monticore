/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serializing;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;

/**
 * Interface for all Serialization classes for specific symbols and scopes that relies on Google's
 * Gson API for json.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 * @param <T>
 */
@Deprecated // IDeSer will be used instead
public interface ISerialization<T> extends JsonSerializer<T>, JsonDeserializer<T> {
  
  public Class<T> getSerializedClass();
  
  public static final String PACKAGE = "package";
  
  public static final String IMPORTS = "imports";
  
  public static final String SUBSCOPES = "subScopes";
  
  public static final String SYMBOLS = "symbols";
  
  public static final String EXPORTS_SYMBOLS = "exportsSymbols";
  
  public static final String IS_SHADOWING_SCOPE = "isShadowingScope";
  
  public static final String NAME = "name";
 
  @Deprecated // Can be removed after release 5.0.3
  public static final String CLASS = "class";

  public static final String KIND = "kind";
  
  // TODO: Can be removed if ScopeSpanningSymbols are removed
  public static final String SCOPESPANNING_SYMBOL = "spanningSymbol";
  
  default T fail() throws JsonParseException {
    throw new JsonParseException(
        "Deserialization of '" + getSerializedClass().getName() + "' with '" + this.getClass()
            + "' failed!");
  }
  
  default boolean isCorrectSerializer(JsonObject json) {
    if (json.has(ISerialization.KIND)) {
      String name = json.get(ISerialization.KIND).getAsString();
      return getSerializedClass().getName().equals(name);
    }
    else {
      fail();
      return false;
    }
  }
  
}
