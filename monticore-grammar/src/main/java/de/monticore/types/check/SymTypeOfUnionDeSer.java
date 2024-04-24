/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.List;

public class SymTypeOfUnionDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeOfUnion";
  protected static final String SERIALIZED_TYPES = "unionizedTypes";

  public String serialize(SymTypeOfUnion toSerialize) {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, SERIALIZED_KIND);

    jp.beginArray(SERIALIZED_TYPES);
    toSerialize.getUnionizedTypeSet().stream()
        .map(SymTypeExpression::printAsJson)
        .sorted()
        .forEach(jp::valueJson);
    jp.endArray();

    jp.endObject();
    return jp.getContent();
  }

  public SymTypeOfUnion deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeOfUnion deserialize(JsonObject serialized) {
    return deserialize(serialized, null);
  }

  /**
   * @param enclosingScope can be null
   */
  public SymTypeOfUnion deserialize(JsonObject serialized, IBasicSymbolsScope enclosingScope) {
    if (serialized.hasMember(SERIALIZED_TYPES)) {
      List<SymTypeExpression> unionizedTypesList =
          SymTypeExpressionDeSer.deserializeListMember(
              SERIALIZED_TYPES, serialized, enclosingScope);
      return SymTypeExpressionFactory.createUnion(new HashSet<>(unionizedTypesList));
    }
    Log.error(
        "0x9E2F7 Internal error: Loading ill-structured SymTab: missing "
            + SERIALIZED_TYPES
            + "of SymTypeOfUnion "
            + serialized);
    return null;
  }
}
