package de.monticore.interpreter.values;

import de.monticore.values.MCValue;

import java.util.Optional;

public class MCSignalReturnForInterpreter
    extends AbstractMCSignalFlowControlForInterpreter
    implements de.monticore.values.MCSignalReturn {

  // Null-able for efficiency
  protected final MCValue value;

  public MCSignalReturnForInterpreter(MCValue value) {
    this.value = value;
  }

  public MCSignalReturnForInterpreter() {
    this.value = null;
  }

  public static void signal(MCValue value) {
    throw new MCSignalReturnForInterpreter(value);
  }

  public static void signal() {
    throw new MCSignalReturnForInterpreter();
  }

  @Override
  public boolean isReturn() {
    return true;
  }

  public Optional<MCValue> getValue() {
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
