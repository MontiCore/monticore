/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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
 * Sets the enclosing scope of all AST nodes, if not already set.
 * Uses a stack-based approach in order to access information of
 * parent nodes
 *
 * @author Pedram Mir Seyed Nazari
 */
public class EnclosingScopeOfNodesInitializer implements CommonVisitor {

  private final Deque<Scope> scopeStack = new ArrayDeque<>();

  @Override
  public void visit(ASTNode node) {
    if (!node.getEnclosingScope().isPresent() && !scopeStack.isEmpty()) {
      node.setEnclosingScope(scopeStack.peekLast());
    }

    if (node.getSymbol().isPresent()) {
      if(node.getSymbol().get() instanceof ScopeSpanningSymbol) {
        scopeStack.addLast(((ScopeSpanningSymbol) node.getSymbol().get()).getSpannedScope());
        return;
      }
    }

    if (node.getEnclosingScope().isPresent()) {
      scopeStack.addLast(node.getEnclosingScope().get());
    }
  }

  @Override
  public void endVisit(ASTNode node) {
    if (!scopeStack.isEmpty()) {
      scopeStack.pollLast();
    }

    // TODO PN include the spanned symbol in the check below
//    if (node.getEnclosingScope().isPresent() && scopeStack.isEmpty()) {
//      // TODO PN add error code
//      Log.error("Scope should contain at least the enclosing scope of the node.");
//    }
//    else if (!node.getEnclosingScope().isPresent() && !scopeStack.isEmpty()) {
//      // TODO PN add error code
//      Log.error("The node does not have an enclosing scope. So, the stack is expected to be empty.");
//    }
//
//    if (node.getEnclosingScope().isPresent() && !scopeStack.isEmpty()) {
//      if (node.getEnclosingScope().get() == scopeStack.peekLast()) {
//        scopeStack.pollLast();
//      }
//      else {
//        // TODO PN add error code
//        Log.error("The enclosing scope of the node and the current scope of the stack should be the same");
//      }
//    }
  }

}
