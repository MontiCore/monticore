/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor.inheritance.delegator;

import mc.feature.visitor.inheritance.c._ast.ASTXC;
import mc.feature.visitor.inheritance.c._ast.ASTYC;
import mc.feature.visitor.inheritance.c._visitor.CVisitor2;

/**
 * Simple Visitor for C that keeps track of the actual methods called using the
 * StringBuilder given in the constructor.
 *
 */
public class SimpleCVisitor implements CVisitor2 {
  final private StringBuilder run;
  
  public SimpleCVisitor(StringBuilder run) {
    this.run = run;
  }
  
  @Override
  public void visit(ASTXC node) {
    run.append("SimpleCVisitor.vXC");
  }
  
  @Override
  public void endVisit(ASTXC node) {
    run.append("SimpleCVisitor.eXC");
  }
  
  @Override
  public void visit(ASTYC node) {
    run.append("SimpleCVisitor.vYC");
  }
  
  @Override
  public void endVisit(ASTYC node) {
    run.append("SimpleCVisitor.eYC");
  }
}
