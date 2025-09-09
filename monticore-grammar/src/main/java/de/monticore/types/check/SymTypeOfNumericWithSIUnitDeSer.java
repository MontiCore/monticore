/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

public class SymTypeOfNumericWithSIUnitDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND =
      "de.monticore.types.check.SymTypeOfNumericWithSIUnit";
  protected static final String SERIALIZED_NUMERIC = "numeric";
  protected static final String SERIALIZED_SIUNIT = "siunit";

  public String serialize(SymTypeOfNumericWithSIUnit toSerialize) {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, SERIALIZED_KIND);

    SymTypeExpressionDeSer
        .serializeMember(jp, SERIALIZED_NUMERIC, toSerialize.getNumericType());
    SymTypeExpressionDeSer
        .serializeMember(jp, SERIALIZED_SIUNIT, toSerialize.getSIUnitType());

    jp.endObject();
    return jp.getContent();
  }

  public SymTypeOfNumericWithSIUnit deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized), null);
  }

  public SymTypeOfNumericWithSIUnit deserialize(JsonObject serialized, IBasicSymbolsScope enclosingScope) {
    if (serialized.hasMember(SERIALIZED_NUMERIC) &&
        serialized.hasMember(SERIALIZED_SIUNIT)
    ) {
      SymTypeExpression numeric = SymTypeExpressionDeSer
          .deserializeMember(SERIALIZED_NUMERIC, serialized, enclosingScope);
      SymTypeExpression siunit = SymTypeExpressionDeSer
          .deserializeMember(SERIALIZED_SIUNIT, serialized, enclosingScope);
      return SymTypeExpressionFactory.createNumericWithSIUnit(siunit.asSIUnitType(), numeric);
    }
    else {
      Log.error("0x9E2E2 internal error:"
          + " loading ill-structured SymTab: missing" + SERIALIZED_NUMERIC
          + ", or " + SERIALIZED_SIUNIT
          + " of SymTypeOfNumericWithSIUnit " + serialized);
      return null;
    }
  }

}
