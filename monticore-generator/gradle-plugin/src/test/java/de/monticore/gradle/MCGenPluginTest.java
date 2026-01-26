/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.tooling.internal.consumer.ConnectorServices;
import org.gradle.tooling.internal.consumer.DefaultGradleConnector;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.gradle.testkit.runner.TaskOutcome.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test if the plugin correctly configures a gradle project
 * and uses the build cache.
 * <p>
 * The StatisticListeners output is also tested
 * <p>
 * Note: Due to the JUnit version used, we are unable to use parameterized tests
 */
@Execution(ExecutionMode.SAME_THREAD) // Do not run in parallel, too memory hungry
@NotThreadSafe // Technically thread safe, just memory hungry
public class MCGenPluginTest {
  // TODO: Use @TempDir instead of manual creation and deletion of the temporary folder
  // see: https://github.com/gradle/gradle/issues/12535
  // @TempDir
  public Path temporaryFolder;
  File testProjectDir;
  File settingsFile;
  File propertiesFile;
  File buildFile;
  File grammarDir;
  
  // TODO: Remove when using TempDir
  private static List<Path> workingDirs = new ArrayList<>();

  @BeforeEach
  public void setup() throws IOException {
    testProjectDir = createDirectory(temporaryFolder.resolve("projectDir"));
    settingsFile = new File(testProjectDir, "settings.gradle");
    buildFile = new File(testProjectDir, "build.gradle");
    propertiesFile = new File(testProjectDir, "gradle.properties");
    grammarDir = new File(testProjectDir, "src/main/grammars");
  }
  
  // TODO: Remove when using TempDir
  @BeforeEach
  void createWorkspace() throws IOException{
    this.temporaryFolder = Files.createTempDirectory(getClass().getSimpleName());
    workingDirs.add(this.temporaryFolder);
  }
  
  // TODO: Remove when using TempDir
  @AfterEach
  void resetGradleConnector() {
    ConnectorServices.reset();
  }
  
  // TODO: Remove when using TempDir
  @AfterAll
  static void deleteWorkspace() throws IOException, InterruptedException {
    DefaultGradleConnector.close();
    Thread.sleep(100);
    for (Path workingDir : workingDirs) {
      FileUtils.forceDelete(workingDir.toFile());
    }
  }
  
