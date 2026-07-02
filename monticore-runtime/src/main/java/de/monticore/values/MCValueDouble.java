/* (c) https://github.com/MontiCore/monticore */
package de.monticore.values;

public class MCValueDouble implements MCValue {

  protected double value;

  public MCValueDouble(double value) {
    this.value = value;
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public boolean isDouble() {
    return true;
  }

  @Override
  public byte asByte() {
    return (byte) value;
  }

  @Override
  public short asShort() {
    return (short) value;
  }

  @Override
  public char asChar() {
    return (char) value;
  }

  @Override
  public int asInt() {
    return (int) value;
  }

  @Override
  public long asLong() {
    return (long) value;
  }

  @Override
  public float asFloat() {
    return (float) value;
  }

  @Override
  public double asDouble() {
    return value;
  }

  @Override
  public Double asNativeObject() {
    return value;
  }

  @Override
  public String asString() {
    return String.valueOf(value);
  }

  @Override
  public boolean checkEqualityOperator(MCValue other) {
    return (other.isDouble() || other.isInt())
        && value == other.asDouble();
  }

  @Override
  public String printType() {
    return "Double";
  }

  @Override
  public String printValue() {
    return String.valueOf(value);
  }

}
