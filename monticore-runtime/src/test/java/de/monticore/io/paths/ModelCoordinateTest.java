/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io.paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class ModelCoordinateTest {
  
  ModelCoordinate testInfo;
  
  ModelCoordinate testInfoWithWhitespace;
  
  public ModelCoordinateTest() throws MalformedURLException {
    
    URL location = Paths.get("src/test/resources/de/monticore/parsing/SimpleStateChart.mc4")
        .toFile().toURI().toURL();
    URL locationWithWS = Paths.get("src/te st/resources/de/monticore/parsing/SimpleStateChart.mc4")
        .toFile().toURI().toURL();
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
    assertEquals(Paths.get("src/test/resources/de/monticore/parsing/SimpleStateChart.mc4").toUri()
        .toURL(), testInfo.getLocation());
  }
  
  @Test
  public void testGetLocationWithWhitespace() throws MalformedURLException {
    assertEquals(Paths.get("src/te st/resources/de/monticore/parsing/SimpleStateChart.mc4").toUri()
        .toURL(), testInfoWithWhitespace.getLocation());
  }
  
  @Test
  public void testGetParentDirectoryPath() {
    assertEquals(Paths.get("src/test/resources").toAbsolutePath(), testInfo.getParentDirectoryPath());
  }
  
  @Test
  public void testGetParentDirectoryPathWithWhitespace() {
    assertEquals(Paths.get("src/te st/resources").toAbsolutePath(), testInfoWithWhitespace.getParentDirectoryPath());
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
