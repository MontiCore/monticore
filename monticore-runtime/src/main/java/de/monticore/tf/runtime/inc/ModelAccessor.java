package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;

import java.util.List;
import java.util.Map;

public class ModelAccessor<E extends ITraverser> implements IModelAccessor<E> {
  
  private final IndexHandler<E> indexHandler;
  
  public ModelAccessor(E traverser, ASTNode... roots) {
    this.indexHandler = new IndexHandler<>(traverser, roots);
  }
  
  public ModelAccessor(E traverser, Map<String, IModelIndex<E>> customIndices, ASTNode... roots) {
    this.indexHandler = new IndexHandler<>(traverser, customIndices, roots);
  }
  
  public ModelAccessor(E traverser, List<ASTNode> roots) {
    this.indexHandler = new IndexHandler<>(traverser, roots);
  }
  
  public ModelAccessor(E traverser, Map<String, IModelIndex<E>> customIndices,
      List<ASTNode> roots) {
    this.indexHandler = new IndexHandler<>(traverser, customIndices, roots);
  }
  
  @Override
  public void notifyAdd(ASTNode node, ASTNode parent) {
    this.indexHandler.onASTNodeAddition(node, parent);
  }
  
  @Override
  public void notifyDeletion(ASTNode node, ASTNode parent) {
    this.indexHandler.onASTNodeRemoval(node, parent);
  }
  
  @Override
  public void notifyModification(ASTNode node, ASTNode parent) {
    this.indexHandler.onASTNodeModification(node, parent);
  }
  
  @Override
  public IndexHandler<E> indices() {
    return this.indexHandler;
  }
}
