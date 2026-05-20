package de.monticore.values;

public interface MCSignalBreak extends MCSignalFlowControl {

  @Override
  default boolean isBreak() {
    return true;
  }

  @Override
  default String printType() {
    return "Break-Signal";
  }

  @Override
  default String printValue() {
    return "";
  }

}
