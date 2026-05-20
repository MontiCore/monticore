package de.monticore.values;

import java.util.Optional;

public interface MCSignalReturn extends MCSignalFlowControl {

  @Override
  default boolean isReturn() {
    return true;
  }

  Optional<MCValue> getValue();

  @Override
  default String printType() {
    return "Return-Signal";
  }

  @Override
  default String printValue() {
    if (getValue().isEmpty()) {
      return "no value";
    }
    else {
      MCValue value = getValue().get();
      return value.printValue() + " : " + value.printType();
    }
  }

}
