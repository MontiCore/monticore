/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.Value;

public class LongValue implements Value {

  protected long value;

  public LongValue(long value) {
    this.value = value;
  }

  @Override
  public boolean isLong() {
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
    return Long.toString(value);
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
