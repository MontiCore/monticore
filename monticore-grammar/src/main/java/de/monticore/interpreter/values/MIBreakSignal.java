package de.monticore.interpreter.values;

public class MIBreakSignal extends MIFlowControlSignal {

  /**
   * signals the current thread.
   */
  public static void signal() {
    throw new MIBreakSignal();
  }

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
