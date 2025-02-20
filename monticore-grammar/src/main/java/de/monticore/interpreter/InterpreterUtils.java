package de.monticore.interpreter;

import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

import static de.monticore.interpreter.MIValueFactory.createValue;

public class InterpreterUtils {
  
  public static MIValue calcOpPrimitive(MIValue v1, MIValue v2, String resultType, BinaryOperator<Integer> opInt, BinaryOperator<Long> opLong,
      BinaryOperator<Float> opFloat, BinaryOperator<Double> opDouble, String opName) {
    
    
    switch (resultType) {
      case BasicSymbolsMill.INT: return createValue((int)opInt.apply(v1.asInt(), v2.asInt()));
      case BasicSymbolsMill.LONG: return createValue((long)opLong.apply(v1.asLong(), v2.asLong()));
      case BasicSymbolsMill.FLOAT: return createValue((float)opFloat.apply(v1.asFloat(), v2.asFloat()));
      case BasicSymbolsMill.DOUBLE: return createValue((double)opDouble.apply(v1.asDouble(), v2.asDouble()));
    }
    String errorMsg = opName + " operation with result of type " + resultType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  public static MIValue calcBitwiseOpPrimitive(MIValue v1, MIValue v2, String resultType, BinaryOperator<Integer> opInt, BinaryOperator<Long> opLong,
      String opName) {
    switch (resultType) {
      case BasicSymbolsMill.INT: return createValue((int)opInt.apply(v1.asInt(), v2.asInt()));
      case BasicSymbolsMill.LONG: return createValue((long)opLong.apply(v1.asLong(), v2.asLong()));
    }
    String errorMsg = opName + " operation with result of type " + resultType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  public static MIValue calcBitwiseLogicalOpPrimitive(MIValue v1, MIValue v2, String resultType, BinaryOperator<Boolean> opBool, BinaryOperator<Integer> opInt,
      BinaryOperator<Long> opLong, String opName) {
    switch (resultType) {
      case BasicSymbolsMill.BOOLEAN: return createValue((boolean)opBool.apply(v1.asBoolean(), v2.asBoolean()));
      case BasicSymbolsMill.INT: return createValue((int)opInt.apply(v1.asInt(), v2.asInt()));
      case BasicSymbolsMill.LONG: return createValue((long)opLong.apply(v1.asLong(), v2.asLong()));
    }
    String errorMsg = opName + " operation with result of type " + resultType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  public static MIValue calcShiftPrimitive(MIValue v1, MIValue v2, String resultType, BiFunction<Integer, Long, Integer> opInt, BinaryOperator<Long> opLong,
      String opName) {
    switch (resultType) {
      case BasicSymbolsMill.INT: return createValue((int)opInt.apply(v1.asInt(), v2.asLong()));
      case BasicSymbolsMill.LONG: return createValue((long)opLong.apply(v1.asLong(), v2.asLong()));
    }
    String errorMsg = opName + " operation with result of type " + resultType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  public static MIValue calcOp(MIValue v1, MIValue v2, SymTypeExpression resultType, BinaryOperator<Integer> opInt, BinaryOperator<Long> opLong,
      BinaryOperator<Float> opFloat, BinaryOperator<Double> opDouble, String opName) {
    if (resultType.isPrimitive()) {
      return calcOpPrimitive(v1, v2, resultType.asPrimitive().getPrimitiveName(), opInt, opLong, opFloat, opDouble, opName);
    }
    
    String errorMsg = opName + " operation with result of type " + resultType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  public static MIValue calcBitwiseOp(MIValue v1, MIValue v2, SymTypeExpression resultType, BinaryOperator<Integer> opInt, BinaryOperator<Long> opLong,
      String opName) {
    if (resultType.isPrimitive()) {
      return calcBitwiseOpPrimitive(v1, v2, resultType.asPrimitive().getPrimitiveName(), opInt, opLong, opName);
    }
    
    String errorMsg = opName + " operation with result of type " + resultType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  public static MIValue calcBitwiseLogicalOp(MIValue v1, MIValue v2, SymTypeExpression resultType, BinaryOperator<Boolean> opBool, BinaryOperator<Integer> opInt,
      BinaryOperator<Long> opLong, String opName) {
    if (resultType.isPrimitive()) {
      return calcBitwiseLogicalOpPrimitive(v1, v2, resultType.asPrimitive().getPrimitiveName(), opBool, opInt, opLong, opName);
    }
    
    String errorMsg = opName + " operation with result of type " + resultType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  public static MIValue calcShift(MIValue v1, MIValue v2, SymTypeExpression resultType, BiFunction<Integer, Long, Integer> opInt,
      BinaryOperator<Long> opLong, String opName) {
    if (resultType.isPrimitive()) {
      return calcShiftPrimitive(v1, v2, resultType.asPrimitive().getPrimitiveName(), opInt, opLong, opName);
    }
    
    String errorMsg = opName + " operation with result of type " + resultType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  public static MIValue convertToPrimitiveExplicit(String from, String to, MIValue value) {
    if (to.equals(BasicSymbolsMill.BOOLEAN) || from.equals(BasicSymbolsMill.BOOLEAN)) {
      String errorMsg = "Cast to or from boolean is not supported.";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    if (to.equals(BasicSymbolsMill.BYTE)) {
      switch (from) {
        case BasicSymbolsMill.DOUBLE: return createValue((byte) value.asDouble());
        case BasicSymbolsMill.FLOAT: return createValue((byte) value.asFloat());
        case BasicSymbolsMill.LONG: return createValue((byte) value.asLong());
        case BasicSymbolsMill.INT: return createValue((byte) value.asInt());
        case BasicSymbolsMill.SHORT: return createValue((byte) value.asShort());
        case BasicSymbolsMill.CHAR: return createValue((byte) value.asChar());
        default: return createValue(value.asByte());
      }
      
    } else if (to.equals(BasicSymbolsMill.SHORT)) {
      switch (from) {
        case BasicSymbolsMill.DOUBLE: return createValue((short) value.asDouble());
        case BasicSymbolsMill.FLOAT: return createValue((short) value.asFloat());
        case BasicSymbolsMill.LONG: return createValue((short) value.asLong());
        case BasicSymbolsMill.INT: return createValue((short) value.asInt());
        case BasicSymbolsMill.CHAR: return createValue((short) value.asChar());
        default: return createValue(value.asShort());
      }
      
    } else if (to.equals(BasicSymbolsMill.CHAR)) {
      switch (from) {
        case BasicSymbolsMill.DOUBLE: return createValue((char) value.asDouble());
        case BasicSymbolsMill.FLOAT: return createValue((char) value.asFloat());
        case BasicSymbolsMill.LONG: return createValue((char) value.asLong());
        case BasicSymbolsMill.INT: return createValue((char) value.asInt());
        case BasicSymbolsMill.SHORT: return createValue((char) value.asShort());
        case BasicSymbolsMill.BYTE: return createValue((char) value.asByte());
        default: return createValue(value.asChar());
      }
      
    } else if (to.equals(BasicSymbolsMill.INT)) {
      switch (from) {
        case BasicSymbolsMill.DOUBLE: return createValue((int) value.asDouble());
        case BasicSymbolsMill.FLOAT: return createValue((int) value.asFloat());
        case BasicSymbolsMill.LONG: return createValue((int) value.asLong());
        default: return createValue(value.asInt());
      }
      
    } else if (to.equals(BasicSymbolsMill.LONG)) {
      if (from.equals(BasicSymbolsMill.DOUBLE)) {
        return createValue((long)value.asDouble());
      } else if (from.equals(BasicSymbolsMill.FLOAT)) {
        return createValue((long)value.asFloat());
      }
      return createValue(value.asLong());
      
    } else if (to.equals(BasicSymbolsMill.FLOAT)) {
      if (from.equals(BasicSymbolsMill.DOUBLE)) {
        return createValue((float)value.asDouble());
      }
      return createValue(value.asFloat());
      
    } else if (to.equals(BasicSymbolsMill.DOUBLE)) {
      return createValue(value.asDouble());
    }
    
    String errorMsg = "Cast from " + from + " to " + to + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  public static MIValue convertToPrimitiveImplicit(String targetType, MIValue value) {
    if (targetType.equals(BasicSymbolsMill.BYTE)) {
      return createValue(value.asByte());
    } else if (targetType.equals(BasicSymbolsMill.SHORT)) {
      return createValue(value.asShort());
    } else if (targetType.equals(BasicSymbolsMill.CHAR)) {
      return createValue(value.asChar());
    } else if (targetType.equals(BasicSymbolsMill.INT)) {
      return createValue(value.asInt());
    } else if (targetType.equals(BasicSymbolsMill.LONG)) {
      return createValue(value.asLong());
    } else if (targetType.equals(BasicSymbolsMill.FLOAT)) {
      return createValue(value.asFloat());
    } else if (targetType.equals(BasicSymbolsMill.DOUBLE)) {
      return createValue(value.asDouble());
    }
    
    String errorMsg = "Implicit cast to " + targetType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
}
