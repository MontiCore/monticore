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

public class SymTypeOfIntersectionDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeOfIntersection";
  protected static final String SERIALIZED_TYPES = "intersectedTypes";

  public String serialize(SymTypeOfIntersection toSerialize) {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, SERIALIZED_KIND);

    jp.beginArray(SERIALIZED_TYPES);
    toSerialize.getIntersectedTypeSet().stream()
        .map(SymTypeExpression::printAsJson)
        .sorted()
        .forEach(jp::valueJson);
    jp.endArray();

    jp.endObject();
    return jp.getContent();
  }

  public SymTypeOfIntersection deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeOfIntersection deserialize(JsonObject serialized) {
    return deserialize(serialized, null);
  }

  /**
   * @param enclosingScope can be null
   */
  public SymTypeOfIntersection deserialize(JsonObject serialized, IBasicSymbolsScope enclosingScope) {
    if (serialized.hasMember(SERIALIZED_TYPES)) {
      List<SymTypeExpression> intersectedTypesList =
          SymTypeExpressionDeSer.deserializeListMember(SERIALIZED_TYPES, serialized, enclosingScope);
      return SymTypeExpressionFactory.createIntersection(new HashSet<>(intersectedTypesList));
    }
    Log.error(
        "0x9E2F7 Internal error: Loading ill-structured SymTab: missing "
            + SERIALIZED_TYPES
            + "of SymTypeOfIntersection "
            + serialized);
    return null;
  }
}
