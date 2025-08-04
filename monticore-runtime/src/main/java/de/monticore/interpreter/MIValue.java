/* (c) https://github.com/MontiCore/monticore */
package de.monticore.interpreter;

import de.monticore.interpreter.values.FunctionMIValue;
import de.se_rwth.commons.logging.Log;

public interface MIValue {
  
  default boolean isWriteable() { return false; }
  
  default boolean isPrimitive() { return false; }

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
  
  default boolean isFunction() {
    return false;
  }
  
  default boolean isVoid() {
    return false;
  }
  
  default boolean isSIUnit() {
    return false;
  }
  
  default boolean isFlowControlSignal() { return false;}
  
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
    Log.error("0x31251 Type boolean is not applicable for " + printType() + " (" + printValue() + ").");
    return false;
  }
  
  default byte asByte() {
    Log.error("0x31252 Type byte is not applicable for " + printType() + " (" + printValue() + ").");
    return 0;
  }
  
  default char asChar() {
    Log.error("0x31253 Type char is not applicable for " + printType() + " (" + printValue() + ").");
    return '\0';
  }
  
  default short asShort() {
    Log.error("0x31254 Type short is not applicable for " + printType() + " (" + printValue() + ").");
    return 0;
  }
  
  default int asInt() {
    Log.error("0x31255 Type int is not applicable for " + printType() + " (" + printValue() + ").");
    return 0;
  }
  
  default long asLong() {
    Log.error("0x31256 Type long is not applicable for " + printType() + " (" + printValue() + ").");
    return 0L;
  }
  
  default float asFloat() {
    Log.error("0x31257 Type float is not applicable for " + printType() + " (" + printValue() + ").");
    return 0.0f;
  }
  
  default double asDouble() {
    Log.error("0x31258 Type double is not applicable for " + printType() + " (" + printValue() + ").");
    return 0.0;
  }
  
  default FunctionMIValue asFunction() {
    Log.error("0x57099 Type function is not applicable for " + printType() + " (" + printValue() + ").");
    return null;
  }
  
  default Object asObject() {
    Log.error("0x31259 Type object is not applicable for " + printType() + " (" + printValue() + ").");
    return null;
  }
  default String asError() {
    Log.error("0x57092 Type Error is not applicable for " + printType() + " (" + printValue() + ").");
    return null;
  }
  
  default MIValue asReturnValue() {
    Log.error("0x57083 Type ReturnValue is not applicable for " + printType() + " (" + printValue() + ").");
    return null;
  }
  
  default String printType() {
    Log.error("0x31260 printType is not applicable for '" + getClass().getName() + "'.");
    return "UnknownType";
  }
  
  default String printValue() {
    Log.error("0x31261 printValue is not applicable for '" + getClass().getName() + "'.");
    return "UnknownValue";
  }
  
}
