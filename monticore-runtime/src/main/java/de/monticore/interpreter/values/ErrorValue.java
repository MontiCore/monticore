package de.monticore.interpreter.values;

import de.monticore.interpreter.Value;

public class ErrorValue implements Value {
  
  String message;
  
  public ErrorValue(String message) {
    this.message = message;
  }
  
  @Override
  public boolean isError() {
    return true;
  }
  
}
