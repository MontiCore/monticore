/*
 * ******************************************************************************
 * MontiCore Language Workbench
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
