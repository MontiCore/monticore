/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
  default void onTransformationStart(@Nonnull String transformationName) {
    // Override to add custom implementation
  }
  
  /**
   * Called when a transformation ends.
   *
   * @param transformationName the name of the transformation
   */
  default void onTransformationEnd(@Nonnull String transformationName) {
    // Override to add custom implementation
  }
  
  /**
   * Called when an AST node is attached to a parent node.
   *
   * @param node the AST node that was attached
   * @param parent the parent node to which the node was attached, or {@code null} if the node is a root node
   */
  default void onASTNodeAttach(@Nonnull ASTNode node, @Nullable ASTNode parent) {
    // Override to add custom implementation
  }
  
  /**
   * Called when an AST node is detached from a parent node.
   *
   * @param node the AST node that was detached
   * @param parent the former parent node from which the node was detached
   */
  default void onASTNodeDetach(@Nonnull ASTNode node, @Nonnull ASTNode parent) {
    // Override to add custom implementation
  }
  
  /**
   * Called when an attribute of an AST node is modified.
   *
   * @param node the AST node whose attribute was modified
   * @param attributeName the name of the modified attribute
   * @param modificationType the type of attribute modification
   * @param oldValue the previous value of the attribute, or {@code null} if not applicable
   * @param newValue the new value of the attribute, or {@code null} if not applicable
   */
  default void onASTNodeModification(@Nonnull ASTNode node, @Nonnull String attributeName,
      ModificationOp modificationType, @Nullable Object oldValue,
      @Nullable Object newValue) {
    // Override to add custom implementation
  }

  /**
   * Called when an element in a list attribute of an AST node is modified.
   *
   * @param node the AST node whose list attribute was modified
   * @param attributeName the name of the modified list attribute
   * @param idx the index of the modified element within the list
   * @param modificationType the type of list-element modification
   * @param oldValue the previous value of the element, or {@code null} if not applicable
   * @param newValue the new value of the element, or {@code null} if not applicable
   */
  default void onASTNodeListModification(@Nonnull ASTNode node, String attributeName, int idx,
      ModificationOp modificationType, @Nullable Object oldValue, @Nullable Object newValue) {
    // Override to add custom implementation
  }
}