  File createDirectory(Path path) throws IOException{
    return Files.createDirectory(path).toFile();
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
    assertTrue(result.getOutput().contains("generateMCGrammars"));
    assertEquals(SUCCESS, result.task(":tasks").getOutcome());
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
    writeFile(propertiesFile, "de.monticore.gradle.show_performance_statistic=true\norg.gradle.jvmargs=-XX:MaxMetaspaceSize=1g\n");
    String buildFileContent = "plugins {\n" +
            "    id 'de.monticore.generator' \n" +
            "}\n" +
            createMCToolDependency();
    writeFile(buildFile, buildFileContent);
    // Note: We are unable to load MCBasics or compile,
    // as the monticore-grammar dependency might not be available yet
    writeFile(new File(grammarDir, "MyTestGrammar.mc4"),
            "grammar MyTestGrammar { Monti = \"Core\"; }");
    writeFile(new File(grammarDir, "MyTestGrammarS.mc4"),
            "grammar MyTestGrammarS extends MyTestGrammar { Monti = \"Core\"; }");

    // use a custom gradle home directory to ensure fresh caches
    File gradleHome = createDirectory(temporaryFolder.resolve("gradleHome"));

    BuildResult result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments(withProperties("generateMCGrammars", "--build-cache", "--info", "--stacktrace", "-g", gradleHome.getAbsolutePath()))
            .build();

    // file MyTestGrammar is worked on
//    assertTrue(result.getOutput(), result.getOutput().contains("[MyTestGrammar.mc4]"));  // The Log-Prefix is unreliable
    assertTrue(result.getOutput().contains("/src/main/grammars/MyTestGrammar.mc4"), result.getOutput());
    // file MyTestGrammarS is worked on
//    assertTrue(result.getOutput(), result.getOutput().contains("[MyTestGrammarS.mc4]")); // The Log-Prefix is unreliable
    assertTrue(result.getOutput().contains("/src/main/grammars/MyTestGrammarS.mc4"), result.getOutput());
    // and the task was successful
    assertEquals(SUCCESS, result.task(":generateMCGrammars").getOutcome());

    JsonObject taskStats = checkAndGetStats(result.getOutput(), ":generateMCGrammars");
    assertFalse(taskStats.getBooleanMember("UpToDate"));
    assertFalse(taskStats.getBooleanMember("Cached"));
    assertFalse(taskStats.getBooleanMember("hasError"));
    assertEquals("de.monticore.gradle.gen.MCGenTask_Decorated", taskStats.getStringMember("Type"));

    // Test build-cache, by first deleting the build dir
    de.se_rwth.commons.Files.deleteFiles(new File(testProjectDir, "build"));
    // and run again
    result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments(withProperties("generateMCGrammars", "--build-cache", "--info", "-g", gradleHome.getAbsolutePath()))
            .build();
    // and then check, that the build cache was used
    assertEquals(FROM_CACHE, result.task(":generateMCGrammars").getOutcome(),
        "generateMCGrammars was not cached");

    taskStats = checkAndGetStats(result.getOutput(), ":generateMCGrammars");
    assertTrue(taskStats.getBooleanMember("UpToDate"));
    assertTrue(taskStats.getBooleanMember("Cached"));
    assertFalse(taskStats.getBooleanMember("hasError"));
    assertEquals("de.monticore.gradle.gen.MCGenTask_Decorated", taskStats.getStringMember("Type"));


    // Next, test up-to-date checks:
    // by changing MyTestGrammarS
    writeFile(new File(grammarDir, "MyTestGrammarS.mc4"),
            "grammar MyTestGrammarS extends MyTestGrammar { Monti = \"Core2\"; }");
    // and run again
    result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments(withProperties("generateMCGrammars", "--build-cache", "--info", "-g", gradleHome.getAbsolutePath()))
            .build();
    // and the task was successful
    assertEquals(SUCCESS, result.task(":generateMCGrammars").getOutcome());
    // Only MyTestGrammarS SHOULD not be up-to-date
    assertTrue(result.getOutput().contains("MyTestGrammar.mc4 is UP-TO-DATE, no action required"),
        result.getOutput());
    assertFalse(result.getOutput().contains("MyTestGrammarS.mc4 is UP-TO-DATE, no action required"),
        result.getOutput());

    taskStats = checkAndGetStats(result.getOutput(), ":generateMCGrammars");
    assertFalse(taskStats.getBooleanMember("UpToDate")); // Note: The task is not up-to-date, as one of its inputs has changed
    assertFalse(taskStats.getBooleanMember("Cached"));
    assertFalse(taskStats.getBooleanMember("hasError"));
    assertEquals("de.monticore.gradle.gen.MCGenTask_Decorated", taskStats.getStringMember("Type"));


    // and change MyTestGrammar
    writeFile(new File(grammarDir, "MyTestGrammar.mc4"),
            "grammar MyTestGrammar { Monti = \"Core2\"; }");
    // and run again
    result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments(withProperties("generateMCGrammars", "--build-cache", "--info", "-g", gradleHome.getAbsolutePath()))
            .build();
    // Nothing SHOULD not be up-to-date
    assertFalse(result.getOutput().contains("MyTestGrammar.mc4 is UP-TO-DATE, no action required"), result.getOutput());
    assertFalse(result.getOutput().contains("MyTestGrammarS.mc4 is UP-TO-DATE, no action required"), result.getOutput());

    taskStats = checkAndGetStats(result.getOutput(), ":generateMCGrammars");
    assertFalse(taskStats.getBooleanMember("UpToDate"));
    assertFalse(taskStats.getBooleanMember("Cached"));
    assertFalse(taskStats.getBooleanMember("hasError"));
    assertEquals("de.monticore.gradle.gen.MCGenTask_Decorated", taskStats.getStringMember("Type"));

  }

