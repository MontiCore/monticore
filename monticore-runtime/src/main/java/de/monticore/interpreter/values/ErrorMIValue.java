package de.monticore.interpreter.values;

public class ErrorMIValue implements MIFlowControlSignal {
  
  String message;
  
  public ErrorMIValue(String message) {
    this.message = message;
  }
  
  @Override
  public boolean isError() {
    return true;
  }
  
  @Override
  public String asError() {
    return message;
  }
  
  @Override
  public String printType() {
    return "Error";
  }
  
  @Override
  public String printValue() {
    return message;
  }
  
}
