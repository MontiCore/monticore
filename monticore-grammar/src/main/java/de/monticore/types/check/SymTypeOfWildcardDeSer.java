/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

public class SymTypeOfWildcardDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeOfWildcard";
  protected static final String SERIALIZED_ISUPPER = "isUpper";
  protected static final String SERIALIZED_BOUND = "bound";

  public String serialize(SymTypeOfWildcard toSerialize) {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, SERIALIZED_KIND);
    if(toSerialize.hasBound()) {
      jp.member(SERIALIZED_ISUPPER, toSerialize.isUpper());
      SymTypeExpressionDeSer.serializeMember(jp, SERIALIZED_BOUND, toSerialize.getBound());
    }
    jp.endObject();
    return jp.getContent();
  }

  public SymTypeOfWildcard deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeOfWildcard deserialize(JsonObject serialized) {
    return deserialize(serialized, null);
  }

  /**
   * @param enclosingScope can be null
   */
  public SymTypeOfWildcard deserialize(JsonObject serialized, IBasicSymbolsScope enclosingScope) {
    if (serialized.hasMember(SERIALIZED_BOUND)) {
      // isUpper == false iff. value is not serialized
      boolean isUpper = serialized.hasBooleanMember(SERIALIZED_ISUPPER);
      SymTypeExpression bound = SymTypeExpressionDeSer
          .deserializeMember(SERIALIZED_BOUND, serialized, enclosingScope);
      return SymTypeExpressionFactory.createWildcard(isUpper, bound);
    }
    return SymTypeExpressionFactory.createWildcard();
  }

}
