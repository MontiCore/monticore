package de.monticore.interpreter.values;

import java.util.Optional;

public class MIReturnSignal extends MIFlowControlSignal {

  // Null-able for efficiency
  protected final MIValue value;

  public MIReturnSignal(MIValue value) {
    this.value = value;
  }

  public MIReturnSignal() {
    this.value = null;
  }

  public static void signal(MIValue value) {
    throw new MIReturnSignal(value);
  }

  public static void signal() {
    throw new MIReturnSignal();
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
