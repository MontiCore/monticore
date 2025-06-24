/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public class FloatMIValue implements MIValue {

  protected float value;

  public FloatMIValue(float value) {
    this.value = value;
  }

  @Override
  public boolean isFloat() {
    return true;
  }

  @Override
  public double asDouble() {
    return value;
  }

  @Override
  public float asFloat() {
    return value;
  }
  
  @Override
  public String printType() {
    return "Float";
  }
  
  @Override
  public String printValue() {
    return String.valueOf(value);
  }
}
