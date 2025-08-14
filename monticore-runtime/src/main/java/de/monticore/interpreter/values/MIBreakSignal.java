package de.monticore.interpreter.values;

public class MIBreakSignal implements MIFlowControlSignal {

  @Override
  public boolean isBreak() {
    return true;
  }

  @Override
  public String printType() {
    return "Break-Signal";
  }

  @Override
  public String printValue() {
    return "";
  }

}
