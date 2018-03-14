/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import static de.monticore.generating.templateengine.TestConstants.TEMPLATE_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.io.FileReaderWriterMock;

/**
 * A simple unit test invoking a template which uses the new template logger.
 *
 * @since 4.0.1
 */
public class TemplateLoggerTest {
  
  private static final File TARGET_DIR = new File("target");
  
  private TemplateControllerMock tc;
  
  private GlobalExtensionManagement glex;
    
  private FileReaderWriterMock fileHandler;
  
  /**
   * Sets up the template controller for this 'test'. This is copied entirely
   * from {@link TemplateControllerTest} but uses the production
   * {@link FreeMarkerTemplateEngine} instead of the mock in order to have the
   * template actually executed. Otherwise the log statements would do nothing.
   */
  @Before
  public void setup() {
    glex = new GlobalExtensionManagement();
 
    fileHandler = new FileReaderWriterMock();
    GeneratorSetup config = new GeneratorSetup();
    config.setGlex(glex);
    config.setFileHandler(fileHandler);
    config.setOutputDirectory(TARGET_DIR);
    config.setTracing(false);
    // .externalTemplatePaths(new File[]{})
    tc = new TemplateControllerMock(config, "");
  }
  
  /**
   * Executes a test templates which invokes the template logger.
   */
  @Test
  public void demonstrateTemplateLogging() {
    StringBuilder result = tc.include(TEMPLATE_PACKAGE + "Log");
    assertNotNull(result);
    assertEquals("A", result.toString().trim());
  }
  
}
