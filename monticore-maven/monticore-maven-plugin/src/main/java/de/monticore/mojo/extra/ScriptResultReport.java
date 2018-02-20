/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mojo.extra;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import com.google.common.collect.Lists;

/**
 * This report MOJO collects and provides the output of certain analysis scripts as a fancy report.
 * 
 * @author (last commit) $Author: ahorst $
 */
@Mojo(name = "script-report", defaultPhase = LifecyclePhase.SITE)
public class ScriptResultReport extends AbstractMavenReport {
  
  private static final String OUTPUT_NAME = "script-report";
  
  /**
   * Directory where reports will go.
   */
  @Parameter(property = "project.reporting.outputDirectory", required = true, readonly = true)
  private String outputDirectory;
  
  @Parameter(required = false, defaultValue = "${project.build.directory}/scripts")
  private File scriptOutputDirectory;
  
  /**
   * Can be used to provide paths to additional files to include as part of the script report.
   */
  @Parameter(required = false)
  private List<File> additionalOutputs;
  
  /**
   * Contains a list of additional executables to execute.
   */
  @Parameter(required = false)
  private List<Executable> additionalExecutables;
  
  /**
   * Controls whether the contained scripts are to be skipped.
   */
  @Parameter(defaultValue = "false", required = false)
  private boolean skipDefaultScripts;
  
  @Parameter(required = false)
  private Executable detailedErrorList;
  
  @Parameter(required = false)
  private Executable errorList;
  
  @Parameter(required = false)
  private Executable findDoubleFileNames;
  
  @Parameter(required = false)
  private Executable ftlAnalysis;
  
  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject mavenProject;
  
  @Component
  private Renderer renderer;
  
  @Override
  public String getOutputName() {
    return OUTPUT_NAME;
  }
  
  @Override
  public String getName(Locale locale) {
    // TODO use bundle and locales
    return "Script Result Report";
  }
  
  @Override
  public String getDescription(Locale locale) {
    // TODO use bundle and locales
    return "This report contains the results of certain analysis scripts.";
  }
  
  @Override
  protected Renderer getSiteRenderer() {
    return renderer;
  }
  
  @Override
  protected String getOutputDirectory() {
    return outputDirectory;
  }
  
  protected File getScriptOutputDirectory() {
    return scriptOutputDirectory;
  }
  
  @Override
  protected MavenProject getProject() {
    return mavenProject;
  }
  
  protected List<File> getAdditionalOutputs() {
    if (additionalOutputs == null) {
      additionalOutputs = Lists.newArrayList();
    }
    return additionalOutputs;
  }
  
  protected List<Executable> getAdditionalExecutables() {
    if (additionalExecutables == null) {
      additionalExecutables = Lists.newArrayList();
    }
    return additionalExecutables;
  }
  
  protected Executable getDetailedErrorList() {
    if (detailedErrorList == null) {
      List<String> arguments = Lists.newArrayList(getProject().getBasedir().getPath(),
          getScriptOutputDirectory().getPath().concat("/temp"));
      detailedErrorList = new Executable(new File(getScriptOutputDirectory(),
          "bin/detailedErrorList.sh"), arguments);
    }
    return detailedErrorList;
  }
  
  protected Executable getErrorList() {
    if (errorList == null) {
      List<String> arguments = Lists.newArrayList(getProject().getBasedir().getPath(),
          getScriptOutputDirectory().getPath().concat("/temp"));
      errorList = new Executable(new File(getScriptOutputDirectory(), "bin/errorList.sh"),
          arguments);
    }
    return errorList;
  }
  
  protected Executable getFindDoubleFileNames() {
    if (findDoubleFileNames == null) {
      List<String> arguments = Lists.newArrayList(
          getProject().getBasedir().getPath(),
          getScriptOutputDirectory().getPath().concat("/temp"));
      findDoubleFileNames = new Executable(new File(getScriptOutputDirectory(),
          "bin/findDoubleFileNames.sh"), arguments);
    }
    return findDoubleFileNames;
  }
  
  protected Executable getFtlAnalysis() {
    if (ftlAnalysis == null) {
      List<String> arguments = Lists.newArrayList(
          getProject().getBasedir().getPath().concat("/gtr/src").concat(":")
              .concat(getProject().getBasedir().getPath().concat("/use/src")),
          "configure.StartAllOutput",
          getScriptOutputDirectory().getPath().concat("/temp/ftlAnalysis.tmp"));
      ftlAnalysis = new Executable(new File(getScriptOutputDirectory(), "bin/ftlAnalysis.sh"),
          arguments);
    }
    return ftlAnalysis;
  }
  
