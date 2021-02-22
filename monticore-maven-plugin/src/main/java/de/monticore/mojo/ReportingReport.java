/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mojo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

/**
 * Generates a report based on the MontiCore reporting system. A given base directory is searched
 * for the default set of MontiCore reports. These are then collected per processed grammar (based
 * on the parent directory structure) and then aggregated in this report.
 *
 */
@Mojo(name = "reporting-report", defaultPhase = LifecyclePhase.SITE)
@Execute(phase = LifecyclePhase.GENERATE_SOURCES)
public class ReportingReport extends AbstractMavenReport {
  
  public static final String OUTPUT_NAME = "monticore-reporting";
  
  @Override
  public String getOutputName() {
    return OUTPUT_NAME;
  }
  
  /**
   * Directory where reports will go.
   */
  @Parameter(property = "project.reporting.outputDirectory", required = true, readonly = true)
  private String outputDirectory;
  
  @Override
  protected String getOutputDirectory() {
    return this.outputDirectory;
  }
  
  /**
   * The base directory for reports generated by MontiCore.
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-sources/${plugin.goalPrefix}/sourcecode/reports")
  private File reportsBaseDirectory;
  
  protected File getReportsBaseDirectory() {
    return this.reportsBaseDirectory;
  }
  
  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject mavenProject;
  
  @Override
  protected MavenProject getProject() {
    return mavenProject;
  }
  
  @Component
  private Renderer renderer;
  
  @Override
  protected Renderer getSiteRenderer() {
    return renderer;
  }
  
  /* The default list of reports generated by MontiCore. */
  protected static final List<String> REPORTS = Arrays.asList(new String[] {
      "01_Summary.txt",
      "02_GeneratedFiles.txt",
      "03_HandwrittenCodeFiles.txt",
      "04_Templates.txt",
      "05_HookPoint.txt",
      "06_Instantiations.txt",
      "07_Variables.txt",
      "08_Detailed.txt",
      "09_TemplateTree.txt",
      "10_NodeTree.txt",
      "11_NodeTreeDecorated.txt",
      "12_TypesOfNodes.txt",
      "13_SymbolTable.txt",
      "14_Transformations.txt",
      "15_ArtifactGml.gml",
      "16_ArtifactGv.gv",
      "18_ObjectDiagram.txt"
  });
  
  @Override
  public String getName(Locale locale) {
    return "MontiCore Reporting";
  }
  
  @Override
  public String getDescription(Locale locale) {
    return "This report contains the the reports generated by MontiCore.";
  }
  
  @Override
  protected void executeReport(Locale locale) throws MavenReportException {
    Map<String, List<Path>> modelReports = new LinkedHashMap<>();
    
    try {
      modelReports = Files.walk(getReportsBaseDirectory().toPath())
          .sorted()
          .filter(p -> REPORTS.contains(p.getFileName().toString()))
          .collect(Collectors.groupingBy(p -> p.getParent().getFileName().toString()));
    }
    catch (IOException e) {
      getLog().error(e);
    }
    
    modelReports.entrySet().forEach(
        e -> getLog().debug("Reports for " + e.getKey() + ": " + e.getValue()));
    
    getLog().debug("Rendering reporting sites.");
    new ReportsRenderer(getSink(), modelReports, getOutputDirectory(), getSinkFactory(), getLog())
        .render();
  }
  
  /**
   * @see org.apache.maven.reporting.AbstractMavenReport#canGenerateReport()
   */
  @Override
  public boolean canGenerateReport() {
    if (getProject().getPackaging().equals("pom")) {
      getLog().info("MontiCore reports are not available for POM modules.");
      return false;
    }
    return true;
  }
  
}