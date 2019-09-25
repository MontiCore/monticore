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
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class SymTypePackageDeSer implements IDeSer<SymTypePackage> {
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    // TODO: anpassen, nachdem package umbenannt ist
    return "de.monticore.types.check.SymTypePackage";
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(SymTypePackage toSerialize) {
    return toSerialize.printAsJson();
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<SymTypePackage> deserialize(String serialized) {
    return deserialize(JsonParser.parseJson(serialized));
  }
  
  public Optional<SymTypePackage> deserialize(JsonElement serialized) {
    if (serialized.isJsonString() && serialized.getAsJsonString().getValue().equals("package")) {
      // TODO: check if creating a new instance is feasible
      return Optional.of(new SymTypePackage());
    }
    return Optional.empty();
  }
  
}
