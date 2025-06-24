/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public class BooleanMIValue implements MIValue {

  protected boolean value;

  public BooleanMIValue(boolean value){
    this.value = value;
  }

  @Override
  public boolean isBoolean() {
    return true;
  }

  @Override
  public boolean asBoolean() {
    return value;
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
