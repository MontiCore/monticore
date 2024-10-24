/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class SymTypeOfGenericsDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeOfGenerics";
  protected static final String SERIALIZED_TYPE_CONSTRUCTOR = "typeConstructorFullName";
  protected static final String SERIALIZED_ARGUMENTS = "arguments";

  public String serialize(SymTypeOfGenerics toSerialize) {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, SERIALIZED_KIND);
    jp.member(SERIALIZED_TYPE_CONSTRUCTOR, toSerialize.getTypeConstructorFullName());
    SymTypeExpressionDeSer.serializeMember(jp, SERIALIZED_ARGUMENTS, toSerialize.getArgumentList());
    jp.endObject();
    return jp.getContent();
  }

  public SymTypeOfGenerics deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeOfGenerics deserialize(JsonObject serialized) {
    return deserialize(serialized, null);
  }

  /**
   * @param enclosingScope can be null
   */
  public SymTypeOfGenerics deserialize(JsonObject serialized, IBasicSymbolsScope enclosingScope) {
    if (serialized.hasStringMember(SERIALIZED_TYPE_CONSTRUCTOR) &&
        serialized.hasArrayMember(SERIALIZED_ARGUMENTS)) {
      String typeConstructorFullName = serialized.getStringMember(SERIALIZED_TYPE_CONSTRUCTOR);
      if (enclosingScope == null) {
        // support deprecated behavior
        enclosingScope = BasicSymbolsMill.globalScope();
      }
      TypeSymbolSurrogate typeSym = new TypeSymbolSurrogate(typeConstructorFullName);
      typeSym.setEnclosingScope(enclosingScope);

      List<SymTypeExpression> arguments = SymTypeExpressionDeSer
          .deserializeListMember(SERIALIZED_ARGUMENTS, serialized, enclosingScope);

      return SymTypeExpressionFactory
          .createGenerics(typeSym, arguments);
    }
    Log.error(
        "0x823F6 Internal error: Loading ill-structured SymTab: missing typeConstructorFullName of SymTypeOfGenerics "
            + serialized);
    return null;
  }
}
