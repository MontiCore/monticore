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
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

public class SymTypeOfObjectDeSer implements IDeSer<SymTypeOfObject> {

  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
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
  public SymTypeOfObject deserialize(String serialized) {
    return deserialize(JsonParser.parse(serialized));
  }

  public SymTypeOfObject deserialize(JsonElement serialized) {
    if (JsonUtil.isCorrectDeSerForKind(this, serialized)) {
      JsonObject o = serialized.getAsJsonObject();  //if it has a kind, it is an object
      String objName = o.getStringMember("objName");
      TypeSymbol typeLoader = null; // TODO AB: waits for TypeSymbolLoader
      return SymTypeExpressionFactory.createTypeObject(objName, typeLoader);
    }
    Log.error("0x823F4 Internal error: Cannot load \""
        + serialized + "\" as  SymTypeOfObject!");
    return null;
  }

}
