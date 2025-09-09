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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FileReaderWriterTest {
  
  Path testPath = Paths.get("target/test/FileHandlertest.txt");
  
  String testContent = "Hello World";
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @AfterEach
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
    Assertions.assertEquals(testContent, FileReaderWriter.readFromFile(testPath));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
