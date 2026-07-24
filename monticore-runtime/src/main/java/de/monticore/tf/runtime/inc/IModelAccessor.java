/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

/**
 * Provides notifications about structural and attribute changes in a model
 * and offers access to the corresponding managed indices.
 *
 * @param <E> the traverser type used by the associated indices
 */
public interface IModelAccessor<E extends ITraverser> {
  
  /**
   * Notifies that a transformation has started.
   *
   * @param transformationName the name of the transformation
   */
  void notifyTransformationStart(@Nonnull String transformationName);

  /**
   * Notifies that a transformation has ended.
   *
   * @param transformationName the name of the transformation
   */
  void notifyTransformationEnd(@Nonnull String transformationName);
  
  /**
   * Notifies that a node has been attached to a parent node.
   *
   * @param node the attached node
   * @param parent the parent the node was attached to, or {@code null} if the node is a root node
   */
  void notifyNodeAttach(@Nonnull ASTNode node, @Nullable ASTNode parent);
  
  /**
   * Notifies that a node has been detached from a parent node.
   *
   * @param node the detached node
   * @param parent the parent the node was detached from
   */
  void notifyNodeDetach(@Nonnull ASTNode node, @Nonnull ASTNode parent);
  
  /**
   * Notifies that a node attribute has been modified.
   *
   * @param node the modified node
   * @param parent the parent containing the node, or {@code null} if no parent is available
   * @param attributeName the name of the modified attribute
   * @param oldValue the previous attribute value
   * @param newValue the new attribute value
   */
  void notifyModification(@Nonnull ASTNode node, @Nullable ASTNode parent, String attributeName, Object oldValue, Object newValue);
  
  
  /**
   * Returns the listeners used for incremental model changes.
   *
   * @return a collection of listeners
   */
  Collection<IIncrementalListener> listeners();
  
  /**
   * Returns a candidate index.
   *
   * @return the candidate index
   */
  CandidateIndex getCandidateIndex();
  
  /**
   * Returns a parent index.
   *
   * @return the parent index
   */
  ParentIndex getParentIndex();
  
  /**
   * Checks whether a custom index with the given name exists.
   *
   * @param name the index name
   * @return {@code true} if a custom index with the given name is registered
   */
  boolean hasCustomIndex(String name);
  
  /**
   * Returns the custom index registered under the given name.
   *
   * @param name the index name
   * @return an {@link Optional} containing the registered custom index, or an
   *     empty {@link Optional} if none exists
   */
  Optional<IModelIndex> getCustomIndex(String name);
}
