/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter;

import de.se_rwth.commons.logging.Log;

public interface Value {

  default boolean isBoolean() {
    return false;
  }

  default boolean isInt() {
    return false;
  }

  default boolean isDouble() {
    return false;
  }

  default boolean isString() {
    return false;
  }

  default boolean isChar() {
    return false;
  }

  default boolean isObject() {
    return false;
  }


  default boolean isLong() {
    return false;
  }

  default boolean isFloat() {
    return false;
  }

  default boolean asBoolean() {
    Log.error("0x31251 Type boolean is not applicable for result value.");
    return false;
  }

  default int asInt() {
    Log.error("0x31251 Type int is not applicable for result value.");
    return 0;
  }

  default double asDouble() {
    Log.error("0x31251 Type double is not applicable for result value.");
    return 0.0;
  }

  default String asString() {
    Log.error("0x31251 Type String is not applicable for result value.");
    return "";
  }

  default char asChar() {
    Log.error("0x31251 Type char is not applicable for result value.");
    return '\u0000';
  }

  default Object asObject() {
    Log.error("0x31251 Type Object is not applicable for result value.");
    return new Object();
  }

  default long asLong() {
    Log.error("0x31251 Type long is not applicable for result value.");
    return 0L;
  }

  default float asFloat() {
    Log.error("0x31251 Type float is not applicable for result value.");
    return 0.0f;
  }
}
