/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.SummaryReporter;
import de.monticore.gradle.StatisticsHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNumber;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.visitor.ITraverser;

import java.time.Duration;
import java.time.Instant;

/**
 * Reporter, which sends its result to a remote server.
 * This is used to track calls of the MontiCore jar
 */
public class StatisticsReporter extends SummaryReporter {
  static final String SIMPLE_FILE_NAME = "20_Statistics";
  protected Instant startTime;

  protected JsonObject report = new JsonObject(); // Main JSON, which is sent to server
  protected JsonObject summary = new JsonObject();


  public StatisticsReporter(String outputDir, String modelName, ReportingRepository repository, ITraverser traverser) {
    super(outputDir, modelName, repository, traverser);
    this.qualifiedFileName = SIMPLE_FILE_NAME;
    startTime = Instant.now();
  }

  public StatisticsReporter(JsonElement parameter, String outputDir, String modelName, ReportingRepository repository, ITraverser traverser) {
    this(outputDir, modelName, repository, traverser);
    report.putMember("parameter", parameter);
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
  public void flush(ASTNode ast) {
    writeContent(ast);  // fills summary JSON-Object
    report.putMember("summary", summary);

    report.putMember("duration", new JsonNumber("" + Duration.between(startTime, Instant.now()).toMillis()));


    // Create JSON-String and write it to file
    String report = this.report.print(new IndentPrinter());
    writeLine(report);

    // Store JSON-String in statistics
    StatisticsHandler.storeReport(report, StatisticsHandler.ReportType.JarReport);

    // Cleanup
    resetVariables();
    closeFile();
  }
}
