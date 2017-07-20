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

package mc.feature.ast;

import mc.GeneratorIntegrationsTest;
import mc.feature.hwc.statechartdsl._ast.StatechartDSLNodeFactory;
import mc.feature.hwc.statechartdsl._ast.ASTState;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GeneratedAstClassesTest extends GeneratorIntegrationsTest {
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  @Test
  public void testErrorsIfNullByAstNodes() {
    ASTState b = StatechartDSLNodeFactory.createASTState();
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("must not be null.");
    b.setTransitions(null);
  }
  
  @Test
  public void testErrorsIfNullByAstNodeFactories() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("must not be null.");
    StatechartDSLNodeFactory.createASTCode(null);
  }
  
}
