package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexHandler<E extends ITraverser> implements IIncrementalListener {
  
  private final CandidateIndex<E> candidateIndex;
  private final ParentIndex<E> parentIndex;
  
  private final Map<String, IModelIndex<E>> customIndices;
  
  public IndexHandler(E traverser, ASTNode ...roots) {
    this(traverser, new HashMap<>(), List.of(roots));
  }
  
  public IndexHandler(E traverser, Map<String, IModelIndex<E>> customIndices, ASTNode... roots) {
    this(traverser, customIndices, List.of(roots));
  }
  
  public IndexHandler(E traverser, List<ASTNode> roots) {
    this(traverser, new HashMap<>(), roots);
  }
  
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
  
  public CandidateIndex<E> getCandidateIndex() {
    return candidateIndex;
  }
  
  public ParentIndex<E> getParentIndex() {
    return parentIndex;
  }
  
  public boolean hasCustomIndex(String name) {
    return this.customIndices.containsKey(name);
  }
  
  public IModelIndex<E> getCustomIndex(String name) {
    return this.customIndices.get(name);
  }
  
  @Override
  public void onASTNodeAddition(ASTNode node, ASTNode parent) {
    this.candidateIndex.onASTNodeAddition(node, parent);
    this.parentIndex.onASTNodeAddition(node, parent);
    
    this.customIndices.values().forEach(index -> index.onASTNodeAddition(node, parent));
  }
  
  @Override
  public void onASTNodeRemoval(ASTNode node, ASTNode parent) {
    this.candidateIndex.onASTNodeRemoval(node, parent);
    this.parentIndex.onASTNodeRemoval(node, parent);
    
    this.customIndices.values().forEach(index -> index.onASTNodeRemoval(node, parent));
  }
  
  @Override
  public void onASTNodeModification(ASTNode node, ASTNode parent, String attributeName,
      Object oldValue, Object newValue) {
    this.candidateIndex.onASTNodeModification(node, parent, attributeName, oldValue, newValue);
    this.parentIndex.onASTNodeModification(node, parent, attributeName, oldValue, newValue);
    
    this.customIndices.values().forEach(index -> index.onASTNodeModification(node, parent, attributeName, oldValue, newValue));
  }
}
