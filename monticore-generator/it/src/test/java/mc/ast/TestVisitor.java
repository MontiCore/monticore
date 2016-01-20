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

package mc.ast;

import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._ast.ASTFeatureDSLNode;
import mc.feature.featuredsl._ast.ASTState;
import mc.feature.featuredsl._visitor.FeatureDSLVisitor;

import org.junit.Assert;

public class TestVisitor implements FeatureDSLVisitor {
  
  @Override
  public void traverse(ASTAutomaton a) {
    // Don't visit children
  }
  
  public void visit(ASTState a) {
    Assert.fail("Should be ignored by overriding the traverse method of automaton");
  }
  
  public void run(ASTFeatureDSLNode ast) {
    ast.accept(getRealThis());
  }
  
}
