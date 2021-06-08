/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

/**
 * Represents a Json null, which is a value type in Json. This class exists for
 * reasons of completeness, but you should avoid using it 
 * (cf. Hoare's "billion-dollar mistake")
 */
public class JsonNull implements JsonElement {
  
  public JsonNull() {
  }
  
  /**
   * @see de.monticore.symboltable.serialization.json.JsonElement#isJsonNull()
   */
  @Override
  public boolean isJsonNull() {
    return true;
  }
  
  /**
   * @see de.monticore.symboltable.serialization.json.JsonElement#getAsJsonNull()
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
