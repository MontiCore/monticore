/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.tooling.internal.consumer.DefaultGradleConnector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the up-to-date checks and incremental behavior of the {@link MontiTransExec} task
 * using a fake TFGenTool, which generates two files per model.
 */
@Execution(ExecutionMode.SAME_THREAD)
public class MontiTransExecTest {

  /** A TFGenTool mimicking the CLI of the generated TFGenTools. */
  static final String FAKE_TOOL = """
      import java.nio.file.*;

      public class FakeTFGen {
        public static void main(String[] args) throws Exception {
          Path model = Paths.get(args[1]);
          Path out = Paths.get(args[3]);
          String name = model.getFileName().toString().replace(".mtr", "");
          String content = Files.readString(model);
          if (content.contains("THROW")) {
            throw new IllegalStateException("Invalid model " + name);
          }
          if (content.contains("LOGERROR")) {
            de.se_rwth.commons.logging.Log.error("0xFAKE Invalid model " + name);
            return;
          }
          System.out.println("FAKEGEN " + name);
          Path pkg = out.resolve("gen").resolve(name.toLowerCase());
          Files.createDirectories(pkg);
          Files.writeString(pkg.resolve(name + ".java"), content);
          Files.writeString(pkg.resolve(name + "Helper.java"), content);
        }
      }
      """;

  /** Mimics the Log of the TFGenTools, which logs errors without throwing (fail quick disabled). */
  static final String FAKE_LOG = """
      package de.se_rwth.commons.logging;

      public class Log {
        static long errors = 0;
        public static void error(String msg) { errors++; System.out.println(msg); }
        public static long getErrorCount() { return errors; }
      }
      """;

  static final List<Path> workingDirs = new ArrayList<>();

  File projectDir;
  File modelsDir;
  File genDir;

  @BeforeEach
  void setup() throws IOException {
    // Not using @TempDir, as the TestKit daemon may still hold file handles (see gradle/gradle#12535)
    projectDir = Files.createTempDirectory(getClass().getSimpleName()).toFile();
    workingDirs.add(projectDir.toPath());
    modelsDir = new File(projectDir, "models");
    genDir = new File(projectDir, "build/gen/gen");

    write("settings.gradle", "rootProject.name = 'montitrans-test'\ninclude 'tool'");
    // the fake tool is a separate project without the MontiCore plugin
    write("tool/build.gradle", "plugins { id 'java' }");
    write("tool/src/main/java/FakeTFGen.java", FAKE_TOOL);
    write("tool/src/main/java/de/se_rwth/commons/logging/Log.java", FAKE_LOG);
  }

  @AfterAll
  static void cleanup() {
    DefaultGradleConnector.close();
    for (Path dir : workingDirs) {
      FileUtils.deleteQuietly(dir.toFile());
    }
  }

  void writeBuildFile(String taskConfig) throws IOException {
    write("build.gradle", """
        plugins {
          id 'de.monticore.generator'
        }
        evaluationDependsOn(':tool')

        tasks.register('generateTR', de.monticore.MontiTransExec) {
          classPath.from(project(':tool').sourceSets.main.output)
          TFGenTool = 'FakeTFGen'
          outputDir = layout.buildDirectory.dir('gen')
        """ + taskConfig + """
        }
        """);
  }

  @Test
  void testIncrementalGeneration() throws IOException {
    writeBuildFile("  models.from(fileTree('models') { include '**/*.mtr' })");
    write("models/A.mtr", "a");
    write("models/sub/B.mtr", "b");

    // initial run: all models are generated
    BuildResult result = run();
    assertEquals(SUCCESS, result.task(":generateTR").getOutcome());
    assertEquals(List.of("A", "B"), generated(result));
    assertGenerated("A", "B");

    // nothing changed: up-to-date
    result = run();
    assertEquals(UP_TO_DATE, result.task(":generateTR").getOutcome());

    // a modified model: only it is regenerated
    write("models/A.mtr", "a2");
    result = run();
    assertEquals(SUCCESS, result.task(":generateTR").getOutcome());
    assertEquals(List.of("A"), generated(result));
    assertEquals("a2", read("a/A.java"));

    // an added model: only it is generated
    write("models/C.mtr", "c");
    result = run();
    assertEquals(List.of("C"), generated(result));
    assertGenerated("A", "B", "C");

    // a removed model: nothing is generated, its outputs are kept (known issue)
    Files.delete(modelsDir.toPath().resolve("sub/B.mtr"));
    result = run();
    assertEquals(SUCCESS, result.task(":generateTR").getOutcome());
    assertEquals(List.of(), generated(result));
    assertGenerated("A", "B", "C");

    // a renamed model: only the new name is generated
    Files.move(modelsDir.toPath().resolve("C.mtr"), modelsDir.toPath().resolve("D.mtr"));
    result = run();
    assertEquals(List.of("D"), generated(result));
    assertGenerated("A", "B", "C", "D");

    // a deleted output: non-incremental, Gradle removes the previous outputs
    // (including those of removed models) and everything is regenerated
    Files.delete(genDir.toPath().resolve("a/A.java"));
    result = run();
    assertEquals(List.of("A", "D"), generated(result));
    assertGenerated("A", "D");

    // modified TFGenTool classpath: non-incremental, everything is regenerated
    write("tool/src/main/java/Other.java", "public class Other {}");
    result = run();
    assertEquals(List.of("A", "D"), generated(result));
    assertGenerated("A", "D");

    // no models anymore: Gradle removes all outputs
    FileUtils.deleteDirectory(modelsDir);
    run();
    assertGenerated();
  }

