/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor.inheritance.delegator;

import mc.feature.visitor.inheritance.a._ast.ASTXA;
import mc.feature.visitor.inheritance.a._visitor.AHandler;
import mc.feature.visitor.inheritance.a._visitor.ATraverser;

/**
 * Simple Visitor for A that keeps track of the actual methods called using the
 * StringBuilder given in the constructor: tA = traverse A; hA = handle A; vA =
 * visit A; eA = endVisit A.
 *
 */
public class SimpleAHandler implements AHandler {
  final private StringBuilder run;
  
  ATraverser traverser;
 
  public SimpleAHandler(StringBuilder run) {
    this.run = run;
  }
  
  @Override
  public ATraverser getTraverser() {
    return this.traverser;
  }
  
  @Override
  public void setTraverser(ATraverser traverser) {
    this.traverser = traverser;
  }
  
  @Override
  public void handle(ASTXA node) {
    run.append("SimpleAVisitor.hXA");
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
  }
  
  @Override
  public void traverse(ASTXA node) {
    run.append("SimpleAVisitor.tXA");
  }
  
}
