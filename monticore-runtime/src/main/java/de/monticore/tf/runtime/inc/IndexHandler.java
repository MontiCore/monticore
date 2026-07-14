package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coordinates the built-in and custom model indices and forwards incremental
 * model change events to all registered indices.
 *
 * @param <E> the traverser type used to initialize and register indices
 */
public class IndexHandler<E extends ITraverser> implements IIncrementalListener {
  
  private final CandidateIndex<E> candidateIndex;
  private final ParentIndex<E> parentIndex;
  
  private final Map<String, IModelIndex<E>> customIndices;
  
  /**
   * Creates an index handler with the built-in indices and no custom indices.
   *
   * @param traverser the traverser used to initialize the indices
   * @param roots the root nodes used for index initialization
   */
  public IndexHandler(E traverser, ASTNode ...roots) {
    this(traverser, new HashMap<>(), List.of(roots));
  }
  
  /**
   * Creates an index handler with the built-in indices and the given custom
   * indices.
   *
   * @param traverser the traverser used to initialize the indices
   * @param customIndices the custom indices to register by name
   * @param roots the root nodes used for index initialization
   */
  public IndexHandler(E traverser, Map<String, IModelIndex<E>> customIndices, ASTNode... roots) {
    this(traverser, customIndices, List.of(roots));
  }
  
  /**
   * Creates an index handler with the built-in indices and no custom indices.
   *
   * @param traverser the traverser used to initialize the indices
   * @param roots the root nodes used for index initialization
   */
  public IndexHandler(E traverser, List<ASTNode> roots) {
    this(traverser, new HashMap<>(), roots);
  }
  
  /**
   * Creates an index handler, initializes all built-in and custom indices, and
   * registers them through an index initializer.
   *
   * @param traverser the traverser used to initialize the indices
   * @param customIndices the custom indices to register by name
   * @param roots the root nodes used for index initialization
   */
  public IndexHandler(E traverser, Map<String, IModelIndex<E>> customIndices, List<ASTNode> roots) {
    this.candidateIndex = new CandidateIndex<>();
    this.parentIndex = new ParentIndex<>();
    this.customIndices = new HashMap<>(customIndices);
    
    IndexInitializer<E> initializer = new IndexInitializer<>(traverser, roots);
    initializer.addIndex(this.candidateIndex);
    initializer.addIndex(this.parentIndex);
    this.customIndices.values().forEach(initializer::addIndex);
    
    initializer.init();
  }
  
  /**
   * Returns the built-in candidate index.
   *
   * @return the candidate index
   */
  public CandidateIndex<E> getCandidateIndex() {
    return candidateIndex;
  }
  
  /**
   * Returns the built-in parent index.
   *
   * @return the parent index
   */
  public ParentIndex<E> getParentIndex() {
    return parentIndex;
  }
  
  /**
   * Checks whether a custom index with the given name exists.
   *
   * @param name the index name
   * @return {@code true} if a custom index with the given name is registered
   */
  public boolean hasCustomIndex(String name) {
    return this.customIndices.containsKey(name);
  }
  
  /**
   * Returns the custom index registered under the given name.
   *
   * @param name the index name
   * @return the registered custom index, or {@code null} if none exists
   */
  public IModelIndex<E> getCustomIndex(String name) {
    return this.customIndices.get(name);
  }
  
  /**
   * Forwards a node attach event to all managed indices.
   *
   * @param node the attached node
   * @param parent the parent the node was attached to
   */
  @Override
  public void onASTNodeAttach(ASTNode node, ASTNode parent) {
    this.candidateIndex.onASTNodeAttach(node, parent);
    this.parentIndex.onASTNodeAttach(node, parent);
    
    this.customIndices.values().forEach(index -> index.onASTNodeAttach(node, parent));
  }
  
  /**
   * Forwards a node detach event to all managed indices.
   *
   * @param node the detached node
   * @param parent the parent the node was detached from
   */
  @Override
  public void onASTNodeDetach(ASTNode node, ASTNode parent) {
    this.candidateIndex.onASTNodeDetach(node, parent);
    this.parentIndex.onASTNodeDetach(node, parent);
    
    this.customIndices.values().forEach(index -> index.onASTNodeDetach(node, parent));
  }
  
  /**
   * Forwards a node modification event to all managed indices.
   *
   * @param node the modified node
   * @param parent the parent containing the node
   * @param attributeName the name of the modified attribute
   * @param oldValue the previous attribute value
   * @param newValue the new attribute value
   */
  @Override
  public void onASTNodeModification(ASTNode node, ASTNode parent, String attributeName,
      Object oldValue, Object newValue) {
    this.candidateIndex.onASTNodeModification(node, parent, attributeName, oldValue, newValue);
    this.parentIndex.onASTNodeModification(node, parent, attributeName, oldValue, newValue);
    
    this.customIndices.values().forEach(index -> index.onASTNodeModification(node, parent, attributeName, oldValue, newValue));
  }
}
