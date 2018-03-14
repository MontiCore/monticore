/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import java.util.ArrayDeque;
import java.util.Deque;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.CommonVisitor;

/**
 * Sets the enclosing scope of all AST nodes, if not already set. Uses a stack-based approach in
 * order to access information of parent nodes
 *
 * @author Pedram Mir Seyed Nazari
 */
public class EnclosingScopeOfNodesInitializer implements CommonVisitor {
  
  private final Deque<Scope> scopeStack = new ArrayDeque<>();
  
  @Override
  public void visit(ASTNode node) {
    if (!node.getEnclosingScopeOpt().isPresent() && !scopeStack.isEmpty()) {
      node.setEnclosingScope(scopeStack.peekLast());
    }
    
    if (node.getSymbolOpt().isPresent()) {
      if (node.getSymbolOpt().get() instanceof ScopeSpanningSymbol) {
        scopeStack.addLast(((ScopeSpanningSymbol) node.getSymbolOpt().get()).getSpannedScope());
        return;
      }
    }
    
    if (node.getSpannedScopeOpt().isPresent()) {
      scopeStack.addLast(node.getSpannedScopeOpt().get());
      return;
    }
    
    if (node.getEnclosingScopeOpt().isPresent()) {
      scopeStack.addLast(node.getEnclosingScopeOpt().get());
    }
  }
  
  @Override
  public void endVisit(ASTNode node) {
    if (!scopeStack.isEmpty()) {
      scopeStack.pollLast();
    }
  }
  
}
