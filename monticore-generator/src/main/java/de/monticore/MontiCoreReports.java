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
  @Deprecated
  protected MontiCoreReports(
      String reportDirectory,
      IterablePath handwrittenPath,
      IterablePath templatePath) {
    this.outputDirectory = reportDirectory;
    this.reportDirectory = reportDirectory;
    this.handwrittenPath = handwrittenPath;
    this.templatePath = templatePath;
  }

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
    MontiCoreNodeIdentifierHelper identifierHelper = new MontiCoreNodeIdentifierHelper();
    ReportingRepository repository = new ReportingRepository(identifierHelper);
    repository.initAllTemplates();
    repository.initAllHWJava(this.handwrittenPath);
    repository.initAllHWTemplates(this.templatePath);
    
    ReportManager reports = new ReportManager(this.outputDirectory);
    
    SummaryReporter summary = new SummaryReporter(this.reportDirectory, modelName, repository);
    GeneratedFilesReporter generated = new GeneratedFilesReporter(this.reportDirectory, modelName,
        repository);
    HandWrittenCodeReporter handwritten = new HandWrittenCodeReporter(this.reportDirectory,
        modelName, repository);
    TemplatesReporter templates = new TemplatesReporter(this.reportDirectory, modelName, repository);
    HookPointReporter hookPoints = new HookPointReporter(this.reportDirectory, modelName,
        repository);
    InstantiationsReporter instantiations = new InstantiationsReporter(this.reportDirectory,
        modelName);
    VariablesReporter variables = new VariablesReporter(this.reportDirectory, modelName);
    DetailedReporter detail = new DetailedReporter(this.reportDirectory, modelName, repository);
    TemplateTreeReporter templateTree = new TemplateTreeReporter(this.reportDirectory, modelName);
    InvolvedFilesReporter ioReporter = new InvolvedFilesReporter(this.reportDirectory);
    NodeTreeReporter nodeTree = new NodeTreeReporter(this.reportDirectory, modelName, repository);
    NodeTreeDecoratedReporter nodeTreeDecorated = new NodeTreeDecoratedReporter(
        this.reportDirectory, modelName, repository);
    NodeTypesReporter nodeTypes = new NodeTypesReporter(this.reportDirectory, modelName);
    SymbolTableReporter symbolTable = new SymbolTableReporter(this.reportDirectory, modelName, repository);
    TransformationReporter transformations = new TransformationReporter(this.reportDirectory,
        modelName, repository);
    ArtifactGmlReporter artifactGml = new ArtifactGmlReporter(this.reportDirectory, modelName);
    ArtifactGVReporter artifactGV = new ArtifactGVReporter(this.reportDirectory, modelName);
    InputOutputFilesReporter inputOutput = new InputOutputFilesReporter(this.outputDirectory);
    ODReporter objDiagram = new ODReporter(this.reportDirectory, modelName, repository);
    SuccessfulReporter finishReporter = new SuccessfulReporter(this.reportDirectory, modelName);
    IncGenCheckReporter incGenCheck = new IncGenCheckReporter(this.outputDirectory, modelName);
    IncGenGradleReporter gradleReporter = new IncGenGradleReporter(this.outputDirectory, modelName);

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
