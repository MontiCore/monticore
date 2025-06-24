package de.monticore.interpreter;

import de.monticore.interpreter.values.*;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.lang.reflect.Field;
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
      String errorMsg = "0x57060 Cast to or from boolean is not supported.";
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
    
    String errorMsg = "0x57061 Cast from " + from + " to " + to + " is not supported.";
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
    
    String errorMsg = "0x57062 Implicit cast to " + targetType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  public static MIValue convertImplicit(SymTypeExpression targetType, MIValue value) {
    if (targetType.isPrimitive()) {
      return convertToPrimitiveImplicit(targetType.asPrimitive().getPrimitiveName(), value);
    } else {
      return value; // everything else is an object or function which is handled by reflection
    }
  }
  
  public static MIValue getObjectAttribute(ObjectMIValue object, String attributeName, SymTypeExpression type) {
    Field attribute;
    try {
      attribute = object.getClass().getField(attributeName);
    } catch (NoSuchFieldException e) {
      String errorMsg = "0x57063 Tried to access attribute '" + attributeName + "' of class '"
          + object.getClass().getName() + "'. No such attribute exists.";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    
    try {
      if (type.isPrimitive()) {
        String typeName = type.asPrimitive().getPrimitiveName();
        if (typeName.equals(BasicSymbolsMill.BOOLEAN)) {
          return MIValueFactory.createValue(attribute.getBoolean(object));
        } else if (typeName.equals(BasicSymbolsMill.BYTE)) {
          return MIValueFactory.createValue(attribute.getByte(object));
        } else if (typeName.equals(BasicSymbolsMill.SHORT)) {
          return MIValueFactory.createValue(attribute.getShort(object));
        } else if (typeName.equals(BasicSymbolsMill.CHAR)) {
          return MIValueFactory.createValue(attribute.getChar(object));
        } else if (typeName.equals(BasicSymbolsMill.INT)) {
          return MIValueFactory.createValue(attribute.getInt(object));
        } else if (typeName.equals(BasicSymbolsMill.LONG)) {
          return MIValueFactory.createValue(attribute.getLong(object));
        } else if (typeName.equals(BasicSymbolsMill.FLOAT)) {
          return MIValueFactory.createValue(attribute.getFloat(object));
        } else if (typeName.equals(BasicSymbolsMill.DOUBLE)) {
          return MIValueFactory.createValue(attribute.getDouble(object));
        }
        
      } else if (type.isObjectType()) {
        return MIValueFactory.createValue(attribute.get(object));
      }
    } catch (IllegalAccessException e) {
      String errorMsg = "0x57064 Tried to access attribute '" + attributeName + "' of class '"
          + object.getClass().getName() + "'. Attribute is not accessible.";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    
    String errorMsg = "0x57065 Attribute Access operation does not support attributes of type '"
      + type.printFullName() + "'.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  public static Class<?> typeOfValue(MIValue value) {
    if (value.isBoolean()) {
      return boolean.class;
    } else if (value.isChar()) {
      return char.class;
    } else if (value.isByte()) {
      return byte.class;
    } else if (value.isShort()) {
      return short.class;
    } else if (value.isInt()) {
      return int.class;
    } else if (value.isLong()) {
      return long.class;
    } else if (value.isFloat()) {
      return float.class;
    } else if (value.isDouble()) {
      return double.class;
    } else if (value.isObject()) {
      return value.asObject().getClass();
    } else if (value.isFunction()) {
      // TODO maybe abstract method in FunctionMIValue that builds Function-Object
    }
    String errorMsg = "0x57066 Failed to get java type of value.";
    Log.error(errorMsg);
    return null;
  }
  
  public static Object valueToObject(MIValue value) {
    if (value.isBoolean()) {
      return value.asBoolean();
    } else if (value.isChar()) {
      return value.asChar();
    } else if (value.isByte()) {
      return value.asByte();
    } else if (value.isShort()) {
      return value.asShort();
    } else if (value.isInt()) {
      return value.asInt();
    } else if (value.isLong()) {
      return value.asLong();
    } else if (value.isFloat()) {
      return value.asFloat();
    } else if (value.isDouble()) {
      return value.asDouble();
    } else if (value.isObject()) {
      return value.asObject();
    } else if (value.isFunction()) {
      // TODO maybe abstract method in FunctionMIValue that builds Function-Object
    }
    String errorMsg = "0x57067 Failed to get java type of value.";
    Log.error(errorMsg);
    return null;
  }
  
  public static MIValue objectToValue(Object object) {
    if (object instanceof Boolean) {
      return new BooleanMIValue((Boolean)object);
    } else if (object instanceof Character) {
      return new CharMIValue((Character)object);
    } else if (object instanceof Byte) {
      return new ByteMIValue((Byte)object);
    } else if (object instanceof Short) {
      return new ShortMIValue((Short)object);
    } else if (object instanceof Integer) {
      return new IntMIValue((Integer)object);
    } else if (object instanceof Long) {
      return new LongMIValue((Long)object);
    } else if (object instanceof Float) {
      return new FloatMIValue((Float)object);
    } else if (object instanceof Double) {
      return new DoubleMIValue((Double)object);
    }
    
    return new ObjectMIValue(object);
  }
  
}
