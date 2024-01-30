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

public class SymTypeOfObjectDeSer {

  // Care: the following String needs to be adapted if the package was renamed
  public static final String SERIALIZED_KIND = "de.monticore.types.check.SymTypeOfObject";
  protected static final String SERIALIZED_OBJNAME = "objName";

  public String serialize(SymTypeOfObject toSerialize) {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, SERIALIZED_KIND);
    jp.member(SERIALIZED_OBJNAME, toSerialize.getObjName());
    jp.endObject();
    return jp.getContent();
  }

  public SymTypeOfObject deserialize(String serialized) {
    return deserialize(JsonParser.parseJsonObject(serialized));
  }

  public SymTypeOfObject deserialize(JsonObject serialized) {
    return deserialize(serialized, null);
  }

  /**
   * @param enclosingScope can be null
   */
  public SymTypeOfObject deserialize(JsonObject serialized, IBasicSymbolsScope enclosingScope) {
    if (serialized.hasStringMember(SERIALIZED_OBJNAME)) {
      String objName = serialized.getStringMember(SERIALIZED_OBJNAME);
      if (enclosingScope == null) {
        // support deprecated behavior
        enclosingScope = BasicSymbolsMill.globalScope();
      }
      TypeSymbolSurrogate typeSym = new TypeSymbolSurrogate(objName);
      typeSym.setEnclosingScope(enclosingScope);
      return SymTypeExpressionFactory.createTypeObject(typeSym);
    }
    Log.error("0x823F4 Internal error: Cannot load \""
        + serialized + "\" as  SymTypeOfObject!");
    return null;
  }
}
