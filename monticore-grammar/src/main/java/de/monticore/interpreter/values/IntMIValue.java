/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

public class IntMIValue implements MIValue {

  protected int value;

  public IntMIValue(int value) {
    this.value = value;
  }

  @Override
  public boolean isPrimitive() {
    return true;
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
  public Integer asNativeObject() {
    return value;
  }

  @Override
  public String asString() {
    return String.valueOf(value);
  }

  @Override
  public boolean checkEqualityOperator(MIValue other) {
    return other.isInt() || other.isDouble() && this.value == other.asDouble();
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
