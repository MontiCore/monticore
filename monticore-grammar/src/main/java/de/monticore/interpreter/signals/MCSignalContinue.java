/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.signals;

/**
 * Represents that a {@code continue} statement has been executed.
 */
public class MCSignalContinue
    extends MCSignalFlowControl {

  /**
   * signals the current thread.
   */
  public static void signal() {
    throw new MCSignalContinue();
  }

}
