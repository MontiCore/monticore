package de.monticore.interpreter.signals;

public class MCSignalBreak
    extends MCSignalFlowControl {

  /**
   * signals the current thread.
   */
  public static void signal() {
    throw new MCSignalBreak();
  }

}
