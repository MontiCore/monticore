package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public class VoidMIValue implements MIValue {

  @Override
  public boolean isVoid() {
    return true;
  }

  @Override
  public String printType() {
    return "Void";
  }

  @Override
  public String printValue() {
    return "";
  }

}
