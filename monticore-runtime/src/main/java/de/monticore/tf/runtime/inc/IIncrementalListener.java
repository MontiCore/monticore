package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;

/**
 * Listener interface for reacting to incremental changes in an AST.
 *
 * <p>Implementations of this interface are notified whenever AST nodes are
 * attached, detached, or modified. This can be used to keep auxiliary data
 * structures, such as indexes or caches, synchronized with the current AST.</p>
 */
public interface IIncrementalListener {
  
  /**
   * Called when a transformation starts.
   *
   * @param transformationName the name of the transformation
   */
  default void onTransformationStart(String transformationName) {
    // Override to add custom implementation
  }
  
  /**
   * Called when a transformation ends.
   *
   * @param transformationName the name of the transformation
   */
  default void onTransformationEnd(String transformationName) {
    // Override to add custom implementation
  }
  
  /**
   * Called when an AST node is attached to a parent node.
   *
   * @param node the AST node that was attached
   * @param parent the parent node to which the node was attached
   */
  default void onASTNodeAttach(ASTNode node, ASTNode parent) {
    // Override to add custom implementation
  }
  
  /**
   * Called when an AST node is detached from a parent node.
   *
   * @param node the AST node that was detached
   * @param parent the former parent node from which the node was detached
   */
  default void onASTNodeDetach(ASTNode node, ASTNode parent) {
    // Override to add custom implementation
  }
  
  /**
   * Called when an attribute of an AST node is modified.
   *
   * @param node the AST node whose attribute was modified
   * @param parent the parent node of the modified AST node
   * @param attributeName the name of the modified attribute
   * @param oldValue the previous value of the attribute
   * @param newValue the new value of the attribute
   */
  default void onASTNodeModification(ASTNode node, ASTNode parent, String attributeName, Object oldValue,
      Object newValue) {
    // Override to add custom implementation
  }
}
