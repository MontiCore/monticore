/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.Value;
import de.se_rwth.commons.logging.Log;

public class CharValue implements Value {

  protected char value;

  public CharValue(char value){
    this.value = value;
  }

  @Override
  public boolean isChar() {
    return true;
  }

  @Override
  public int asInt() {
    return value;
  }

  @Override
  public double asDouble() {
    return value;
  }

  @Override
  public String asString() {
    return Character.toString(value);
  }

  @Override
  public char asChar() {
    return value;
  }

  @Override
  public long asLong() {
    return value;
  }

  @Override
  public float asFloat() {
    return value;
  }

}
