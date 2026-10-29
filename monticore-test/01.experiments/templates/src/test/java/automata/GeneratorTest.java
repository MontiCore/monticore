/* (c) https://github.com/MontiCore/monticore */
package automata;

import com.google.common.io.Resources;

import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.reporter.TemplateSourceMappingReporter;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.sourcemap.DecodedMapping;
import de.monticore.sourcemap.DecodedSource;
import de.monticore.sourcemap.DecodedSourceMap;
import de.monticore.sourcemap.Decoding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class GeneratorTest {

  @BeforeEach
  public void init(){
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void clearFindings(){
    Log.getFindings().clear();
  }

  @BeforeEach
  public void cleanTargetDirectory() throws IOException {
    FileUtils.cleanDirectory(new File("target/statepattern"));
  }

  /* We execute the Generator here, but do not really test it:
   * (instead we use tests against the generated classes)
   */
  @Test
  public void testPingPong(){
    TemplatesTool.main(new String[] { "src/test/resources/example/PingPong.aut", "src/product/java", "target/statepattern" });
    assertEquals(0, Log.getErrorCount());
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testSimple12(){
    TemplatesTool.main(new String[] { "src/test/resources/example/Simple12.aut","src/product/java", "target/statepattern" });
    assertEquals(0, Log.getErrorCount());
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testWrongArguments(){
    TemplatesTool.main(new String[] {  });
    assertEquals(1, Log.getErrorCount());
    assertEquals("0xEE631 Please specify at least 3 arguments: \n"
        + "1. automata modelfile,\n"
        + "2. handcodedPath,\n"
        + "3. output directory.\n"
        + "4. (optional) templatePath\n"
        + "5. (optional) tracing", Log.getFindings().get(0).buildMsg());
  }

  @Test
  public void testSourceMapPingPong() throws IOException {
    try {
      ReportManager.ReportManagerFactory factory = modelName -> {
        ReportManager reports = new ReportManager("target/statepattern");
        reports.addReportEventHandler(new TemplateSourceMappingReporter("target/statepattern", modelName, false));
        return reports;
      };
      Reporting.init("target/statepattern", factory);
      Reporting.on("PingPong");
      TemplatesTool.main(new String[] { "src/test/resources/example/PingPong.aut", "src/product/java", "target/statepattern" });
      assertEquals(0, Log.getErrorCount());
      MCAssertions.assertNoFindings();

      File mapFile = new File("target/statepattern/AbstractPingPongState.java.map");
      assertTrue(mapFile.exists());
      String json = Files.readString(mapFile.toPath());
      DecodedSourceMap decoded = Decoding.parseSourceMapJson(json, null);


      URL templateUrl = getClass().getClassLoader().getResource("AbstractState.ftl");
      DecodedSource src = new DecodedSource(templateUrl);

      List<DecodedMapping> mappings = getMappingsForAbstractPingPongState(src, true);

      File generatedFile = new File("target/statepattern/AbstractPingPongState.java");
      DecodedSourceMap expected = new DecodedSourceMap(generatedFile.getAbsolutePath(), List.of(src), mappings);

      assertEquals(expected, decoded);

    } finally {
      Reporting.off();
    }
  }

  @Test
  public void testSourceMapPingPongWithoutTracing() throws IOException {
    try {
      ReportManager.ReportManagerFactory factory = modelName -> {
        ReportManager reports = new ReportManager("target/statepattern");
        reports.addReportEventHandler(new TemplateSourceMappingReporter("target/statepattern", modelName, true));
        return reports;
      };
      Reporting.init("target/statepattern", factory);
      Reporting.on("PingPong");
      TemplatesTool.main(new String[] { "src/test/resources/example/PingPong.aut", "src/product/java", "target/statepattern", "false" });
      assertEquals(0, Log.getErrorCount());
      MCAssertions.assertNoFindings();

      File mapFile = new File("target/statepattern/AbstractPingPongState.java.map");
      assertTrue(mapFile.exists());
      String json = Files.readString(mapFile.toPath());
      DecodedSourceMap decoded = Decoding.parseSourceMapJson(json, null);


      URL templateUrl = getClass().getClassLoader().getResource("AbstractState.ftl");
      String templateContent = Resources.toString(templateUrl, StandardCharsets.UTF_8);
      DecodedSource src = new DecodedSource(templateUrl, templateContent, false);

      List<DecodedMapping> mappings = getMappingsForAbstractPingPongState(src, false);

      File generatedFile = new File("target/statepattern/AbstractPingPongState.java");
      DecodedSourceMap expected = new DecodedSourceMap(generatedFile.getAbsolutePath(), List.of(src), mappings);

      assertEquals(expected, decoded);

    } finally {
      Reporting.off();
    }
  }

  @Test
  public void testSourceMapPingPongWithoutContent() throws IOException {
    try {
      ReportManager.ReportManagerFactory factory = modelName -> {
        ReportManager reports = new ReportManager("target/statepattern");
        reports.addReportEventHandler(new TemplateSourceMappingReporter("target/statepattern", modelName, false));
        return reports;
      };
      Reporting.init("target/statepattern", factory);
      Reporting.on("PingPong");
      TemplatesTool.main(new String[] { "src/test/resources/example/PingPong.aut", "src/product/java", "target/statepattern", "false" });
      assertEquals(0, Log.getErrorCount());
      MCAssertions.assertNoFindings();

      File mapFile = new File("target/statepattern/AbstractPingPongState.java.map");
      assertTrue(mapFile.exists());
      String json = Files.readString(mapFile.toPath());
      DecodedSourceMap decoded = Decoding.parseSourceMapJson(json, null);


      URL templateUrl = getClass().getClassLoader().getResource("AbstractState.ftl");
      DecodedSource src = new DecodedSource(templateUrl);

      List<DecodedMapping> mappings = getMappingsForAbstractPingPongState(src, false);

      File generatedFile = new File("target/statepattern/AbstractPingPongState.java");
      DecodedSourceMap expected = new DecodedSourceMap(generatedFile.getAbsolutePath(), List.of(src), mappings);

      assertEquals(expected, decoded);

    } finally {
      Reporting.off();
    }
  }

  @Test
  public void testDisabledSourceMap(){
    try {
      ReportManager.ReportManagerFactory factory = modelName -> {
        ReportManager reports = new ReportManager("target/statepattern");
        return reports;
      };
      Reporting.init("target/statepattern", factory);
      Reporting.on("PingPong");
      TemplatesTool.main(new String[] { "src/test/resources/example/PingPong.aut", "src/product/java", "target/statepattern", "false" });
      assertEquals(0, Log.getErrorCount());
      MCAssertions.assertNoFindings();

      File mapFile = new File("target/statepattern/AbstractPingPongState.java.map");
      assertFalse(mapFile.exists());

    } finally {
      Reporting.off();
    }
  }

  private List<DecodedMapping> getMappingsForAbstractPingPongState(DecodedSource src, boolean isTracing){
    return List.of(
        new DecodedMapping(2 + (isTracing ? 1 : 0), 0, src, 5, 0, null),
        // Verify summary of the class
        new DecodedMapping(3 + (isTracing ? 1 : 0), 0, src, 6, 0, null),
        new DecodedMapping(4 + (isTracing ? 1 : 0), 0, src, 7, 0, null),
        new DecodedMapping(5 + (isTracing ? 1 : 0), 0, src, 8, 0, null),
        new DecodedMapping(6 + (isTracing ? 1 : 0), 0, src, 9, 0, null),
        // class definition
        new DecodedMapping(7 + (isTracing ? 1 : 0), 0, src, 10, 0, null),
        new DecodedMapping(7 + (isTracing ? 1 : 0), 22, src, 10, 22, null),
        new DecodedMapping(7 + (isTracing ? 1 : 0), 43, src, 10, 34, null),
        new DecodedMapping(8 + (isTracing ? 1 : 0), 0, src, 11, 0, null),
        // ----------------------- Start Game -----------------------
        // Summary of handleStartGame
        new DecodedMapping(10 + (isTracing ? 1 : 0), 0, src, 14, 0, null),
        new DecodedMapping(11 + (isTracing ? 1 : 0), 0, src, 15, 0, null),
        new DecodedMapping(11 + (isTracing ? 1 : 0), 24, src, 15, 24, null),
        new DecodedMapping(11 + (isTracing ? 1 : 0), 33, src, 15, 49, null),
        new DecodedMapping(12 + (isTracing ? 1 : 0), 0, src, 16, 0, null),
        new DecodedMapping(13 + (isTracing ? 1 : 0), 0, src, 17, 0, null),
        new DecodedMapping(13 + (isTracing ? 1 : 0), 12, src, 17, 12, null),
        new DecodedMapping(13 + (isTracing ? 1 : 0), 20, src, 17, 24, null),
        new DecodedMapping(14 + (isTracing ? 1 : 0), 0, src, 18, 0, null),
        // method definition
        new DecodedMapping(15 + (isTracing ? 1 : 0), 0, src, 19, 0, null),
        new DecodedMapping(15 + (isTracing ? 1 : 0), 13, src, 19, 13, null),
        new DecodedMapping(15 + (isTracing ? 1 : 0), 22, src, 19, 38, null),
        new DecodedMapping(15 + (isTracing ? 1 : 0), 23, src, 19, 39, null),
        new DecodedMapping(15 + (isTracing ? 1 : 0), 31, src, 19, 51, null),
        // method body
        new DecodedMapping(16 + (isTracing ? 1 : 0), 0, src, 20, 0, null),
        new DecodedMapping(17 + (isTracing ? 1 : 0), 0, src, 21, 0, null),
        new DecodedMapping(18 + (isTracing ? 1 : 0), 0, src, 22, 0, null),
        new DecodedMapping(19 + (isTracing ? 1 : 0), 0, src, 23, 0, null),
        // ----------------------- Stop Game -----------------------
        // Summary of handleStopGame
        new DecodedMapping(20 + (isTracing ? 1 : 0), 0, src, 14, 0, null),
        new DecodedMapping(21 + (isTracing ? 1 : 0), 0, src, 15, 0, null),
        new DecodedMapping(21 + (isTracing ? 1 : 0), 24, src, 15, 24, null),
        new DecodedMapping(21 + (isTracing ? 1 : 0), 32, src, 15, 49, null),
        new DecodedMapping(22 + (isTracing ? 1 : 0), 0, src, 16, 0, null),
        new DecodedMapping(23 + (isTracing ? 1 : 0), 0, src, 17, 0, null),
        new DecodedMapping(23 + (isTracing ? 1 : 0), 12, src, 17, 12, null),
        new DecodedMapping(23 + (isTracing ? 1 : 0), 20, src, 17, 24, null),
        new DecodedMapping(24 + (isTracing ? 1 : 0), 0, src, 18, 0, null),
        // method definition
        new DecodedMapping(25 + (isTracing ? 1 : 0), 0, src, 19, 0, null),
        new DecodedMapping(25 + (isTracing ? 1 : 0), 13, src, 19, 13, null),
        new DecodedMapping(25 + (isTracing ? 1 : 0), 21, src, 19, 38, null),
        new DecodedMapping(25 + (isTracing ? 1 : 0), 22, src, 19, 39, null),
        new DecodedMapping(25 + (isTracing ? 1 : 0), 30, src, 19, 51, null),
        // method body
        new DecodedMapping(26 + (isTracing ? 1 : 0), 0, src, 20, 0, null),
        new DecodedMapping(27 + (isTracing ? 1 : 0), 0, src, 21, 0, null),
        new DecodedMapping(28 + (isTracing ? 1 : 0), 0, src, 22, 0, null),
        new DecodedMapping(29 + (isTracing ? 1 : 0), 0, src, 23, 0, null),
        // ----------------------- Return Ball -----------------------
        // Summary of handleReturnBall
        new DecodedMapping(30 + (isTracing ? 1 : 0), 0, src, 14, 0, null),
        new DecodedMapping(31 + (isTracing ? 1 : 0), 0, src, 15, 0, null),
        new DecodedMapping(31 + (isTracing ? 1 : 0), 24, src, 15, 24, null),
        new DecodedMapping(31 + (isTracing ? 1 : 0), 34, src, 15, 49, null),
        new DecodedMapping(32 + (isTracing ? 1 : 0), 0, src, 16, 0, null),
        new DecodedMapping(33 + (isTracing ? 1 : 0), 0, src, 17, 0, null),
        new DecodedMapping(33 + (isTracing ? 1 : 0), 12, src, 17, 12, null),
        new DecodedMapping(33 + (isTracing ? 1 : 0), 20, src, 17, 24, null),
        new DecodedMapping(34 + (isTracing ? 1 : 0), 0, src, 18, 0, null),
        // method definition
        new DecodedMapping(35 + (isTracing ? 1 : 0), 0, src, 19, 0, null),
        new DecodedMapping(35 + (isTracing ? 1 : 0), 13, src, 19, 13, null),
        new DecodedMapping(35 + (isTracing ? 1 : 0), 23, src, 19, 38, null),
        new DecodedMapping(35 + (isTracing ? 1 : 0), 24, src, 19, 39, null),
        new DecodedMapping(35 + (isTracing ? 1 : 0), 32, src, 19, 51, null),
        // method body
        new DecodedMapping(36 + (isTracing ? 1 : 0), 0, src, 20, 0, null),
        new DecodedMapping(37 + (isTracing ? 1 : 0), 0, src, 21, 0, null),
        new DecodedMapping(38 + (isTracing ? 1 : 0), 0, src, 22, 0, null),
        new DecodedMapping(39 + (isTracing ? 1 : 0), 0, src, 23, 0, null),
        // closing bracket
        new DecodedMapping(41 + (isTracing ? 1 : 0), 0, src, 25, 0, null)
    );
  }
}