  //////////////////////
  @Test
  public void testMultiProject_v7_4_2() throws IOException {
    this.testMultiProject("7.4.2");
  }

  @Test
  public void testMultiProject_v7_6_4() throws IOException {
    this.testMultiProject("7.6.4");
  }

  @Test
  public void testMultiProject_v8_0_1() throws IOException {
    this.testMultiProject("8.0.1");
  }

  @Test
  public void testMultiProject_v8_7() throws IOException {
    this.testMultiProject("8.7");
  }

  // Test if the generate task succeeds within a multi-project build
  // and is cacheable
  // and up-to-date-checks work on modified files
  // and up-to-date-checks work on modified super files
  void testMultiProject(String version) throws IOException {
    writeFile(settingsFile, "rootProject.name = 'hello-world'\ninclude('A')\ninclude('B')");
    writeFile(propertiesFile, "de.monticore.gradle.show_performance_statistic=true\norg.gradle.jvmargs=-XX:MaxMetaspaceSize=1g\n");
    String buildFileContentA = "plugins {" +
            "    id 'de.monticore.generator' \n" +
            "    id 'maven-publish' \n" +
            "}\n" +
            "publishing { " +
            "  publications { " +
            "    maven(MavenPublication) {\n" +
            "      groupId = 'de.mc.test'\n" +
            "      artifactId = 'A'\n" +
            "      version = '0.1'\n" +
            "      from components.java\n" +
            "    }" +
            "  }" +
            "}\n" + createMCToolDependency();
    var aDir = new File(testProjectDir, "A");
    writeFile(new File(aDir, "build.gradle"), buildFileContentA);
    // Note: We are unable to load MCBasics or compile,
    // as the monticore-grammar dependency might not be available yet
    writeFile(new File(new File(aDir, "src/main/grammars"), "MyTestGrammar.mc4"),
              "grammar MyTestGrammar { Monti = \"Core\"; }");

    String buildFileContentB = "plugins {" +
            "    id 'de.monticore.generator' \n" +
            "}\n" +
            "dependencies { " +
            "  grammar(project(':A')) " +
            "}\n"
            + createMCToolDependency();
    var bDir = new File(testProjectDir, "B");
    writeFile(new File(bDir, "build.gradle"), buildFileContentB);


    writeFile(new File(new File(bDir, "src/main/grammars"), "MyTestGrammarS.mc4"),
              "grammar MyTestGrammarS extends MyTestGrammar { Monti = \"Core\"; }");

    // use a custom gradle home directory to ensure fresh cashes
    File gradleHome = createDirectory(temporaryFolder.resolve("gradleHome"));

    BuildResult result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments(withProperties("generateMCGrammars", "--build-cache", "--info", "-g", gradleHome.getAbsolutePath()))
            .build();

    // file MyTestGrammar is worked on
    //    assertTrue(result.getOutput(), result.getOutput().contains("[MyTestGrammar.mc4]"));  // The Log-Prefix is unreliable
    assertTrue(result.getOutput().contains("/src/main/grammars/MyTestGrammar.mc4"), result.getOutput());
    // file MyTestGrammarS is worked on
    //    assertTrue(result.getOutput(), result.getOutput().contains("[MyTestGrammarS.mc4]"));  // The Log-Prefix is unreliable
    assertTrue(result.getOutput().contains("/src/main/grammars/MyTestGrammarS.mc4"), result.getOutput());
    // and the task was successful
    assertEquals(SUCCESS, result.task(":A:generateMCGrammars").getOutcome());
    assertEquals(SUCCESS, result.task(":B:generateMCGrammars").getOutcome());

    JsonObject taskStats = checkAndGetStats(result.getOutput(), ":A:generateMCGrammars");
    assertFalse(taskStats.getBooleanMember("UpToDate"));
    assertFalse(taskStats.getBooleanMember("Cached"));
    assertFalse(taskStats.getBooleanMember("hasError"));
    assertEquals("de.monticore.gradle.gen.MCGenTask_Decorated", taskStats.getStringMember("Type"));

    // Test build-cache, by first deleting the build dir
    de.se_rwth.commons.Files.deleteFiles(new File(testProjectDir, "build"));
    de.se_rwth.commons.Files.deleteFiles(new File(aDir, "build"));
    de.se_rwth.commons.Files.deleteFiles(new File(bDir, "build"));

    // and run again
    result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments(withProperties("generateMCGrammars", "--build-cache", "--info", "-g", gradleHome.getAbsolutePath()))
            .build();

    // and then check, that the build cache was used
    assertEquals(FROM_CACHE, result.task(":A:generateMCGrammars").getOutcome(),
        "A:generateMCGrammars was not cached");
    assertEquals(FROM_CACHE, result.task(":B:generateMCGrammars").getOutcome(),
        "B:generateMCGrammars was not cached");


    taskStats = checkAndGetStats(result.getOutput(), ":B:generateMCGrammars");
    assertTrue(taskStats.getBooleanMember("UpToDate"));
    assertTrue(taskStats.getBooleanMember("Cached"));
    assertFalse(taskStats.getBooleanMember("hasError"));
    assertEquals("de.monticore.gradle.gen.MCGenTask_Decorated", taskStats.getStringMember("Type"));


    // Next, test up-to-date checks:
    // by changing MyTestGrammarS
    writeFile(new File(new File(bDir, "src/main/grammars"), "MyTestGrammarS.mc4"),
              "grammar MyTestGrammarS extends MyTestGrammar { Monti = \"Core2\"; }");
    // and run again
    result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments(withProperties("generateMCGrammars", "--build-cache", "--info", "-g", gradleHome.getAbsolutePath()))
            .build();
    // and the B-task was successful
    assertEquals(SUCCESS, result.task(":B:generateMCGrammars").getOutcome());
    // the A-task should be up to date (i.e., not even pulled from the cache)
    assertEquals(UP_TO_DATE, result.task(":A:generateMCGrammars").getOutcome());
    //  and thus, MyTestGrammar should not be printed to the log
    assertFalse(result.getOutput().contains("MyTestGrammar.mc4 is UP-TO-DATE, no action required"), result.getOutput());
    assertFalse(result.getOutput().contains("MyTestGrammar.mc4 is *NOT* UP-TO-DATE"), result.getOutput());
    // Only MyTestGrammarS SHOULD not be up-to-date
    assertFalse(result.getOutput().contains("MyTestGrammarS.mc4 is UP-TO-DATE, no action required"), result.getOutput());
    assertTrue(result.getOutput().contains("MyTestGrammarS.mc4 is *NOT* UP-TO-DATE"), result.getOutput());

    taskStats = checkAndGetStats(result.getOutput(), ":B:generateMCGrammars");
    assertFalse(taskStats.getBooleanMember("UpToDate")); // Note: The task is not up-to-date, as one of its inputs has changed
    assertFalse(taskStats.getBooleanMember("Cached"));
    assertFalse(taskStats.getBooleanMember("hasError"));
    assertEquals("de.monticore.gradle.gen.MCGenTask_Decorated", taskStats.getStringMember("Type"));


    // and change MyTestGrammar
    writeFile(new File(new File(aDir, "src/main/grammars"), "MyTestGrammar.mc4"),
              "grammar MyTestGrammar { Monti = \"Core2\"; }");
    // and run again
    result = GradleRunner.create()
            .withPluginClasspath()
            .withGradleVersion(version)
            .withProjectDir(testProjectDir)
            .withArguments(withProperties("generateMCGrammars", "--build-cache", "--info", "-g", gradleHome.getAbsolutePath()))
            .build();
    // Nothing SHOULD not be up-to-date
    assertFalse(result.getOutput().contains("MyTestGrammar.mc4 is UP-TO-DATE, no action required"), result.getOutput());
    assertFalse(result.getOutput().contains("MyTestGrammarS.mc4 is UP-TO-DATE, no action required"), result.getOutput());

    taskStats = checkAndGetStats(result.getOutput(), ":A:generateMCGrammars");
    assertFalse(taskStats.getBooleanMember("UpToDate"));
    assertFalse(taskStats.getBooleanMember("Cached"));
    assertFalse(taskStats.getBooleanMember("hasError"));
    assertEquals("de.monticore.gradle.gen.MCGenTask_Decorated", taskStats.getStringMember("Type"));

    taskStats = checkAndGetStats(result.getOutput(), ":B:generateMCGrammars");
    assertFalse(taskStats.getBooleanMember("UpToDate"));
    assertFalse(taskStats.getBooleanMember("Cached"));
    assertFalse(taskStats.getBooleanMember("hasError"));
    assertEquals("de.monticore.gradle.gen.MCGenTask_Decorated", taskStats.getStringMember("Type"));
  }


