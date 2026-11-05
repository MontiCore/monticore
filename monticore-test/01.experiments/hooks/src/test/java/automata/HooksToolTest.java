/* (c) https://github.com/MontiCore/monticore */
package automata;

import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.reporter.TemplateSourceMappingReporter;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import hooks.HooksTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HooksToolTest {

  @BeforeEach
  public void init(){
    LogStub.init();         // replace log by a sideffect free variant
    Log.enableFailQuick(false);
    // LogStub.initPlusLog();  // for manual testing purpose only
  }

  @BeforeEach
  public void clearFindings(){
    Log.getFindings().clear();
  }

  /* We execute the Generator here, but do not really test it:
   * (instead we use tests against the generated classes)
   *
   * The execution of the generator is needed to get the classes generated.
   * We reuse the automata models from the templates-Experiment
   */
  @Test
  public void testPingPong(){
    HooksTool.main(new String[] {
         "../templates/src/test/resources/example/PingPong.aut",
        "src/product/java",
        "target/statepattern",
        "src/main/resources"});
    assertEquals(0, Log.getErrorCount());
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testSimple12(){
    HooksTool.main(new String[] {
        "../templates/src/test/resources/example/Simple12.aut",
        "src/product/java",
        "target/statepattern",
        "src/main/resources"});
    assertEquals(0, Log.getErrorCount());
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testPingPong2(){
    TemplatesTool.main(new String[] {
        "../templates/src/test/resources/example/PingPong.aut",
        "src/product/java",
        "target/statepattern2",
        "src/main/templates"});
    assertEquals(0, Log.getErrorCount());
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testTrackedIncludesWithHooks() throws IOException {
    File inclFile = new File("target/statepattern", "PingPong.java.incl.json");
    if (inclFile.exists()) {
      inclFile.delete();
    }

    try {
      ReportManager.ReportManagerFactory factory = modelName -> {
        ReportManager reports = new ReportManager("target/statepattern");
        reports.addReportEventHandler(new TemplateSourceMappingReporter("target/statepattern", modelName, true));
        return reports;
      };
      Reporting.init("target/statepattern", factory);
      Reporting.on("PingPong");

      HooksTool.main(new String[] {
          "../templates/src/test/resources/example/PingPong.aut",
          "src/product/java",
          "target/statepattern",
          "src/main/resources"
      });

      assertEquals(0, Log.getErrorCount());
      MCAssertions.assertNoFindings();

      assertTrue(inclFile.exists());

      String json = Files.readString(inclFile.toPath());
      JsonObject root = JsonParser.parseJsonObject(json);

      List<JsonElement> sources = root.getArrayMember("sources");
      assertEquals(1, sources.size());
      assertTrue(sources.get(0).getAsJsonString().getValue().endsWith("Statechart.ftl"));

      List<JsonElement> includedFiles = root.getArrayMember("includedFiles");
      assertEquals(2, includedFiles.size());
      assertEquals("MyStateAttributes.ftl", includedFiles.get(0).getAsJsonString().getValue());
      assertEquals("MyStateGetter.ftl", includedFiles.get(1).getAsJsonString().getValue());

      List<JsonElement> includes = root.getArrayMember("includes");
      assertEquals(6, includes.size());

      for (int i = 0; i < 6; i++) {
        JsonObject include = includes.get(i).getAsJsonObject();
        assertEquals(0, include.getIntegerMember("source"));
        assertEquals(i % 2, include.getIntegerMember("target"));

        JsonObject start = include.getObjectMember("start");
        assertEquals(46, start.getIntegerMember("line"));
        assertEquals(2, start.getIntegerMember("col"));

        JsonObject end = include.getObjectMember("end");
        assertEquals(46, end.getIntegerMember("line"));
        assertEquals(67, end.getIntegerMember("col"));
      }

    } finally {
      Reporting.off();
    }
  }

}
