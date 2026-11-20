/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Selects (and possibly reorders or duplicates) columns of tuples of a fixed
 * arity. Masks are used for projections, join keys and index keys.
 * <p>
 * Example: {@code TupleMask.of(3, 2, 0)} transforms {@code [a, b, c]} into
 * {@code [c, a]}.
 */
public final class TupleMask {

  final int[] indices;

  private final int sourceArity;

  private TupleMask(int sourceArity, int[] indices) {
    this.sourceArity = sourceArity;
    this.indices = indices;
  }

  /**
   * @param sourceArity arity of the tuples this mask is applied to
   * @param indices     the selected columns, in output order
   */
  public static TupleMask of(int sourceArity, int... indices) {
    if (sourceArity < 0) {
      throw new IllegalArgumentException("Negative source arity " + sourceArity);
    }
    for (int index : indices) {
      if (index < 0 || index >= sourceArity) {
        throw new IllegalArgumentException(
            "Index " + index + " out of range for source arity " + sourceArity);
      }
    }
    return new TupleMask(sourceArity, indices.clone());
  }

  /**
   * @return a mask selecting all columns in their original order
   */
  public static TupleMask identity(int arity) {
    int[] indices = new int[arity];
    for (int i = 0; i < arity; i++) {
      indices[i] = i;
    }
    return new TupleMask(arity, indices);
  }

  /**
   * @return a mask selecting no column
   */
  public static TupleMask empty(int sourceArity) {
    return of(sourceArity);
  }

  /**
   * @return a mask selecting exactly the columns not selected by this mask,
   * in ascending order
   */
  public TupleMask complement() {
    boolean[] selected = new boolean[sourceArity];
    for (int index : indices) {
      selected[index] = true;
    }
    int[] result = new int[sourceArity];
    int size = 0;
    for (int i = 0; i < sourceArity; i++) {
      if (!selected[i]) {
        result[size++] = i;
      }
    }
    return new TupleMask(sourceArity, Arrays.copyOf(result, size));
  }

  public int getSize() {
    return indices.length;
  }

  public int getSourceArity() {
    return sourceArity;
  }

  public int getIndex(int position) {
    return indices[position];
  }

  public int[] getIndices() {
    return indices.clone();
  }

  /**
   * @return whether this mask selects all columns in their original order,
   * i.e. {@link #transform(Tuple)} returns tuples equal to its arguments
   */
  public boolean isIdentity() {
    if (indices.length != sourceArity) {
      return false;
    }
    for (int i = 0; i < indices.length; i++) {
      if (indices[i] != i) {
        return false;
      }
    }
    return true;
  }

  /**
   * Applies this mask to a tuple.
   *
   * @param tuple a tuple of arity {@link #getSourceArity()}
   * @return a new tuple containing the selected values of {@code tuple}, in
   * the order of this mask
   * @throws IllegalArgumentException if the arity of {@code tuple} does not
   *                                  match the source arity of this mask
   */
  public Tuple transform(Tuple tuple) {
    if (tuple.size() != sourceArity) {
      throw new IllegalArgumentException(
          "Mask for arity " + sourceArity + " applied to tuple " + tuple);
    }
    Object[] result = new Object[indices.length];
    for (int i = 0; i < indices.length; i++) {
      result[i] = tuple.getStored(indices[i]);
    }
    return Tuple.wrap(result);
  }

  /**
   * Applies this mask to a list, e.g. to derive the column types of a
   * projected node from the column types of its parent.
   *
   * @param list a list of size {@link #getSourceArity()}
   * @return a new list containing the selected elements of {@code list}, in
   * the order of this mask
   * @throws IllegalArgumentException if the size of {@code list} does not
   *                                  match the source arity of this mask
   */
  public <T> List<T> transform(List<T> list) {
    if (list.size() != sourceArity) {
      throw new IllegalArgumentException(
          "Mask for arity " + sourceArity + " applied to list of size " + list.size());
    }
    List<T> result = new ArrayList<>(indices.length);
    for (int index : indices) {
      result.add(list.get(index));
    }
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TupleMask other)) {
      return false;
    }
    return sourceArity == other.sourceArity && Arrays.equals(indices, other.indices);
  }

  @Override
  public int hashCode() {
    return 31 * sourceArity + Arrays.hashCode(indices);
  }

  @Override
  public String toString() {
    return "TupleMask" + Arrays.toString(indices) + "/" + sourceArity;
  }
}
