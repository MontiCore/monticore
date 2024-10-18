/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cli;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.monticore.MontiCoreConfiguration.*;
import static org.junit.Assert.assertTrue;

/**
 * A collection of exemplary use cases for the CLI arguments. These unit tests
 * do not really test something but are written to try out certain argument
 * combinations and hence designed to not fail.
 *
 */
public class MontiCoreToolTest {

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

  public MontiCoreToolTest() throws IOException {
  }

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    Grammar_WithConceptsMill.reset();
    CD4CodeMill.reset();
    Grammar_WithConceptsMill.init();
    CD4CodeMill.init();
  }
  
  @Test
  public void testMontiCoreCLI() {
    new MontiCoreTool().run(simpleArgs);
    
    Assertions.assertTrue(!false);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testMontiCoreDevLogCLI() {
    new MontiCoreTool().run(devLogArgs);
    
    Assertions.assertTrue(!false);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testMontiCoreCustomLogCLI() {
    new MontiCoreTool().run(customLogArgs);
    
    Assertions.assertTrue(!false);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMontiCoreCustomScriptCLI() {
    new MontiCoreTool().run(customScriptArgs);
    
    Assertions.assertTrue(!false);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMontiCoreCustomEmfScriptCLI() {
    new MontiCoreTool().run(customEmfScriptArgs);
    
    Assertions.assertTrue(!false);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testHelp() {
    new MontiCoreTool().run(help);

    Assertions.assertTrue(!false);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
}
  @Disabled // It's not possible to switch off fail quick (Logger in CLI)
  @Test
  public void testArgsWithNoGrammars() {
    new MontiCoreTool().run(argsWithNoGrammars);
    
    Assertions.assertTrue(!false);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Test that there is no randomness / non-determinism in the generator.
   * Running twice on the same input (with only different output directories) must lead to
   * *exactly* the same output.
   * This is used for Gradle-Caching!
   */
  @Test
  public void testReproducibilitySimpleGrammar() throws IOException {
    generateGrammarAndCheckFiles(Path.of("reports", "de.monticore.automaton","20_Statistics.txt"),
            "src/test/resources/de/monticore/Automaton.mc4");
  }

  @Test
  public void testReproducibilityMultipleGrammars() throws IOException {
    generateGrammarAndCheckFiles(Path.of("reports", "combininggrammar","20_Statistics.txt"),
            "src/test/resources/CombiningGrammar.mc4");
  }

  public void generateGrammarAndCheckFiles(Path path, String grammar) throws IOException {
    File reproOutDir1 = Paths.get("target/test-run-repo/repo1/").toFile();
    File reproOutDir2 = Paths.get("target/test-run-repo/repo2/").toFile();

    Set<Path> allowedDirtyFiles = new HashSet<>();
    allowedDirtyFiles.add(path);
      // "20_Statistics" contains the execution duration, which varies between runs

    String[] reproducableArgs1 = {
        "-" + GRAMMAR,
        grammar,
        "-" + MODELPATH, "src/test/resources",
        "-" + OUT, reproOutDir1.toString(),
        "-" + HANDCODEDPATH, "src/test/java",
        "-" + GENDST_LONG, "false"};

    new MontiCoreTool().run(reproducableArgs1);
    FileUtils.deleteDirectory(reproOutDir2);
    FileUtils.moveDirectory(reproOutDir1, reproOutDir2);
    Grammar_WithConceptsMill.reset();
    CD4CodeMill.reset();

    new MontiCoreTool().run(reproducableArgs1);


    List<String> diff = new ArrayList<>();
    for(File f1: FileUtils.listFiles(reproOutDir1, null, true)) {
      if (f1.isFile()) {
        Path relPath1 = reproOutDir1.toPath().relativize(f1.toPath());
        File f2 = Paths.get(reproOutDir2.toString(), relPath1.toString()).toFile();

        if(!allowedDirtyFiles.contains(relPath1) && !FileUtils.contentEquals(f1, f2)) {
          diff.add(relPath1.toString());
        }

        Assertions.assertTrue(f2.isFile(), "File does not exist \n\t" + f2.getAbsolutePath());
        /*assertTrue("Different output generating twice! \n" +
              "\t" + f1.getAbsolutePath() + "\n" +
              "\t" + f2.getAbsolutePath() + "\n",
            FileUtils.contentEquals(f1, f2));
         */
      }
    }
    diff.forEach(s -> System.err.println("\t " + s));
    Assertions.assertTrue(diff.isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @AfterEach
  public void tearDown() throws Exception {
    Reporting.off();
  }
}
