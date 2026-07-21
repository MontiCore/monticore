/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import de.monticore.ast.ASTCNode;
import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;
import de.monticore.visitor.IVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.*;

/**
 * Index for AST nodes that can be used as candidates for later processing.
 *
 * <p>The index stores AST nodes grouped by their concrete runtime class.
 * Additionally, it stores subtype relationships between AST-related classes
 * to allow lookup of candidates by supertype.</p>
 *
 * @param <E> type of traverser this index can be registered into
 */
public class CandidateIndex<E extends ITraverser> implements IModelIndex<E> {
  
  /**
   * Stores candidate AST nodes grouped by their concrete class.
   *
   * <p>For example, all nodes of type {@code ASTFoo} are stored under the key
   * {@code ASTFoo.class}.</p>
   */
  protected Multimap<Class<? extends ASTNode>, ASTNode> candidates;
  
  /**
   * Stores known subtype relationships.
   *
   * <p>The key is a superclass or interface, and the values are known subtypes
   * of that class or interface.</p>
   */
  protected Multimap<Class<?>, Class<?>> subTypes;
  
  
  /**
   * Creates an empty candidate index.
   */
  public CandidateIndex() {
    // LinkedHashMultimap preserves insertion order and avoids duplicate key-value pairs.
    this.candidates = LinkedHashMultimap.create();
    this.subTypes = LinkedHashMultimap.create();
  }
  
  /**
   * Finalizes the initialization of the index.
   *
   * <p>For every already known candidate node, this method determines its
   * supertypes and stores the corresponding subtype relationships.</p>
   */
  @Override
  public void finalizeInitialization() {
    this.candidates.forEach((k, v) -> {
      checkSuperTypes(k);
    });
  }
  
  /**
   * Recursively determines all superclasses and interfaces of the given class
   * and stores the corresponding subtype relationships.
   *
   * @param clazz class whose supertypes should be checked
   */
  protected void checkSuperTypes(Class<?> clazz) {
    Class<?> superClass = clazz.getSuperclass();
    
    // If the class has a superclass, store the relationship:
    // superclass -> current class.
    if (superClass != null) {
      this.subTypes.put(superClass, clazz);
      
      // ASTCNode is treated as the upper boundary of the relevant AST hierarchy.
      if (superClass != ASTCNode.class) {
        checkSuperTypes(superClass);
      }
    }
    
    // Also consider all interfaces implemented by the class.
    for (Class<?> i : clazz.getInterfaces()) {
      this.subTypes.put(i, clazz);
      checkSuperTypes(i);
    }
  }
  
  /**
   * Returns all candidate nodes that have exactly the given class.
   *
   * @param clazz AST node class to look up
   * @return collection of stored AST nodes of the given class
   */
  public Collection<ASTNode> getCandidateNodes(Class<? extends ASTNode> clazz) {
    return this.candidates.get(clazz);
  }
  
  /**
   * Checks whether the index contains candidate nodes for the given class.
   *
   * @param clazz AST node class to check
   * @return {@code true} if candidates exist for the class, otherwise {@code false}
   */
  public boolean hasCandidateNodes(Class<? extends ASTNode> clazz) {
    return this.candidates.containsKey(clazz);
  }
  
  /**
   * Returns all known subtypes of the given class or interface.
   *
   * <p>The method performs an iterative depth-first search over the stored
   * subtype relationships.</p>
   *
   * @param clazz class or interface whose subtypes should be returned
   * @return unmodifiable set of all known subtypes
   */
  protected Collection<Class<?>> getSubTypes(Class<?> clazz) {
    Set<Class<?>> result  = new HashSet<>();
    Set<Class<?>> visited = new HashSet<>();
    
    // Get the directly known subtypes of the given class.
    Collection<Class<?>> direct = this.subTypes.get(clazz);
    
    // Stack used for iterative depth-first traversal.
    Deque<Class<?>> stack = new ArrayDeque<>(direct);
    
    while (!stack.isEmpty()) {
      Class<?> cur = stack.pop();
      
      // Skip classes that have already been visited.
      // This prevents repeated processing and protects against cycles.
      if (!visited.add(cur)) {
        continue;
      }
      
      result.add(cur);
      
      // Add further subtypes of the current class to the traversal stack.
      Collection<Class<?>> further = this.subTypes.get(cur);
      for (Class<?> child : further) {
        if (!visited.contains(child)) {
          stack.push(child);
        }
      }
    }
    // Return an unmodifiable view so callers cannot change the result set.
    return Collections.unmodifiableSet(result);
  }
  
  /**
   * Returns all candidate nodes whose class is a subtype of the given class.
   *
   * @param clazz superclass or interface whose subtype candidates should be returned
   * @return collection of AST nodes whose classes are known subtypes of {@code clazz}
   */
  public Collection<ASTNode> getSubTypeCandidateNodes(Class<?> clazz) {
    return getSubTypes(clazz).stream()
        // Only consider subtypes for which candidate nodes are stored.
        .filter(x -> this.candidates.containsKey(x))
        // Retrieve the candidate nodes for each subtype.
        // The cast is safe here because only classes contained in the candidates map are used.
        .map(x -> this.candidates.get((Class<? extends ASTNode>) x))
        // Flatten the collections of candidates into a single stream.
        .flatMap(Collection::stream)
        // Collect the result into a list.
        .toList();
  }
  
  /**
   * Returns all AST nodes stored in this index.
   *
   * @return collection of all candidate nodes, independent of their class
   */
  public Collection<ASTNode> getAllNodes() {
    return this.candidates.values();
  }
  
  /**
   * Called when an AST node is attached to a parent node.
   *
   * <p>The attached node is added to the candidate index.</p>
   *
   * @param node attached AST node
   * @param parent parent node to which the node was attached
   */
  @Override
  public void onASTNodeAttach(ASTNode node, ASTNode parent) {
    this.candidates.put(node.getClass(), node);
    Log.debug(() -> "Added node with type %s!".formatted(node.getClass()), "CandidateIndex");
  }
  
  /**
   * Called when an AST node is detached from a parent node.
   *
   * <p>The detached node is removed from the candidate index.</p>
   *
   * @param node detached AST node
   * @param parent former parent node
   */
  @Override
  public void onASTNodeDetach(ASTNode node, ASTNode parent) {
    this.candidates.remove(node.getClass(), node);
    Log.debug(() -> "Deleted node with type %s!".formatted(node.getClass()), "CandidateIndex");
  }
  
  /**
   * Called when an attribute of an AST node is modified.
   *
   * <p>This index does not react to attribute changes because it only tracks
   * node existence and node classes.</p>
   *
   * @param node modified AST node
   * @param parent parent node of the modified node
   * @param attributeName name of the modified attribute
   * @param oldValue previous value of the attribute
   * @param newValue new value of the attribute
   */
  @Override
  public void onASTNodeModification(ASTNode node, ASTNode parent, String attributeName,
      Object oldValue, Object newValue) {
    // CandidateIndex does not care about modifications
  }
  
  /**
   * Registers this index into the given traverser.
   *
   * <p>Whenever the traverser visits an AST node, the node is automatically
   * added to the candidate index.</p>
   *
   * @param traverser traverser into which the visitor should be registered
   */
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
