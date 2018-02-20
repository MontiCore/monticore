/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor.inheritance.delegator;

import mc.feature.visitor.inheritance.a._ast.ASTXA;
import mc.feature.visitor.inheritance.a._visitor.AVisitor;

/**
 * Visitor that does not implement the realThis pattern and hence cannot be
 * composed.
 *
 * @author Robert Heim
 */
public class MissingRealThisAVisitor implements AVisitor {
  final private StringBuilder run;
  
  public MissingRealThisAVisitor(StringBuilder run) {
    this.run = run;
  }
  
  @Override
  public void handle(ASTXA node) {
    run.append("MissingRealThisAVisitor.hA");
  }
  
  @Override
  public void traverse(ASTXA node) {
    run.append("MissingRealThisAVisitor.tA");
  }
  
  @Override
  public void visit(ASTXA node) {
    run.append("MissingRealThisAVisitor.vA");
  }
  
  @Override
  public void endVisit(ASTXA node) {
    run.append("MissingRealThisAVisitor.eA");
  }
  
}
