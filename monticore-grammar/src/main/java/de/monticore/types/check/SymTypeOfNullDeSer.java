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
    // comparison to "nullType", i.e. _nullTypeString because the serializer (print()-function) uses that
    if (serialized.isJsonString() && serialized.getAsJsonString().getValue().equals(DefsTypeBasic._nullTypeString)) {
      return Optional.of(SymTypeExpressionFactory.createTypeOfNull());
    }
    return Optional.empty();
  }
  
}
