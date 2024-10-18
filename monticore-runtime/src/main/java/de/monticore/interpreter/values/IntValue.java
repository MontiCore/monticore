/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.Value;

public class IntValue implements Value {

  protected int value;

  public IntValue(int value) {
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
  public String asString() {
    return Integer.toString(value);
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
