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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        + "4. (optional) templatePath", Log.getFindings().get(0).buildMsg());
  }

  @Test
  public void testSourceMapPingPongWithoutTracing() throws Exception {
    try {
      ReportManager.ReportManagerFactory factory = modelName -> {
        ReportManager reports = new ReportManager("target/statepattern");
        reports.addReportEventHandler(new TemplateSourceMappingReporter("target/statepattern", modelName, true));
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
      String templateContent = Resources.toString(templateUrl, StandardCharsets.UTF_8);
      DecodedSource src = new DecodedSource(templateUrl, templateContent, false);

      List<DecodedMapping> mappings = List.of(
          // Verify summary of the class
          new DecodedMapping(3, 0, src, 6, 0, null),
          new DecodedMapping(4, 0, src, 7, 0, null),
          new DecodedMapping(5, 0, src, 8, 0, null),
          new DecodedMapping(6, 0, src, 9, 0, null),
          // class definition
          new DecodedMapping(7, 0, src, 10, 0, null),
          new DecodedMapping(7, 22, src, 10, 22, null),
          new DecodedMapping(7, 43, src, 10, 34, null),
          // ----------------------- Start Game -----------------------
          // Summary of handleStartGame
          new DecodedMapping(10, 0, src, 15, 0, null),
          new DecodedMapping(11, 0, src, 16, 0, null),
          new DecodedMapping(11, 14, src, 16, 24, null),
          new DecodedMapping(12, 0, src, 17, 0, null),
          new DecodedMapping(13, 0, src, 18, 0, null),
          new DecodedMapping(13, 12, src, 18, 12, null),
          new DecodedMapping(13, 20, src, 18, 24, null),
          new DecodedMapping(14, 0, src, 19, 0, null),
          // method definition
          new DecodedMapping(15, 0, src, 19, 0, null),
          new DecodedMapping(15, 13, src, 19, 13, null),
          new DecodedMapping(15, 22, src, 19, 38, null),
          new DecodedMapping(15, 31, src, 19, 51, null),
          // method body
          new DecodedMapping(16, 0, src, 20, 0, null),
          new DecodedMapping(17, 0, src, 21, 0, null),
          new DecodedMapping(18, 0, src, 22, 0, null),
          new DecodedMapping(19, 0, src, 23, 0, null),
          // ----------------------- Stop Game -----------------------
          // Summary of handleStopGame
          new DecodedMapping(20, 0, src, 15, 0, null),
          new DecodedMapping(21, 0, src, 16, 0, null),
          new DecodedMapping(21, 14, src, 16, 24, null),
          new DecodedMapping(22, 0, src, 17, 0, null),
          new DecodedMapping(23, 0, src, 18, 0, null),
          new DecodedMapping(23, 12, src, 18, 12, null),
          new DecodedMapping(23, 20, src, 18, 24, null),
          new DecodedMapping(24, 0, src, 19, 0, null),
          // method definition
          new DecodedMapping(25, 0, src, 19, 0, null),
          new DecodedMapping(25, 13, src, 19, 13, null),
          new DecodedMapping(25, 22, src, 19, 38, null),
          new DecodedMapping(25, 31, src, 19, 51, null),
          // method body
          new DecodedMapping(26, 0, src, 20, 0, null),
          new DecodedMapping(27, 0, src, 21, 0, null),
          new DecodedMapping(28, 0, src, 22, 0, null),
          new DecodedMapping(29, 0, src, 23, 0, null),
          // ----------------------- Return Ball -----------------------
          // Summary of handleReturnBall
          new DecodedMapping(30, 0, src, 15, 0, null),
          new DecodedMapping(31, 0, src, 16, 0, null),
          new DecodedMapping(31, 14, src, 16, 24, null),
          new DecodedMapping(32, 0, src, 17, 0, null),
          new DecodedMapping(33, 0, src, 18, 0, null),
          new DecodedMapping(33, 12, src, 18, 12, null),
          new DecodedMapping(33, 20, src, 18, 24, null),
          new DecodedMapping(34, 0, src, 19, 0, null),
          // method definition
          new DecodedMapping(35, 0, src, 19, 0, null),
          new DecodedMapping(35, 13, src, 19, 13, null),
          new DecodedMapping(35, 23, src, 19, 38, null),
          new DecodedMapping(35, 36, src, 19, 51, null),
          // method body
          new DecodedMapping(36, 0, src, 20, 0, null),
          new DecodedMapping(37, 0, src, 21, 0, null),
          new DecodedMapping(38, 0, src, 22, 0, null),
          new DecodedMapping(39, 0, src, 23, 0, null),
          // closing bracket
          new DecodedMapping(41, 0, src, 25, 0, null)
      );

      File generatedFile = new File("target/statepattern/AbstractPingPongState.java");
      DecodedSourceMap expected = new DecodedSourceMap(generatedFile.getAbsolutePath(), List.of(src), mappings);

      assertEquals(expected, decoded);

    } finally {
      Reporting.off();
    }
  }
}