  @Override
  protected void executeReport(Locale locale) throws MavenReportException {
    // TODO use bundle and locales
    
    // execute the default scripts
    if (!skipDefaultScripts) {
      
      // hm, what might happen if we also start using .bat scripts. . .
      if (System.getProperty("os.name").startsWith("Windows")) {
        getLog()
            .warn(
                "Script result reports are platform dependant and only work on Linux. Period. Default skripts will be skipped.");
      }
      else {
        // copy stuff... it's so ugly
        ClassLoader loader = getClass().getClassLoader();
        File tempDir = new File(getScriptOutputDirectory(), "temp");
        File binDir = new File(getScriptOutputDirectory(), "bin");
        File doubleNames = new File(binDir, "findDoubleFileNames.JavaStandardNames.txt");
        File detailScript = new File(binDir, "detailedErrorList.sh");
        File errorScript = new File(binDir, "errorList.sh");
        File doubleScript = new File(binDir, "findDoubleFileNames.sh");
        File ftlScript = new File(binDir, "ftlAnalysis.sh");
        
        tempDir.mkdirs();
        binDir.mkdirs();
        
        writeStreamToFile(
            loader.getResourceAsStream("util/findDoubleFileNames.JavaStandardNames.txt"),
            doubleNames);
        writeStreamToFile(loader.getResourceAsStream("scripts/detailedErrorList.sh"), detailScript);
        writeStreamToFile(loader.getResourceAsStream("scripts/errorList.sh"), errorScript);
        writeStreamToFile(loader.getResourceAsStream("scripts/findDoubleFileNames.sh"),
            doubleScript);
        writeStreamToFile(loader.getResourceAsStream("scripts/ftlAnalysis.sh"), ftlScript);
        
        detailScript.setExecutable(true);
        errorScript.setExecutable(true);
        doubleScript.setExecutable(true);
        ftlScript.setExecutable(true);
        
        getLog().debug("Executing default scripts:");
        getLog().info("Executing script " + getDetailedErrorList().getPathToExecutable());
        getLog().debug(getDetailedErrorList().toString());
        getAdditionalOutputs().add(
            new Execution(getDetailedErrorList(), getScriptOutputDirectory(), getLog()).execute());
        getLog().debug("Executed script " + getDetailedErrorList().getPathToExecutable());
        
        getLog().info("Executing script " + getErrorList().getPathToExecutable());
        getLog().debug(getErrorList().toString());
        getAdditionalOutputs().add(
            new Execution(getErrorList(), getScriptOutputDirectory(), getLog()).execute());
        getLog().debug("Executed script " + getErrorList().getPathToExecutable());
        
        getLog().info("Executing script " + getFindDoubleFileNames().getPathToExecutable());
        getLog().debug(getFindDoubleFileNames().toString());
        getAdditionalOutputs()
            .add(
                new Execution(getFindDoubleFileNames(), getScriptOutputDirectory(), getLog())
                    .execute());
        getLog().debug("Executed script " + getFindDoubleFileNames().getPathToExecutable());
        
        getLog().info("Executing script " + getFtlAnalysis().getPathToExecutable());
        getLog().debug(getFtlAnalysis().toString());
        getAdditionalOutputs().add(
            new Execution(getFtlAnalysis(), getScriptOutputDirectory(), getLog()).execute());
        getLog().debug("Executed script " + getFtlAnalysis().getPathToExecutable());
      }
    }
    
    // hm, what might happen if we also start using .bat scripts. . .
    if (!getAdditionalExecutables().isEmpty()
        && System.getProperty("os.name").startsWith("Windows")) {
      getLog()
          .warn(
              "Script result reports are platform dependant and only work on Linux. Period. Additional scripts will be skipped.");
    }
    else {
      // execute the additional scripts
      getLog().debug("Executing additional scripts:");
      for (Executable executable : getAdditionalExecutables()) {
        getLog().info("Executing script:");
        getLog().debug(executable.toString());
        getAdditionalOutputs().add(
            new Execution(executable, getScriptOutputDirectory(), getLog()).execute());
        getLog().debug("Executed script " + executable.getPathToExecutable());
      }
    }
    
    getLog().debug("Rendering report sites.");
    new ScriptReportRenderer(getSink(), getAdditionalOutputs(), getOutputDirectory(),
        getSinkFactory(), getLog()).render();
  }
  
  private void writeStreamToFile(InputStream stream, File file) {
    if (stream == null) {
      throw new IllegalArgumentException("0xA4087 Argument stream must not be null!");
    }
    if (file == null) {
      throw new IllegalArgumentException("0xA4088 Argument file must not be null!");
    }
    FileOutputStream output = null;
    try {
      output = new FileOutputStream(file);
      int read = stream.read();
      while (read != -1) {
        output.write(read);
        read = stream.read();
      }
    }
    catch (IOException e) {
      getLog().error(e);
    }
    finally {
      try {
        if (output != null) {
          output.close();
        }
      }
      catch (IOException e) {
        getLog().error(e);
      }
    }
  }
}
