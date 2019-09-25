/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.types.check;

import java.util.Optional;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class SymTypeOfObjectDeSer implements IDeSer<SymTypeOfObject> {
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    // TODO: anpassen, nachdem package umbenannt ist
    return "de.monticore.types.check.SymTypeOfObject";
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(SymTypeOfObject toSerialize) {
    return toSerialize.printAsJson();
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<SymTypeOfObject> deserialize(String serialized) {
    return deserialize(JsonParser.parseJson(serialized));
  }
  
  public Optional<SymTypeOfObject> deserialize(JsonElement serialized) {
    if (JsonUtil.isCorrectDeSerForKind(this, serialized)) {
      Optional<String> objName = JsonUtil.getOptStringMember(serialized, "objName");
      if (!objName.isPresent()) {
        Log.error("Could not find objName of SymTypeOfObject " + serialized);
      }
      // TODO: Deserialize TypeSymbol if it is present
      SymTypeOfObject obj = new SymTypeOfObject(objName.get());
      return Optional.of(obj);
    }
    return Optional.empty();
  }
  
}
