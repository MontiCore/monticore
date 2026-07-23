/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;
import org.jspecify.annotations.NonNull;

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
  public void onASTNodeAttach(@NonNull ASTNode node, ASTNode parent) {
    Log.info(
        "Node attached: " + node.getClass().getSimpleName() + " to parent: " + parent.getClass()
            .getSimpleName(), LoggingListener.class.getSimpleName());
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
}
