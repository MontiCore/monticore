package de.monticore.symboltable.serialization.json;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class JsonNumber implements JsonElement {
  
  protected String value;
  
  /**
   * Constructor for automata._symboltable.serialization.json.JsonNumber
   * 
   * @param number
   */
  public JsonNumber(String value) {
    super();
    this.value = value;
  }
  
  /**
   * @see automata._symboltable.serialization.json.JsonElement#isJsonNumber()
   */
  @Override
  public boolean isJsonNumber() {
    return true;
  }
  
  /**
   * @see automata._symboltable.serialization.json.JsonElement#getAsJsonNumber()
   */
  @Override
  public JsonNumber getAsJsonNumber() {
    return this;
  }
  
  /**
   * @return number
   */
  public float getNumberAsFloat() {
    return Float.parseFloat(value);
  }
  
  /**
   * @param number the number to set
   */
  public void setNumber(float value) {
    this.value = value + "";
  }
  
  /**
   * @return number
   */
  public double getNumberAsDouble() {
    return Double.parseDouble(value);
  }
  
  /**
   * @param number the number to set
   */
  public void setNumber(double value) {
    this.value = value + "";
  }
  
  /**
   * @return number
   */
  public int getNumberAsInt() {
    return Integer.parseInt(value);
  }
  
  /**
   * @param number the number to set
   */
  public void setNumber(int value) {
    this.value = value + "";
  }
  
  /**
   * @return number
   */
  public long getNumberAsLong() {
    return Long.parseLong(value);
  }
  
  /**
   * @param number the number to set
   */
  public void setNumber(long value) {
    this.value = value + "";
  }
  
  @Override
  public String toString() {
    return value;
  }
  
}
