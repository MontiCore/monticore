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
public class JsonNumber implements JsonElement {
  
  protected String number;
  
  /**
   * Constructor for automata._symboltable.serialization.json.JsonNumber
   * 
   * @param number
   */
  public JsonNumber(String number) {
    super();
    this.number = number;
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
    return Float.parseFloat(number);
  }
  
  /**
   * @param number the number to set
   */
  public void setNumber(float number) {
    this.number = number + "";
  }
  
  /**
   * @return number
   */
  public double getNumberAsDouble() {
    return Double.parseDouble(number);
  }
  
  /**
   * @param number the number to set
   */
  public void setNumber(double number) {
    this.number = number + "";
  }
  
  /**
   * @return number
   */
  public int getNumberAsInt() {
    return Integer.parseInt(number);
  }
  
  /**
   * @param number the number to set
   */
  public void setNumber(int number) {
    this.number = number + "";
  }
  
  /**
   * @return number
   */
  public long getNumberAsLong() {
    return Long.parseLong(number);
  }
  
  /**
   * @param number the number to set
   */
  public void setNumber(long number) {
    this.number = number + "";
  }
  
  @Override
  public String toString() {
    return number;
  }
  
}
