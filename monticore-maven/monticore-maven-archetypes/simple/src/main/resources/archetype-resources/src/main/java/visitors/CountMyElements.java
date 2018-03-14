/* (c) https://github.com/MontiCore/monticore */

package ${package}.visitors;

import ${package}.mydsl._ast.ASTMyElement;
import ${package}.mydsl._visitor.MyDSLVisitor;

/**
 * Counts the elements of a model.
 */
public class CountMyElements implements MyDSLVisitor {
  
  private int count = 0;
  
  @Override
  public void visit(ASTMyElement node) {
    count++;
  }
  
  public int getCount() {
    return count;
  }
}
