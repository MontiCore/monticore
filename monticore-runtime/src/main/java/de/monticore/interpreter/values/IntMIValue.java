/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public class IntMIValue implements MIValue {

  protected int value;

  public IntMIValue(int value) {
    this.value = value;
  }

  @Override
  public boolean isInt() {
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
  public long asLong() {
    return value;
  }

  @Override
  public float asFloat() {
    return value;
  }
  
  @Override
  public String printType() {
    return "Integer";
  }
  
  @Override
  public String printValue() {
    return String.valueOf(value);
  }
}
