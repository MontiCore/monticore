/* (c) https://github.com/MontiCore/monticore */

package mc.examples.coord.polar.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import mc.examples.polar.coordpolar._ast.ASTCoordinate;
import mc.examples.polar.coordpolar._ast.ASTCoordpolarNode;
import mc.examples.polar.coordpolar._visitor.CoordpolarVisitor;

/**
 * This class is responsible for pretty-printing polar coordinates The Visitor
 * pattern traverses a tree in depth first, the visit and ownVisit-methods are
 * called when a node is traversed, the endVisit methods are called when the
 * whole subtree of a node has been traversed. The ownVisit-Methods stop the
 * automatic traversal order and allow to explictly visit subtrees by calling
 * getVisitor().startVisit(ASTNode)
 */
public class PolarPrettyPrinterConcreteVisitor implements CoordpolarVisitor {
  
  // printer to use
  private IndentPrinter p;
  
  /**
   * Setup this PrettyPrinter for polar coordinates
   * 
   * @param printer printer to use
   */
  public PolarPrettyPrinterConcreteVisitor(IndentPrinter printer) {
    this.p = printer;
  }
  
  /**
   * Prints the polar coordinates
   * 
   * @param a coordinate
   */
  public void visit(ASTCoordinate a) {
    p.print("[" + a.getD() + ";" + a.getPhi() + "] ");
  }
  
  public void print(ASTCoordpolarNode ast) {
    ast.accept(getRealThis());
  }
  
}
