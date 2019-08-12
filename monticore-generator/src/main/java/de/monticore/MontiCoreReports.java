/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportManager.ReportManagerFactory;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.*;
import de.monticore.io.paths.IterablePath;

/**
 * Initializes and provides the set of reports desired for MontiCore to the
 * reporting framework.
 *
 */
public class MontiCoreReports implements ReportManagerFactory {

  private String outputDirectory;

  private String reportDirectory;

  private IterablePath handwrittenPath;
  
  private IterablePath templatePath;
  

  /**
   * Constructor for de.monticore.MontiCoreReports
   */
  protected MontiCoreReports(
          String outputDirectory,
          String reportDiretory,
          IterablePath handwrittenPath,
          IterablePath templatePath) {
    this.outputDirectory = outputDirectory;
    this.reportDirectory = reportDiretory;
    this.handwrittenPath = handwrittenPath;
    this.templatePath = templatePath;
  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.ReportManager.ReportManagerFactory#provide(java.lang.String)
   */
  @Override
  public ReportManager provide(String modelName) {
    String lowerCaseName = modelName.toLowerCase();
    MontiCoreNodeIdentifierHelper identifierHelper = new MontiCoreNodeIdentifierHelper();
    ReportingRepository repository = new ReportingRepository(identifierHelper);
    repository.initAllTemplates();
    repository.initAllHWJava(this.handwrittenPath);
    repository.initAllHWTemplates(this.templatePath);
    
    ReportManager reports = new ReportManager(this.outputDirectory);
    
    SummaryReporter summary = new SummaryReporter(this.reportDirectory, lowerCaseName, repository);
    GeneratedFilesReporter generated = new GeneratedFilesReporter(this.reportDirectory, lowerCaseName,
        repository);
    HandWrittenCodeReporter handwritten = new HandWrittenCodeReporter(this.reportDirectory,
        lowerCaseName, repository);
    TemplatesReporter templates = new TemplatesReporter(this.reportDirectory, lowerCaseName, repository);
    HookPointReporter hookPoints = new HookPointReporter(this.reportDirectory, lowerCaseName,
        repository);
    InstantiationsReporter instantiations = new InstantiationsReporter(this.reportDirectory,
        lowerCaseName);
    VariablesReporter variables = new VariablesReporter(this.reportDirectory, lowerCaseName);
    DetailedReporter detail = new DetailedReporter(this.reportDirectory, lowerCaseName, repository);
    TemplateTreeReporter templateTree = new TemplateTreeReporter(this.reportDirectory, lowerCaseName);
    InvolvedFilesReporter ioReporter = new InvolvedFilesReporter(this.reportDirectory);
    NodeTreeReporter nodeTree = new NodeTreeReporter(this.reportDirectory, lowerCaseName, repository);
    NodeTreeDecoratedReporter nodeTreeDecorated = new NodeTreeDecoratedReporter(
        this.reportDirectory, lowerCaseName, repository);
    NodeTypesReporter nodeTypes = new NodeTypesReporter(this.reportDirectory, lowerCaseName);
    SymbolTableReporter symbolTable = new SymbolTableReporter(this.reportDirectory, lowerCaseName, repository);
    TransformationReporter transformations = new TransformationReporter(this.reportDirectory,
        lowerCaseName, repository);
    ArtifactGmlReporter artifactGml = new ArtifactGmlReporter(this.reportDirectory, lowerCaseName);
    ArtifactGVReporter artifactGV = new ArtifactGVReporter(this.reportDirectory, lowerCaseName);
    InputOutputFilesReporter inputOutput = new InputOutputFilesReporter(this.outputDirectory);
    ODReporter objDiagram = new ODReporter(this.reportDirectory, lowerCaseName, repository);
    SuccessfulReporter finishReporter = new SuccessfulReporter(this.reportDirectory, lowerCaseName);
    IncGenCheckReporter incGenCheck = new IncGenCheckReporter(this.outputDirectory, lowerCaseName);
    IncGenGradleReporter gradleReporter = new IncGenGradleReporter(this.outputDirectory, lowerCaseName);

    reports.addReportEventHandler(summary); // 01_Summary
    reports.addReportEventHandler(generated); // 02_GeneratedFiles
    reports.addReportEventHandler(handwritten); // 03_HandwrittenCodeFiles
    reports.addReportEventHandler(templates); // 04_Templates
    reports.addReportEventHandler(hookPoints); // 05_HookPoint
    reports.addReportEventHandler(instantiations); // 06_Instantiations
    reports.addReportEventHandler(variables); // 07_Variables
    reports.addReportEventHandler(detail); // 08_Detailed
    reports.addReportEventHandler(templateTree); // 09_TemplateTree
    reports.addReportEventHandler(nodeTree); // 10_NodeTree
    reports.addReportEventHandler(nodeTreeDecorated); // 11_NodeTreeDecorated
    reports.addReportEventHandler(nodeTypes); // 12_TypesOfNodes
    reports.addReportEventHandler(symbolTable); // 13_SymbolTable
    reports.addReportEventHandler(transformations); // 14_Transformations
    reports.addReportEventHandler(artifactGml); // 15_ArtifactGml
    reports.addReportEventHandler(artifactGV); // 16_ArtifactGv
    reports.addReportEventHandler(inputOutput); // 17_InputOutputFiles
    reports.addReportEventHandler(ioReporter); // 18_InvolvedFiles
    reports.addReportEventHandler(finishReporter); // 19_Successful
    reports.addReportEventHandler(objDiagram); // ObjectDiagram
    reports.addReportEventHandler(incGenCheck); // IncGenCheck
    reports.addReportEventHandler(gradleReporter);

    return reports;
  }
  
}
