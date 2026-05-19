package de.monticore.interpreter.values;

public class MISignalContinue extends MISignalFlowControl {

  /**
   * signals the current thread.
   */
  public static void signal() {
    throw new MISignalContinue();
  }

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
