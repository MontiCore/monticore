/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serialization.json;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class JsonNull implements JsonElement {
  
  public JsonNull() {
  }
  
  /**
   * @see automata._symboltable.serialization.json.JsonElement#isJsonNull()
   */
  @Override
  public boolean isJsonNull() {
    return true;
  }
  
  /**
   * @see automata._symboltable.serialization.json.JsonElement#getAsJsonNull()
   */
  @Override
  public JsonNull getAsJsonNull() {
    return this;
  }
  
  @Override
  public String toString() {
    return "null";
  }
  
}
