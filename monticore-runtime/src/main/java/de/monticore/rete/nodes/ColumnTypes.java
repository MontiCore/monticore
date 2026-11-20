/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Helper methods for reasoning about column types and the optionality of
 * columns (see {@link TupleSource#getColumnOptionality()}) while constructing
 * a Rete network.
 */
public final class ColumnTypes {

  /**
   * @return the optionality flags of {@code arity} columns, none of them optional
   */
  public static List<Boolean> noOptionalColumns(int arity) {
    return Collections.nCopies(arity, false);
  }

  /**
   * Determines the optionality of the columns of a node whose tuples come
   * from either of two sources, e.g. the columns of a union.
   *
   * @return for each column, whether it is optional in {@code a} or in {@code b}
   * @throws IllegalArgumentException if the lists differ in size
   */
  public static List<Boolean> eitherOptional(List<Boolean> a, List<Boolean> b) {
    if (a.size() != b.size()) {
      throw new IllegalArgumentException("Optionality flags of different sizes: " + a
          + " and " + b);
    }
    List<Boolean> result = new ArrayList<>(a.size());
    for (int i = 0; i < a.size(); i++) {
      result.add(a.get(i) || b.get(i));
    }
    return result;
  }

  /**
   * @throws IllegalArgumentException if the given column of {@code source}
   *                                  is optional
   */
  public static void checkNotOptional(TupleSource source, int column, String usage) {
    if (source.isOptional(column)) {
      throw new IllegalArgumentException("Column " + column + " of " + source
          + " is optional, but " + usage + " must not be optional");
    }
  }

  /**
   * @return the type names, e.g. {@code [ASTState, ASTTransition?]}, where
   * {@code ?} marks optional columns
   */
  public static String toString(List<Class<?>> types, List<Boolean> optionality) {
    return IntStream.range(0, types.size())
        .mapToObj(i -> types.get(i).getSimpleName() + (optionality.get(i) ? "?" : ""))
        .collect(Collectors.joining(", ", "[", "]"));
  }

  private ColumnTypes() {
  }

  /**
   * Checks whether values of two columns can ever be equal, e.g. to reject
   * joins that can never produce a result. Two unrelated classes never
   * overlap, while an interface overlaps with every non-final class (a
   * subclass may implement it).
   *
   * @return whether some object may be an instance of both types
   */
  public static boolean mayOverlap(Class<?> a, Class<?> b) {
    if (a.isAssignableFrom(b) || b.isAssignableFrom(a)) {
      return true;
    }
    if (a.isInterface() && b.isInterface()) {
      return true;
    }
    if (a.isInterface()) {
      return !Modifier.isFinal(b.getModifiers());
    }
    if (b.isInterface()) {
      return !Modifier.isFinal(a.getModifiers());
    }
    // two unrelated classes
    return false;
  }

  /**
   * Determines the type of a column whose values are known to be instances
   * of both types, e.g. the key columns of a join.
   *
   * @return the more specific of both types if they are related, {@code a} otherwise
   */
  public static Class<?> narrower(Class<?> a, Class<?> b) {
    return a.isAssignableFrom(b) ? b : a;
  }

  /**
   * Determines the type of a column whose values are instances of either
   * type, e.g. a column of a union. The supertypes of {@code a} (classes and
   * interfaces) are searched breadth-first for the first one {@code b} is
   * assignable to.
   *
   * @return a common supertype of both types, preferring types closer to
   * {@code a} and avoiding {@link Object} where possible
   */
  public static Class<?> commonSupertype(Class<?> a, Class<?> b) {
    if (a.isAssignableFrom(b)) {
      return a;
    }
    if (b.isAssignableFrom(a)) {
      return b;
    }
    Deque<Class<?>> queue = new ArrayDeque<>();
    Set<Class<?>> visited = new HashSet<>();
    queue.add(a);
    while (!queue.isEmpty()) {
      Class<?> current = queue.poll();
      if (current != Object.class && current.isAssignableFrom(b)) {
        return current;
      }
      if (current.getSuperclass() != null && visited.add(current.getSuperclass())) {
        queue.add(current.getSuperclass());
      }
      for (Class<?> itf : current.getInterfaces()) {
        if (visited.add(itf)) {
          queue.add(itf);
        }
      }
    }
    return Object.class;
  }
}
