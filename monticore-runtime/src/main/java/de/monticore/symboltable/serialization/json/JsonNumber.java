/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

/**
 * Represents a number in JSON. JSON does not distinguish different data types for numbers,
 * therefore this class internally uses number parsers to return the number in a Java number data
 * type of your choice. For example, to return the number as a long, call getNumberAsLong().
 */
public class JsonNumber implements JsonElement {
  
  protected String value;
  
  /**
   * Constructor for de.monticore._symboltable.serialization.json.JsonNumber
   * 
   * @param value
   */
  public JsonNumber(String value) {
    super();
    this.value = value;
  }
  
  /**
   * @see de.monticore.symboltable.serialization.json.JsonElement#isJsonNumber()
   */
  @Override
  public boolean isJsonNumber() {
    return true;
  }
  
  /**
   * @see de.monticore.symboltable.serialization.json.JsonElement#getAsJsonNumber()
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
   * @param value the number to set
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
   * @param value the number to set
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
   * @param value the number to set
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
   * @param value the number to set
   */
  public void setNumber(long value) {
    this.value = value + "";
  }
  
  @Override
  public String toString() {
    return value;
  }
  
}
