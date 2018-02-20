/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor;

import mc.feature.visitor.sub._ast.ASTE;
import mc.feature.visitor.sub._visitor.SubVisitor;

public class SubConcreteVisitor implements SubVisitor {
  
  private boolean visited = false;

  /**
   * @see mc.feature.visitor.sub._visitor.SubVisitor#visit(mc.feature.visitor.sub._ast.ASTE)
   */
  @Override
  public void visit(ASTE node) {
    visited = true;
  }
  
  public boolean hasVisited() {
    return visited;
  }
  
}
