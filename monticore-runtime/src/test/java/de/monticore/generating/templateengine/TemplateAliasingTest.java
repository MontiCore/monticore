/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Joiner;

import de.monticore.generating.GeneratorSetup;
import de.monticore.io.FileReaderWriterMock;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import freemarker.core.Macro;

public class TemplateAliasingTest {

  private static final File TARGET_DIR = new File("target");
  private static final int NUMBER_ALIASES = 18;
  public static final String ALIASES_PACKAGE = "de.monticore.generating.templateengine.templates.aliases.";


  private TemplateControllerMock tc;
  
  private GeneratorSetup config;


  @BeforeClass
  public static void init() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setup() {

    FileReaderWriterMock fileHandler = new FileReaderWriterMock();

    config = new GeneratorSetup();
    config.setFileHandler(fileHandler);
    config.setOutputDirectory(TARGET_DIR);
    config.setTracing(false);
    
    tc = new TemplateControllerMock(config, "");

    Slf4jLog.getFindings().clear();
  }

  @Test
  public void testIncludeAlias() {
    StringBuilder templateOutput =
        tc.include(ALIASES_PACKAGE + "IncludeAlias");
    assertEquals("Plain is included.", templateOutput.toString());
  }

  @Ignore
  @Test
  public void testIncludeArgsAndSignatureAlias() {
    assertTrue(config.getAliases().isEmpty());
    StringBuilder templateOutput =
        tc.include(ALIASES_PACKAGE + "IncludeArgsAndSignatureAlias");
    TemplateController tcChild = tc.getSubController().getSubController();
    assertNotNull(tcChild);
    
    assertEquals(3, tcChild.getArguments().size());
    assertEquals("name", tcChild.getArguments().get(0));
    assertEquals("age", tcChild.getArguments().get(1));
    assertEquals("city", tcChild.getArguments().get(2));
    
    assertEquals(3, tcChild.getArguments().size());
    assertEquals("Charly", tcChild.getArguments().get(0));
    assertEquals("30", tcChild.getArguments().get(1));
    assertEquals("Aachen", tcChild.getArguments().get(2));
    
    assertEquals("Name is Charly, age is 30, city is Aachen", templateOutput.toString());
    
    assertAliases(tcChild, config.getAliases().size()
    );
  }

  @Test
  public void testLogAliases() {
    assertTrue(config.getAliases().isEmpty());
    tc.include(ALIASES_PACKAGE + "LogAliases");
    assertAliases(tc, NUMBER_ALIASES);

    /* TODO GV, RH: is deprecated?
    Collection<String> expectedLogs = Arrays.asList(
        "Trace Message",
        "Debug Message",
        "Info Message",
        "Warn Message",
        "Error Message"
        );
*/
    Collection<String> expectedLogs = Arrays.asList(
        "Warn Message",
        "Error Message"
        );

    assertEquals(2, Slf4jLog.getFindings().size());
    assertErrors(expectedLogs, Slf4jLog.getFindings());
  }

  
  /**
   * Asserts that each of the expectedErrors is found at least once in the
   * actualErrors.
   * 
   * @param expectedErrors
   * @param actualErrors
   */
  private static void assertErrors(Collection<String> expectedErrors,
      Collection<Finding> actualErrors) {
    String actualErrorsJoined = "\nactual Errors: \n\t" + Joiner.on("\n\t").join(actualErrors);
    for (String expectedError : expectedErrors) {
      boolean found = actualErrors.stream().filter(s -> s.getMsg().equals(expectedError)).count() >= 1;
      assertTrue("The following expected error was not found: " + expectedError
          + actualErrorsJoined, found);
    }
  }

  private void assertAliases(TemplateController tc, int expectedNumberAliases) {
    List<Macro> aliases = config.getAliases();
    assertNotNull(aliases);
    assertEquals(expectedNumberAliases, aliases.size());
    
    for (Macro macro : aliases) {
      assertTrue(macro.isFunction());
    }
  }
  
}
