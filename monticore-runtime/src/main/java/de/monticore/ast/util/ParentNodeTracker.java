// (c) https://github.com/MontiCore/monticore
package de.monticore.ast.util;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.IVisitor;

import java.util.Optional;
import java.util.Stack;

/**
 * Grants Access to the parent node for each (non-root) node.
 * <p>
 * Usage: Add this to a traverser;
 * while traversing a node,
 * {@link #getCurrentParentNode()}
 * returns the parent node of the currently traversed node.
 * <p>
 * This does not grant access to the parent of the top-most node traversed.
 */
public class ParentNodeTracker implements IVisitor {

  protected Stack<ASTNode> parentStack = new Stack<>();

  /**
   * Returns the parent node of the currently traversed node.
   *
   * @return the parent node or empty, iff it is the top most node traversed.
   */
  public Optional<ASTNode> getCurrentParentNode() {
    if (parentStack.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(parentStack.peek());
  }

  @Override
  public void visit(ASTNode node) {
    parentStack.push(node);
  }

  @Override
  public void endVisit(ASTNode node) {
    parentStack.pop();
  }

}
