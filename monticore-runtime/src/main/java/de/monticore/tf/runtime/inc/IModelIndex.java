/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

/**
 * Represents a model index that listens to incremental model changes.
 *
 * <p>Implementations are typically managed by a {@link IModelAccessor} and may
 * perform additional setup in {@link #finalizeInitialization()} after the
 * initial model traversal has emitted its events.</p>
 */
public interface IModelIndex extends IIncrementalListener {
  
  /**
   * Finalizes index-specific setup after initial events have been processed.
   * Implementations may override this hook if additional initialization is
   * required.
   */
  default void finalizeInitialization() {}
}
