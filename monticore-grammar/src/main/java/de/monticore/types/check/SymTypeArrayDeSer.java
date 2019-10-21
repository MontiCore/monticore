/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import java.util.Optional;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.se_rwth.commons.logging.Log;

public class SymTypeArrayDeSer implements IDeSer<SymTypeArray> {
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    // Care: the following String needs to be adapted if the package was renamed
    return "de.monticore.types.check.SymTypeArray";
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(SymTypeArray toSerialize) {
    return toSerialize.printAsJson();
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<SymTypeArray> deserialize(String serialized) {
    return deserialize(JsonParser.parseJson(serialized));
  }
  
  public Optional<SymTypeArray> deserialize(JsonElement serialized) {
    if (JsonUtil.isCorrectDeSerForKind(this, serialized)) {
      Optional<Integer> dim = JsonUtil.getOptIntMember(serialized, "dim");
      if (!dim.isPresent()) {
        Log.error("0x823F2 Internal error: Loading ill-structured SymTab: missing dim of SymTypeArray " + serialized);
        dim = Optional.of(Integer.valueOf(1)); // Dummy value, because dimension is absent
      }
      Optional<SymTypeExpression> argument = Optional.empty();
      if (serialized.getAsJsonObject().containsKey("argument")) {
        argument = SymTypeExpressionDeSer.theDeSer
            .deserialize(serialized.getAsJsonObject().get("argument"));
      }
      if (argument.isPresent()) {
        // TODO AB: use the appropriate creation from SymType.Factory
        return Optional.of(new SymTypeArray(dim.get(), argument.get()));
      }
      Log.error("0x823F3 Internal error: Loading ill-structured SymTab: missing argument of SymTypeArray " + serialized);
    }
    return Optional.empty();
  }
}
