/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.signals;

import java.util.Optional;

/**
 * Represents that a {@code break} statement has been executed.
 */
public class MCSignalBreak
    extends MCSignalFlowControl {

  // Null-able for efficiency
  protected final String label;

  public MCSignalBreak() {
    this.label = null;
  }

  public MCSignalBreak(String label) {
    this.label = label;
  }

  /**
   * signals the current thread.
   */
  public static void signal() {
    throw new MCSignalBreak();
  }

  public static void signal(String label) {
    throw new MCSignalBreak(label);
  }

  public Optional<String> getLabel() {
    return Optional.ofNullable(label);
  }

}
