/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

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
import java.util.Collections;

import static org.gradle.testkit.runner.TaskOutcome.FROM_CACHE;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;

/**
 * Test if the plugin correctly configures a gradle project
 * and uses the build cache.
 * <p>
 * Note: Due to the JUnit version used, we are unable to use parameterized tests
 */
public class MCGenPluginTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
  File testProjectDir;
  File settingsFile;
  File buildFile;
  File grammarDir;

  @Before
  public void setup() throws IOException {
    testProjectDir = temporaryFolder.newFolder();
    settingsFile = new File(testProjectDir, "settings.gradle");
    buildFile = new File(testProjectDir, "build.gradle");
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

  // Test if the generate task succeeds and is cacheable
  void testGenerateGrammar(String version) throws IOException {
    writeFile(settingsFile, "rootProject.name = 'hello-world'");
    String buildFileContent = "plugins {" +
            "    id 'de.monticore.generator' " +
            "}";
    writeFile(buildFile, buildFileContent);
    // Note: We are unable to load MCBasics or compile,
    // as the monticore-grammar dependency might not be available yet
    writeFile(new File(grammarDir, "MyTestGrammar.mc4"),
            "grammar MyTestGrammar { Monti = \"Core\"; }");


    BuildResult result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments("generateMCGrammars", "--build-cache")
            .build();

    // file MyTestGrammar is worked on
    Assert.assertTrue(result.getOutput(), result.getOutput().contains("[MyTestGrammar.mc4]"));
    // and the task was successful
    Assert.assertEquals(SUCCESS, result.task(":generateMCGrammars").getOutcome());

    // Test build-cache, by first deleting the build dir
    de.se_rwth.commons.Files.deleteFiles(new File(testProjectDir, "build"));
    // and run again
    result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments("generateMCGrammars", "--build-cache")
            .build();
    // and then check, that the build cache was used
    Assert.assertEquals("generateMCGrammars was not cached",
            FROM_CACHE, result.task(":generateMCGrammars").getOutcome());
  }


  void writeFile(File destination, String content) throws IOException {
    destination.getParentFile().mkdirs();
    destination.createNewFile();
    Files.write(destination.toPath(), Collections.singleton(content));
  }
}
