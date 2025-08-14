package de.monticore.interpreter.values;

import de.monticore.interpreter.MIValue;

public abstract class WriteableMIValue implements MIValue {

  @Override
  public boolean isWriteable() {
    return true;
  }

  public abstract void write(MIValue value);

  public abstract MIValue getMIValue();

  @Override
  public boolean isPrimitive() {

    return getMIValue().isPrimitive();
  }

  @Override
  public boolean isBoolean() {
    return getMIValue().isBoolean();
  }

  @Override
  public boolean isByte() {
    return getMIValue().isByte();
  }

  @Override
  public boolean isChar() {
    return getMIValue().isChar();
  }

  @Override
  public boolean isShort() {
    return getMIValue().isShort();
  }

  @Override
  public boolean isInt() {
    return getMIValue().isInt();
  }

  @Override
  public boolean isLong() {
    return getMIValue().isLong();
  }

  @Override
  public boolean isFloat() {
    return getMIValue().isFloat();
  }

  @Override
  public boolean isDouble() {
    return getMIValue().isDouble();
  }

  @Override
  public boolean isObject() {
    return getMIValue().isObject();
  }

  @Override
  public boolean isFunction() {
    return getMIValue().isFunction();
  }

  @Override
  public boolean isVoid() {
    return getMIValue().isVoid();
  }

  @Override
  public boolean isSIUnit() {
    return getMIValue().isSIUnit();
  }

  @Override
  public boolean isFlowControlSignal() {
    return getMIValue().isFlowControlSignal();
  }

  @Override
  public boolean isError() {
    return getMIValue().isError();
  }

  @Override
  public boolean isBreak() {
    return getMIValue().isBreak();
  }

  @Override
  public boolean isContinue() {
    return getMIValue().isContinue();
  }

  @Override
  public boolean isReturn() {
    return getMIValue().isReturn();
  }

  @Override
  public boolean asBoolean() {
    return getMIValue().asBoolean();
  }

  @Override
  public byte asByte() {
    return getMIValue().asByte();
  }

  @Override
  public char asChar() {
    return getMIValue().asChar();
  }

  @Override
  public short asShort() {
    return getMIValue().asShort();
  }

  @Override
  public int asInt() {
    return getMIValue().asInt();
  }

  @Override
  public long asLong() {
    return getMIValue().asLong();
  }

  @Override
  public float asFloat() {
    return getMIValue().asFloat();
  }

  @Override
  public double asDouble() {
    return getMIValue().asDouble();
  }

  @Override
  public FunctionMIValue asFunction() {
    return getMIValue().asFunction();
  }

  @Override
  public Object asObject() {
    return getMIValue().asObject();
  }

  @Override
  public MIValue asReturnValue() {
    return getMIValue().asReturnValue();
  }

  @Override
  public String asError() {
    return getMIValue().asError();
  }

}
