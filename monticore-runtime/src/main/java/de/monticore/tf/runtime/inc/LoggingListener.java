/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Logs incremental transformation and AST change events.
 */
public class LoggingListener implements IIncrementalListener {
  
  /**
   * Logs that a transformation has started.
   *
   * @param transformationName the name of the transformation
   */
  @Override
  public void onTransformationStart(@Nonnull String transformationName) {
    Log.info("Transformation started: " + transformationName,
        LoggingListener.class.getSimpleName());
  }
  
  /**
   * Logs that a transformation has ended.
   *
   * @param transformationName the name of the transformation
   */
  @Override
  public void onTransformationEnd(@Nonnull String transformationName) {
    Log.info("Transformation ended: " + transformationName, LoggingListener.class.getSimpleName());
  }

  /**
   * Logs that an AST node has been created.
   *
   * @param node the newly created AST node
   */
  @Override
  public void onASTNodeCreation(@Nonnull ASTNode node) {
    Log.info("AST node created: " + node.getClass().getSimpleName(), LoggingListener.class.getSimpleName());
  }

  /**
   * Logs that a node has been attached to a parent node.
   *
   * @param node the attached node
   * @param parent the parent node
   */
  @Override
  public void onASTNodeAttach(@Nonnull ASTNode node, @Nullable ASTNode parent) {
    if (parent == null) {
      Log.info(
          "Root Node attached: " + node.getClass().getSimpleName(), LoggingListener.class.getSimpleName());
    } else {
      Log.info(
          "Node attached: " + node.getClass().getSimpleName() + " to parent: " + parent.getClass()
              .getSimpleName(), LoggingListener.class.getSimpleName());
    }
  }
  
  /**
   * Logs that a node has been detached from a parent node.
   *
   * @param node the detached node
   * @param parent the former parent node
   */
  @Override
  public void onASTNodeDetach(@Nonnull ASTNode node, @Nonnull ASTNode parent) {
    Log.info(
        "Node detached: " + node.getClass().getSimpleName() + " from parent: " + parent.getClass()
            .getSimpleName(), LoggingListener.class.getSimpleName());
  }
   
  /**
   * Logs that a node attribute has been modified.
   *
   * @param node the modified node
   * @param attributeName the name of the modified attribute
   * @param modificationType the type of modification
   * @param oldValue the previous value of the attribute
   * @param newValue the new value of the attribute
   */
  @Override
  public void onASTNodeModification(@Nonnull ASTNode node, @Nonnull String attributeName,
      ModificationOp modificationType, @Nullable Object oldValue,
      @Nullable Object newValue) {
    String message = switch (modificationType) {
      case SET -> "Node modified: " + node.getClass().getSimpleName() + " attribute: " + attributeName + " value: " + newValue;
      case UNSET -> "Node modified: " + node.getClass().getSimpleName() + " attribute: " + attributeName + " old value: " + oldValue;
      case REPLACE -> "Node modified: " + node.getClass().getSimpleName() + " attribute: " + attributeName + " old value: " + oldValue + " new value: " + newValue;
    };
    Log.info(message, LoggingListener.class.getSimpleName());
  }
  
  /**
   * Logs that an element in a list attribute of a node has been modified.
   *
   * @param node the modified node
   * @param attributeName the name of the modified list attribute
   * @param idx the index of the modified element within the list
   * @param modificationType the type of list-element modification
   * @param oldValue the previous value of the element
   * @param newValue the new value of the element
   */
  @Override
  public void onASTNodeListModification(@Nonnull ASTNode node, String attributeName, int idx,
      ModificationOp modificationType, @Nullable Object oldValue,
      @Nullable Object newValue) {
    String message = switch (modificationType) {
      case SET -> "Node list modified: " + node.getClass().getSimpleName() + " attribute: " + attributeName + " index: " + idx + " value: " + newValue;
      case UNSET -> "Node list modified: " + node.getClass().getSimpleName() + " attribute: " + attributeName + " index: " + idx + " old value: " + oldValue;
      case REPLACE -> "Node list modified: " + node.getClass().getSimpleName() + " attribute: " + attributeName + " index: " + idx + " old value: " + oldValue + " new value: " + newValue;
    };
    Log.info(message, LoggingListener.class.getSimpleName());
  }
}
