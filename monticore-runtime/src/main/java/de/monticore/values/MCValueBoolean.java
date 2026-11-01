/* (c) https://github.com/MontiCore/monticore */
package de.monticore.values;

public class MCValueBoolean implements MCValue {

  protected boolean value;

  public MCValueBoolean(boolean value) {
    this.value = value;
  }

  @Override
  public boolean isBoolean() {
    return true;
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public boolean asBoolean() {
    return value;
  }

  @Override
  public Boolean asNativeObject() {
    return value;
  }

  @Override
  public String asString() {
    return String.valueOf(value);
  }

  @Override
  public boolean checkEqualityOperator(MCValue other) {
    return other.isBoolean() && value == other.asBoolean();
  }

  @Override
  public boolean equals(Object otherObj) {
    if (this == otherObj) {
      return true;
    }
    if (!(otherObj instanceof MCValueBoolean other)) {
      return false;
    }
    return value == other.value;
  }

  @Override
  public int hashCode() {
    return Boolean.hashCode(value);
  }

  @Override
  public String printType() {
    return "Boolean";
  }

  @Override
  public String printValue() {
    return String.valueOf(value);
  }

}
