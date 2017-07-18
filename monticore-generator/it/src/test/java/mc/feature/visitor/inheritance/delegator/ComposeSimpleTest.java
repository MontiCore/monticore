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

import org.junit.Before;
import org.junit.Test;

import mc.feature.visitor.inheritance.a._ast.ASTXA;
import mc.feature.visitor.inheritance.a._visitor.AVisitor;
import mc.feature.visitor.inheritance.b._ast.ASTXB;
import mc.feature.visitor.inheritance.b._ast.ASTYB;
import mc.feature.visitor.inheritance.b._ast.ASTZB;
import mc.feature.visitor.inheritance.b._visitor.BVisitor;
import mc.feature.visitor.inheritance.c._ast.ASTXC;
import mc.feature.visitor.inheritance.c._ast.ASTYC;
import mc.feature.visitor.inheritance.c._visitor.CVisitor;
import mc.feature.visitor.inheritance.c._visitor.CDelegatorVisitor;

/**
 * Tests composing simple visiors using the delegator visitor. The
 * SimpleXVisitors append "[NameOfVisitor].[h|t|v|e][ASTNode]" when a method of
 * them is called.
 *
 * @author Robert Heim
 */
public class ComposeSimpleTest extends CommonVisitorTest {
  
  // the composer
  private CDelegatorVisitor v = new CDelegatorVisitor();
  
  // the simple visitors about to compose
  private AVisitor aVis = new SimpleAVisitor(run);
  
  private BVisitor bVis = new SimpleBVisitor(run);
  
  private CVisitor cVis = new SimpleCVisitor(run);
  
  private boolean setUpDone = false;
  
  @Before
  public void setUp() {
    run.setLength(0);
    expectedRun.setLength(0);
    if (!setUpDone) {
      setUpDone = true;
      v.setAVisitor(aVis);
      v.setBVisitor(bVis);
      v.setCVisitor(cVis);
    }
  }
  
  @Test
  public void testSimpleComposed() {
    v.handle(ASTXA.getBuilder().build());
    assertEquals("SimpleAVisitor.hXASimpleAVisitor.vXASimpleAVisitor.tXASimpleAVisitor.eXA",
        run.toString());
  }
  
  @Test
  public void testSimpleComposed2() {
    v.handle(ASTXB.getBuilder().build());
    assertEquals("SimpleBVisitor.hXBSimpleBVisitor.vXBSimpleBVisitor.tXBSimpleBVisitor.eXB",
        run.toString());
  }
  
  @Test
  public void testSimpleComposed3() {
    v.handle(ASTXC.getBuilder().build());
    assertEquals("SimpleCVisitor.hXCSimpleCVisitor.vXCSimpleCVisitor.tXCSimpleCVisitor.eXC",
        run.toString());
  }
  
  @Test
  public void testSimpleComposed4() {
    ASTYB yb = ASTYB.getBuilder().build();
    ASTYC yc = ASTYC.getBuilder()
        .yB(yb)
        .build();
    v.handle(yc);
    // first part of yc handling
    expectedRun.append("SimpleCVisitor.hYCSimpleCVisitor.vYCSimpleCVisitor.tYC");
    // handle yb
    expectedRun.append("SimpleBVisitor.hYBSimpleBVisitor.vYBSimpleBVisitor.tYBSimpleBVisitor.eYB");
    // rest of yc
    expectedRun.append("SimpleCVisitor.eYC");
    assertEquals(expectedRun.toString(), run.toString());
  }
  
  @Test
  public void testSimpleComposed5() {
    ASTYB yb = ASTYB.getBuilder().build();
    ASTXA xa = ASTXA.getBuilder().build();
    ASTZB zb = ASTZB.getBuilder()
        .xA(xa)
        .yB(yb)
        .build();
    v.handle(zb);
    
    // first part of zb handling
    expectedRun.append("SimpleBVisitor.hZBSimpleBVisitor.vZBSimpleBVisitor.tZB");
    // handle child xa
    expectedRun.append("SimpleAVisitor.hXASimpleAVisitor.vXASimpleAVisitor.tXASimpleAVisitor.eXA");
    // handle child yb
    expectedRun.append("SimpleBVisitor.hYBSimpleBVisitor.vYBSimpleBVisitor.tYBSimpleBVisitor.eYB");
    // rest of zb
    expectedRun.append("SimpleBVisitor.eZB");
    assertEquals(expectedRun.toString(), run.toString());
  }
  
  /**
   * Composing only inheritance visitors
   */
  @Test
  public void testDelegtor3() {
    // TODO RH
  }
  
  /**
   * Composing simple with delegator
   */
  @Test
  public void testDelegtor4() {
    // TODO RH
  }
  
  /**
   * Composing inheritance with delegator
   */
  @Test
  public void testDelegtor5() {
    // TODO RH
  }
  
  /**
   * Composing simple, inheritance and delegator
   */
  @Test
  public void testDelegtor6() {
    // TODO RH
  }
  
  /**
   * Composing only delegators
   */
  @Test
  public void testDelegtor7() {
    // TODO RH
  }
  
  // .
  
  public static class MyCVisitor implements
      CVisitor {
    final private StringBuilder run;
    
    public MyCVisitor(StringBuilder run) {
      this.run = run;
    }
    
    @Override
    public void visit(ASTXA node) {
      run.append("MyCVisitor.A");
    }
    
    @Override
    public void visit(ASTXB node) {
      run.append("MyCVisitor.B");
    }
    
    @Override
    public void visit(ASTXC node) {
      run.append("MyCVisitor.C");
    }
    
    // realthis pattern
    private CVisitor realThis;
    
    @Override
    public void setRealThis(CVisitor realThis) {
      this.realThis = realThis;
    }
    
    @Override
    public CVisitor getRealThis() {
      return realThis;
    }
  }
  
}
