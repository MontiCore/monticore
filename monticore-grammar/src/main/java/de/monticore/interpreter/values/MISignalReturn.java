package de.monticore.interpreter.values;

import java.util.Optional;

public class MISignalReturn extends MISignalFlowControl {

  // Null-able for efficiency
  protected final MIValue value;

  public MISignalReturn(MIValue value) {
    this.value = value;
  }

  public MISignalReturn() {
    this.value = null;
  }

  public static void signal(MIValue value) {
    throw new MISignalReturn(value);
  }

  public static void signal() {
    throw new MISignalReturn();
  }

  @Override
  public boolean isReturn() {
    return true;
  }

  public Optional<MIValue> getValue() {
    return Optional.ofNullable(value);
  }

  @Override
  public String printType() {
    return "Return-Signal";
  }

  @Override
  public String printValue() {
    if (value == null) {
      return "no value";
    }
    else {
      return value.printValue() + " : " + value.printType();
    }
  }
}
