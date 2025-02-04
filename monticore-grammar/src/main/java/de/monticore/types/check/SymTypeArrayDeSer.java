/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

public class SymTypeArrayDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeArray";
  public static final String SERIALIZED_ARGUMENT = "argument";
  public static final String SERIALIZED_DIM = "dim";

  public String serialize(SymTypeArray toSerialize) {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, SERIALIZED_KIND);
    jp.memberJson(SERIALIZED_ARGUMENT,
        SymTypeExpressionDeSer.getInstance().serialize(toSerialize.argument));
    jp.member(SERIALIZED_DIM, toSerialize.getDim());
    jp.endObject();
    return jp.getContent();
  }

  public SymTypeArray deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeArray deserialize(JsonObject serialized) {
    return deserialize(serialized, null);
  }

  /**
   * @param enclosingScope can be null
   */
  public SymTypeArray deserialize(JsonObject serialized, IBasicSymbolsScope enclosingScope) {
    if (serialized.hasIntegerMember(SERIALIZED_DIM) && serialized.hasMember(SERIALIZED_ARGUMENT)) {
      int dim = serialized.getIntegerMember(SERIALIZED_DIM);
      JsonElement argumentJson = serialized.getMember(SERIALIZED_ARGUMENT);
      SymTypeExpression arg = SymTypeExpressionDeSer.getInstance().deserialize(argumentJson, enclosingScope);
      IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
      return SymTypeExpressionFactory.createTypeArray(arg.print(), gs, dim, arg);
    }
    Log.error(
        "0x823F2 Internal error: Cannot deserialize \"" + serialized + "\" as SymTypeArray!");
    return null;
  }

}
