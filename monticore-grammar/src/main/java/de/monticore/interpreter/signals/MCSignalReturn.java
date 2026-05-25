/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.signals;

import de.monticore.values.MCValue;

import java.util.Optional;

public class MCSignalReturn
    extends MCSignalFlowControl {

  // Null-able for efficiency
  protected final MCValue value;

  public MCSignalReturn(MCValue value) {
    this.value = value;
  }

  public MCSignalReturn() {
    this.value = null;
  }

  public static void signal(MCValue value) {
    throw new MCSignalReturn(value);
  }

  public static void signal() {
    throw new MCSignalReturn();
  }

  public Optional<MCValue> getValue() {
    return Optional.ofNullable(value);
  }

}
