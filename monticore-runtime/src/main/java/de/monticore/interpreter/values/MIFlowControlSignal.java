package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public interface MIFlowControlSignal extends MIValue {
  
  @Override
  public default boolean isFlowControlSignal() {
    return true;
  }
}
