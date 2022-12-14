/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportManager.ReportManagerFactory;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.IncGenGradleReporter;
import de.monticore.io.paths.MCPath;

import java.nio.file.Path;
import java.util.function.Function;

/**
 * Initializes and provides the set of reports desired for MontiCore to the
 * reporting framework.
 *
 */
public class MontiCoreReportsLight implements ReportManagerFactory {

  protected String outputDirectory;

  protected String reportDirectory;

  protected MCPath handwrittenPath;

  protected MCPath templatePath;

  protected Function<Path, Path> reportPathOutput;

  protected IncGenGradleReporter gradleReporter;


  /**
   * Constructor for de.monticore.MontiCoreReports
   */
  protected MontiCoreReportsLight(
          String outputDirectory,
          String reportDirectory,
          Function<Path, Path> reportPathOutput,
          MCPath handwrittenPath,
          MCPath templatePath) {
    this.outputDirectory = outputDirectory;
    this.handwrittenPath = handwrittenPath;
    this.templatePath = templatePath;
    this.reportDirectory = reportDirectory;
    this.reportPathOutput = reportPathOutput;
  }

  /**
   * @see ReportManagerFactory#provide(String)
   */
  @Override
  public ReportManager provide(String modelName) {
    MontiCoreNodeIdentifierHelper identifierHelper = new MontiCoreNodeIdentifierHelper();
    ReportingRepository repository = new ReportingRepository(identifierHelper);
    repository.initAllTemplates();

    ReportManager reports = new ReportManager(this.outputDirectory);
    
    gradleReporter = new IncGenGradleReporter(this.reportDirectory, reportPathOutput, modelName);

    //reports.addReportEventHandler(inputOutput); // 17_InputOutputFiles
    //reports.addReportEventHandler(incGenCheck); // IncGenCheck
    reports.addReportEventHandler(gradleReporter);

    return reports;
  }

  public void close(){
    gradleReporter.closeFile();
  }
}
