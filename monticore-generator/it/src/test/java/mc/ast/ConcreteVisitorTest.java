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

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._ast.FeatureDSLNodeFactory;

import org.junit.Test;

public class ConcreteVisitorTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testA() {
    
    ASTAutomaton a = FeatureDSLNodeFactory.createASTAutomaton();
    a.getStates().add(FeatureDSLNodeFactory.createASTState());
    a.getStates().add(FeatureDSLNodeFactory.createASTState());
    a.getStates().add(FeatureDSLNodeFactory.createASTState());
    
    TestVisitor v = new TestVisitor();
    v.run(a);
  }
}
