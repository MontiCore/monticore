/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

public class SymTypeVariableDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeVariable";

  public String serialize(SymTypeVariable toSerialize) {
    return toSerialize.printAsJson();
  }

  public SymTypeVariable deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeVariable deserialize(JsonObject serialized) {
    if (serialized.hasStringMember("varName")) {
      String varName = serialized.getStringMember("varName");
      IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
      return SymTypeExpressionFactory.createTypeVariable(varName, gs);
    }
    Log.error("0x823F5 Internal error: Cannot load \"" + serialized + "\" as  SymTypeVariable!");
    return null;
  }
}
