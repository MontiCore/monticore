/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public class DoubleMIValue implements MIValue {

  protected double value;

  public DoubleMIValue(double value) {
    this.value = value;
  }

  @Override
  public boolean isDouble() {
    return true;
  }

  @Override
  public double asDouble() {
    return value;
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
