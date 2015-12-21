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

package mc.feature.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import mc.feature.automaton.automaton._ast.AutomatonNodeFactory;
import mc.feature.delete.deletetest._ast.ASTChild;
import mc.feature.delete.deletetest._ast.ASTParent;
import mc.feature.delete.deletetest._ast.DeleteTestNodeFactory;
import mc.feature.featuredsl._ast.ASTA;
import mc.feature.featuredsl._ast.FeatureDSLNodeFactory;

public class ASTTest {
  
  @Test
  public void testNpeDeepClone() {
    mc.feature.automaton.automaton._ast.ASTAutomaton a = AutomatonNodeFactory.createASTAutomaton();
    assertNull(a.deepClone().getName());
  }
  
  @Test
  public void testGet_ChildNodes1() {
    List<ASTA> aList = new ArrayList<>();
    ASTA a = FeatureDSLNodeFactory.createASTA();
    assertEquals(0, aList.size());
    aList.add(a);
    assertEquals(1, aList.size());
  }
  
  @Test
  public void testGet_ChildNodes2() {
    ASTParent p = DeleteTestNodeFactory.createASTParent();
    ASTChild s = DeleteTestNodeFactory.createASTChild();
    p.get_Children().add(s);
    p.setSon(s);
    assertEquals(1, p.get_Children().size());
    assertTrue(p.get_Children().contains(s));
  }
}
