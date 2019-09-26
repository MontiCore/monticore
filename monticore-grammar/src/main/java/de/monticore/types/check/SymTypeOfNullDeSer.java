/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import java.util.Optional;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;

public class SymTypeOfNullDeSer implements IDeSer<SymTypeOfNull> {
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    return "de.monticore.types.check.SymTypeOfNull";
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(SymTypeOfNull toSerialize) {
    return toSerialize.printAsJson();
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<SymTypeOfNull> deserialize(String serialized) {
    return deserialize(JsonParser.parseJson(serialized));
  }

  public Optional<SymTypeOfNull> deserialize(JsonElement serialized) {
    if (serialized.isJsonString() && serialized.getAsJsonString().getValue().equals("nullType")) {
      // TODO: check if creating a new instance is feasible
      return Optional.of(new SymTypeOfNull());
    }
    return Optional.empty();
  }
  
}
