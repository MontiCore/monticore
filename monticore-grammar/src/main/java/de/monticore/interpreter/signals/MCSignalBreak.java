/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.signals;

/**
 * Represents that a {@code break} statement has been executed.
 */
public class MCSignalBreak
    extends MCSignalFlowControl {

  /**
   * signals the current thread.
   */
  public static void signal() {
    throw new MCSignalBreak();
  }

}
