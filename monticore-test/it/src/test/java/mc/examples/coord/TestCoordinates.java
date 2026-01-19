/* (c) https://github.com/MontiCore/monticore */

package mc.examples.coord;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.monticore.prettyprint.IndentPrinter;
import mc.GeneratorIntegrationsTest;
import mc.examples.cartesian.coordcartesian.CoordcartesianMill;
import mc.examples.cartesian.coordcartesian._ast.ASTCoordinateFile;
import mc.examples.cartesian.coordcartesian._parser.CoordcartesianParser;
import mc.examples.cartesian.coordcartesian._visitor.CoordcartesianTraverser;
import mc.examples.coord.cartesian.prettyprint.CartesianPrettyPrinterConcreteVisitor;
import mc.examples.coord.polar.prettyprint.PolarPrettyPrinterConcreteVisitor;
import mc.examples.coord.transform.CartesianToPolar;
import mc.examples.coord.transform.Mirror;
import mc.examples.polar.coordpolar.CoordpolarMill;
import mc.examples.polar.coordpolar._parser.CoordpolarParser;
import mc.examples.polar.coordpolar._visitor.CoordpolarTraverser;

import static org.junit.jupiter.api.Assertions.*;

public class TestCoordinates extends GeneratorIntegrationsTest {
  
  private static final double DELTA = 1e-5;
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testCoordcartesianParser() throws IOException {
    CoordcartesianParser parser = new CoordcartesianParser();
    Optional<ASTCoordinateFile> astCartesian = parser
        .parseCoordinateFile("src/test/resources/examples/coord/coordinates.cart"); // (2,4)
                                                                      // (5,2)
                                                                      // (1,7)
    assertFalse(parser.hasErrors());
    assertTrue(astCartesian.isPresent());
    
    assertEquals(3, astCartesian.get().getCoordinateList().size());
    
    assertEquals(2, astCartesian.get().getCoordinateList().get(0).getX());
    assertEquals(4, astCartesian.get().getCoordinateList().get(0).getY());
    
    assertEquals(5, astCartesian.get().getCoordinateList().get(1).getX());
    assertEquals(2, astCartesian.get().getCoordinateList().get(1).getY());
    
    assertEquals(1, astCartesian.get().getCoordinateList().get(2).getX());
    assertEquals(7, astCartesian.get().getCoordinateList().get(2).getY());
  
    assertTrue(Log.getFindings().isEmpty());
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
    
    assertEquals(3, astPolar.get().getCoordinateList().size());
    

    
    assertEquals(1.0, astPolar.get().getCoordinateList().get(0).getD(), DELTA);
    assertEquals(0.5, astPolar.get().getCoordinateList().get(0).getPhi(), DELTA);
    
    assertEquals(2.5, astPolar.get().getCoordinateList().get(1).getD(), DELTA);
    assertEquals(1.3, astPolar.get().getCoordinateList().get(1).getPhi(), DELTA);
    
    assertEquals(47.11, astPolar.get().getCoordinateList().get(2).getD(), DELTA);
    assertEquals(0.815, astPolar.get().getCoordinateList().get(2).getPhi(), DELTA);
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void cartesian2Polar() throws IOException {
    CoordcartesianParser parser = new CoordcartesianParser();
    Optional<ASTCoordinateFile> astCartesian = parser
        .parseCoordinateFile("src/test/resources/examples/coord/coordinates.cart");
    assertFalse(parser.hasErrors());
    assertTrue(astCartesian.isPresent());
    
    // Transform cartesian to polar coordinates
    CoordcartesianTraverser t1 = CoordcartesianMill.traverser();
    CartesianToPolar transformer = new CartesianToPolar();
    t1.add4Coordcartesian(transformer);
    astCartesian.get().accept(t1);
    
    // Create PrettyPrinter
    CoordpolarTraverser t2 = CoordpolarMill.traverser();
    IndentPrinter ip = new IndentPrinter();
    PolarPrettyPrinterConcreteVisitor p = new PolarPrettyPrinterConcreteVisitor(ip);
    t2.add4Coordpolar(p);
    
    // Pretty-print the cartesian coordinates
    transformer.getResult().accept(t2);
    
    mc.examples.polar.coordpolar._parser.CoordpolarParser polarParser = new CoordpolarParser();
    Optional<mc.examples.polar.coordpolar._ast.ASTCoordinateFile> astPolar = polarParser
        .parseCoordinateFile(new StringReader(ip.getContent()));
    assertFalse(polarParser.hasErrors());
    assertTrue(astPolar.isPresent());
    
    assertEquals(3, astPolar.get().getCoordinateList().size());
    
    assertEquals(4.47213, astPolar.get().getCoordinateList().get(0).getD(), DELTA);
    assertEquals(1.10714, astPolar.get().getCoordinateList().get(0).getPhi(), DELTA);
    
    assertEquals(5.38516, astPolar.get().getCoordinateList().get(1).getD(), DELTA);
    assertEquals(0.380506, astPolar.get().getCoordinateList().get(1).getPhi(), DELTA);
    
    assertEquals(7.07106, astPolar.get().getCoordinateList().get(2).getD(), DELTA);
    assertEquals(1.428899, astPolar.get().getCoordinateList().get(2).getPhi(), DELTA);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void mirrorTransformation() throws IOException {
    CoordcartesianParser parser = new CoordcartesianParser();
    Optional<ASTCoordinateFile> astCartesian = parser
        .parseCoordinateFile("src/test/resources/examples/coord/coordinates.cart");
    assertFalse(parser.hasErrors());
    assertTrue(astCartesian.isPresent());
    
    assertEquals(3, astCartesian.get().getCoordinateList().size());
    
    assertEquals(2, astCartesian.get().getCoordinateList().get(0).getX());
    assertEquals(4, astCartesian.get().getCoordinateList().get(0).getY());
    
    assertEquals(5, astCartesian.get().getCoordinateList().get(1).getX());
    assertEquals(2, astCartesian.get().getCoordinateList().get(1).getY());
    
    assertEquals(1, astCartesian.get().getCoordinateList().get(2).getX());
    assertEquals(7, astCartesian.get().getCoordinateList().get(2).getY());
    
    // Transform cartesian to polar coordinates
    CoordcartesianTraverser t1 = CoordcartesianMill.traverser();
    Mirror transformer = new Mirror();
    t1.add4Coordcartesian(transformer);
    astCartesian.get().accept(t1);
    
    // Create PrettyPrinter
    CoordcartesianTraverser t2 = CoordcartesianMill.traverser();
    IndentPrinter ip = new IndentPrinter();
    CartesianPrettyPrinterConcreteVisitor p = new CartesianPrettyPrinterConcreteVisitor(ip);
    t2.add4Coordcartesian(p);
    
    // Pretty-print the cartesian coordinates
    astCartesian.get().accept(t2);
    
    Optional<ASTCoordinateFile> astTransformed = parser.parseCoordinateFile(new StringReader(ip.getContent()));
    assertFalse(parser.hasErrors());
    assertTrue(astTransformed.isPresent());
    
    assertEquals(3, astTransformed.get().getCoordinateList().size());
    
    assertEquals(4, astTransformed.get().getCoordinateList().get(0).getX());
    assertEquals(2, astTransformed.get().getCoordinateList().get(0).getY());
    
    assertEquals(2, astTransformed.get().getCoordinateList().get(1).getX());
    assertEquals(5, astTransformed.get().getCoordinateList().get(1).getY());
    
    assertEquals(7, astTransformed.get().getCoordinateList().get(2).getX());
    assertEquals(1, astTransformed.get().getCoordinateList().get(2).getY());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
