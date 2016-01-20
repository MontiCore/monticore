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
  
  @Test
  public void testCoordcartesianParser() throws IOException {
    CoordcartesianParser parser = new CoordcartesianParser();
    Optional<ASTCoordinateFile> astCartesian = parser
        .parseCoordinateFile("src/test/resources/examples/coord/coordinates.cart"); // (2,4)
                                                                      // (5,2)
                                                                      // (1,7)
    assertFalse(parser.hasErrors());
    assertTrue(astCartesian.isPresent());
    
    assertEquals(astCartesian.get().getCoordinates().size(), 3);
    
    assertEquals(astCartesian.get().getCoordinates().get(0).getX(), "2");
    assertEquals(astCartesian.get().getCoordinates().get(0).getY(), "4");
    
    assertEquals(astCartesian.get().getCoordinates().get(1).getX(), "5");
    assertEquals(astCartesian.get().getCoordinates().get(1).getY(), "2");
    
    assertEquals(astCartesian.get().getCoordinates().get(2).getX(), "1");
    assertEquals(astCartesian.get().getCoordinates().get(2).getY(), "7");
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
    
    assertEquals(astPolar.get().getCoordinates().size(), 3);
    
    assertEquals(astPolar.get().getCoordinates().get(0).getD(), "1,0");
    assertEquals(astPolar.get().getCoordinates().get(0).getPhi(), "0,5");
    
    assertEquals(astPolar.get().getCoordinates().get(1).getD(), "2,5");
    assertEquals(astPolar.get().getCoordinates().get(1).getPhi(), "1,3");
    
    assertEquals(astPolar.get().getCoordinates().get(2).getD(), "47,11");
    assertEquals(astPolar.get().getCoordinates().get(2).getPhi(), "0,815");
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
    
    assertEquals(astPolar.get().getCoordinates().size(), 3);
    
    assertEquals(astPolar.get().getCoordinates().get(0).getD(), "4,472");
    assertEquals(astPolar.get().getCoordinates().get(0).getPhi(), "1,107");
    
    assertEquals(astPolar.get().getCoordinates().get(1).getD(), "5,385");
    assertEquals(astPolar.get().getCoordinates().get(1).getPhi(), "0,381");
    
    assertEquals(astPolar.get().getCoordinates().get(2).getD(), "7,071");
    assertEquals(astPolar.get().getCoordinates().get(2).getPhi(), "1,429");
    
  }
  
  @Test
  public void mirrorTransformation() throws IOException {
    CoordcartesianParser parser = new CoordcartesianParser();
    Optional<ASTCoordinateFile> astCartesian = parser
        .parseCoordinateFile("src/test/resources/examples/coord/coordinates.cart");
    assertFalse(parser.hasErrors());
    assertTrue(astCartesian.isPresent());
    
    assertEquals(astCartesian.get().getCoordinates().size(), 3);
    
    assertEquals(astCartesian.get().getCoordinates().get(0).getX(), "2");
    assertEquals(astCartesian.get().getCoordinates().get(0).getY(), "4");
    
    assertEquals(astCartesian.get().getCoordinates().get(1).getX(), "5");
    assertEquals(astCartesian.get().getCoordinates().get(1).getY(), "2");
    
    assertEquals(astCartesian.get().getCoordinates().get(2).getX(), "1");
    assertEquals(astCartesian.get().getCoordinates().get(2).getY(), "7");
    
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
    
    assertEquals(astTransformed.get().getCoordinates().size(), 3);
    
    assertEquals(astTransformed.get().getCoordinates().get(0).getX(), "4");
    assertEquals(astTransformed.get().getCoordinates().get(0).getY(), "2");
    
    assertEquals(astTransformed.get().getCoordinates().get(1).getX(), "2");
    assertEquals(astTransformed.get().getCoordinates().get(1).getY(), "5");
    
    assertEquals(astTransformed.get().getCoordinates().get(2).getX(), "7");
    assertEquals(astTransformed.get().getCoordinates().get(2).getY(), "1");
    
  }
  
}
