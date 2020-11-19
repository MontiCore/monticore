/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor.inheritance.delegator;

import mc.feature.visitor.inheritance.a._ast.ASTXA;
import mc.feature.visitor.inheritance.b._ast.ASTXB;
import mc.feature.visitor.inheritance.b._visitor.BTraverser;
import mc.feature.visitor.inheritance.c._ast.ASTXC;
import mc.feature.visitor.inheritance.c._ast.ASTYC;
import mc.feature.visitor.inheritance.c._visitor.CHandler;
import mc.feature.visitor.inheritance.c._visitor.CTraverser;

/**
 * Simple Visitor for C that keeps track of the actual methods called using the
 * StringBuilder given in the constructor.
 *
 */
public class SimpleCHandler implements CHandler {
  final private StringBuilder run;
  
  private CTraverser traverser;
  
  public SimpleCHandler(StringBuilder run) {
    this.run = run;
  }
  
  @Override
  public CTraverser getTraverser() {
    return this.traverser;
  }
  
  @Override
  public void setTraverser(CTraverser traverser) {
    this.traverser = traverser;
  }
  
  @Override
  public void handle(ASTXC node) {
    run.append("SimpleCVisitor.hXC");
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
  }
  
  @Override
  public void traverse(ASTXC node) {
    run.append("SimpleCVisitor.tXC");
  }
  
  @Override
  public void handle(ASTYC node) {
    run.append("SimpleCVisitor.hYC");
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
  }
  
  @Override
  public void traverse(ASTYC node) {
    run.append("SimpleCVisitor.tYC");
    if (null != node.getYB()) {
      node.getYB().accept(getTraverser());
    }
  }
  
}
