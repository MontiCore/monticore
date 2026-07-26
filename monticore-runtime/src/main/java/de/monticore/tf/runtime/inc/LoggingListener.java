/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;
import org.jspecify.annotations.NonNull;

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
  public void onTransformationStart(@NonNull String transformationName) {
    Log.info("Transformation started: " + transformationName,
        LoggingListener.class.getSimpleName());
  }
  
  /**
   * Logs that a transformation has ended.
   *
   * @param transformationName the name of the transformation
   */
  @Override
  public void onTransformationEnd(@NonNull String transformationName) {
    Log.info("Transformation ended: " + transformationName, LoggingListener.class.getSimpleName());
  }
  
  /**
   * Logs that a node has been attached to a parent node.
   *
   * @param node the attached node
   * @param parent the parent node
   */
  @Override
  public void onASTNodeAttach(@NonNull ASTNode node, @Nullable ASTNode parent) {
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
  public void onASTNodeDetach(@NonNull ASTNode node, @NonNull ASTNode parent) {
    Log.info(
        "Node detached: " + node.getClass().getSimpleName() + " from parent: " + parent.getClass()
            .getSimpleName(), LoggingListener.class.getSimpleName());
  }
  
  @Override
  public void onASTNodeModification(@NonNull ASTNode node, @NonNull String attributeName,
      ModificationOp modificationType, @org.jspecify.annotations.Nullable Object oldValue,
      @org.jspecify.annotations.Nullable Object newValue) {
    String message = switch (modificationType) {
      case SET -> "Node modified: " + node.getClass().getSimpleName() + " attribute: " + attributeName + " value: " + newValue;
      case UNSET -> "Node modified: " + node.getClass().getSimpleName() + " attribute: " + attributeName + " old value: " + oldValue;
      case REPLACE -> "Node modified: " + node.getClass().getSimpleName() + " attribute: " + attributeName + " old value: " + oldValue + " new value: " + newValue;
    };
    Log.info(message, LoggingListener.class.getSimpleName());
  }
  
  @Override
  public void onASTNodeListModification(@NonNull ASTNode node, String attributeName, int idx,
      ModificationOp modificationType, @org.jspecify.annotations.Nullable Object oldValue,
      @org.jspecify.annotations.Nullable Object newValue) {
    String message = switch (modificationType) {
      case SET -> "Node list modified: " + node.getClass().getSimpleName() + " attribute: " + attributeName + " index: " + idx + " value: " + newValue;
      case UNSET -> "Node list modified: " + node.getClass().getSimpleName() + " attribute: " + attributeName + " index: " + idx + " old value: " + oldValue;
      case REPLACE -> "Node list modified: " + node.getClass().getSimpleName() + " attribute: " + attributeName + " index: " + idx + " old value: " + oldValue + " new value: " + newValue;
    };
    Log.info(message, LoggingListener.class.getSimpleName());
  }
}
