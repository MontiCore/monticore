/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.tuple;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An immutable, positional tuple of values flowing through a Rete network.
 * <p>
 * Elements are compared with {@link Object#equals(Object)}. As MontiCore AST
 * classes do not override {@code equals}, AST nodes are compared by identity,
 * while attribute values (Strings, numbers, enums, ...) are compared by value.
 * {@code null} elements are not allowed.
 * <p>
 * Optional columns (e.g. the columns of an optional pattern part) may contain
 * {@link #ABSENT} (⊥) instead of a value. Tuples containing {@code ABSENT}
 * are compared like other tuples, i.e. {@code ABSENT} equals itself here, so
 * that memories can find and delete such tuples. Reading {@code ABSENT} via
 * {@link #get(int)}, {@link #get(int, Class)} or {@link #asList()} yields
 * {@code null}.
 */
public final class Tuple {

  public static final Tuple EMPTY = new Tuple(new Object[0]);

  /**
   * The content of an optional column without a value (⊥). Read as
   * {@code null} via the getters of a tuple.
   */
  public static final Object ABSENT = Absent.INSTANCE;

  private enum Absent {
    INSTANCE;

    @Override
    public String toString() {
      return "⊥";
    }
  }

  private final Object[] values;

  private final int hash;

  private Tuple(Object[] values) {
    this.values = values;
    this.hash = Arrays.hashCode(values);
  }

  /**
   * Creates a tuple containing the given values. A value of an optional
   * column may be {@link #ABSENT}.
   *
   * @throws NullPointerException if any value is {@code null}
   */
  public static Tuple of(Object... values) {
    Object[] copy = values.clone();
    for (Object value : copy) {
      Objects.requireNonNull(value, "Tuples must not contain null values");
    }
    return copy.length == 0 ? EMPTY : new Tuple(copy);
  }

  /**
   * Wraps the given array without copying. The array must not be modified
   * afterwards and must not contain {@code null}.
   */
  static Tuple wrap(Object[] values) {
    return values.length == 0 ? EMPTY : new Tuple(values);
  }

  public int size() {
    return values.length;
  }

  /**
   * @return the value at the given position, {@code null} if it is {@link #ABSENT}
   */
  public Object get(int index) {
    return values[index] == ABSENT ? null : values[index];
  }

  /**
   * @return the value at the given position cast to the given type,
   * {@code null} if it is {@link #ABSENT}
   * @throws ClassCastException if the value is not an instance of {@code type}
   */
  public <T> T get(int index, Class<T> type) {
    return type.cast(get(index));
  }

  /**
   * @return whether the value at the given position is {@link #ABSENT}
   */
  public boolean isAbsent(int index) {
    return values[index] == ABSENT;
  }

  /**
   * @return whether the tuple contains {@link #ABSENT}
   */
  public boolean containsAbsent() {
    for (Object value : values) {
      if (value == ABSENT) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return the value at the given position as stored, i.e. {@link #ABSENT}
   * instead of {@code null}; used to copy values between tuples
   */
  Object getStored(int index) {
    return values[index];
  }

  /**
   * @return a new tuple consisting of this tuple's values followed by the given value
   */
  public Tuple append(Object value) {
    Objects.requireNonNull(value, "Tuples must not contain null values");
    Object[] result = Arrays.copyOf(values, values.length + 1);
    result[values.length] = value;
    return new Tuple(result);
  }

  /**
   * @return a new tuple consisting of this tuple's values followed by the
   * values of {@code other} selected by {@code otherMask}
   */
  public Tuple concat(Tuple other, TupleMask otherMask) {
    int[] indices = otherMask.indices;
    Object[] result = Arrays.copyOf(values, values.length + indices.length);
    for (int i = 0; i < indices.length; i++) {
      result[values.length + i] = other.values[indices[i]];
    }
    return wrap(result);
  }

  /**
   * @return the values of the tuple (unmodifiable), {@code null} for {@link #ABSENT}
   */
  public List<Object> asList() {
    Object[] result = values.clone();
    for (int i = 0; i < result.length; i++) {
      if (result[i] == ABSENT) {
        result[i] = null;
      }
    }
    return List.of(result);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Tuple other)) {
      return false;
    }
    return hash == other.hash && Arrays.equals(values, other.values);
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  public String toString() {
    return Arrays.toString(values);
  }
}
