/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportManager.ReportManagerFactory;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.IncGenCheckReporter;
import de.monticore.generating.templateengine.reporting.reporter.IncGenGradleReporter;
import de.monticore.generating.templateengine.reporting.reporter.InputOutputFilesReporter;
import de.monticore.io.paths.IterablePath;

/**
 * Initializes and provides the set of reports desired for MontiCore to the
 * reporting framework.
 *
 */
public class MontiCoreReportsLight implements ReportManagerFactory {

  private String outputDirectory;

  private IterablePath handwrittenPath;

  private IterablePath templatePath;

  /**
   * Constructor for de.monticore.MontiCoreReports
   */
  protected MontiCoreReportsLight(
          String outputDirectory,
          String reportDiretory,
          IterablePath handwrittenPath,
          IterablePath templatePath) {
    this.outputDirectory = outputDirectory;
    this.handwrittenPath = handwrittenPath;
    this.templatePath = templatePath;
  }

  /**
   * @see ReportManagerFactory#provide(String)
   */
  @Override
  public ReportManager provide(String modelName) {
    MontiCoreNodeIdentifierHelper identifierHelper = new MontiCoreNodeIdentifierHelper();
    ReportingRepository repository = new ReportingRepository(identifierHelper);
    repository.initAllTemplates();
    repository.initAllHWJava(this.handwrittenPath);
    repository.initAllHWTemplates(this.templatePath);
    
    ReportManager reports = new ReportManager(this.outputDirectory);
    
    InputOutputFilesReporter inputOutput = new InputOutputFilesReporter(this.outputDirectory);
    IncGenCheckReporter incGenCheck = new IncGenCheckReporter(this.outputDirectory, modelName);
    IncGenGradleReporter gradleReporter = new IncGenGradleReporter(this.outputDirectory, modelName);

    reports.addReportEventHandler(inputOutput); // 17_InputOutputFiles
    reports.addReportEventHandler(incGenCheck); // IncGenCheck
    reports.addReportEventHandler(gradleReporter);

    return reports;
  }
  
}
