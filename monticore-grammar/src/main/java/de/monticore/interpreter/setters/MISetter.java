// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.setters;

import de.monticore.interpreter.frames.MIFrame;
import de.monticore.interpreter.values.MIValue;

/**
 * Represents a function that is used to set a value to an LValue.
 */
public interface MISetter {

  default boolean isSetterBoolean() {
    return false;
  }

  default boolean isSetterInt() {
    return false;
  }

  default boolean isSetterDouble() {
    return false;
  }

  default boolean isSetterValue() {
    return false;
  }

  default MISetterBoolean asSetterBoolean() {
    throw new RuntimeException("Not a setter for boolean");
  }

  default MISetterInt asSetterInt() {
    throw new RuntimeException("Not a setter for int");
  }

  default MISetterDouble asSetterDouble() {
    throw new RuntimeException("Not a setter for double");
  }

  default MISetterValue asSetterValue() {
    throw new RuntimeException("Not a setter for value");
  }

  /**
   * This is the less efficient version of the specific setters,
   * but sometimes, we only have access to an MIValue anyway.
   *
   * @param value The value to be set. It must have the correct type.
   */
  void set(MIFrame frame, MIValue value);

}
