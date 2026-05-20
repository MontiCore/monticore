package de.monticore.values;

/**
 * These values control the flow of execution.
 */
public interface MCSignalFlowControl extends MCValue {

  @Override
  default boolean isFlowControlSignal() {
    return true;
  }

  @Override
  default Object asNativeObject() {
    return this;
  }

  @Override
  default boolean checkEqualityOperator(MCValue other) {
    return this == other;
  }

}
