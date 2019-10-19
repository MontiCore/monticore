/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import java.util.Optional;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.se_rwth.commons.logging.Log;

public class SymTypeVariableDeSer implements IDeSer<SymTypeVariable> {
  
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
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<SymTypeVariable> deserialize(String serialized) {
    return deserialize(JsonParser.parseJson(serialized));
  }
  
  public Optional<SymTypeVariable> deserialize(JsonElement serialized) {
    if (JsonUtil.isCorrectDeSerForKind(this, serialized)) {
      Optional<String> varName = JsonUtil.getOptStringMember(serialized, "varName");
      if (varName.isPresent()) {
        // TODO AB: korrekten Konstruktor einsetzen (TypeSym!)
        return Optional.of(SymTypeExpressionFactory.createTypeVariable(varName.get()));
      }
      Log.error("0x823F5 Internal error: Loading ill-structured SymTab: missing varName of SymTypeVariable " + serialized);
    }
    return Optional.empty();
  }
  
}