  @Test
  void testLegacySingleInput() throws IOException {
    writeBuildFile("  input = file('models/A.mtr')");
    write("models/A.mtr", "a");

    BuildResult result = run();
    assertEquals(SUCCESS, result.task(":generateTR").getOutcome());
    assertEquals(List.of("A"), generated(result));
    assertGenerated("A");

    result = run();
    assertEquals(UP_TO_DATE, result.task(":generateTR").getOutcome());

    write("models/A.mtr", "a2");
    result = run();
    assertEquals(List.of("A"), generated(result));
    assertEquals("a2", read("a/A.java"));
  }

  @Test
  void testToolException() throws IOException {
    writeBuildFile("  models.from(fileTree('models') { include '**/*.mtr' })");
    write("models/A.mtr", "THROW");

    BuildResult result = runner().buildAndFail();
    assertTrue(result.getOutput().contains("The TFGenTool failed for"), result.getOutput());
    assertTrue(result.getOutput().contains("Invalid model A"), result.getOutput());
  }

  @Test
  void testToolLogsError() throws IOException {
    writeBuildFile("  models.from(fileTree('models') { include '**/*.mtr' })");
    write("models/A.mtr", "LOGERROR");

    BuildResult result = runner().buildAndFail();
    assertTrue(result.getOutput().contains("The TFGenTool reported errors for"), result.getOutput());

    // the task is not up-to-date after a failure
    result = runner().buildAndFail();
    assertTrue(result.getOutput().contains("The TFGenTool reported errors for"), result.getOutput());

    write("models/A.mtr", "a");
    result = run();
    assertEquals(SUCCESS, result.task(":generateTR").getOutcome());
    assertEquals(List.of("A"), generated(result));
  }

  /* ======================================================================
   * Helpers
   * ====================================================================== */

  GradleRunner runner() {
    return GradleRunner.create()
        .withPluginClasspath()
        .withProjectDir(projectDir)
        .withArguments("generateTR", "--stacktrace");
  }

  BuildResult run() {
    return runner().build();
  }

  /** @return the models, for which the fake TFGenTool has been invoked */
  static List<String> generated(BuildResult result) {
    List<String> models = new ArrayList<>();
    Matcher m = Pattern.compile("^FAKEGEN (\\w+)", Pattern.MULTILINE).matcher(result.getOutput());
    while (m.find()) {
      models.add(m.group(1));
    }
    models.sort(null);
    return models;
  }

  /** Asserts that exactly the outputs of the given models exist */
  void assertGenerated(String... models) throws IOException {
    List<String> expected = new ArrayList<>();
    for (String model : models) {
      expected.add(model.toLowerCase() + "/" + model + ".java");
      expected.add(model.toLowerCase() + "/" + model + "Helper.java");
    }
    expected.sort(null);

    List<String> actual = new ArrayList<>();
    if (genDir.exists()) {
      try (var files = Files.walk(genDir.toPath())) {
        files.filter(Files::isRegularFile)
            .forEach(f -> actual.add(genDir.toPath().relativize(f).toString().replace('\\', '/')));
      }
    }
    actual.sort(null);
    assertEquals(expected, actual);
  }

  String read(String generatedFile) throws IOException {
    return Files.readString(genDir.toPath().resolve(generatedFile), StandardCharsets.UTF_8);
  }

  void write(String path, String content) throws IOException {
    File file = new File(projectDir, path);
    file.getParentFile().mkdirs();
    Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
  }

}
