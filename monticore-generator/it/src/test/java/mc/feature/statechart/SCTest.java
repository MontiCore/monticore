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

package mc.feature.statechart;

import static org.junit.Assert.assertEquals;
import mc.GeneratorIntegrationsTest;
import mc.feature.hwc.statechartdsl._ast.StatechartDSLNodeFactory;
import mc.feature.statechart.statechartdsl._ast.ASTState;

import org.junit.Test;

public class SCTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testToString() {
    ASTState a = StatechartDSLNodeFactory.createASTState();
    a.setName("a");
    assertEquals("a", a.toString());
  }
}
