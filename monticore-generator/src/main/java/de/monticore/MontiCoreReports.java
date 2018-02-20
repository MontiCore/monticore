/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportManager.ReportManagerFactory;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.ArtifactGVReporter;
import de.monticore.generating.templateengine.reporting.reporter.ArtifactGmlReporter;
import de.monticore.generating.templateengine.reporting.reporter.DetailedReporter;
import de.monticore.generating.templateengine.reporting.reporter.SuccessfulReporter;
import de.monticore.generating.templateengine.reporting.reporter.InvolvedFilesReporter;
import de.monticore.generating.templateengine.reporting.reporter.GeneratedFilesReporter;
import de.monticore.generating.templateengine.reporting.reporter.HandWrittenCodeReporter;
import de.monticore.generating.templateengine.reporting.reporter.HookPointReporter;
import de.monticore.generating.templateengine.reporting.reporter.InputOutputFilesReporter;
import de.monticore.generating.templateengine.reporting.reporter.InstantiationsReporter;
import de.monticore.generating.templateengine.reporting.reporter.NodeTreeDecoratedReporter;
import de.monticore.generating.templateengine.reporting.reporter.NodeTreeReporter;
import de.monticore.generating.templateengine.reporting.reporter.NodeTypesReporter;
import de.monticore.generating.templateengine.reporting.reporter.SummaryReporter;
import de.monticore.generating.templateengine.reporting.reporter.SymbolTableReporter;
import de.monticore.generating.templateengine.reporting.reporter.TemplateTreeReporter;
import de.monticore.generating.templateengine.reporting.reporter.TemplatesReporter;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.monticore.generating.templateengine.reporting.reporter.VariablesReporter;
import de.monticore.io.paths.IterablePath;

/**
 * Initializes and provides the set of reports desired for MontiCore to the
 * reporting framework.
 *
 */
public class MontiCoreReports implements ReportManagerFactory {
  
  private String outputDirectory;
  
  private IterablePath handwrittenPath;
  
  private IterablePath templatePath;
  
  /**
   * Constructor for de.monticore.MontiCoreReports
   */
  protected MontiCoreReports(
      String outputDirectory,
      IterablePath handwrittenPath,
      IterablePath templatePath) {
    this.outputDirectory = outputDirectory;
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
    
    SummaryReporter summary = new SummaryReporter(this.outputDirectory, modelName, repository);
    GeneratedFilesReporter generated = new GeneratedFilesReporter(this.outputDirectory, modelName,
        repository);
    HandWrittenCodeReporter handwritten = new HandWrittenCodeReporter(this.outputDirectory,
        modelName, repository);
    TemplatesReporter templates = new TemplatesReporter(this.outputDirectory, modelName, repository);
    HookPointReporter hookPoints = new HookPointReporter(this.outputDirectory, modelName,
        repository);
    InstantiationsReporter instantiations = new InstantiationsReporter(this.outputDirectory,
        modelName);
    VariablesReporter variables = new VariablesReporter(this.outputDirectory, modelName);
    DetailedReporter detail = new DetailedReporter(this.outputDirectory, modelName, repository);
    TemplateTreeReporter templateTree = new TemplateTreeReporter(this.outputDirectory, modelName);
    InvolvedFilesReporter ioReporter = new InvolvedFilesReporter(this.outputDirectory);
    NodeTreeReporter nodeTree = new NodeTreeReporter(this.outputDirectory, modelName, repository);
    NodeTreeDecoratedReporter nodeTreeDecorated = new NodeTreeDecoratedReporter(
        this.outputDirectory, modelName, repository);
    NodeTypesReporter nodeTypes = new NodeTypesReporter(this.outputDirectory, modelName);
    SymbolTableReporter symbolTable = new SymbolTableReporter(this.outputDirectory, modelName, repository);
    TransformationReporter transformations = new TransformationReporter(this.outputDirectory,
        modelName, repository);
    ArtifactGmlReporter artifactGml = new ArtifactGmlReporter(this.outputDirectory, modelName);
    ArtifactGVReporter artifactGV = new ArtifactGVReporter(this.outputDirectory, modelName);
    InputOutputFilesReporter inputOutput = new InputOutputFilesReporter(this.outputDirectory);
    ODReporter objDiagram = new ODReporter(this.outputDirectory, modelName, repository);
    SuccessfulReporter finishReporter = new SuccessfulReporter(this.outputDirectory, modelName);

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

    
    return reports;
  }
  
}
