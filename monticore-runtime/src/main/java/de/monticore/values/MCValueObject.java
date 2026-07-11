/* (c) https://github.com/MontiCore/monticore */
package de.monticore.values;

import com.google.common.base.Preconditions;

import java.util.Objects;

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
    assert !(value instanceof Number) :
        "MCValueInt/MCValueDouble should be used";
    assert !(value instanceof Boolean) :
        "MCValueBoolean should be used";
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
  public boolean isString() {
    return value instanceof String;
  }

  @Override
  public boolean checkEqualityOperator(MCValue other) {
    return other.isObjectOfJava() && value == other.asObject().value;
  }

  @Override
  public boolean equals(Object otherObj) {
    if (this == otherObj) {
      return true;
    }
    if (!(otherObj instanceof MCValueObject other)) {
      return false;
    }
    return Objects.equals(value, other.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
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

}
