package de.monticore.values;

public interface MCSignalContinue extends MCSignalFlowControl {

  @Override
  default boolean isContinue() {
    return true;
  }

  @Override
  default String printType() {
    return "Continue";
  }

  @Override
  default String printValue() {
    return "";
  }

}
