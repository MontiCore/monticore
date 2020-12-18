/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
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

  public SymTypeArray deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeArray deserialize(JsonObject serialized) {
    if (serialized.hasIntegerMember("dim") && serialized.hasMember("argument")) {
      int dim = serialized.getIntegerMember("dim");
      JsonElement argumentJson = serialized.getMember("argument");
      SymTypeExpression arg = SymTypeExpressionDeSer.getInstance().deserialize(argumentJson);
      IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
      return SymTypeExpressionFactory.createTypeArray(arg.print(), gs, dim, arg);
    }
    Log.error(
        "0x823F2 Internal error: Cannot deserialize \"" + serialized + "\" as SymTypeArray!");
    return null;
  }

}
