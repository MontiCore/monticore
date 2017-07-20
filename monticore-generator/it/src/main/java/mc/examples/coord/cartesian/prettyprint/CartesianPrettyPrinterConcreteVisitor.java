/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
