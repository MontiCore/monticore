/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter.values;

public interface MIValue {

  default boolean isPrimitive() {
    return false;
  }

  default boolean isBoolean() {
    return false;
  }

  default boolean isByte() {
    return false;
  }

  default boolean isChar() {
    return false;
  }

  default boolean isShort() {
    return false;
  }

  default boolean isInt() {
    return false;
  }

  default boolean isLong() {
    return false;
  }

  default boolean isFloat() {
    return false;
  }

  default boolean isDouble() {
    return false;
  }

  default boolean isObject() {
    return false;
  }

  default boolean isObjectOfSymbol() {
    return false;
  }

  default boolean isObjectOfJava() {
    return false;
  }

  /**
   * helper for String specifically
   */
  default boolean isString() {
    return false;
  }

  default boolean isFunction() {
    return false;
  }

  default boolean isVoid() {
    return false;
  }

  default boolean isSIUnit() {
    return false;
  }

  default boolean isFlowControlSignal() {
    return false;
  }

  default boolean isError() {
    return false;
  }

  default boolean isBreak() {
    return false;
  }

  default boolean isContinue() {
    return false;
  }

  default boolean isReturn() {
    return false;
  }

  default boolean asBoolean() {
    throw new IllegalStateException();
  }

  default byte asByte() {
    throw new IllegalStateException();
  }

  default char asChar() {
    throw new IllegalStateException();
  }

  default short asShort() {
    throw new IllegalStateException();
  }

  default int asInt() {
    throw new IllegalStateException();
  }

  default long asLong() {
    throw new IllegalStateException();
  }

  default float asFloat() {
    throw new IllegalStateException();
  }

  default double asDouble() {
    throw new IllegalStateException();
  }

  default FunctionMIValue asFunction() {
    throw new IllegalStateException();
  }

  /**
   * cf. {@link #asNativeObject()}.
   *
   * @return The value as an {@link MIValueObject}, iff it is one.
   */
  default MIValueObject asObject() {
    throw new IllegalStateException();
  }

  default String asString() {
    throw new IllegalStateException();
  }

  default String asError() {
    throw new IllegalStateException();
  }

  default MIValue asReturnValue() {
    throw new IllegalStateException();
  }

  /**
   * Used to pass the value to native Java classes and similar,
   * e.g., collections.
   * <p>
   * Note: not all values can have a native representation,
   * as such, their native version is the MIValue itself.
   *
   * @return The value as a Java native Object.
   */
  Object asNativeObject();

  /**
   * basically implements {@code ==}
   *
   * @param other the value to check against this
   * @return whether they are considered equal
   */
  boolean checkEqualityOperator(MIValue other);

  /**
   * Print the type of the MIValue in human-readable form
   */
  String printType();

  /**
   * Print the value in human-readable form
   */
  String printValue();

}
