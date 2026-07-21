/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;

import java.util.ArrayList;
import java.util.List;

/**
 * Initializes model indices by registering them into a traverser, traversing
 * the given root nodes, and finalizing each index afterward.
 *
 * @param <E> the traverser type used during index initialization
 */
public class IndexInitializer<E extends ITraverser> {
  
  protected final List<ASTNode> roots;
  protected final E traverser;
  protected List<IModelIndex<E>> indices;
  
  /**
   * Creates a new index initializer for the given traverser and root nodes.
   *
   * @param traverser the traverser used to initialize the indices
   * @param roots the root nodes that will be traversed
   */
  public IndexInitializer(E traverser, List<ASTNode> roots) {
    this.traverser = traverser;
    this.roots = roots;
    this.indices = new ArrayList<>();
  }
  
  /**
   * Adds an index to the initialization sequence.
   *
   * @param index the index to add
   * @return this initializer for fluent chaining
   */
  public IndexInitializer<E> addIndex(IModelIndex<E> index) {
    this.indices.add(index);
    return this;
  }
  
  /**
   * Registers all indices into the traverser, traverses all root nodes, and
   * then finalizes each index.
   */
  public void init() {
    this.indices.forEach(index -> index.registerIntoTraverser(traverser));
    for (ASTNode root : this.roots) {
      root.accept(this.traverser);
    }
    this.indices.forEach(IModelIndex::finalizeInitialization);
  }
}
