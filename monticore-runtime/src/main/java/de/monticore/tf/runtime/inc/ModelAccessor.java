package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;

import java.util.List;
import java.util.Map;

/**
 * Provides a facade for propagating model change notifications and accessing
 * the managed indices.
 *
 * @param <E> the traverser type used to initialize the underlying indices
 */
public class ModelAccessor<E extends ITraverser> implements IModelAccessor<E> {
  
  private final IndexHandler<E> indexHandler;
  
  /**
   * Creates a model accessor with the default indices and the given root nodes.
   *
   * @param traverser the traverser used to initialize the indices
   * @param roots the root nodes used for initialization
   */
  public ModelAccessor(E traverser, ASTNode... roots) {
    this.indexHandler = new IndexHandler<>(traverser, roots);
  }
  
  /**
   * Creates a model accessor with the given custom indices and root nodes.
   *
   * @param traverser the traverser used to initialize the indices
   * @param customIndices the custom indices to register by name
   * @param roots the root nodes used for initialization
   */
  public ModelAccessor(E traverser, Map<String, IModelIndex<E>> customIndices, ASTNode... roots) {
    this.indexHandler = new IndexHandler<>(traverser, customIndices, roots);
  }
  
  /**
   * Creates a model accessor with the default indices and the given root nodes.
   *
   * @param traverser the traverser used to initialize the indices
   * @param roots the root nodes used for initialization
   */
  public ModelAccessor(E traverser, List<ASTNode> roots) {
    this.indexHandler = new IndexHandler<>(traverser, roots);
  }
  
  /**
   * Creates a model accessor with the given custom indices and root nodes.
   *
   * @param traverser the traverser used to initialize the indices
   * @param customIndices the custom indices to register by name
   * @param roots the root nodes used for initialization
   */
  public ModelAccessor(E traverser, Map<String, IModelIndex<E>> customIndices,
      List<ASTNode> roots) {
    this.indexHandler = new IndexHandler<>(traverser, customIndices, roots);
  }
  
  /**
   * Forwards a node attach notification to the underlying index handler.
   *
   * @param node the attached node
   * @param parent the parent the node was attached to
   */
  @Override
  public void notifyNodeAttach(ASTNode node, ASTNode parent) {
    this.indexHandler.onASTNodeAttach(node, parent);
  }
  
  /**
   * Forwards a node detach notification to the underlying index handler.
   *
   * @param node the detached node
   * @param parent the parent the node was detached from
   */
  @Override
  public void notifyNodeDetach(ASTNode node, ASTNode parent) {
    this.indexHandler.onASTNodeDetach(node, parent);
  }
  
  /**
   * Forwards an attribute modification notification to the underlying index
   * handler.
   *
   * @param node the modified node
   * @param parent the parent containing the node
   * @param attributeName the name of the modified attribute
   * @param oldValue the previous attribute value
   * @param newValue the new attribute value
   */
  @Override
  public void notifyModification(ASTNode node, ASTNode parent, String attributeName, Object oldValue, Object newValue) {
    this.indexHandler.onASTNodeModification(node, parent, attributeName, oldValue, newValue);
  }
  
  /**
   * Returns the index handler managed by this accessor.
   *
   * @return the index handler
   */
  @Override
  public IndexHandler<E> indices() {
    return this.indexHandler;
  }
}
