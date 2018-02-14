/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
