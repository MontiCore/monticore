/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor.inheritance.delegator;

import mc.feature.visitor.inheritance.b._ast.ASTXB;
import mc.feature.visitor.inheritance.b._ast.ASTYB;
import mc.feature.visitor.inheritance.b._ast.ASTZB;
import mc.feature.visitor.inheritance.b._visitor.BVisitor2;

/**
 * Simple Visitor for B that keeps track of the actual methods called using the
 * StringBuilder given in the constructor.
 *
 */
public class SimpleBVisitor implements BVisitor2 {
  final private StringBuilder run;
  
  public SimpleBVisitor(StringBuilder run) {
    this.run = run;
  }
  
  @Override
  public void visit(ASTXB node) {
    run.append("SimpleBVisitor.vXB");
  }
  
  @Override
  public void endVisit(ASTXB node) {
    run.append("SimpleBVisitor.eXB");
  }
  
  @Override
  public void visit(ASTYB node) {
    run.append("SimpleBVisitor.vYB");
  }
  
  @Override
  public void endVisit(ASTYB node) {
    run.append("SimpleBVisitor.eYB");
  }
  
  @Override
  public void visit(ASTZB node) {
    run.append("SimpleBVisitor.vZB");
  }
  
  @Override
  public void endVisit(ASTZB node) {
    run.append("SimpleBVisitor.eZB");
  }
}
