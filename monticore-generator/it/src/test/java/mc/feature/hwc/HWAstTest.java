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

package mc.feature.hwc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl._ast.ASTStatechart;
import mc.feature.hwc.statechartdsl._ast.ASTTransition;
import mc.feature.hwc.statechartdsl._ast.StatechartDSLNodeFactory;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class HWAstTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testHWAstNodeClass() {
    ASTStatechart a = StatechartDSLNodeFactory.createASTStatechart();
    a.setName("a");
    assertEquals("My statechart is a", a.toString());
  }
  
  @Test
  public void testHWInterfaceAstBaseNode() {
    ASTStatechart a = StatechartDSLNodeFactory.createASTStatechart();
    assertEquals("ASTStatechart", a.foo());
    
    ASTState b = StatechartDSLNodeFactory.createASTState();
    assertEquals("ASTState", b.foo());
  }
  
  @Test
  public void testHWAstNodeFactory() {
    // Call the method of the HW node factory
    ASTStatechart a = StatechartDSLNodeFactory.createASTStatechart();
    assertEquals("default", a.getName());
    
    // Call the method of the generated node factory
    ASTTransition b = StatechartDSLNodeFactory.createASTTransition();
    assertNull(b.getFrom());
  }
  
}
