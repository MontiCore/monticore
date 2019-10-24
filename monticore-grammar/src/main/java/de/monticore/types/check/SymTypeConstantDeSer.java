/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import java.util.Optional;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.se_rwth.commons.logging.Log;

public class SymTypeConstantDeSer implements IDeSer<SymTypeConstant> {
  
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
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<SymTypeConstant> deserialize(String serialized) {
    return deserialize(JsonParser.parseJson(serialized));
  }
  
  public Optional<SymTypeConstant> deserialize(JsonElement serialized) {
    if (JsonUtil.isCorrectDeSerForKind(this, serialized)) {
      Optional<String> constName = JsonUtil.getOptStringMember(serialized, "constName");
      if (constName.isPresent()) {
        return Optional.of(SymTypeExpressionFactory.createTypeConstant(constName.get()));
      }
      Log.error("0x823F1 Internal error: Loading ill-structured SymTab: missing constName of SymTypeConstant " + serialized);
    }
    return Optional.empty();
  }
}
