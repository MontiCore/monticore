/* (c) https://github.com/MontiCore/monticore */

package mc.examples.coord.transform;

import mc.examples.cartesian.coordcartesian._ast.ASTCoordinate;
import mc.examples.cartesian.coordcartesian._visitor.CoordcartesianVisitor2;

/**
 * This Visitors traverses all coordinates and mirrors them
 */
public class Mirror implements CoordcartesianVisitor2 {
  
  @Override
  public void visit(ASTCoordinate a) {
    int y = a.getY();
    a.setY(a.getX());
    a.setX(y);
  }
  
}
