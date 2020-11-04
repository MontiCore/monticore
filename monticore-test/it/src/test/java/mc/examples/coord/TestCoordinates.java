/* (c) https://github.com/MontiCore/monticore */

package mc.examples.coord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Test;

import de.monticore.prettyprint.IndentPrinter;
import mc.GeneratorIntegrationsTest;
import mc.examples.cartesian.coordcartesian._ast.ASTCoordinateFile;
import mc.examples.cartesian.coordcartesian._parser.CoordcartesianParser;
import mc.examples.coord.cartesian.prettyprint.CartesianPrettyPrinterConcreteVisitor;
import mc.examples.coord.polar.prettyprint.PolarPrettyPrinterConcreteVisitor;
import mc.examples.coord.transform.CartesianToPolar;
import mc.examples.coord.transform.Mirror;
import mc.examples.polar.coordpolar._parser.CoordpolarParser;

public class TestCoordinates extends GeneratorIntegrationsTest {
  
  private static final double DELTA = 1e-5;
  
  @Test
  public void testCoordcartesianParser() throws IOException {
    CoordcartesianParser parser = new CoordcartesianParser();
    Optional<ASTCoordinateFile> astCartesian = parser
        .parseCoordinateFile("src/test/resources/examples/coord/coordinates.cart"); // (2,4)
                                                                      // (5,2)
                                                                      // (1,7)
    assertFalse(parser.hasErrors());
    assertTrue(astCartesian.isPresent());
    
    assertEquals(astCartesian.get().getCoordinateList().size(), 3);
    
    assertEquals(astCartesian.get().getCoordinateList().get(0).getX(), 2);
    assertEquals(astCartesian.get().getCoordinateList().get(0).getY(), 4);
    
    assertEquals(astCartesian.get().getCoordinateList().get(1).getX(), 5);
    assertEquals(astCartesian.get().getCoordinateList().get(1).getY(), 2);
    
    assertEquals(astCartesian.get().getCoordinateList().get(2).getX(), 1);
    assertEquals(astCartesian.get().getCoordinateList().get(2).getY(), 7);
  }
  
  @Test
  public void testCoordpolarParser() throws IOException {
    mc.examples.polar.coordpolar._parser.CoordpolarParser parser = new CoordpolarParser();
    Optional<mc.examples.polar.coordpolar._ast.ASTCoordinateFile> astPolar = parser
        .parseCoordinateFile("src/test/resources/examples/coord/coordinates.polar");
    // [1,0;0,5]
    // [2,5;1,3]
    // [47,11;0,815]
    assertFalse(parser.hasErrors());
    assertTrue(astPolar.isPresent());
    
    assertEquals(astPolar.get().getCoordinateList().size(), 3);
    

    
    assertEquals(astPolar.get().getCoordinateList().get(0).getD(), 1.0, DELTA);
    assertEquals(astPolar.get().getCoordinateList().get(0).getPhi(), 0.5, DELTA);
    
    assertEquals(astPolar.get().getCoordinateList().get(1).getD(), 2.5, DELTA);
    assertEquals(astPolar.get().getCoordinateList().get(1).getPhi(), 1.3, DELTA);
    
    assertEquals(astPolar.get().getCoordinateList().get(2).getD(), 47.11, DELTA);
    assertEquals(astPolar.get().getCoordinateList().get(2).getPhi(), 0.815, DELTA);
  }
  
  @Test
  public void cartesian2Polar() throws IOException {
    CoordcartesianParser parser = new CoordcartesianParser();
    Optional<ASTCoordinateFile> astCartesian = parser
        .parseCoordinateFile("src/test/resources/examples/coord/coordinates.cart");
    assertFalse(parser.hasErrors());
    assertTrue(astCartesian.isPresent());
    
    // Transform cartesian to polar coordinates
    CartesianToPolar transformer = new CartesianToPolar();
    transformer.transform(astCartesian.get());
    
    // Create PrettyPrinter
    IndentPrinter ip = new IndentPrinter();
    PolarPrettyPrinterConcreteVisitor p = new PolarPrettyPrinterConcreteVisitor(ip);
    
    // Pretty-print the cartesian coordinates
    p.print(transformer.getResult());
    
    mc.examples.polar.coordpolar._parser.CoordpolarParser polarParser = new CoordpolarParser();
    Optional<mc.examples.polar.coordpolar._ast.ASTCoordinateFile> astPolar = polarParser
        .parseCoordinateFile(new StringReader(ip.getContent()));
    assertFalse(polarParser.hasErrors());
    assertTrue(astPolar.isPresent());
    
    assertEquals(astPolar.get().getCoordinateList().size(), 3);
    
    assertEquals(astPolar.get().getCoordinateList().get(0).getD(), 4.47213, DELTA);
    assertEquals(astPolar.get().getCoordinateList().get(0).getPhi(), 1.10714, DELTA);
    
    assertEquals(astPolar.get().getCoordinateList().get(1).getD(), 5.38516, DELTA);
    assertEquals(astPolar.get().getCoordinateList().get(1).getPhi(), 0.380506, DELTA);
    
    assertEquals(astPolar.get().getCoordinateList().get(2).getD(), 7.07106, DELTA);
    assertEquals(astPolar.get().getCoordinateList().get(2).getPhi(), 1.428899, DELTA);
    
  }
  
  @Test
  public void mirrorTransformation() throws IOException {
    CoordcartesianParser parser = new CoordcartesianParser();
    Optional<ASTCoordinateFile> astCartesian = parser
        .parseCoordinateFile("src/test/resources/examples/coord/coordinates.cart");
    assertFalse(parser.hasErrors());
    assertTrue(astCartesian.isPresent());
    
    assertEquals(astCartesian.get().getCoordinateList().size(), 3);
    
    assertEquals(astCartesian.get().getCoordinateList().get(0).getX(), 2);
    assertEquals(astCartesian.get().getCoordinateList().get(0).getY(), 4);
    
    assertEquals(astCartesian.get().getCoordinateList().get(1).getX(), 5);
    assertEquals(astCartesian.get().getCoordinateList().get(1).getY(), 2);
    
    assertEquals(astCartesian.get().getCoordinateList().get(2).getX(), 1);
    assertEquals(astCartesian.get().getCoordinateList().get(2).getY(), 7);
    
    // Transform cartesian to polar coordinates
    Mirror transformer = new Mirror();
    transformer.transform(astCartesian.get());
    
    // Create PrettyPrinter
    IndentPrinter ip = new IndentPrinter();
    CartesianPrettyPrinterConcreteVisitor p = new CartesianPrettyPrinterConcreteVisitor(ip);
    
    // Pretty-print the cartesian coordinates
    p.print(astCartesian.get());
    
    Optional<ASTCoordinateFile> astTransformed = parser.parseCoordinateFile(new StringReader(ip.getContent()));
    assertFalse(parser.hasErrors());
    assertTrue(astTransformed.isPresent());
    
    assertEquals(astTransformed.get().getCoordinateList().size(), 3);
    
    assertEquals(astTransformed.get().getCoordinateList().get(0).getX(), 4);
    assertEquals(astTransformed.get().getCoordinateList().get(0).getY(), 2);
    
    assertEquals(astTransformed.get().getCoordinateList().get(1).getX(), 2);
    assertEquals(astTransformed.get().getCoordinateList().get(1).getY(), 5);
    
    assertEquals(astTransformed.get().getCoordinateList().get(2).getX(), 7);
    assertEquals(astTransformed.get().getCoordinateList().get(2).getY(), 1);
    
  }
  
}
