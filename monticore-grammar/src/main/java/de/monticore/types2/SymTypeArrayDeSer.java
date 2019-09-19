/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.types2;

import java.util.Optional;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class SymTypeArrayDeSer implements IDeSer<SymTypeArray> {
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    // TODO: anpassen, nachdem package umbenannt ist
    return "de.monticore.types2.SymTypeArray";
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
        Log.error("Could not find dim of SymTypeArray " + serialized);
      }
      Optional<SymTypeExpression> argument = Optional.empty();
      if (serialized.getAsJsonObject().containsKey("argument")) {
        argument = new SymTypeExpressionDeSer()
            .deserialize(serialized.getAsJsonObject().get("argument"));
      }
      if (!argument.isPresent()) {
        Log.error("Could not find argument of SymTypeArray " + serialized);
      }
      return Optional.of(new SymTypeArray(dim.get(), argument.get()));
    }
    return Optional.empty();
  }
}
