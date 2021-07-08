/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cli;

import de.monticore.cli.MontiCoreStandardCLI;
import de.monticore.MontiCoreConfiguration;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.se_rwth.commons.logging.Log;
import org.junit.*;

import static de.monticore.MontiCoreConfiguration.*;
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
      "-" + GRAMMAR,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + MODELPATH, "src/test/resources",
      "-" + OUT, "target/test-run",
      "-" + HANDCODEDPATH, "src/test/java" };
  
  /**
   * Arguments activating the detailed developer logging.
   */
  static String[] devLogArgs = {
      "-" + MODELPATH, "src/test/resources",
      "-" + OUT, "target/test-run",
      "-" + GRAMMAR,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + HANDCODEDPATH, "src/test/java",
      "-" + DEV };
  
  /**
   * Arguments specifying a custom log configuration file to use.
   */
  static String[] customLogArgs = {
      "-" + GRAMMAR,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + MODELPATH, "src/test/resources",
      "-" + OUT, "target/test-run",
      "-" + HANDCODEDPATH, "src/test/java",
      "-" + CUSTOMLOG, "src/test/resources/test.logging.xml" };
  
  /**
   * Arguments for using a custom Groovy script.
   */
  static String[] customScriptArgs = {
      "-" + GRAMMAR,
      "src/test/resources/de/monticore/Automaton.mc4",
      "-" + MODELPATH, "src/test/resources",
      "-" + OUT, "target/test-run",
      "-" + HANDCODEDPATH, "src/test/java",
      "-" + SCRIPT, "src/test/resources/my_noemf.groovy",
      };
  
  /**
   * Arguments for using a custom Groovy script.
   */
  static String[] customEmfScriptArgs = {
      "-" + GRAMMAR,
      "src/test/resources/de/monticore/AutomatonEmf.mc4",
      "-" + MODELPATH, "src/test/resources",
      "-" + OUT, "target/test-run",
      "-" + HANDCODEDPATH, "src/test/java",
      "-" + SCRIPT, "src/test/resources/my_emf.groovy",
      };
  
  /**
   * These arguments specify inputs where there are no ".mc4" files. This will
   * be reported to the user instead of silently doing nothing.
   */
  static String[] argsWithNoGrammars = {
      "-" + GRAMMAR,
      "src/test/resources/monticore",
      "-" + MODELPATH, "src/test/resources",
      "-" + OUT, "target/test-run",
      "-" + HANDCODEDPATH, "src/test/java" };
  
  static String[] help = {
      "-" + HELP
  };
  
  @BeforeClass
  public static void deactivateFailQuick() {
    Log.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setup() {
    GrammarFamilyMill.reset();
    GrammarFamilyMill.init();
  }
  
  @Test
  public void testMontiCoreCLI() {
    new MontiCoreStandardCLI().run(simpleArgs);
    
    assertTrue(!false);
  }
  
  @Test
  public void testMontiCoreDevLogCLI() {
    new MontiCoreStandardCLI().run(devLogArgs);
    
    assertTrue(!false);
  }
  
  @Test
  public void testMontiCoreCustomLogCLI() {
    new MontiCoreStandardCLI().run(customLogArgs);
    
    assertTrue(!false);
  }

  @Test
  public void testMontiCoreCustomScriptCLI() {
    new MontiCoreStandardCLI().run(customScriptArgs);
    
    assertTrue(!false);
  }

  @Test
  public void testMontiCoreCustomEmfScriptCLI() {
    new MontiCoreStandardCLI().run(customEmfScriptArgs);
    
    assertTrue(!false);
  }
  
  @Test
  public void testHelp() {
    new MontiCoreStandardCLI().run(help);

    assertTrue(!false);
}
  @Ignore // It's not possible to switch off fail quick (Logger in CLI)
  @Test
  public void testArgsWithNoGrammars() {
    new MontiCoreStandardCLI().run(argsWithNoGrammars);
    
    assertTrue(!false);
  }
  
  @After
  public void tearDown() throws Exception {
    Reporting.off();
  }
}
