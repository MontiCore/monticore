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

import java.io.IOException;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl._ast.ASTStatechart;
import mc.feature.hwc.statechartdsl._ast.ASTTransition;
import mc.feature.hwc.statechartdsl._ast.StatechartDSLMill;

public class BuildersTest {
  
  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() throws RecognitionException, IOException {
    Log.getFindings().clear();
  }
  
  @Test
  public void testMyTransitionBuilder() throws IOException {
    ASTTransition transition = StatechartDSLMill.transitionBuilder().from("setByGenBuilder").from("xxxx").to("setByGenBuilder").build();
    assertEquals("xxxxSuf2", transition.getFrom());
  }
  
  @Test
  public void testHWCClassGeneratedBuilder() throws IOException {
    ASTStatechart aut = StatechartDSLMill.statechartBuilder().name("setByGeneratedBuilder").build();
    assertEquals("setByGeneratedBuilder", aut.getName());
  }
  
  @Test
  public void testHWCClassHWCBuilder() throws IOException {
    ASTState state = StatechartDSLMill.stateBuilder().name("x2").r__final(true).name("state1").build();
    assertEquals(state.getName(), "state1Suf1");
  }
  
}
