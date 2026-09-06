/* (c) https://github.com/MontiCore/monticore */
package de.monticore.values;

import com.google.common.base.Preconditions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A value that is a Java Object.
 * <p>
 * Note: we deliberately do not interpret Objects based on Symbols alone;
 * We assume that all (Object-)Types are already available as Java classes,
 * as the use-cases for runtime-definition of types are rather rare.
 */
public class MCValueObject implements MCValue {

  protected Object value;

  public MCValueObject(Object value) {
    this.value = value;
  }

  @Override
  public boolean isObject() {
    return true;
  }

  @Override
  public MCValueObject asObject() {
    return this;
  }

  public <T> T asNativeObject(Class<T> clazz) {
    if (value == null) {
      return null;
    }
    Preconditions.checkNotNull(clazz);
    Preconditions.checkArgument(clazz.isInstance(value),
        "0xF1012 internal error : "
            + "Tried to cast a " + printType()
            + " to the non-compatible type " + clazz.getTypeName()
    );
    return clazz.cast(value);
  }

  @SuppressWarnings("unchecked")
  public <T> T unsafeCast() {
    return (T) value;
  }

  @Override
  public Object asNativeObject() {
    return value;
  }

  @Override
  public String asString() {
    return String.valueOf(value);
  }

  // further type checks and conversions

  @Override
  public boolean isBoolean() {
    return value instanceof Boolean;
  }

  @Override
  public boolean asBoolean() {
    if (isBoolean()) {
      return this.<Boolean> unsafeCast();
    }
    throw createIllegalConversionException("boolean");
  }

  @Override
  public boolean isByte() {
    return value instanceof Byte;
  }

  @Override
  public byte asByte() {
    if (isByte()) {
      return this.<Byte> unsafeCast();
    }
    throw createIllegalConversionException("byte");
  }

  @Override
  public boolean isChar() {
    return value instanceof Character;
  }

  @Override
  public char asChar() {
    if (isChar()) {
      return this.<Character> unsafeCast();
    }
    throw createIllegalConversionException("char");
  }

  @Override
  public boolean isShort() {
    return value instanceof Short;
  }

  @Override
  public short asShort() {
    if (isShort()) {
      return this.<Short> unsafeCast();
    }
    throw createIllegalConversionException("short");
  }

  @Override
  public boolean isInt() {
    return value instanceof Integer;
  }

  @Override
  public int asInt() {
    if (isInt()) {
      return this.<Integer> unsafeCast();
    }
    throw createIllegalConversionException("int");
  }

  @Override
  public boolean isLong() {
    return value instanceof Long;
  }

  @Override
  public long asLong() {
    if (isLong()) {
      return this.<Long> unsafeCast();
    }
    throw createIllegalConversionException("long");
  }

  @Override
  public boolean isFloat() {
    return value instanceof Float;
  }

  @Override
  public float asFloat() {
    if (isFloat()) {
      return this.<Float> unsafeCast();
    }
    throw createIllegalConversionException("float");
  }

  @Override
  public boolean isDouble() {
    return value instanceof Double;
  }

  @Override
  public double asDouble() {
    if (isDouble()) {
      return this.<Double> unsafeCast();
    }
    throw createIllegalConversionException("double");
  }

  @Override
  public boolean isString() {
    return value instanceof String;
  }

  public boolean isArray() {
    return value.getClass().isArray();
  }

  /**
   * Converts the array value to a list of {@link Object}.
   * S.a. {@link #isArray()}
   *
   * @return the list of objects.
   */
  public List<Object> arrayToObjectList() {
    final int length = Array.getLength(value);
    List<Object> list = new ArrayList<>(length);
    for (int i = 0; i < length; i++) {
      list.add(Array.get(value, i));
    }
    return list;
  }

  @Override
  public boolean checkEqualityOperator(MCValue other) {
    return other.isObject() && value == other.asObject().value;
  }

  @Override
  public String printType() {
    String typeStr = value != null
        ? value.getClass().getTypeName()
        : "null";
    return "Object(" + typeStr + ")";
  }

  @Override
  public String printValue() {
    return String.valueOf(value);
  }

  // helper

  protected IllegalStateException createIllegalConversionException(String type) {
    return new IllegalStateException("0xF1381 internal error: "
        + "Tried to convert MIValue to " + type + ", even though it is not;"
        + " Type: " + printType()
        + " Value: " + printValue()
    );
  }

}
