/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

public class SymTypeVariableDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeVariable";

  public String serialize(SymTypeVariable toSerialize) {
    return toSerialize.printAsJson();
  }

  public SymTypeVariable deserialize(String serialized, IBasicSymbolsScope enclosingScope) {
    return deserialize(JsonParser.parseJsonObject(serialized), enclosingScope);
  }

  public SymTypeVariable deserialize(JsonObject serialized, IBasicSymbolsScope enclosingScope) {
    if (serialized.hasStringMember("varName")) {
      String varName = serialized.getStringMember("varName");
      return SymTypeExpressionFactory.createTypeVariable(varName, enclosingScope);
    }
    Log.error("0x823F5 Internal error: Cannot load \"" + serialized + "\" as  SymTypeVariable!");
    return null;
  }
}
