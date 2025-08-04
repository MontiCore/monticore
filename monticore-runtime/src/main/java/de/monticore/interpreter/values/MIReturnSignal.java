package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public class MIReturnSignal implements MIFlowControlSignal {
  
  MIValue value;
  
  public MIReturnSignal() {
    this.value = new VoidMIValue();
  }
  
  public MIReturnSignal(MIValue value) {
    this.value = value;
  }
  
  @Override
  public boolean isReturn() {
    return true;
  }
  
  @Override
  public MIValue asReturnValue() {
    return value;
  }
  
  @Override
  public String printType() {
    return "Return";
  }
  
  @Override
  public String printValue() {
    return value.printType() + "(" + value.printValue() + ")";
  }
}
