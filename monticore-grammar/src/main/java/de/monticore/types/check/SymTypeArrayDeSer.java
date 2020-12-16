/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

public class SymTypeArrayDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeArray";

  public String serialize(SymTypeArray toSerialize) {
    return toSerialize.printAsJson();
  }

  public SymTypeArray deserialize(String serialized, IBasicSymbolsScope enclosingScope) {
    return deserialize(JsonParser.parseJsonObject(serialized), enclosingScope);
  }

  public SymTypeArray deserialize(JsonObject serialized, IBasicSymbolsScope enclosingScope) {
    if (serialized.hasIntegerMember("dim") && serialized.hasMember("argument")) {
      int dim = serialized.getIntegerMember("dim");
      JsonElement argumentJson = serialized.getMember("argument");
      SymTypeExpression argument = SymTypeExpressionDeSer.getInstance()
          .deserialize(argumentJson, enclosingScope);
      return SymTypeExpressionFactory.createTypeArray(argument.print(),
          enclosingScope, dim, argument);
    }
    else {
      Log.error(
          "0x823F2 Internal error: Cannot deserialize \"" + serialized + "\" as SymTypeArray!");
    }
    return null;
  }

}
