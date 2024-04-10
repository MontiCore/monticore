/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class SymTypeOfFunctionDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeOfFunction";
  protected static final String SERIALIZED_RETURNTYPE = "returnType";
  protected static final String SERIALIZED_ARGUMENTTYPES = "argumentTypes";
  protected static final String SERIALIZED_ELLIPTIC = "elliptic";

  public String serialize(SymTypeOfFunction toSerialize) {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, SERIALIZED_KIND);
    SymTypeExpressionDeSer.serializeMember(jp, SERIALIZED_RETURNTYPE, toSerialize.getType());
    SymTypeExpressionDeSer.serializeMember(jp, SERIALIZED_ARGUMENTTYPES,
        toSerialize.getArgumentTypeList());
    jp.member(SERIALIZED_ELLIPTIC, toSerialize.elliptic);
    jp.endObject();
    return jp.getContent();
  }

  public SymTypeOfFunction deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeOfFunction deserialize(JsonObject serialized) {
    return deserialize(serialized, null);
  }

  /**
   * @param enclosingScope can be null
   */
  public SymTypeOfFunction deserialize(JsonObject serialized, IBasicSymbolsScope enclosingScope) {
    if (serialized.hasMember(SERIALIZED_RETURNTYPE)) {
      SymTypeExpression returnType = SymTypeExpressionDeSer
          .deserializeMember(SERIALIZED_RETURNTYPE, serialized, enclosingScope);
      List<SymTypeExpression> arguments =
          SymTypeExpressionDeSer.deserializeListMember(SERIALIZED_ARGUMENTTYPES, serialized, enclosingScope);
      boolean isElliptic = serialized
          .getBooleanMemberOpt(SERIALIZED_ELLIPTIC)
          .orElse(false);
      return SymTypeExpressionFactory.createFunction(returnType, arguments, isElliptic);
    }
    Log.error(
        "0x9E2F6 Internal error: Loading ill-structured SymTab: missing "
            + SERIALIZED_RETURNTYPE
            + "of SymTypeOfFunction "
            + serialized);
    return null;
  }
}
