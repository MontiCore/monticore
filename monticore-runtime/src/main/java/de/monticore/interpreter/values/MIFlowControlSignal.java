package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public interface MIFlowControlSignal extends MIValue {

  @Override
  default boolean isFlowControlSignal() {
    return true;
  }

}
