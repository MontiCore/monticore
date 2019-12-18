/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;
import de.se_rwth.commons.logging.Log;

public class SymTypeVariableDeSer implements IDeSer<SymTypeVariable, ITypeSymbolsScope> {

  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    return "de.monticore.types.check.SymTypeVariable";
  }

  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(SymTypeVariable toSerialize) {
    return toSerialize.printAsJson();
  }

  /**
   *
   * @param serialized
   * @param enclosingScope
   * @return
   */
  @Override
  public SymTypeVariable deserialize(String serialized, ITypeSymbolsScope enclosingScope) {
    return deserialize(JsonParser.parse(serialized), enclosingScope);
  }

  public SymTypeVariable deserialize(JsonElement serialized, ITypeSymbolsScope enclosingScope) {
    if (JsonUtil.isCorrectDeSerForKind(this, serialized)) {
      JsonObject o = serialized.getAsJsonObject();  //if it has a kind, it is an object
      String varName = o.getStringMember("varName");
      return SymTypeExpressionFactory.createTypeVariable(varName, enclosingScope);
    }
    Log.error("0x823F5 Internal error: Cannot load \"" + serialized + "\" as  SymTypeVariable!");
    return null;
  }

}
