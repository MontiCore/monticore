/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;

import java.io.File;

import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.io.FileReaderWriterMock;
import org.junit.jupiter.api.Test;

/**
 * A simple unit test invoking a template which uses the new template logger.
 *
 */
public class TemplateLoggerTest {
  
  private static final File TARGET_DIR = new File("target");
  
  private TemplateControllerMock tc;
  
  private GlobalExtensionManagement glex;
    
  private FileReaderWriterMock fileHandler;
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  /**
   * Sets up the template controller for this 'test'. This is copied entirely
   * from {@link TemplateControllerTest} but uses the production
   * {@link FreeMarkerTemplateEngine} instead of the mock in order to have the
   * template actually executed. Otherwise the log statements would do nothing.
   */
  @BeforeEach
  public void setup() {
    glex = new GlobalExtensionManagement();
 
    fileHandler = new FileReaderWriterMock();
    FileReaderWriter.init(fileHandler);

    GeneratorSetup config = new GeneratorSetup();
    config.setGlex(glex);
    config.setOutputDirectory(TARGET_DIR);
    config.setTracing(false);
    // .externalTemplatePaths(new File[]{})
    tc = new TemplateControllerMock(config, "");
  }

  @AfterAll
  public static void resetFileReaderWriter() {
    FileReaderWriter.init();
  }
  
  /**
   * Executes a test templates which invokes the template logger.
   */
  @Test
  public void demonstrateTemplateLogging() {
    StringBuilder result = tc.include(TEMPLATE_PACKAGE + "Log");
    Assertions.assertNotNull(result);
    Assertions.assertEquals("A", result.toString().trim());
  }

}
