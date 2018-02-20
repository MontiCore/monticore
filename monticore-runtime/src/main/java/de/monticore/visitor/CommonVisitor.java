/* (c) https://github.com/MontiCore/monticore */

package de.monticore.visitor;

import de.monticore.ast.ASTNode;

/**
 * Visitor that ignores the actual node type and calls the same visit Method for
 * all nodes. Note that its structure is the same as language specific visitors,
 * but its not compatible with them in terms of composition.
 * 
 * Example of implementing and running the common visitor on a given AST ast:
 * <pre>
 *  public class NodeCounter implements CommonVisit {
 *   int nodeCount = 0;
 *   {@literal @}Override
 *   public void visit(ASTNode n) {
 *     nodeCount++;
 *   }
 *   public int getNodeCount() {
 *     return nodeCount;
 *   }
 * }
 * NodeCounter nc = new NodeCounter();
 * nc.handle(ast);
 * System.out.println(nc.getNodeCount());
 * </pre>
 * @author Robert Heim
 */
public interface CommonVisitor {
  
  default public void handle(ASTNode node) {
    visit(node);
    traverse(node);
    endVisit(node);
  }
  
  default public void traverse(ASTNode node) {
    for (ASTNode child : node.get_Children()) {
      handle(child);
    }
  }
  
  default public void visit(ASTNode node) {
  }
  
  default public void endVisit(ASTNode node) {
  }
}
