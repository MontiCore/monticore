/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.SummaryReporter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNumber;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;
import java.util.UUID;

/**
 * Reporter, which sends its result to a remote server.
 * This is used to track calls of the MontiCore jar
 */
@Deprecated
public class StatisticsReporterFix extends SummaryReporter {
  public static final String SIMPLE_FILE_NAME = "20_Statistics";
  protected String STAT_TYPE;
  protected Instant startTime;

  protected JsonObject report = new JsonObject(); // Main JSON, which is sent to server
  protected JsonObject summary = new JsonObject();


  protected StatisticsReporterFix(String outputDir, String modelName, ReportingRepository repository, ITraverser traverser) {
    super(outputDir, modelName, repository, traverser);
    this.qualifiedFileName = SIMPLE_FILE_NAME;
    startTime = Instant.now();

    report.putMember("uuid", new UserJsonString(UUID.randomUUID().toString()));
    putVersion();
  }

  public StatisticsReporterFix(JsonElement parameter, String stat_type, String outputDir, String modelName, ReportingRepository repository, ITraverser traverser) {
    this(outputDir, modelName, repository, traverser);
    STAT_TYPE = stat_type;
    report.putMember("parameter", parameter);
    StatisticsHandlerFix.storeReport(report.print(new IndentPrinter()), "PRE_" + STAT_TYPE);
  }

  @Override
  protected void writeSummaryLine(String string, int number) {
    summary.putMember(string, new JsonNumber("" + number));
  }

  @Override
  protected void writeHeader() {
    // Do nothing, there should be no header
    // Otherwise output is not valid JSON
  }

  @Override
  public void reportError(String msg){
    report.putMember("error", new UserJsonString(msg));
    super.reportError(msg);
  }

  @Override
  public void flush(ASTNode ast) {
    writeContent(ast);  // fills summary JSON-Object
    report.putMember("summary", summary);

    report.putMember("duration", new JsonNumber(String.valueOf(Duration.between(startTime, Instant.now()).toMillis())));


    // Create JSON-String and write it to file
    String report = this.report.print(new IndentPrinter());
    writeLine(report);

    // Store JSON-String in statistics
    StatisticsHandlerFix.storeReport(report, STAT_TYPE);

    // Cleanup
    resetVariables();
    closeFile();
  }

  private void putVersion() {
    try {
      Properties properties = new Properties();
      properties.load(this.getClass().getResourceAsStream("/buildInfo.properties"));
      report.putMember("version", new UserJsonString(properties.getProperty("version")));

    } catch (Exception e) {
      Log.debug("0xA9005 Could not find local properties file", this.getClass().getName());
    }
  }
}
