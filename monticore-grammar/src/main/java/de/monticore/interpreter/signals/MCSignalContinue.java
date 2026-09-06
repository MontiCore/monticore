/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.signals;

import java.util.Optional;

/**
 * Represents that a {@code continue} statement has been executed.
 */
public class MCSignalContinue
    extends MCSignalFlowControl {

  // Null-able for efficiency
  protected final String label;

  public MCSignalContinue() {
    this.label = null;
  }

  public MCSignalContinue(String label) {
    this.label = label;
  }

  /**
   * signals the current thread.
   */
  public static void signal() {
    throw new MCSignalContinue();
  }

  public static void signal(String label) {
    throw new MCSignalContinue(label);
  }

  public Optional<String> getLabel() {
    return Optional.ofNullable(label);
  }

}
