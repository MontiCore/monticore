/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import java.util.ArrayList;
import java.util.List;

import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class JsonUtil {
  
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
  
  public static JsonPrinter serializeScopeSpanningSymbol(IScopeSpanningSymbol spanningSymbol) {
    JsonPrinter spPrinter = new JsonPrinter();
    spPrinter.beginObject();
    spPrinter.member(JsonConstants.KIND, spanningSymbol.getClass().getName());
    spPrinter.member(JsonConstants.NAME, spanningSymbol.getName());
    spPrinter.endObject();
    return spPrinter;
  }
  
}
