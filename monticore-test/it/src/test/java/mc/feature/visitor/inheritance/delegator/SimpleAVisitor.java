/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor.inheritance.delegator;

import mc.feature.visitor.inheritance.a._ast.ASTXA;
import mc.feature.visitor.inheritance.a._visitor.AVisitor2;

/**
 * Simple Visitor for A that keeps track of the actual methods called using the
 * StringBuilder given in the constructor: tA = traverse A; hA = handle A; vA =
 * visit A; eA = endVisit A.
 *
 */
public class SimpleAVisitor implements AVisitor2 {
  final private StringBuilder run;
  
  public SimpleAVisitor(StringBuilder run) {
    this.run = run;
  }
  
  @Override
  public void visit(ASTXA node) {
    run.append("SimpleAVisitor.vXA");
  }
  
  @Override
  public void endVisit(ASTXA node) {
    run.append("SimpleAVisitor.eXA");
  }
}
