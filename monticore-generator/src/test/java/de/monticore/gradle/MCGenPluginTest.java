/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.gradle.testkit.runner.TaskOutcome.FROM_CACHE;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;

/**
 * Test if the plugin correctly configures a gradle project
 * and uses the build cache.
 * <p>
 * The StatisticListeners output is also tested
 * <p>
 * Note: Due to the JUnit version used, we are unable to use parameterized tests
 */
public class MCGenPluginTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
  File testProjectDir;
  File settingsFile;
  File propertiesFile;
  File buildFile;
  File grammarDir;

  @Before
  public void setup() throws IOException {
    testProjectDir = temporaryFolder.newFolder();
    settingsFile = new File(testProjectDir, "settings.gradle");
    buildFile = new File(testProjectDir, "build.gradle");
    propertiesFile = new File(testProjectDir, "gradle.properties");
    grammarDir = new File(testProjectDir, "src/main/grammars");
  }

  @Test
  public void testCanApplyPlugin_v7_4_2() throws IOException {
    this.testCanApplyPlugin("7.4.2");
  }

  @Test
  public void testCanApplyPlugin_v7_6_4() throws IOException {
    this.testCanApplyPlugin("7.6.4");
  }

  @Test
  public void testCanApplyPlugin_v8_0_1() throws IOException {
    this.testCanApplyPlugin("8.0.1");
  }

  @Test
  public void testCanApplyPlugin_v8_7() throws IOException {
    this.testCanApplyPlugin("8.7");
  }


  // Test if the plugin can be applied
  void testCanApplyPlugin(String version) throws IOException {
    writeFile(settingsFile, "rootProject.name = 'hello-world'");
    String buildFileContent = "plugins {" +
            "    id 'de.monticore.generator' " +
            "}";
    writeFile(buildFile, buildFileContent);

    BuildResult result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments("tasks")
            .build();

    // A generateMCGrammars task was added
    Assert.assertTrue(result.getOutput().contains("generateMCGrammars"));
    Assert.assertEquals(SUCCESS, result.task(":tasks").getOutcome());
  }


  //////////////////////
  @Test
  public void testGenerateGrammar_v7_4_2() throws IOException {
    this.testGenerateGrammar("7.4.2");
  }

  @Test
  public void testGenerateGrammar_v7_6_4() throws IOException {
    this.testGenerateGrammar("7.6.4");
  }

  @Test
  public void testGenerateGrammar_v8_0_1() throws IOException {
    this.testGenerateGrammar("8.0.1");
  }

  @Test
  public void testGenerateGrammar_v8_7() throws IOException {
    this.testGenerateGrammar("8.7");
  }

  // Test if the generate task succeeds
  // and is cacheable
  // and up-to-date-checks work on modified files
  // and up-to-date-checks work on modified super files
  void testGenerateGrammar(String version) throws IOException {
    writeFile(settingsFile, "rootProject.name = 'hello-world'");
    writeFile(propertiesFile, "de.monticore.gradle.show_performance_statistic=true");
    String buildFileContent = "plugins {" +
            "    id 'de.monticore.generator' " +
            "}";
    writeFile(buildFile, buildFileContent);
    // Note: We are unable to load MCBasics or compile,
    // as the monticore-grammar dependency might not be available yet
    writeFile(new File(grammarDir, "MyTestGrammar.mc4"),
            "grammar MyTestGrammar { Monti = \"Core\"; }");
    writeFile(new File(grammarDir, "MyTestGrammarS.mc4"),
            "grammar MyTestGrammarS extends MyTestGrammar { Monti = \"Core\"; }");


    BuildResult result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments("generateMCGrammars", "--build-cache", "--info")
            .build();

    // file MyTestGrammar is worked on
    Assert.assertTrue(result.getOutput(), result.getOutput().contains("[MyTestGrammar.mc4]"));
    // file MyTestGrammarS is worked on
    Assert.assertTrue(result.getOutput(), result.getOutput().contains("[MyTestGrammarS.mc4]"));
    // and the task was successful
    Assert.assertEquals(SUCCESS, result.task(":generateMCGrammars").getOutcome());

    JsonObject taskStats = checkAndGetStats(result.getOutput(), "generateMCGrammars");
    Assert.assertFalse(taskStats.getBooleanMember("UpToDate"));
    Assert.assertFalse(taskStats.getBooleanMember("Cached"));
    Assert.assertFalse(taskStats.getBooleanMember("hasError"));
    Assert.assertEquals("de.monticore.gradle.gen.MCGenTask_Decorated", taskStats.getStringMember("Type"));

    // Test build-cache, by first deleting the build dir
    de.se_rwth.commons.Files.deleteFiles(new File(testProjectDir, "build"));
    // and run again
    result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments("generateMCGrammars", "--build-cache", "--info")
            .build();
    // and then check, that the build cache was used
    Assert.assertEquals("generateMCGrammars was not cached",
            FROM_CACHE, result.task(":generateMCGrammars").getOutcome());

    taskStats = checkAndGetStats(result.getOutput(), "generateMCGrammars");
    Assert.assertTrue(taskStats.getBooleanMember("UpToDate"));
    Assert.assertTrue(taskStats.getBooleanMember("Cached"));
    Assert.assertFalse(taskStats.getBooleanMember("hasError"));
    Assert.assertEquals("de.monticore.gradle.gen.MCGenTask_Decorated", taskStats.getStringMember("Type"));


    // Next, test up-to-date checks:
    // by changing MyTestGrammarS
    writeFile(new File(grammarDir, "MyTestGrammarS.mc4"),
            "grammar MyTestGrammarS extends MyTestGrammar { Monti = \"Core2\"; }");
    // and run again
    result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments("generateMCGrammars", "--build-cache", "--info")
            .build();
    // and the task was successful
    Assert.assertEquals(SUCCESS, result.task(":generateMCGrammars").getOutcome());
    // Only MyTestGrammarS SHOULD not be up-to-date
    Assert.assertTrue(result.getOutput(), result.getOutput().contains("MyTestGrammar.mc4 is UP-TO-DATE, no action required"));
    Assert.assertFalse(result.getOutput(), result.getOutput().contains("MyTestGrammarS.mc4 is UP-TO-DATE, no action required"));

    taskStats = checkAndGetStats(result.getOutput(), "generateMCGrammars");
    Assert.assertFalse(taskStats.getBooleanMember("UpToDate")); // Note: The task is not up-to-date, as one of its inputs has changed
    Assert.assertFalse(taskStats.getBooleanMember("Cached"));
    Assert.assertFalse(taskStats.getBooleanMember("hasError"));
    Assert.assertEquals("de.monticore.gradle.gen.MCGenTask_Decorated", taskStats.getStringMember("Type"));


    // and change MyTestGrammar
    writeFile(new File(grammarDir, "MyTestGrammar.mc4"),
            "grammar MyTestGrammar { Monti = \"Core2\"; }");
    // and run again
    result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments("generateMCGrammars", "--build-cache", "--info")
            .build();
    // Nothing SHOULD not be up-to-date
    Assert.assertFalse(result.getOutput(), result.getOutput().contains("MyTestGrammar.mc4 is UP-TO-DATE, no action required"));
    Assert.assertFalse(result.getOutput(), result.getOutput().contains("MyTestGrammarS.mc4 is UP-TO-DATE, no action required"));

    taskStats = checkAndGetStats(result.getOutput(), "generateMCGrammars");
    Assert.assertFalse(taskStats.getBooleanMember("UpToDate"));
    Assert.assertFalse(taskStats.getBooleanMember("Cached"));
    Assert.assertFalse(taskStats.getBooleanMember("hasError"));
    Assert.assertEquals("de.monticore.gradle.gen.MCGenTask_Decorated", taskStats.getStringMember("Type"));

  }


  void writeFile(File destination, String content) throws IOException {
    destination.getParentFile().mkdirs();
    destination.createNewFile();
    Files.write(destination.toPath(), Collections.singleton(content));
  }

  JsonObject checkAndGetStats(String output, String taskName) {
    Optional<String> statLine = Arrays.stream(output.split(System.lineSeparator()))
            .filter(l -> l.startsWith("{\"Tasks\":")).findFirst();
    Assert.assertTrue(output, statLine.isPresent());
    JsonObject stats = JsonParser.parseJsonObject(statLine.get());
    for (var task : stats.getArrayMember("Tasks")) {
      if (taskName.equals(task.getAsJsonObject().getStringMember("Name"))) {
        return task.getAsJsonObject();
      }
    }
    Assert.fail("Task " + taskName + " was not found within the stats");
    return null;
  }
}
