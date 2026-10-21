/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper methods for reasoning about column types while constructing a
 * Rete network.
 */
public final class ColumnTypes {

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
