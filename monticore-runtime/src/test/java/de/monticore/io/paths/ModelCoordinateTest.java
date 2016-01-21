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

package de.monticore.io.paths;

import org.junit.Test;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModelCoordinateTest {
  
  ModelCoordinate testInfo;
  
  ModelCoordinate testInfoWithWhitespace;
  
  public ModelCoordinateTest() throws MalformedURLException {
    
    Path location = Paths.get("src/test/resources/de/monticore/parsing/SimpleStateChart.mc4");
    Path locationWithWS = Paths
        .get("src/te st/resources/de/monticore/parsing/SimpleStateChart.mc4");
    Path qualifiedPath = Paths.get("de/monticore/parsing/SimpleStateChart.mc4");
    testInfo = ModelCoordinates.createFullCoordinate(qualifiedPath, location);
    testInfoWithWhitespace = ModelCoordinates.createFullCoordinate(qualifiedPath, locationWithWS);
  }
  
  @Test
  public void testGetName() {
    assertEquals("SimpleStateChart.mc4", testInfo.getName());
  }
  
  @Test
  public void testGetBaseName() {
    assertEquals("SimpleStateChart", testInfo.getBaseName());
  }
  
  @Test
  public void testGetExtension() {
    assertEquals("mc4", testInfo.getExtension());
  }
  
  @Test
  public void testGetQualifiedPath() {
    assertEquals(Paths.get("de/monticore/parsing/SimpleStateChart.mc4"),
        testInfo.getQualifiedPath());
  }
  
  @Test
  public void testGetPackagePath() {
    assertEquals(Paths.get("de/monticore/parsing"), testInfo.getPackagePath());
  }
  
  @Test
  public void testGetQualifiedBaseName() {
    assertEquals("de.monticore.parsing.SimpleStateChart", testInfo.getQualifiedBaseName());
  }
  
  @Test
  public void testGetQualifiedBasePath() {
    assertEquals(Paths.get("de/monticore/parsing/SimpleStateChart"),
        testInfo.getQualifiedBasePath());
  }
  
  @Test
  public void testGetLocation() throws MalformedURLException {
    assertEquals(Paths.get("src/test/resources/de/monticore/parsing/SimpleStateChart.mc4"),
        testInfo.getLocation());
  }
  
  @Test
  public void testGetLocationWithWhitespace() throws MalformedURLException {
    assertEquals(Paths.get("src/te st/resources/de/monticore/parsing/SimpleStateChart.mc4"),
        testInfoWithWhitespace.getLocation());
  }
  
  @Test
  public void testGetParentDirectoryPath() {
    assertEquals(Paths.get("src/test/resources").toAbsolutePath(),
        testInfo.getParentDirectoryPath());
  }
  
  @Test
  public void testGetParentDirectoryPathWithWhitespace() {
    assertEquals(Paths.get("src/te st/resources").toAbsolutePath(),
        testInfoWithWhitespace.getParentDirectoryPath());
  }
  
  @Test
  public void testHasLocation() {
    assertTrue(testInfo.hasLocation());
  }
  
  @Test
  public void testIsQualified() {
    assertTrue(testInfo.isQualified());
  }
}
