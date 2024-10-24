/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.Value;

public class FloatValue implements Value {

  protected float value;

  public FloatValue(float value) {
    this.value = value;
  }

  @Override
  public boolean isFloat() {
    return true;
  }

  @Override
  public int asInt() {
    return (int) value;
  }

  @Override
  public double asDouble() {
    return value;
  }

  @Override
  public String asString() {
    return Float.toString(value);
  }

  @Override
  public long asLong() {
    return (long) value;
  }

  @Override
  public float asFloat() {
    return value;
  }
}
