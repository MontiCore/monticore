/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.types.check;

import java.util.Optional;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;

/**
 * This class serializes and deserializes SymTypeVoid instances as the json value "void".
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class SymTypeVoidDeSer implements IDeSer<SymTypeVoid> {
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    // TODO: anpassen, nachdem package umbenannt ist
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
    if (serialized.isJsonString() && serialized.getAsJsonString().getValue().equals("void")) {
      // TODO: check if creating a new instance is feasible
      return Optional.of(new SymTypeVoid());
    }
    return Optional.empty();
  }
  
}
