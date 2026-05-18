package de.monticore.interpreter.values;

public class MIContinueSignal extends MIFlowControlSignal {

  /**
   * signals the current thread.
   */
  public static void signal() {
    throw new MIContinueSignal();
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
