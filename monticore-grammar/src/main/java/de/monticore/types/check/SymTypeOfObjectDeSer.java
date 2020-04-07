/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;
import de.se_rwth.commons.logging.Log;

public class SymTypeOfObjectDeSer implements IDeSer<SymTypeOfObject, ITypeSymbolsScope> {

  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    return "de.monticore.types.check.SymTypeOfObject";
  }

  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(SymTypeOfObject toSerialize) {
    return toSerialize.printAsJson();
  }

  /**
   *
   * @param serialized
   * @param enclosingScope
   * @return
   */
  @Override
  public SymTypeOfObject deserialize(String serialized, ITypeSymbolsScope enclosingScope) {
    return deserialize(JsonParser.parse(serialized), enclosingScope);
  }

  public SymTypeOfObject deserialize(JsonElement serialized, ITypeSymbolsScope enclosingScope) {
    if (JsonDeSers.isCorrectDeSerForKind(this, serialized)) {
      JsonObject o = serialized.getAsJsonObject();  //if it has a kind, it is an object
      String objName = o.getStringMember("objName");
      return SymTypeExpressionFactory.createTypeObject(objName, enclosingScope);
    }
    Log.error("0x823F4 Internal error: Cannot load \""
        + serialized + "\" as  SymTypeOfObject!");
    return null;
  }

}
