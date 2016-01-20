/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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

package mc.examples.coord.transform;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import mc.examples.cartesian.coordcartesian._ast.ASTCoordinateFile;
import mc.examples.cartesian.coordcartesian._visitor.CoordcartesianVisitor;
import mc.examples.polar.coordpolar._ast.CoordpolarNodeFactory;

public class CartesianToPolar implements CoordcartesianVisitor {
  
  /**
   * result of a transformation
   */
  protected mc.examples.polar.coordpolar._ast.ASTCoordinateFile result;
  
  /**
   * Returns the result of a transformation
   * 
   * @return Returns the result.
   */
  public mc.examples.polar.coordpolar._ast.ASTCoordinateFile getResult() {
    return result;
  }
  
  /**
   * Type change only: mc.examples.coord.cartesian.ASTCoordinateFile ->
   * mc.examples.coord.polar.ASTCoordinateFile
   * 
   * @param a CoordinateFile to transform
   */
  @Override
  public void visit(mc.examples.cartesian.coordcartesian._ast.ASTCoordinateFile a) {
    result = CoordpolarNodeFactory.createASTCoordinateFile();
  }
  
  /**
   * Transforms carthesian to polar coordinates
   * 
   * @param a Coordinate to transform
   */
  @Override
  public void visit(mc.examples.cartesian.coordcartesian._ast.ASTCoordinate a) {
    
    DecimalFormat Reals = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.GERMAN));
    
    // d = sqrt(x*x + y*y)
    String d = Reals.format(
        Math.sqrt(Double.parseDouble(a.getX())
            * Double.parseDouble(a.getX())
            + Double.parseDouble(a.getY())
            * Double.parseDouble(a.getY())));
    
    // angle = atan2(y,x)
    String angle = Reals.format(
        Math.atan2(Double.parseDouble(a.getY()),
            Double.parseDouble(a.getX())));
    
    result.getCoordinates().add(CoordpolarNodeFactory.createASTCoordinate(d, angle));
  }
  
  public void transform(ASTCoordinateFile ast) {
    ast.accept(getRealThis());
  }
}
