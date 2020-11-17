/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor.inheritance.delegator;

import mc.feature.visitor.inheritance.a._ast.ASTXA;
import mc.feature.visitor.inheritance.a._visitor.ATraverser;
import mc.feature.visitor.inheritance.b._ast.ASTXB;
import mc.feature.visitor.inheritance.b._ast.ASTYB;
import mc.feature.visitor.inheritance.b._ast.ASTZB;
import mc.feature.visitor.inheritance.b._visitor.BHandler;
import mc.feature.visitor.inheritance.b._visitor.BTraverser;

/**
 * Simple Visitor for B that keeps track of the actual methods called using the
 * StringBuilder given in the constructor.
 *
 */
public class SimpleBHandler implements BHandler {
  final private StringBuilder run;
  
  private BTraverser traverser;
  
  public SimpleBHandler(StringBuilder run) {
    this.run = run;
  }
  
  @Override
  public BTraverser getTraverser() {
    return this.traverser;
  }
  
  @Override
  public void setTraverser(BTraverser traverser) {
    this.traverser = traverser;
  }
  
  @Override
  public void handle(ASTXB node) {
    run.append("SimpleBVisitor.hXB");
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
  }
  
  @Override
  public void traverse(ASTXB node) {
    run.append("SimpleBVisitor.tXB");
  }
  
  @Override
  public void handle(ASTYB node) {
    run.append("SimpleBVisitor.hYB");
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
  }
  
  @Override
  public void traverse(ASTYB node) {
    run.append("SimpleBVisitor.tYB");
  }
  
  @Override
  public void handle(ASTZB node) {
    run.append("SimpleBVisitor.hZB");
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
  }
  
  @Override
  public void traverse(ASTZB node) {
    run.append("SimpleBVisitor.tZB");
    if (null != node.getXA()) {
      node.getXA().accept(getTraverser());
    }
    if (null != node.getYB()) {
      node.getYB().accept(getTraverser());
    }
  }
  
}
