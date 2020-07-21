/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.se_rwth.commons.logging.Log;

public class SymTypeConstantDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeConstant";

  public String serialize(SymTypeConstant toSerialize) {
    return toSerialize.printAsJson();
  }

  public SymTypeConstant deserialize(String serialized) {
    return deserialize(JsonParser.parse(serialized));
  }

  public SymTypeConstant deserialize(JsonElement serialized) {
    if (JsonDeSers.isCorrectDeSerForKind(SERIALIZED_KIND, serialized)) {
      JsonObject o = serialized.getAsJsonObject();  //if it has a kind, it is an object
      String constName = o.getStringMember("constName");
      return SymTypeExpressionFactory.createTypeConstant(constName);
    }
    Log.error("0x823F1 Internal error: Cannot load \"" + serialized + "\" as  SymTypeConstant!");
    return null;
  }
}
