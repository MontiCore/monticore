/* (c) https://github.com/MontiCore/monticore */
package de.monticore.values;

import java.util.List;

/**
 * A value that is an executable function.
 */
@FunctionalInterface
public interface MCValueFunction extends MCValue {

  MCValue execute(MCValue[] arguments);

  default MCValue execute(List<MCValue> arguments) {
    return execute(arguments.toArray(new MCValue[0]));
  }

  @Override
  default boolean isFunction() {
    return true;
  }

  @Override
  default MCValueFunction asFunction() {
    return this;
  }

  @Override
  default MCValueFunction asNativeObject() {
    return this;
  }

  /**
   * Technically, functions don't have an identity
   * or the identity is (near) non-computable
   * (depending on interpretation).
   * But for simplicity of the MIValue interface,
   * a somewhat reasonable implementation is given.
   *
   * @param other the value to check against this
   * @return whether they are the same function _pointer_
   */
  @Override
  default boolean checkEqualityOperator(MCValue other) {
    return this == other;
  }

  @Override
  default String printType() {
    // need to be done this way to allow this to be a functional interface
    throw new RuntimeException("should be overridden");
  }

  @Override
  default String printValue() {
    return "<callable>";
  }
}
