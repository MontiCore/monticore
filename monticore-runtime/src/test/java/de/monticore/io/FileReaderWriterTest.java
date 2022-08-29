/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileReaderWriterTest {
  
  Path testPath = Paths.get("target/test/FileHandlertest.txt");
  
  String testContent = "Hello World";
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @After
  public void tearDown() {
    try {
      Files.deleteIfExists(testPath);
    }
    catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
  
  /**
   * Implicitly tests read and write in one assert.
   */
  @Test
  public void testFileHandler() {
    FileReaderWriter.init();
    FileReaderWriter.storeInFile(testPath, testContent);
    assertEquals(testContent, FileReaderWriter.readFromFile(testPath));
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
