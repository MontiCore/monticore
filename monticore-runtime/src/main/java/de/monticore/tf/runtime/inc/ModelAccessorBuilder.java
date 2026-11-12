/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

/**
 * Builder for creating configured {@link ModelAccessor} instances.
 *
 * <p>The builder captures initialization roots, custom indices, and listeners,
 * then creates a fully initialized {@link ModelAccessor} through {@link #build()}.
 *
 * <p>To enforce a controlled creation flow, this builder cannot be instantiated
 * directly and is only available via one of the static {@code of(...)} factory methods.
 */
public class ModelAccessorBuilder {

  protected final Supplier<ITraverser> traverser;

  protected final List<ASTNode> roots;

  protected final Map<String, IModelIndex> customIndices = new HashMap<>();

  protected final Set<IIncrementalListener> listeners = new HashSet<>();

  /**
   * Creates a new builder with required traverser and root nodes.
   *
   * <p>This constructor is intentionally private so that callers must use
   * {@link #of(Supplier, ASTNode...)} or {@link #of(Supplier, List)}.
   *
   * @param traverser supplier that provides the traverser used during model initialization
   * @param roots root nodes that are traversed during model initialization
   */
  protected ModelAccessorBuilder(@Nonnull Supplier<ITraverser> traverser, @Nonnull List<ASTNode> roots) {
    Preconditions.checkNotNull(traverser, "traverser must not be null");
    Preconditions.checkNotNull(roots, "roots must not be null");
    this.traverser = traverser;
    this.roots = List.copyOf(roots);
  }

  /**
   * Creates a builder from a traverser supplier and a vararg list of root nodes.
   *
   * <p>Use this factory when roots are naturally available as a parameter list.
   *
   * @param traverser supplier that provides the traverser used during model initialization
   * @param roots root nodes that are traversed during model initialization
   * @return a new builder instance
   */
  public static ModelAccessorBuilder of(@Nonnull Supplier<ITraverser> traverser, @Nonnull ASTNode... roots) {
    Preconditions.checkNotNull(roots, "roots must not be null");
    return new ModelAccessorBuilder(traverser, Arrays.stream(roots).toList());
  }

  /**
   * Creates a builder from a traverser supplier and a list of root nodes.
   *
   * <p>Use this factory when roots are already available as a list.
   *
   * @param traverser supplier that provides the traverser used during model initialization
   * @param roots root nodes that are traversed during model initialization
   * @return a new builder instance
   */
  public static ModelAccessorBuilder of(@Nonnull Supplier<ITraverser> traverser, @Nonnull List<ASTNode> roots) {
    return new ModelAccessorBuilder(traverser, roots);
  }

  /**
   * Registers or replaces a custom model index under the given name.
   *
   * <p>If an index with the same name already exists, it is overwritten.
   *
   * @param name unique name under which the custom index is registered
   * @param customIndex custom index implementation to register
   * @return this builder for fluent chaining
   */
  public ModelAccessorBuilder withCustomIndex(@Nonnull String name, @Nonnull IModelIndex customIndex) {
    Preconditions.checkNotNull(name, "name must not be null");
    Preconditions.checkNotNull(customIndex, "customIndex must not be null");
    this.customIndices.put(name, customIndex);
    return this;
  }

  /**
   * Registers all custom indices from the provided map.
   *
   * <p>Existing entries with matching keys are overwritten by incoming values.
   *
   * @param customIndices map of custom indices keyed by their registration names
   * @return this builder for fluent chaining
   */
  public ModelAccessorBuilder withCustomIndices(@Nonnull Map<String, IModelIndex> customIndices) {
    Preconditions.checkNotNull(customIndices, "customIndices must not be null");
    validateCustomIndices(customIndices);
    this.customIndices.putAll(customIndices);
    return this;
  }

  /**
   * Registers a listener that receives incremental model change events.
   *
   * <p>Duplicate listeners are ignored because listeners are stored in a set.
   *
   * @param listener listener to register
   * @return this builder for fluent chaining
   */
  public ModelAccessorBuilder withListener(@Nonnull IIncrementalListener listener) {
    Preconditions.checkNotNull(listener, "listener must not be null");
    this.listeners.add(listener);
    return this;
  }

  /**
   * Registers all listeners from the provided set.
   *
   * <p>Duplicate listeners are ignored because listeners are stored in a set.
   *
   * @param listeners listeners to register
   * @return this builder for fluent chaining
   */
  public ModelAccessorBuilder withListeners(@Nonnull Set<IIncrementalListener> listeners) {
    Preconditions.checkNotNull(listeners, "listeners must not be null");
    validateListeners(listeners);
    this.listeners.addAll(listeners);
    return this;
  }

  /**
   * Creates a fully initialized {@link ModelAccessor} from the collected configuration.
   *
   * <p>The resulting instance receives defensive copies of custom indices and listeners
   * to prevent accidental external mutation after construction.
   *
   * @return a new configured and initialized model accessor
   */
  public ModelAccessor build() {
    return new ModelAccessor(this.traverser, this.roots, new HashMap<>(this.customIndices), new HashSet<>(this.listeners));
  }

  protected static void validateCustomIndices(Map<String, IModelIndex> customIndices) {
    customIndices.forEach((name, index) -> {
      Preconditions.checkNotNull(name, "custom index name must not be null");
      Preconditions.checkNotNull(index, "custom index instance must not be null");
    });
  }

  protected static void validateListeners(Set<IIncrementalListener> listeners) {
    listeners.forEach(
        listener -> Preconditions.checkNotNull(listener, "listener must not be null"));
  }
}