  void writeFile(File destination, String content) throws IOException {
    destination.getParentFile().mkdirs();
    destination.createNewFile();
    Files.write(destination.toPath(), Collections.singleton(content));
  }

  JsonObject checkAndGetStats(String output, String taskPath) {
    // In case multiple subprojects have reported stats, we have to check all possible json objects
    for (String statLine : output.split(System.lineSeparator())) {
      if(!statLine.startsWith("{\"Tasks\":")) continue;
      JsonObject stats = JsonParser.parseJsonObject(statLine);
      for (var task : stats.getArrayMember("Tasks")) {
        if (taskPath.equals(task.getAsJsonObject().getStringMember("Path"))) {
          return task.getAsJsonObject();
        }
      }
    }
    System.err.println(output);
    fail("Task " + taskPath + " was not found within the stats");
    return null;
  }


  Properties loadProperties() {
    Properties properties = new Properties();
    try {
      properties.load(this.getClass().getClassLoader().getResourceAsStream("buildInfo.properties"));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    return properties;
  }

  List<String> withProperties(String... args) {
    return withProperties(Arrays.asList(args));
  }

  List<String> withProperties(List<String> runnerArgs) {
    List<String> ret = new ArrayList<>(runnerArgs);
    @Nullable
    String mavenRepo = System.getProperty("maven.repo.local");
    if (mavenRepo != null && !mavenRepo.isEmpty()) {
      ret.add("-Dmaven.repo.local=" + mavenRepo + "");
    }
    @Nullable
    String useLocalRepo = System.getProperty("useLocalRepo");
    if (useLocalRepo != null && !useLocalRepo.isEmpty()) {
      ret.add("-PuseLocalRepo=" + useLocalRepo);
    }
    return ret;
  }

  String createMCToolDependency() {
    String projVersion = loadProperties().getProperty("version");
    File mcGenToolJar = new File(new File("../target/libs/"), "monticore-generator-" + projVersion + "-mc-tool.jar");
    return  "repositories {\n" + " if ((\"true\").equals(getProperty('useLocalRepo'))) {\n "
            + "  mavenLocal()\n" + " }\n"
            + " maven{ url  'https://nexus.se.rwth-aachen.de/content/groups/public' }\n"
            + " mavenCentral()\n" + "}\n" +
            // We have to inject the cdlang jar for this project (as it is not yet published)
            "dependencies {\n" + " mcTool files('" + mcGenToolJar.getAbsolutePath().replace("\\", "\\\\")
            + "')\n"
            + "}\n";
  }
}
