/* (c) https://github.com/MontiCore/monticore */

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
    double d = Math.sqrt(a.getX()
        * a.getX()
        + a.getY()
        * a.getY());
    
    // angle = atan2(y,x)
    double angle = Math.atan2(a.getY(), a.getX());
    
    result.getCoordinateList().add(CoordpolarNodeFactory.createASTCoordinate(d, angle));
  }
  
  public void transform(ASTCoordinateFile ast) {
    ast.accept(getRealThis());
  }
}
