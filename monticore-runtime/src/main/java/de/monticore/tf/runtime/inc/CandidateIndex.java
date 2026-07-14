package de.monticore.tf.runtime.inc;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import de.monticore.ast.ASTCNode;
import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;
import de.monticore.visitor.IVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class CandidateIndex<E extends ITraverser> implements IModelIndex<E> {
  
  protected Multimap<Class<? extends ASTNode>, ASTNode> candidates;
  protected Multimap<Class<?>, Class<?>> subTypes;
  
  public CandidateIndex() {
    this.candidates = LinkedHashMultimap.create();
    this.subTypes = LinkedHashMultimap.create();
  }
  
  @Override
  public void finalizeInitialization() {
    this.candidates.forEach((k, v) -> {
      checkSuperTypes(k);
    });
  }
  
  protected void checkSuperTypes(Class<?> clazz) {
    Class<?> superClass = clazz.getSuperclass();
    if (superClass != null) {
      this.subTypes.put(superClass, clazz);
      if (superClass != ASTCNode.class) {
        checkSuperTypes(superClass);
      }
    }
    
    for (Class<?> i : clazz.getInterfaces()) {
      this.subTypes.put(i, clazz);
      checkSuperTypes(i);
    }
  }
  
  public Collection<ASTNode> getCandidateNodes(Class<? extends ASTNode> clazz) {
    return this.candidates.get(clazz);
  }
  
  public boolean hasCandidateNodes(Class<? extends ASTNode> clazz) {
    return this.candidates.containsKey(clazz);
  }
  
  protected Collection<Class<?>> getSubTypes(Class<?> clazz) {
    Set<Class<?>> result  = new HashSet<>();
    Set<Class<?>> visited = new HashSet<>();
    
    Collection<Class<?>> direct = this.subTypes.get(clazz);
    Deque<Class<?>> stack = new ArrayDeque<>(direct);
    
    while (!stack.isEmpty()) {
      Class<?> cur = stack.pop();
      
      if (!visited.add(cur)) {
        continue;
      }
      
      result.add(cur);
      Collection<Class<?>> further = this.subTypes.get(cur);
      for (Class<?> child : further) {
        if (!visited.contains(child)) {
          stack.push(child);
        }
      }
    }
    return Collections.unmodifiableSet(result);
  }
  
  public Collection<ASTNode> getSubTypeCandidateNodes(Class<?> clazz) {
    return getSubTypes(clazz).stream()
        .filter(x -> this.candidates.containsKey(x))
        .map(x -> this.candidates.get((Class<? extends ASTNode>) x))
        .flatMap(Collection::stream)
        .toList();
  }
  
  public Collection<ASTNode> getAllNodes() {
    return this.candidates.values();
  }
  
  @Override
  public void onASTNodeAttach(ASTNode node, ASTNode parent) {
    this.candidates.put(node.getClass(), node);
    Log.debug(() -> "Added node with type %s!".formatted(node.getClass()), "CandidateIndex");
  }
  
  @Override
  public void onASTNodeDetach(ASTNode node, ASTNode parent) {
    this.candidates.remove(node.getClass(), node);
    Log.debug(() -> "Deleted node with type %s!".formatted(node.getClass()), "CandidateIndex");
  }
  
  @Override
  public void onASTNodeModification(ASTNode node, ASTNode parent, String attributeName,
      Object oldValue, Object newValue) {
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
