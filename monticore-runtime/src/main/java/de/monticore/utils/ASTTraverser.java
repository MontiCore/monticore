/* (c) https://github.com/MontiCore/monticore */

package de.monticore.utils;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import de.monticore.ast.ASTNode;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import de.monticore.annotations.Visit;
import de.monticore.annotations.VisitAnnotations;
import de.se_rwth.commons.TreeUtil;

/**
 * An ASTTraverser encapsulates the traversal over every node in an AST. Clients can simply add
 * themselves as visitors and call {@link #traverse}. They will then receive a callback to any
 * method annotated with the {@link Visit} annotation, when the ASTTraverser traverses any node of
 * the type declared by that method.
 * <p>
 * <b>Important remarks:</b></br>
 * <li>Visitors will be called in the order of insertion.</li>
 * <li>Adding or removing a visitor twice in succession has no effect beyond the first
 * addition/removal.</li>
 * <li>Any ASTNodes that visitors add or remove from the traversed AST will not affect the current
 * traversal.</li>
 * 
 * @author Sebastian Oberhoff
 */
public final class ASTTraverser {
  
  /**
   * Associates each visitor with a set of visit methods.
   */
  // using a linked list to preserve insertion order
  // FIXME: Does this break if a visitor removes or adds a visitor during an ongoing iteration
  // over all visitors?
  private final Multimap<Visitor, Method> visitors = LinkedListMultimap.create();
  
  /**
   * Visitors must annotate any methods intended for callbacks with the
   * {@link de.monticore.annotations.Visit @Visit} annotation.
   * <p>
   * Visitors will be called in the order of insertion.
   * 
   * @param firstVisitor an ASTTraverser requires at least one visitor
   * @param remainingVisitors vararg extension for convenience
   * @throws UnsupportedOperationException if one of the declared visit methods does not meet the
   * specification
   */
  public ASTTraverser(Visitor firstVisitor, Visitor... remainingVisitors) {
    addvisitor(firstVisitor);
    for (Visitor visitor : remainingVisitors) {
      addvisitor(visitor);
    }
  }
  
  /**
   * Visitors <i>must</i> annotate any methods intended for callbacks with the
   * {@link de.monticore.annotations.Visit @Visit} annotation. *
   * <p>
   * Visitors will be called in the order of insertion.
   * <p>
   * Adding a visitor twice has no effect beyond the first addition.
   * 
   * @param visitor a Visitor which should be called for each node matching one of the annotated
   * visit methods
   * @throws UnsupportedOperationException if one of the declared visit methods does not meet the
   * specification
   */
  public void addvisitor(Visitor visitor) {
    for (Method visitMethod : VisitAnnotations.visitMethods(visitor)) {
      visitors.put(visitor, visitMethod);
    }
  }
  
  /**
   * Removing a visitor twice has no effect beyond the first removal.
   * 
   * @param visitor the visitor to be removed
   */
  public void removevisitor(Visitor visitor) {
    visitors.removeAll(visitor);
  }
  
  /**
   * Traverses the entire subtree spanned by an ASTNode using a breadth first algorithm, calling all
   * matching @Visit methods on all registered visitors in the order in which they were added.
   * 
   * @param rootNode the root ASTNode where traversal should begin. This doesn't necessarily have to
   * be the true root of the AST.
   */
  public void traverse(ASTNode rootNode) {
    checkState(!visitors.keySet().isEmpty(),
        "The ASTTraverser began a traversal without any visitors.");
    for (ASTNode node : TreeUtil.breadthFirst(rootNode, node -> node.get_Children())) {
      visit(node);
    }
  }
  
  /**
   * Calls the matching visit methods on all registered visitors.
   * 
   * @param currentNode the node that is currently being traversed
   */
  private void visit(ASTNode currentNode) {
    // iterate over every visitor-method pair
    for (Map.Entry<Visitor, Method> entry : visitors.entries()) {
      Visitor visitor = entry.getKey();
      Method visitMethod = entry.getValue();
      // check if the method is applicable to the current node
      if (currentNode.getClass().equals(visitMethod.getParameterTypes()[0])) {
        try {
          visitMethod.invoke(visitor, currentNode);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
          throw new IllegalStateException("0xA4096 Couldn't invoke the Method \"" + visitMethod
              + "\" on the visitor \"" + visitor + ".",
              e);
        }
      }
    }
  }
  
  @Override
  public String toString() {
    return visitors.toString();
  }
  
  /**
   * A marker interface signaling that an implementing class contains methods annotated with
   * {@link de.monticore.annotations.Visit @Visit}
   */
  public interface Visitor {
  }
}
