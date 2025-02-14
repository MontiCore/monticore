package de.monticore.interpreter.values;

import de.monticore.interpreter.Value;

public class ShortValue implements Value {
  
  protected short value;
  
  public ShortValue(short value) {
    this.value = value;
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

}
