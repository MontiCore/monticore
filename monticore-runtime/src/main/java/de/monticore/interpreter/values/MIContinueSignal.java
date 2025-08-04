package de.monticore.interpreter.values;

public class MIContinueSignal implements MIFlowControlSignal {
  
  @Override
  public boolean isContinue() {
    return true;
  }
  
  @Override
  public String printType() {
    return "Continue";
  }
  
  @Override
  public String printValue() {
    return "";
  }
  
}
