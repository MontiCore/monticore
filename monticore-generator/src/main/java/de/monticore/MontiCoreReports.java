/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportManager.ReportManagerFactory;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.ArtifactGVReporter;
import de.monticore.generating.templateengine.reporting.reporter.ArtifactGmlReporter;
import de.monticore.generating.templateengine.reporting.reporter.DetailedReporter;
import de.monticore.generating.templateengine.reporting.reporter.GeneratedFilesReporter;
import de.monticore.generating.templateengine.reporting.reporter.HandWrittenCodeReporter;
import de.monticore.generating.templateengine.reporting.reporter.HookPointReporter;
import de.monticore.generating.templateengine.reporting.reporter.IncGenGradleReporter;
import de.monticore.generating.templateengine.reporting.reporter.InstantiationsReporter;
import de.monticore.generating.templateengine.reporting.reporter.InvolvedFilesReporter;
import de.monticore.generating.templateengine.reporting.reporter.NodeTreeDecoratedReporter;
import de.monticore.generating.templateengine.reporting.reporter.NodeTreeReporter;
import de.monticore.generating.templateengine.reporting.reporter.NodeTypesReporter;
import de.monticore.generating.templateengine.reporting.reporter.SuccessfulReporter;
import de.monticore.generating.templateengine.reporting.reporter.SummaryReporter;
import de.monticore.generating.templateengine.reporting.reporter.TemplateTreeReporter;
import de.monticore.generating.templateengine.reporting.reporter.TemplatesReporter;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.monticore.generating.templateengine.reporting.reporter.VariablesReporter;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsTraverser;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.serialization.json.JsonElement;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Initializes and provides the set of reports desired for MontiCore to the
 * reporting framework.
 *
 */
public class MontiCoreReports implements ReportManagerFactory {

  protected String outputDirectory;

  protected String reportDirectory;

  protected MCPath handwrittenPath;
  
  protected MCPath templatePath;

  protected Function<Path, Path> reportPathOutput;
  protected JsonElement mcConfig;
  
  protected List<AReporter> reporters = new ArrayList<>();

  /**
   * Constructor for de.monticore.MontiCoreReports
   */
  protected MontiCoreReports(
      String outputDirectory,
      String reportDiretory,
      Function<Path, Path> reportPathOutput, MCPath handwrittenPath,
      MCPath templatePath, JsonElement mcConfig) {
    this.outputDirectory = outputDirectory;
    this.reportDirectory = reportDiretory;
    this.handwrittenPath = handwrittenPath;
    this.templatePath = templatePath;
    this.reportPathOutput = reportPathOutput;
    this.mcConfig = mcConfig;
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
    
    ReportManager reports = new ReportManager(this.outputDirectory);

    Grammar_WithConceptsTraverser traverserSummary = Grammar_WithConceptsMill.inheritanceTraverser();
    SummaryReporter summary = new SummaryReporter(this.reportDirectory, lowerCaseName, repository, traverserSummary);
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
    Grammar_WithConceptsTraverser traverserNodeTree = Grammar_WithConceptsMill.inheritanceTraverser();
    NodeTreeReporter nodeTree = new NodeTreeReporter(this.reportDirectory, lowerCaseName, repository, traverserNodeTree);
    Grammar_WithConceptsTraverser traverserNodeTree2 = Grammar_WithConceptsMill.inheritanceTraverser();
    NodeTreeDecoratedReporter nodeTreeDecorated = new NodeTreeDecoratedReporter(
        this.reportDirectory, lowerCaseName, repository, traverserNodeTree2);
    Grammar_WithConceptsTraverser traverserNodeType = Grammar_WithConceptsMill.inheritanceTraverser();
    NodeTypesReporter nodeTypes = new NodeTypesReporter(this.reportDirectory, lowerCaseName, traverserNodeType);
    TransformationReporter transformations = new TransformationReporter(this.reportDirectory,
        lowerCaseName, repository);
    ArtifactGmlReporter artifactGml = new ArtifactGmlReporter(this.reportDirectory, lowerCaseName);
    ArtifactGVReporter artifactGV = new ArtifactGVReporter(this.reportDirectory, lowerCaseName);
    ODReporter objDiagram = new ODReporter(this.reportDirectory, lowerCaseName, repository);
    SuccessfulReporter finishReporter = new SuccessfulReporter(this.reportDirectory, lowerCaseName);
    StatisticsReporterFix statistics = new StatisticsReporterFix(mcConfig, "MC_JAR_JSON", this.reportDirectory, lowerCaseName, repository, traverserSummary);
    IncGenGradleReporter gradleReporter = new IncGenGradleReporter(this.reportDirectory, reportPathOutput, lowerCaseName);

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
    reports.addReportEventHandler(transformations); // 14_Transformations
    reports.addReportEventHandler(artifactGml); // 15_ArtifactGml
    reports.addReportEventHandler(artifactGV); // 16_ArtifactGv
    reports.addReportEventHandler(ioReporter); // 18_InvolvedFiles
    reports.addReportEventHandler(finishReporter); // 19_Successful
    reports.addReportEventHandler(statistics);  // 20_Statistics
    reports.addReportEventHandler(objDiagram); // ObjectDiagram
    //reports.addReportEventHandler(incGenCheck); // IncGenCheck
    reports.addReportEventHandler(gradleReporter);

    reporters.add(summary);
    reporters.add(generated);
    reporters.add(handwritten);
    reporters.add(templates);
    reporters.add(hookPoints);
    reporters.add(instantiations);
    reporters.add(variables);
    reporters.add(detail);
    reporters.add(templateTree);
    reporters.add(nodeTree);
    reporters.add(nodeTreeDecorated);
    reporters.add(nodeTypes);
    reporters.add(transformations);
    reporters.add(artifactGml);
    reporters.add(artifactGV);
    reporters.add(ioReporter);
    reporters.add(finishReporter);
    reporters.add(statistics);
    reporters.add(objDiagram);
    reporters.add(gradleReporter);

    return reports;
  }

  public void close(){
    for (AReporter reporter : reporters) {
      reporter.closeFile();
    }
  }
  
}
