package de.monticore.interpreter.values;

public class MISignalBreak extends MISignalFlowControl {

  /**
   * signals the current thread.
   */
  public static void signal() {
    throw new MISignalBreak();
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
