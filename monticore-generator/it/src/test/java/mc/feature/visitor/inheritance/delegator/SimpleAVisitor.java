/* (c) https://github.com/MontiCore/monticore */

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
