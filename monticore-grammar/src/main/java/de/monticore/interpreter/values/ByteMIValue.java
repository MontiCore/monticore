package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public class ByteMIValue implements MIValue {
  
  protected byte value;
  
  public ByteMIValue(byte value) {
    this.value = value;
  }
  
  @Override
  public boolean isPrimitive() {
    return true;
  }
  
  @Override
  public boolean isByte() {
    return true;
  }
  
  @Override
  public byte asByte() {
    return value;
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
    return "Byte";
  }
  
  @Override
  public String printValue() {
    return String.valueOf(value);
  }
  
}
