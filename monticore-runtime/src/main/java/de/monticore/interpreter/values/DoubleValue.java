/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.Value;
import de.se_rwth.commons.logging.Log;

public class DoubleValue implements Value {

  protected double value;

  public DoubleValue(double value) {
    this.value = value;
  }

  @Override
  public boolean isDouble() {
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
    return Double.toString(value);
  }

  @Override
  public long asLong() {
    return (long) value;
  }

  @Override
  public float asFloat() {
    return (float) value;
  }
}
