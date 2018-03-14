/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io.paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;

import de.monticore.AmbiguityException;
import de.se_rwth.commons.logging.Log;

import org.junit.BeforeClass;
import org.junit.Test;

public class ModelPathTest {
  
  ModelPath modelPath;
  
  Path parentPathOne = Paths.get("src/test/resources/modelpathtest/path1");
  
  Path parentPathTwo = Paths.get("src/test/resources/modelpathtest/path2");

  ModelCoordinate ambiguousModel = ModelCoordinates.createQualifiedCoordinate(Paths
      .get("ambiguousfile.txt"));
  
  ModelCoordinate unambiguousModel = ModelCoordinates.createQualifiedCoordinate(Paths
      .get("unambiguousfile.txt"));
  
  Path unambiguousModelLocation = parentPathOne.resolve("unambiguousfile.txt");
  
  public ModelPathTest() {
    modelPath = new ModelPath(parentPathOne, parentPathTwo);
  }
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testResolveModel() throws URISyntaxException {
    assertTrue(modelPath.resolveModel(unambiguousModel).hasLocation());
    Path resolvedLocation = Paths.get(new File(unambiguousModel.getLocation().toURI()).getPath());
    assertTrue(resolvedLocation.endsWith(unambiguousModelLocation));
  }
  
  @Test
  public void testAmbiguityException() {
    modelPath.resolveModel(ambiguousModel);
    assertEquals(1, Log.getErrorCount());
  }

  @Test
  public void testGetFullPathOfEntries() {
    final ModelPath p = new ModelPath(parentPathOne, parentPathTwo);

    Collection<Path> actualEntries = p.getFullPathOfEntries();
    assertEquals(2, actualEntries.size());

    final Iterator<Path> entriesIterator = actualEntries.iterator();
    assertTrue(entriesIterator.next().endsWith(parentPathOne));
    assertTrue(entriesIterator.next().endsWith(parentPathTwo));
  }
}
