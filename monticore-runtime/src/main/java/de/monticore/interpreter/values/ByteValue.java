package de.monticore.interpreter.values;

import de.monticore.interpreter.Value;

public class ByteValue implements Value {
  
  protected byte value;
  
  public ByteValue(byte value) {
    this.value = value;
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
  
}
