/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.typesymbols._symboltable.ITypeSymbolsScope;
import de.se_rwth.commons.logging.Log;

public class SymTypeConstantDeSer implements IDeSer<SymTypeConstant, ITypeSymbolsScope> {

  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    // Care: the following String needs to be adapted if the package was renamed
    return "de.monticore.types.check.SymTypeConstant";
  }

  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(SymTypeConstant toSerialize) {
    return toSerialize.printAsJson();
  }

  /**
   *
   * @param serialized
   * @param enclosingScope
   * @return
   */
  @Override
  public SymTypeConstant deserialize(String serialized, ITypeSymbolsScope enclosingScope) {
    return deserialize(JsonParser.parse(serialized),enclosingScope);
  }

  public SymTypeConstant deserialize(JsonElement serialized, ITypeSymbolsScope enclosingScope) {
    if (JsonUtil.isCorrectDeSerForKind(this, serialized)) {
      JsonObject o = serialized.getAsJsonObject();  //if it has a kind, it is an object
      String constName = o.getStringMember("constName");
      return SymTypeExpressionFactory.createTypeConstant(constName);
    }
    Log.error("0x823F1 Internal error: Cannot load \"" + serialized + "\" as  SymTypeConstant!");
    return null;
  }
}
