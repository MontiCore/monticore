package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public class ShortMIValue implements MIValue {
  
  protected short value;
  
  public ShortMIValue(short value) {
    this.value = value;
  }
  
  @Override
  public boolean isPrimitive() {
    return true;
  }
  
  @Override
  public boolean isShort() {
    return true;
  }
  
  @Override
  public short asShort() {
    return value;
  }
  
  @Override
  public int asInt() {
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
  public double asDouble() {
    return value;
  }
  
  @Override
  public String printType() {
    return "Short";
  }
  
  @Override
  public String printValue() {
    return String.valueOf(value);
  }

}
