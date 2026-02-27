package de.monticore.interpreter;

import de.monticore.interpreter.values.BooleanMIValue;
import de.monticore.interpreter.values.ByteMIValue;
import de.monticore.interpreter.values.CharMIValue;
import de.monticore.interpreter.values.DoubleMIValue;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.interpreter.values.FloatMIValue;
import de.monticore.interpreter.values.IntMIValue;
import de.monticore.interpreter.values.JavaAttributeMIValue;
import de.monticore.interpreter.values.LongMIValue;
import de.monticore.interpreter.values.ObjectMIValue;
import de.monticore.interpreter.values.ShortMIValue;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

import static de.monticore.interpreter.MIValueFactory.createValue;

/**
 * Utility class for the interpreter.
 * Contains methods for implict/explicit casts, conversion from Object to
 * MIValue or MIValue to object, and the calculation of binary operations.
 */
public class InterpreterUtils {

  // todo use SymTypeExpression, not string for resultType
  // todo opName is not the name, but errorcode+name -> split!
  public static MIValue calcOpPrimitive(MIValue v1, MIValue v2,
      String resultType, BinaryOperator<Integer> opInt,
      BinaryOperator<Long> opLong, BinaryOperator<Float> opFloat,
      BinaryOperator<Double> opDouble, String opName) {

    try {
      switch (resultType) {
        case BasicSymbolsMill.INT:
          return createValue((int) opInt.apply(v1.asInt(), v2.asInt()));
        case BasicSymbolsMill.LONG:
          return createValue((long) opLong.apply(v1.asLong(), v2.asLong()));
        case BasicSymbolsMill.FLOAT:
          return createValue((float) opFloat.apply(v1.asFloat(), v2.asFloat()));
        case BasicSymbolsMill.DOUBLE:
          return createValue((double) opDouble.apply(v1.asDouble(), v2.asDouble()));
      }
    }
    catch (Exception e) {
      // e.g., ArithmeticException 1/0
      Log.error("0x58110 exception occured during interpretation of the "
          + opName + " operator", e);
      return new ErrorMIValue(e);
    }

    String errorMsg = opName + " operation with result of type " + resultType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  public static MIValue calcBitwiseOpPrimitive(MIValue v1, MIValue v2,
      String resultType, BinaryOperator<Integer> opInt,
      BinaryOperator<Long> opLong, String opName) {
    switch (resultType) {
      case BasicSymbolsMill.INT:
        return createValue((int) opInt.apply(v1.asInt(), v2.asInt()));
      case BasicSymbolsMill.LONG:
        return createValue((long) opLong.apply(v1.asLong(), v2.asLong()));
    }
    String errorMsg = opName + " operation with result of type " + resultType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  public static MIValue calcBitwiseLogicalOpPrimitive(MIValue v1, MIValue v2,
      String resultType, BinaryOperator<Boolean> opBool,
      BinaryOperator<Integer> opInt, BinaryOperator<Long> opLong,
      String opName) {
    switch (resultType) {
      case BasicSymbolsMill.BOOLEAN:
        return createValue((boolean) opBool.apply(v1.asBoolean(), v2.asBoolean()));
      case BasicSymbolsMill.INT:
        return createValue((int) opInt.apply(v1.asInt(), v2.asInt()));
      case BasicSymbolsMill.LONG:
        return createValue((long) opLong.apply(v1.asLong(), v2.asLong()));
    }
    String errorMsg = opName + " operation with result of type " + resultType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  public static MIValue calcShiftPrimitive(MIValue v1, MIValue v2,
      String resultType, BiFunction<Integer, Long, Integer> opInt,
      BinaryOperator<Long> opLong, String opName) {
    switch (resultType) {
      case BasicSymbolsMill.INT:
        return createValue((int) opInt.apply(v1.asInt(), v2.asLong()));
      case BasicSymbolsMill.LONG:
        return createValue((long) opLong.apply(v1.asLong(), v2.asLong()));
    }
    String errorMsg = opName + " operation with result of type " + resultType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  /**
   * Calculates the result of a binary operation with the given result type by
   * using the given lambdas for the calculation. Operation should support
   * int, long, float {@literal &} double.
   *
   * @param v1         left operand
   * @param v2         right operand
   * @param resultType result type of the operation
   * @param opInt      lambda for int operation
   * @param opLong     lambda for long operation
   * @param opFloat    lambda for float operation
   * @param opDouble   lambda for double operation
   * @param opName     Name of the operation (for error messages)
   * @return Result of the operation or an error value if the type is not supported.
   */
  public static MIValue calcOp(MIValue v1, MIValue v2,
      SymTypeExpression resultType, BinaryOperator<Integer> opInt,
      BinaryOperator<Long> opLong, BinaryOperator<Float> opFloat,
      BinaryOperator<Double> opDouble, String opName) {
    if (resultType.isPrimitive()) {
      return calcOpPrimitive(v1, v2, resultType.asPrimitive().getPrimitiveName(), opInt, opLong, opFloat, opDouble, opName);
    }

    String errorMsg = opName + " operation with result of type "
        + resultType.printFullName()
        + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  /**
   * Calculates the result of a bitwise binary operation with the given result type by using
   * the given lambdas for the calculation. Operation should support int {@literal &} long.
   *
   * @param v1         left operand
   * @param v2         right operand
   * @param resultType result type of the operation
   * @param opInt      lambda for int operation
   * @param opLong     lambda for long operation
   * @param opName     Name of the operation (for error messages)
   * @return Result of the operation or an error value if the type is not supported.
   */
  public static MIValue calcBitwiseOp(MIValue v1, MIValue v2,
      SymTypeExpression resultType, BinaryOperator<Integer> opInt,
      BinaryOperator<Long> opLong, String opName) {
    if (resultType.isPrimitive()) {
      return calcBitwiseOpPrimitive(v1, v2, resultType.asPrimitive().getPrimitiveName(), opInt, opLong, opName);
    }

    String errorMsg = opName + " operation with result of type "
        + resultType.printFullName()
        + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  /**
   * Calculates the result of a bitwise or logical binary operation with the
   * given result type by using the given lambdas for the calculation.
   * Operation should support boolean, int {@literal &} long.
   *
   * @param v1         left operand
   * @param v2         right operand
   * @param resultType result type of the operation
   * @param opInt      lambda for int operation
   * @param opLong     lambda for long operation
   * @param opName     Name of the operation (for error messages)
   * @return Result of the operation or an error value if the type is not supported.
   */
  public static MIValue calcBitwiseLogicalOp(MIValue v1, MIValue v2,
      SymTypeExpression resultType,
      BinaryOperator<Boolean> opBool, BinaryOperator<Integer> opInt,
      BinaryOperator<Long> opLong, String opName) {
    if (resultType.isPrimitive()) {
      return calcBitwiseLogicalOpPrimitive(v1, v2,
          resultType.asPrimitive().getPrimitiveName(), opBool, opInt,
          opLong, opName);
    }

    String errorMsg = opName + " operation with result of type " + resultType
        + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  /**
   * Calculates the result of a shift-like operation with the
   * given result type by using the given lambdas for the calculation.
   * Shift operations always support up to long for the right side.
   *
   * @param v1         left operand
   * @param v2         right operand
   * @param resultType result type of the operation
   * @param opInt      lambda for int operation
   * @param opLong     lambda for long operation
   * @param opName     Name of the operation (for error messages)
   * @return Result of the operation or an error value if the type is not supported.
   */
  public static MIValue calcShift(MIValue v1, MIValue v2,
      SymTypeExpression resultType,
      BiFunction<Integer, Long, Integer> opInt,
      BinaryOperator<Long> opLong, String opName) {
    if (resultType.isPrimitive()) {
      return calcShiftPrimitive(v1, v2,
          resultType.asPrimitive().getPrimitiveName(), opInt, opLong,
          opName);
    }

    String errorMsg = opName + " operation with result of type " + resultType
        + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  /**
   * Applies an explicit cast on primitive to target primitive type.
   *
   * @return converted MIValue or ErrorMIValue if the cast is not supported or
   *     not possible.
   */
  public static MIValue convertToPrimitiveExplicit(String from, String to,
      MIValue value) {
    if (to.equals(BasicSymbolsMill.BOOLEAN) || from.equals(BasicSymbolsMill.BOOLEAN)) {
      String errorMsg = "0x57060 Cast to or from boolean is not supported.";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    if (to.equals(BasicSymbolsMill.BYTE)) {
      switch (from) {
        case BasicSymbolsMill.DOUBLE:
          return createValue((byte) value.asDouble());
        case BasicSymbolsMill.FLOAT:
          return createValue((byte) value.asFloat());
        case BasicSymbolsMill.LONG:
          return createValue((byte) value.asLong());
        case BasicSymbolsMill.INT:
          return createValue((byte) value.asInt());
        case BasicSymbolsMill.SHORT:
          return createValue((byte) value.asShort());
        case BasicSymbolsMill.CHAR:
          return createValue((byte) value.asChar());
        default:
          return createValue(value.asByte());
      }

    }
    else if (to.equals(BasicSymbolsMill.SHORT)) {
      switch (from) {
        case BasicSymbolsMill.DOUBLE:
          return createValue((short) value.asDouble());
        case BasicSymbolsMill.FLOAT:
          return createValue((short) value.asFloat());
        case BasicSymbolsMill.LONG:
          return createValue((short) value.asLong());
        case BasicSymbolsMill.INT:
          return createValue((short) value.asInt());
        case BasicSymbolsMill.CHAR:
          return createValue((short) value.asChar());
        default:
          return createValue(value.asShort());
      }

    }
    else if (to.equals(BasicSymbolsMill.CHAR)) {
      switch (from) {
        case BasicSymbolsMill.DOUBLE:
          return createValue((char) value.asDouble());
        case BasicSymbolsMill.FLOAT:
          return createValue((char) value.asFloat());
        case BasicSymbolsMill.LONG:
          return createValue((char) value.asLong());
        case BasicSymbolsMill.INT:
          return createValue((char) value.asInt());
        case BasicSymbolsMill.SHORT:
          return createValue((char) value.asShort());
        case BasicSymbolsMill.BYTE:
          return createValue((char) value.asByte());
        default:
          return createValue(value.asChar());
      }

    }
    else if (to.equals(BasicSymbolsMill.INT)) {
      switch (from) {
        case BasicSymbolsMill.DOUBLE:
          return createValue((int) value.asDouble());
        case BasicSymbolsMill.FLOAT:
          return createValue((int) value.asFloat());
        case BasicSymbolsMill.LONG:
          return createValue((int) value.asLong());
        default:
          return createValue(value.asInt());
      }

    }
    else if (to.equals(BasicSymbolsMill.LONG)) {
      if (from.equals(BasicSymbolsMill.DOUBLE)) {
        return createValue((long) value.asDouble());
      }
      else if (from.equals(BasicSymbolsMill.FLOAT)) {
        return createValue((long) value.asFloat());
      }
      return createValue(value.asLong());

    }
    else if (to.equals(BasicSymbolsMill.FLOAT)) {
      if (from.equals(BasicSymbolsMill.DOUBLE)) {
        return createValue((float) value.asDouble());
      }
      return createValue(value.asFloat());

    }
    else if (to.equals(BasicSymbolsMill.DOUBLE)) {
      return createValue(value.asDouble());
    }

    String errorMsg = "0x57061 Cast from " + from + " to " + to
        + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  /**
   * Applies an implicit cast on primitive to target primitive type
   *
   * @return converted MIValue or ErrorMIValue if the cast is not supported or
   *     not possible.
   *
   *     todo replace String with SymTypeExpression for targetType
   */
  public static MIValue convertToPrimitiveImplicit(String targetType,
      MIValue value) {
    if (value.isError()) {
      return value;
    }
    else if (targetType.equals(BasicSymbolsMill.BYTE)) {
      return createValue(value.asByte());
    }
    else if (targetType.equals(BasicSymbolsMill.SHORT)) {
      return createValue(value.asShort());
    }
    else if (targetType.equals(BasicSymbolsMill.CHAR)) {
      return createValue(value.asChar());
    }
    else if (targetType.equals(BasicSymbolsMill.INT)) {
      return createValue(value.asInt());
    }
    else if (targetType.equals(BasicSymbolsMill.LONG)) {
      return createValue(value.asLong());
    }
    else if (targetType.equals(BasicSymbolsMill.FLOAT)) {
      return createValue(value.asFloat());
    }
    else if (targetType.equals(BasicSymbolsMill.DOUBLE)) {
      return createValue(value.asDouble());
    }

    String errorMsg = "0x57062 Implicit cast to " + targetType
        + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  /**
   * Applies an implicit cast on value to targetType.
   * Allows casts of boxtypes and primitives.
   *
   * @return converted MIValue or ErrorMIValue if the cast is not supported or
   *     not possible.
   */
  public static MIValue convertImplicit(SymTypeExpression targetType,
      MIValue value) {
    if (value.isError()) {
      return value;
    }
    else if (targetType.isPrimitive()) {
      value = unboxType(value);
      return convertToPrimitiveImplicit(
          targetType.asPrimitive().getPrimitiveName(), value
      );
    }
    else if (isBoxType(targetType)) {
      SymTypeExpression unboxedType = SymTypeRelations.unbox(targetType);
      value = convertToPrimitiveImplicit(
          unboxedType.asPrimitive().getPrimitiveName(), value
      );
      value = boxValue(value, targetType);
      return value;
    }
    else {
      // value may be primitive with targetType Object; int -> Integer -> Object
      if (value.isPrimitive()) {
        value = boxValue(value);
      }
      return value;
    }
  }

  /**
   * Creates an AttributeMIValue for a non-static attribute of a java-object.
   *
   * @param object Java-Object as MIValue
   * @return Value of the attribute as MIValue converted to the given type.
   *     ErrorMIValue if the attribute does not exist or is not accessible.
   */
  public static MIValue getNonStaticObjectAttribute(ObjectMIValue object,
      String attributeName) {
    Field attribute;
    try {
      attribute = object.asObject().getClass().getField(attributeName);
    }
    catch (NoSuchFieldException e) {
      String errorMsg = "0x57063 Tried to access attribute '" + attributeName
          + "' of class '" + object.getClass().getName()
          + "'. No such attribute exists.";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }

    return new JavaAttributeMIValue(Optional.of(object.asObject()), attribute);
  }

  /**
   * Creates an AttributeMIValue for a non-static attribute of a java-object.
   *
   * @param classObject Java-Class
   * @return Value of the attribute as MIValue converted to the given type.
   *     ErrorMIValue if the attribute does not exist or is not accessible.
   */
  public static MIValue getStaticObjectAttribute(Class<?> classObject,
      String attributeName) {
    Field attribute;
    try {
      attribute = classObject.getField(attributeName);
    }
    catch (NoSuchFieldException e) {
      String errorMsg = "0x57063 Tried to access attribute '" + attributeName
          + "' of class '" + classObject.getName()
          + "'. No such attribute exists.";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }

    return new JavaAttributeMIValue(Optional.empty(), attribute);
  }

  /**
   * Gets the corresponding java type for a given MIValue.
   */
  public static Class<?> typeOfValue(MIValue value) {
    if (value.isBoolean()) {
      return boolean.class;
    }
    else if (value.isChar()) {
      return char.class;
    }
    else if (value.isByte()) {
      return byte.class;
    }
    else if (value.isShort()) {
      return short.class;
    }
    else if (value.isInt()) {
      return int.class;
    }
    else if (value.isLong()) {
      return long.class;
    }
    else if (value.isFloat()) {
      return float.class;
    }
    else if (value.isDouble()) {
      return double.class;
    }
    else if (value.isObject()) {
      return value.asObject().getClass();
    }
    // Functions are not allowed
    String errorMsg = "0x57066 Failed to get java type of " + value.printType()
        + ".";
    Log.error(errorMsg);
    return null;
  }

  /**
   * Converts a MIValue to a java object.
   */
  public static Object valueToObject(MIValue value) {
    if (value.isBoolean()) {
      return value.asBoolean();
    }
    else if (value.isChar()) {
      return value.asChar();
    }
    else if (value.isByte()) {
      return value.asByte();
    }
    else if (value.isShort()) {
      return value.asShort();
    }
    else if (value.isInt()) {
      return value.asInt();
    }
    else if (value.isLong()) {
      return value.asLong();
    }
    else if (value.isFloat()) {
      return value.asFloat();
    }
    else if (value.isDouble()) {
      return value.asDouble();
    }
    else if (value.isObject()) {
      return value.asObject();
    }
    // Functions are not allowed
    String errorMsg = "0x57067 Failed to convert MIValue of type "
        + value.printType() + " to a java object.";
    Log.error(errorMsg);
    return null;
  }

  /**
   * Converts a java object to a MIValue.
   * Boxtypes are converted to their corresponding primitive type.
   */
  public static MIValue objectToValue(Object object) {
    if (object instanceof Boolean) {
      return new BooleanMIValue((Boolean) object);
    }
    else if (object instanceof Character) {
      return new CharMIValue((Character) object);
    }
    else if (object instanceof Byte) {
      return new ByteMIValue((Byte) object);
    }
    else if (object instanceof Short) {
      return new ShortMIValue((Short) object);
    }
    else if (object instanceof Integer) {
      return new IntMIValue((Integer) object);
    }
    else if (object instanceof Long) {
      return new LongMIValue((Long) object);
    }
    else if (object instanceof Float) {
      return new FloatMIValue((Float) object);
    }
    else if (object instanceof Double) {
      return new DoubleMIValue((Double) object);
    }

    return new ObjectMIValue(object);
  }

  /**
   * Checks if SymTypeExpression is a boxtype
   */
  public static boolean isBoxType(SymTypeExpression type) {
    return !type.isPrimitive() && (
        SymTypeRelations.isNumericType(type)
            || SymTypeRelations.isBoolean(type)
    );
  }

  /**
   * Unboxes a value if it is a boxtype.
   */
  public static MIValue unboxType(MIValue value) {
    if (!value.isObject())
      return value;

    Object obj = value.asObject();
    if (obj instanceof Integer) {
      return new IntMIValue((Integer) obj);
    }
    else if (obj instanceof Long) {
      return new LongMIValue((Long) obj);
    }
    else if (obj instanceof Float) {
      return new FloatMIValue((Float) obj);
    }
    else if (obj instanceof Double) {
      return new DoubleMIValue((Double) obj);
    }
    else if (obj instanceof Character) {
      return new CharMIValue((Character) obj);
    }
    else if (obj instanceof Byte) {
      return new ByteMIValue((Byte) obj);
    }
    else if (obj instanceof Short) {
      return new ShortMIValue((Short) obj);
    }

    return value;
  }

  /**
   * Converts a MIValue into its equivalent Boxtype
   */
  public static MIValue boxValue(MIValue value) {
    if (!value.isPrimitive()) {
      return value;
    }

    if (value.isBoolean()) {
      return MIValueFactory.createValue((Boolean) value.asBoolean());
    }
    else if (value.isChar()) {
      return MIValueFactory.createValue((Character) value.asChar());
    }
    else if (value.isByte()) {
      return MIValueFactory.createValue((Byte) value.asByte());
    }
    else if (value.isShort()) {
      return MIValueFactory.createValue((Short) value.asShort());
    }
    else if (value.isInt()) {
      return MIValueFactory.createValue((Integer) value.asInt());
    }
    else if (value.isLong()) {
      return MIValueFactory.createValue((Long) value.asLong());
    }
    else if (value.isFloat()) {
      return MIValueFactory.createValue((Float) value.asFloat());
    }
    else if (value.isDouble()) {
      return MIValueFactory.createValue((Double) value.asDouble());
    }

    return value;
  }

  /**
   * Converts a value to the given box type.
   *
   * @return Boxed value wrapped in an ObjectMIValue
   */
  public static MIValue boxValue(MIValue value, SymTypeExpression boxType) {
    if (SymTypeRelations.isInt(boxType)) {
      return MIValueFactory.createValue(Integer.valueOf(value.asInt()));
    }
    else if (SymTypeRelations.isLong(boxType)) {
      return MIValueFactory.createValue(Long.valueOf(value.asLong()));
    }
    else if (SymTypeRelations.isFloat(boxType)) {
      return MIValueFactory.createValue(Float.valueOf(value.asFloat()));
    }
    else if (SymTypeRelations.isDouble(boxType)) {
      return MIValueFactory.createValue(Double.valueOf(value.asDouble()));
    }
    else if (SymTypeRelations.isChar(boxType)) {
      return MIValueFactory.createValue(Character.valueOf(value.asChar()));
    }
    else if (SymTypeRelations.isByte(boxType)) {
      return MIValueFactory.createValue(Byte.valueOf(value.asByte()));
    }
    else if (SymTypeRelations.isShort(boxType)) {
      return MIValueFactory.createValue(Short.valueOf(value.asShort()));
    }
    else if (SymTypeRelations.isBoolean(boxType)) {
      return MIValueFactory.createValue(Boolean.valueOf(value.asBoolean()));
    }

    String errorMsg = "0x57084 Tried to convert to unknown boxed type.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

}
