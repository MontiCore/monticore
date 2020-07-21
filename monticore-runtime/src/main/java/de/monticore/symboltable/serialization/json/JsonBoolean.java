/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

import de.monticore.symboltable.serialization.JsonPrinter;

/**
 * Represents a Json Boolean. It can be true or - you probably guessed this - false.
 */
public class JsonBoolean implements JsonElement {
  
  protected boolean value;
  
  /**
   * Constructor for de.monticore._symboltable.serialization.json.JsonBoolean
   * 
   * @param value
   */
  public JsonBoolean(boolean value) {
    this.value = value;
  }
  
  /**
   * @see de.monticore.symboltable.serialization.json.JsonElement#isJsonBoolean()
   */
  @Override
  public boolean isJsonBoolean() {
    return true;
  }
  
  /**
   * @see de.monticore.symboltable.serialization.json.JsonElement#getAsJsonBoolean()
   */
  @Override
  public JsonBoolean getAsJsonBoolean() {
    return this;
  }
  
  /**
   * @return value
   */
  public boolean getValue() {
    return this.value;
  }
  
  /**
   * @param value the value to set
   */
  public void setValue(boolean value) {
    this.value = value;
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    JsonPrinter p = new JsonPrinter();
    p.value(value);
    return p.getContent();
  }
  
}
