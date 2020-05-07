/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;
import de.se_rwth.commons.logging.Log;

public class SymTypeArrayDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeArray";

  public String serialize(SymTypeArray toSerialize) {
    return toSerialize.printAsJson();
  }

  public SymTypeArray deserialize(String serialized, ITypeSymbolsScope enclosingScope) {
    return deserialize(JsonParser.parse(serialized), enclosingScope);
  }

  public SymTypeArray deserialize(JsonElement serialized, ITypeSymbolsScope enclosingScope) {
    if (JsonDeSers.isCorrectDeSerForKind(SERIALIZED_KIND, serialized)) {
      JsonObject o = serialized.getAsJsonObject();  //if it has a kind, it is an object
      int dim = o.getIntegerMember("dim");
      JsonElement argumentJson = o.getMember("argument");
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
