/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

import de.monticore.interpreter.Value;

public class BooleanValue implements Value {

  protected boolean value;

  public BooleanValue(boolean value){
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
  public String asString() {
    return String.valueOf(value);
  }
}
