/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;

import java.util.Collection;

/**
 * Provides notifications about structural and attribute changes in a model
 * and offers access to the corresponding index handler.
 *
 * @param <E> the traverser type used by the associated indices
 */
public interface IModelAccessor<E extends ITraverser> {
  
  /**
   * Notifies that a transformation has started.
   *
   * @param transformationName the name of the transformation
   */
  void notifyTransformationStart(String transformationName);

  /**
   * Notifies that a transformation has ended.
   *
   * @param transformationName the name of the transformation
   */
  void notifyTransformationEnd(String transformationName);
  
  /**
   * Notifies that a node has been attached to a parent node.
   *
   * @param node the attached node
   * @param parent the parent the node was attached to
   */
  void notifyNodeAttach(ASTNode node, ASTNode parent);
  
  /**
   * Notifies that a node has been detached from a parent node.
   *
   * @param node the detached node
   * @param parent the parent the node was detached from
   */
  void notifyNodeDetach(ASTNode node, ASTNode parent);
  
  /**
   * Notifies that a node attribute has been modified.
   *
   * @param node the modified node
   * @param parent the parent containing the node
   * @param attributeName the name of the modified attribute
   * @param oldValue the previous attribute value
   * @param newValue the new attribute value
   */
  void notifyModification(ASTNode node, ASTNode parent, String attributeName, Object oldValue, Object newValue);
  
  /**
   * Returns the index handler used to keep model indices in sync.
   *
   * @return the index handler
   */
  IndexHandler<E> indices();
  
  /**
   * Returns the listeners used for incremental model changes.
   *
   * @return a collection of listeners
   */
  Collection<IIncrementalListener> listeners();
}
