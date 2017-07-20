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

package mc.feature.visitor.inheritance;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.monticore.ast.ASTNode;
import mc.GeneratorIntegrationsTest;
import mc.feature.visitor.inheritance.a._ast.ASTXA;
import mc.feature.visitor.inheritance.b._ast.ASTXB;
import mc.feature.visitor.inheritance.c._ast.ASTXC;
import mc.feature.visitor.inheritance.c._visitor.CInheritanceVisitor;
import mc.feature.visitor.inheritance.c._visitor.CVisitor;

/**
 * Tests that for grammar C extends B extends A the CVisitor also visits rules
 * from B and A. Furthermore, we test that rules extending rules from a super
 * grammar are visited in both types, the sub and the super type.
 * 
 * @author Robert Heim
 */
public class VisitorTest extends GeneratorIntegrationsTest {
  @Test
  public void testSimple() {
    SimpleVisitor v = new SimpleVisitor();
    v.handle(ASTXA.getBuilder().build());
    assertEquals("A", v.getRun());
    v.clear();
    v.handle(ASTXB.getBuilder().build());
    assertEquals("B", v.getRun());
    v.clear();
    v.handle(ASTXC.getBuilder().build());
    assertEquals("C", v.getRun());
    v.clear();
  }
  
  @Test
  public void testInheritance() {
    InheritanceVisitor v = new InheritanceVisitor();
    v.handle(ASTXA.getBuilder().build());
    assertEquals("_AA_", v.getRun());
    v.clear();
    v.handle(ASTXB.getBuilder().build());
    assertEquals("_ABBA_", v.getRun());
    v.clear();
    v.handle(ASTXC.getBuilder().build());
    assertEquals("_ABCCBA_", v.getRun());
    v.clear();
  }

  public static class SimpleVisitor implements CVisitor {
    StringBuilder run = new StringBuilder();
    
    public void clear() {
      run.setLength(0);
    }
    
    public String getRun() {
      return run.toString();
    }
    
    @Override
    public void visit(ASTXA node) {
      run.append("A");
    }
    
    @Override
    public void visit(ASTXB node) {
      run.append("B");
    }
    
    @Override
    public void visit(ASTXC node) {
      run.append("C");
    }
    
  }
  
  public static class InheritanceVisitor implements CInheritanceVisitor {
    StringBuilder run = new StringBuilder();
    
    public void clear() {
      run.setLength(0);
    }
    
    public String getRun() {
      return run.toString();
    }
    
    @Override
    public void visit(ASTXA node) {
      run.append("A");
    }
    
    @Override
    public void visit(ASTXB node) {
      run.append("B");
    }
    
    @Override
    public void visit(ASTXC node) {
      run.append("C");
    }
    
    @Override
    public void visit(ASTNode node) {
      run.append("_");
    }
    
    @Override
    public void endVisit(ASTXA node) {
      run.append("A");
    }
    
    @Override
    public void endVisit(ASTXB node) {
      run.append("B");
    }
    
    @Override
    public void endVisit(ASTXC node) {
      run.append("C");
    }
    
    @Override
    public void endVisit(ASTNode node) {
      run.append("_");
    }
  }
}
