package de.monticore.interpreter.signals;

public class MCSignalContinue
    extends MCSignalFlowControl {

  /**
   * signals the current thread.
   */
  public static void signal() {
    throw new MCSignalContinue();
  }

}
