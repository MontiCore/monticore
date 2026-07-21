/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

import de.monticore.visitor.ITraverser;

/**
 * Represents a model index that listens to incremental model changes and can
 * be registered into a traverser.
 *
 * @param <E> the traverser type used by this index
 */
public interface IModelIndex<E extends ITraverser> extends IIncrementalListener {
  
  /**
   * Registers this index into the given traverser.
   *
   * @param traverser the traverser to register this index into
   */
  void registerIntoTraverser(E traverser);
  
  /**
   * Finalizes the index initialization after registration and setup.
   * Implementations may override this hook if additional initialization is
   * required.
   */
  default void finalizeInitialization() {}
}
