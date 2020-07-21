/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

/**
 * This realizes a Json String.
 */
public class JsonString implements JsonElement {
  
  protected String value;
  
  public JsonString(String value) {
    this.value = value;
  }
  
  /**
   * @see de.monticore.symboltable.serialization.json.JsonElement#isJsonString()
   */
  @Override
  public boolean isJsonString() {
    return true;
  }
  
  /**
   * @see de.monticore.symboltable.serialization.json.JsonElement#getAsJsonString()
   */
  @Override
  public JsonString getAsJsonString() {
    return this;
  }

  /**
   * @return value
   */
  public String getValue() {
    return this.value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   *
   * @return
   * @see java.lang.String#length()
   */
  public int length() {
    return this.value.length();
  }
  
  @Override
  public String toString() {
    return value;
  }
  
}
