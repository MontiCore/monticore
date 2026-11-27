/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete;

import com.google.common.primitives.Primitives;
import de.monticore.ast.ASTNode;

import java.util.Objects;

/**
 * Typed key of a binary relation between an AST node (the owner) and a value,
 * e.g. a child AST node or an attribute value. Facts of a feature are
 * reported to a {@link ReteNetwork} via
 * {@link ReteNetwork#featureValueAdded(Feature, ASTNode, Object)} and
 * {@link ReteNetwork#featureValueRemoved(Feature, ASTNode, Object)}.
 * <p>
 * Features are compared by identity. Define each feature once, e.g. as a
 * constant:
 * <pre>
 * static final Feature&lt;ASTAutomaton, ASTState&gt; STATES =
 *     Feature.of("states", ASTAutomaton.class, ASTState.class);
 * </pre>
 *
 * @param <O> type of the owning AST node
 * @param <V> type of the feature values
 */
public final class Feature<O extends ASTNode, V> {

  private final String name;

  private final Class<O> ownerType;

  private final Class<V> valueType;

  private Feature(String name, Class<O> ownerType, Class<V> valueType) {
    this.name = name;
    this.ownerType = ownerType;
    this.valueType = valueType;
  }

  /**
   * @param name      a descriptive name (used for debugging only)
   * @param ownerType the AST type owning this feature
   * @param valueType the type of the values; primitive types are replaced by
   *                  their wrapper types
   */
  public static <O extends ASTNode, V> Feature<O, V> of(String name, Class<O> ownerType,
      Class<V> valueType) {
    return new Feature<>(Objects.requireNonNull(name), Objects.requireNonNull(ownerType),
        Primitives.wrap(Objects.requireNonNull(valueType)));
  }

  public String getName() {
    return name;
  }

  public Class<O> getOwnerType() {
    return ownerType;
  }

  public Class<V> getValueType() {
    return valueType;
  }

  @Override
  public String toString() {
    return ownerType.getSimpleName() + "." + name;
  }
}
