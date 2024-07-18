/* (c) https://github.com/MontiCore/monticore */

package mc.examples.coord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
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
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astCartesian.isPresent());
    
    Assertions.assertEquals(astCartesian.get().getCoordinateList().size(), 3);
    
    Assertions.assertEquals(astCartesian.get().getCoordinateList().get(0).getX(), 2);
    Assertions.assertEquals(astCartesian.get().getCoordinateList().get(0).getY(), 4);
    
    Assertions.assertEquals(astCartesian.get().getCoordinateList().get(1).getX(), 5);
    Assertions.assertEquals(astCartesian.get().getCoordinateList().get(1).getY(), 2);
    
    Assertions.assertEquals(astCartesian.get().getCoordinateList().get(2).getX(), 1);
    Assertions.assertEquals(astCartesian.get().getCoordinateList().get(2).getY(), 7);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testCoordpolarParser() throws IOException {
    mc.examples.polar.coordpolar._parser.CoordpolarParser parser = new CoordpolarParser();
    Optional<mc.examples.polar.coordpolar._ast.ASTCoordinateFile> astPolar = parser
        .parseCoordinateFile("src/test/resources/examples/coord/coordinates.polar");
    // [1,0;0,5]
    // [2,5;1,3]
    // [47,11;0,815]
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astPolar.isPresent());
    
    Assertions.assertEquals(astPolar.get().getCoordinateList().size(), 3);
    

    
    Assertions.assertEquals(astPolar.get().getCoordinateList().get(0).getD(), 1.0, DELTA);
    Assertions.assertEquals(astPolar.get().getCoordinateList().get(0).getPhi(), 0.5, DELTA);
    
    Assertions.assertEquals(astPolar.get().getCoordinateList().get(1).getD(), 2.5, DELTA);
    Assertions.assertEquals(astPolar.get().getCoordinateList().get(1).getPhi(), 1.3, DELTA);
    
    Assertions.assertEquals(astPolar.get().getCoordinateList().get(2).getD(), 47.11, DELTA);
    Assertions.assertEquals(astPolar.get().getCoordinateList().get(2).getPhi(), 0.815, DELTA);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void cartesian2Polar() throws IOException {
    CoordcartesianParser parser = new CoordcartesianParser();
    Optional<ASTCoordinateFile> astCartesian = parser
        .parseCoordinateFile("src/test/resources/examples/coord/coordinates.cart");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astCartesian.isPresent());
    
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
    Assertions.assertFalse(polarParser.hasErrors());
    Assertions.assertTrue(astPolar.isPresent());
    
    Assertions.assertEquals(astPolar.get().getCoordinateList().size(), 3);
    
    Assertions.assertEquals(astPolar.get().getCoordinateList().get(0).getD(), 4.47213, DELTA);
    Assertions.assertEquals(astPolar.get().getCoordinateList().get(0).getPhi(), 1.10714, DELTA);
    
    Assertions.assertEquals(astPolar.get().getCoordinateList().get(1).getD(), 5.38516, DELTA);
    Assertions.assertEquals(astPolar.get().getCoordinateList().get(1).getPhi(), 0.380506, DELTA);
    
    Assertions.assertEquals(astPolar.get().getCoordinateList().get(2).getD(), 7.07106, DELTA);
    Assertions.assertEquals(astPolar.get().getCoordinateList().get(2).getPhi(), 1.428899, DELTA);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void mirrorTransformation() throws IOException {
    CoordcartesianParser parser = new CoordcartesianParser();
    Optional<ASTCoordinateFile> astCartesian = parser
        .parseCoordinateFile("src/test/resources/examples/coord/coordinates.cart");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astCartesian.isPresent());
    
    Assertions.assertEquals(astCartesian.get().getCoordinateList().size(), 3);
    
    Assertions.assertEquals(astCartesian.get().getCoordinateList().get(0).getX(), 2);
    Assertions.assertEquals(astCartesian.get().getCoordinateList().get(0).getY(), 4);
    
    Assertions.assertEquals(astCartesian.get().getCoordinateList().get(1).getX(), 5);
    Assertions.assertEquals(astCartesian.get().getCoordinateList().get(1).getY(), 2);
    
    Assertions.assertEquals(astCartesian.get().getCoordinateList().get(2).getX(), 1);
    Assertions.assertEquals(astCartesian.get().getCoordinateList().get(2).getY(), 7);
    
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
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astTransformed.isPresent());
    
    Assertions.assertEquals(astTransformed.get().getCoordinateList().size(), 3);
    
    Assertions.assertEquals(astTransformed.get().getCoordinateList().get(0).getX(), 4);
    Assertions.assertEquals(astTransformed.get().getCoordinateList().get(0).getY(), 2);
    
    Assertions.assertEquals(astTransformed.get().getCoordinateList().get(1).getX(), 2);
    Assertions.assertEquals(astTransformed.get().getCoordinateList().get(1).getY(), 5);
    
    Assertions.assertEquals(astTransformed.get().getCoordinateList().get(2).getX(), 7);
    Assertions.assertEquals(astTransformed.get().getCoordinateList().get(2).getY(), 1);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
