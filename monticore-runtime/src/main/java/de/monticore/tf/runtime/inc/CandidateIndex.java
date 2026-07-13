package de.monticore.tf.runtime.inc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;
import de.monticore.visitor.IVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;

public class CandidateIndex<E extends ITraverser> implements IModelIndex<E> {
  
  protected Multimap<Class<? extends ASTNode>, ASTNode> candidates;
  protected Multimap<Class<?>, Class<?>> subClasses;
  
  public CandidateIndex() {
    this.candidates = LinkedHashMultimap.create();
    this.subClasses = LinkedHashMultimap.create();
  }
  
  @Override
  public void finalizeInitialization() {
    this.candidates.forEach((k, v) -> {
      checkSuperClass(k);
    });
  }
  
  protected void checkSuperClass(Class<?> clazz) {
    Class<?> superClass = clazz.getSuperclass();
    if (superClass != null) {
      this.subClasses.put(superClass, clazz);
      checkSuperClass(superClass);
    }
  }
  
  public Collection<ASTNode> getCandidateNodes(Class<? extends ASTNode> clazz) {
    return this.candidates.get(clazz);
  }
  
  public boolean hasCandidateNodes(Class<? extends ASTNode> clazz) {
    return this.candidates.containsKey(clazz);
  }
  
  public Collection<ASTNode> getCandidateNodesIncludingSubClasses(Class<? extends ASTNode> clazz) {
    if (!this.subClasses.containsKey(clazz)) {
      return getCandidateNodes(clazz);
    }
    return this.subClasses.get(clazz).stream().filter(x -> this.candidates.containsKey(x))
        .map(x -> this.candidates.get((Class<? extends ASTNode>) x)).flatMap(Collection::stream)
        .toList();
  }
  
  public Collection<ASTNode> getAllNodes() {
    return this.candidates.values();
  }
  
  @Override
  public void onASTNodeAddition(ASTNode node, ASTNode parent) {
    this.candidates.put(node.getClass(), node);
    Log.debug(() -> "Added node with type %s!".formatted(node.getClass()), "CandidateIndex");
  }
  
  @Override
  public void onASTNodeRemoval(ASTNode node, ASTNode parent) {
    this.candidates.remove(node.getClass(), node);
    Log.debug(() -> "Deleted node with type %s!".formatted(node.getClass()), "CandidateIndex");
  }
  
  @Override
  public void onASTNodeModification(ASTNode node, ASTNode parent) {
    // CandidateIndex does not care about modifications
  }
  
  @Override
  public void registerIntoTraverser(E traverser) {
    traverser.add4IVisitor(new IVisitor() {
      
      @Override
      public void visit(ASTNode node) {
        candidates.put(node.getClass(), node);
      }
    });
  }
}
