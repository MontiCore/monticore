/* (c) https://github.com/MontiCore/monticore */

package mc.examples.coord.cartesian.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import mc.examples.cartesian.coordcartesian._ast.ASTCoordcartesianNode;
import mc.examples.cartesian.coordcartesian._ast.ASTCoordinate;
import mc.examples.cartesian.coordcartesian._visitor.CoordcartesianVisitor;
import mc.examples.polar.coordpolar._ast.ASTCoordpolarNode;

/**
 * This class is responsible for pretty-printing cartesian coordinates The
 * Visitor pattern traverses a tree in depth first, the visit and
 * ownVisit-methods are called when a node is traversed, the endVisit methods
 * are called when the whole subtree of a node has been traversed. The
 * ownVisit-Methods stop the automatic traversal order and allow to explictly
 * visit subtrees by calling getVisitor().startVisit(ASTNode)
 */
public class CartesianPrettyPrinterConcreteVisitor implements CoordcartesianVisitor {
  
  // printer to use
  private IndentPrinter p;
  
  /**
   * Setup this PrettyPrinter for cartesian coordinates
   * 
   * @param printer printer to use
   */
  public CartesianPrettyPrinterConcreteVisitor(IndentPrinter printer) {
    this.p = printer;
  }
  
  /**
   * Prints the cartesian coordinates
   * 
   * @param a coordinate
   */
  @Override
  public void visit(ASTCoordinate a) {
    p.print("(" + a.getX() + "," + a.getY() + ") ");
  }
  
  public void print(ASTCoordcartesianNode ast) {
    ast.accept(getRealThis());
  }
 
}
