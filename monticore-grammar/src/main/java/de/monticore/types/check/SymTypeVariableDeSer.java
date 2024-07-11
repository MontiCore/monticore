/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbolSurrogate;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

public class SymTypeVariableDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeVariable";
  protected static final String SERIALIZED_NAME = "varName";

  public String serialize(SymTypeVariable toSerialize) {
    if (toSerialize.isInferenceVariable()) {
      Log.error("0xFD234 internal error: "
          + "trying to serialize a free type variable"
          + ", only variables with symbols may be serialized: "
          + toSerialize.printFullName()
      );
      return "";
    }
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, SERIALIZED_KIND);
    jp.member(SERIALIZED_NAME, toSerialize.getVarName());
    jp.endObject();
    return jp.getContent();
  }

  public SymTypeVariable deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeVariable deserialize(JsonObject serialized) {
    return deserialize(serialized, null);
  }

  /**
   * @param enclosingScope can be null
   */
  public SymTypeVariable deserialize(JsonObject serialized, IBasicSymbolsScope enclosingScope) {
    if (serialized.hasStringMember(SERIALIZED_NAME)) {
      String varName = serialized.getStringMember(SERIALIZED_NAME);
      if(enclosingScope == null) {
        // support deprecated behavior:
        enclosingScope = BasicSymbolsMill.globalScope();
      }
      TypeVarSymbolSurrogate typeVarSym = new TypeVarSymbolSurrogate(varName);
      typeVarSym.setEnclosingScope(enclosingScope);
      return SymTypeExpressionFactory.createTypeVariable(typeVarSym);
    }
    Log.error("0x823F5 Internal error: Cannot load \"" + serialized + "\" as  SymTypeVariable!");
    return null;
  }
}
