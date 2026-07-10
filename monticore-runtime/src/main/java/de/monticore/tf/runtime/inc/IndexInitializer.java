package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;

import java.util.ArrayList;
import java.util.List;

public class IndexInitializer<E extends ITraverser> {
  
  protected final List<ASTNode> roots;
  protected final E traverser;
  protected List<IModelIndex<E>> indices;
  
  public IndexInitializer(E traverser, List<ASTNode> roots) {
    this.traverser = traverser;
    this.roots = roots;
    this.indices = new ArrayList<>();
  }
  
  public IndexInitializer<E> addIndex(IModelIndex<E> index) {
    this.indices.add(index);
    return this;
  }
  
  public void init() {
    this.indices.forEach(index -> index.registerIntoTraverser(traverser));
    for (ASTNode root : this.roots) {
      root.accept(this.traverser);
    }
    this.indices.forEach(IModelIndex::finalizeInitialization);
  }
}
