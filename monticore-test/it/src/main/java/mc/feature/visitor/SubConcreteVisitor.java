/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor;

import mc.feature.visitor.sub._ast.ASTE;
import mc.feature.visitor.sub._visitor.SubVisitor2;

public class SubConcreteVisitor implements SubVisitor2 {
  
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
