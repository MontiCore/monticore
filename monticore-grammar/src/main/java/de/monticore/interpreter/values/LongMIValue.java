/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public class LongMIValue implements MIValue {

  protected long value;

  public LongMIValue(long value) {
    this.value = value;
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public boolean isLong() {
    return true;
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
    return "Long";
  }

  @Override
  public String printValue() {
    return String.valueOf(value) + "L";
  }
}
