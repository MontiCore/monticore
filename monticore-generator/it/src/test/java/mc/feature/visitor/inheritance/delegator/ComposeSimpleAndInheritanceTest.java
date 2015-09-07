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

package mc.feature.visitor.inheritance.delegator;

import static org.junit.Assert.assertEquals;
import mc.feature.visitor.inheritance.a._ast.ASTXA;
import mc.feature.visitor.inheritance.a._visitor.AVisitor;
import mc.feature.visitor.inheritance.b._ast.ASTXB;
import mc.feature.visitor.inheritance.b._visitor.BVisitor;
import mc.feature.visitor.inheritance.c._visitor.CDelegatorVisitor;
import mc.feature.visitor.inheritance.c._visitor.CVisitor;
import mc.feature.visitor.inheritance.c._visitor.CommonCDelegatorVisitor;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests composing of simple visitors with inheritance visitors.
 *
 * @author Robert Heim
 */
public class ComposeSimpleAndInheritanceTest extends CommonVisitorTest {
  
  // the composer
  private CDelegatorVisitor v = new CommonCDelegatorVisitor();
  
  // the visitors about to compose
  private AVisitor aVis = new SimpleAVisitor(run);
  
  private BVisitor bVis = new InheritanceBVisitor(run);
  
  private CVisitor cVis = new InheritanceCVisitor(run);
  
  private boolean setUpDone = false;
  
  @Before
  public void setUp() {
    run.setLength(0);
    expectedRun.setLength(0);
    if (!setUpDone) {
      setUpDone = true;
      v.set_mc_feature_visitor_inheritance_a__visitor_AVisitor(aVis);
      v.set_mc_feature_visitor_inheritance_b__visitor_BVisitor(bVis);
      v.set_mc_feature_visitor_inheritance_c__visitor_CVisitor(cVis);
    }
  }
  
  @Test
  public void testSimpleWithInhertiance() {
    v.handle(ASTXA.getBuilder().build());
    assertEquals("SimpleAVisitor.hXASimpleAVisitor.vXASimpleAVisitor.tXASimpleAVisitor.eXA",
        run.toString());
  }
  
  @Test
  public void testSimpleWithInhertiance2() {
    StringBuilder expectedRun = new StringBuilder();
    
    // handle from inheritance b visitor
    expectedRun.append("InheritanceBVisitor.hXB");
    
    // each visitor is called to visit astnode
    expectedRun.append("InheritanceCVisitor.vASTNode");
    expectedRun.append("InheritanceBVisitor.vASTNode");
    
    // visit node type in each of its super types using the corresponding visitor of the delegator
    expectedRun.append("SimpleAVisitor.vXA");
    expectedRun.append("InheritanceBVisitor.vXB");
    
    expectedRun.append("InheritanceBVisitor.tXB");
    
    expectedRun.append("InheritanceBVisitor.eXB");
    expectedRun.append("SimpleAVisitor.eXA");
    
    expectedRun.append("InheritanceBVisitor.eASTNode");
    expectedRun.append("InheritanceCVisitor.eASTNode");
    
    expectedRun.append("");
    
    // actual
    v.handle(ASTXB.getBuilder().build());
    
    assertEquals(expectedRun.toString(), run.toString());
  }
}
