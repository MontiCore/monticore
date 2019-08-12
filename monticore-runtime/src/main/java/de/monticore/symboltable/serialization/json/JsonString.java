/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class JsonString implements JsonElement {
  
  protected String value;
  
  public JsonString(String value) {
    this.value = value;
  }
  
  /**
   * @see automata._symboltable.serialization.json.JsonElement#isJsonString()
   */
  @Override
  public boolean isJsonString() {
    return true;
  }
  
  /**
   * @see automata._symboltable.serialization.json.JsonElement#getAsJsonString()
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
   * TODO: Write me!
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
