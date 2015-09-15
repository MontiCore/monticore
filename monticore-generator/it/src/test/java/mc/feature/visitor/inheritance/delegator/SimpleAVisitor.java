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

import mc.feature.visitor.inheritance.a._ast.ASTXA;
import mc.feature.visitor.inheritance.a._visitor.AVisitor;

/**
 * Simple Visitor for A that keeps track of the actual methods called using the
 * StringBuilder given in the constructor: tA = traverse A; hA = handle A; vA =
 * visit A; eA = endVisit A.
 *
 * @author Robert Heim
 */
public class SimpleAVisitor implements AVisitor {
  final private StringBuilder run;
  
  public SimpleAVisitor(StringBuilder run) {
    this.run = run;
  }
  
  @Override
  public void handle(ASTXA node) {
    run.append("SimpleAVisitor.hXA");
    AVisitor.super.handle(node);
  }
  
  @Override
  public void traverse(ASTXA node) {
    run.append("SimpleAVisitor.tXA");
    AVisitor.super.traverse(node);
  }
  
  @Override
  public void visit(ASTXA node) {
    run.append("SimpleAVisitor.vXA");
  }
  
  @Override
  public void endVisit(ASTXA node) {
    run.append("SimpleAVisitor.eXA");
  }
  
  // realthis pattern
  private AVisitor realThis;
  
  @Override
  public void setRealThis(AVisitor realThis) {
    this.realThis = realThis;
  }
  
  @Override
  public AVisitor getRealThis() {
    return realThis;
  }
}
