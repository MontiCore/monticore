/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class SymTypeOfFunctionDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = SymTypeOfFunction.JSON_KIND;

  public String serialize(SymTypeOfFunction toSerialize) {
    return toSerialize.printAsJson();
  }

  public SymTypeOfFunction deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeOfFunction deserialize(JsonObject serialized) {
    if (serialized.hasMember(SymTypeOfFunction.JSON_RETURNTYPE)) {
      SymTypeExpression returnType = SymTypeExpressionDeSer.getInstance()
          .deserialize(serialized.getMember(SymTypeOfFunction.JSON_RETURNTYPE));
      List<SymTypeExpression> arguments = new ArrayList<>();
      if (serialized.hasMember(SymTypeOfFunction.JSON_ARGUMENTTYPES)) {
        for (JsonElement e : serialized.getMember(SymTypeOfFunction.JSON_ARGUMENTTYPES)
            .getAsJsonArray().getValues()) {
          arguments.add(SymTypeExpressionDeSer.getInstance().deserialize(e));
        }
      }
      boolean isElliptic = serialized.getBooleanMemberOpt(SymTypeOfFunction.JSON_ELLIPTIC)
          .orElse(false);

      return SymTypeExpressionFactory.createFunction(returnType, arguments, isElliptic);
    }
    Log.error(
        "0x9E2F6 Internal error: Loading ill-structured SymTab: missing "
            + SymTypeOfFunction.JSON_RETURNTYPE
            + "of SymTypeOfFunction "
            + serialized);
    return null;
  }
}
