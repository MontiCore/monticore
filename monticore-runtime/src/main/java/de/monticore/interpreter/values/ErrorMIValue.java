package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public class ErrorMIValue implements MIValue {
  
  String message;
  
  public ErrorMIValue(String message) {
    this.message = message;
  }
  
  @Override
  public boolean isError() {
    return true;
  }
  
}
