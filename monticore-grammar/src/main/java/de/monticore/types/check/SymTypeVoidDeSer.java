/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import java.util.Optional;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;

public class SymTypeVoidDeSer implements IDeSer<SymTypeVoid> {
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    return "de.monticore.types.check.SymTypeVoid";
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(SymTypeVoid toSerialize) {
    return toSerialize.printAsJson();
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<SymTypeVoid> deserialize(String serialized) {
    return deserialize(JsonParser.parseJson(serialized));
  }
  
  public Optional<SymTypeVoid> deserialize(JsonElement serialized) {
    // comparison to "void" because the serializer (print()-function) uses that
    if (serialized.isJsonString() && serialized.getAsJsonString().getValue().equals("void")) {
      return Optional.of(SymTypeExpressionFactory.createTypeVoid());
    }
    return Optional.empty();
  }
  
}
