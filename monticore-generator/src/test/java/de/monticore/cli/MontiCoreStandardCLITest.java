/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cli;

import de.monticore.cli.MontiCoreStandardCLI;
import de.monticore.MontiCoreConfiguration;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * A collection of exemplary use cases for the CLI arguments. These unit tests
 * do not really test something but are written to try out certain argument
 * combinations and hence designed to not fail.
 *
 */
public class MontiCoreStandardCLITest {
  
  /**
   * Pretty default arguments.
   */
  static String[] simpleArgs = {
      "-" + MontiCoreConfiguration.Options.GRAMMARS,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + MontiCoreConfiguration.Options.MODELPATH, "src/test/resources",
      "-" + MontiCoreConfiguration.Options.OUT, "target/test-run",
      "-" + MontiCoreConfiguration.Options.HANDCODEDPATH, "src/test/java" };
  
  /**
   * Arguments activating the detailed developer logging.
   */
  static String[] devLogArgs = {
      "-" + MontiCoreConfiguration.Options.MODELPATH, "src/test/resources",
      "-" + MontiCoreConfiguration.Options.OUT, "target/test-run",
      "-" + MontiCoreConfiguration.Options.GRAMMARS,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + MontiCoreConfiguration.Options.HANDCODEDPATH, "src/test/java",
      "-" + MontiCoreConfiguration.Options.DEV };
  
  /**
   * Arguments specifying a custom log configuration file to use.
   */
  static String[] customLogArgs = {
      "-" + MontiCoreConfiguration.Options.GRAMMARS,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + MontiCoreConfiguration.Options.MODELPATH, "src/test/resources",
      "-" + MontiCoreConfiguration.Options.OUT, "target/test-run",
      "-" + MontiCoreConfiguration.Options.HANDCODEDPATH, "src/test/java",
      "-" + MontiCoreConfiguration.Options.CUSTOMLOG, "src/test/resources/test.logging.xml" };
  
  /**
   * Arguments for using a custom Groovy script.
   */
  static String[] customScriptArgs = {
      "-" + MontiCoreConfiguration.Options.GRAMMARS,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + MontiCoreConfiguration.Options.MODELPATH, "src/test/resources",
      "-" + MontiCoreConfiguration.Options.OUT, "target/test-run",
      "-" + MontiCoreConfiguration.Options.HANDCODEDPATH, "src/test/java",
      "-" + MontiCoreConfiguration.Options.SCRIPT, "src/test/resources/my_noemf.groovy",
      };
  
  /**
   * Arguments for using a custom Groovy script.
   */
  static String[] customEmfScriptArgs = {
      "-" + MontiCoreConfiguration.Options.GRAMMARS,
      "src/test/resources/de/monticore/AutomatonEmf.mc4",
      "-" + MontiCoreConfiguration.Options.MODELPATH, "src/test/resources",
      "-" + MontiCoreConfiguration.Options.OUT, "target/test-run",
      "-" + MontiCoreConfiguration.Options.HANDCODEDPATH, "src/test/java",
      "-" + MontiCoreConfiguration.Options.SCRIPT, "src/test/resources/my_emf.groovy",
      };
  
  /**
   * These arguments specify inputs where there are no ".mc4" files. This will
   * be reported to the user instead of silently doing nothing.
   */
  static String[] argsWithNoGrammars = {
      "-" + MontiCoreConfiguration.Options.GRAMMARS,
      "src/test/resources/monticore",
      "-" + MontiCoreConfiguration.Options.MODELPATH, "src/test/resources",
      "-" + MontiCoreConfiguration.Options.OUT, "target/test-run",
      "-" + MontiCoreConfiguration.Options.HANDCODEDPATH, "src/test/java" };
  
  static String[] help = {
      "-" + MontiCoreConfiguration.Options.HELP
  };
  
  @BeforeClass
  public static void deactivateFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testMontiCoreCLI() {
    MontiCoreStandardCLI.main(simpleArgs);
    
    assertTrue(!false);
  }
  
  @Test
  public void testMontiCoreDevLogCLI() {
    MontiCoreStandardCLI.main(devLogArgs);
    
    assertTrue(!false);
  }
  
  @Test
  public void testMontiCoreCustomLogCLI() {
    MontiCoreStandardCLI.main(customLogArgs);
    
    assertTrue(!false);
  }

  @Test
  public void testMontiCoreCustomScriptCLI() {
    MontiCoreStandardCLI.main(customScriptArgs);
    
    assertTrue(!false);
  }

  @Test
  public void testMontiCoreCustomEmfScriptCLI() {
    MontiCoreStandardCLI.main(customEmfScriptArgs);
    
    assertTrue(!false);
  }
  
  @Test
  public void testHelp() {
    MontiCoreStandardCLI.main(help);

    assertTrue(!false);
}
  @Ignore // It's not possible to switch off fail quick (Logger in CLI)
  @Test
  public void testArgsWithNoGrammars() {
    MontiCoreStandardCLI.main(argsWithNoGrammars);
    
    assertTrue(!false);
  }
  
  @After
  public void tearDown() throws Exception {
    Reporting.off();
  }
}
