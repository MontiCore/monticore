/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mojo.extra;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.reporting.AbstractMavenReportRenderer;

import com.google.common.collect.Lists;

import de.monticore.mojo.ReportRenderer;

public class ScriptReportRenderer extends AbstractMavenReportRenderer {
  
  private List<File> inputFiles = Lists.newArrayList();
  
  private String outputDirectory;
  
  private SinkFactory sinkFactory;
  
  private Log log;
  
  public ScriptReportRenderer(
      Sink sink,
      List<File> inputFiles,
      String outputDirectory,
      SinkFactory sinkFactory,
      Log log) {
    super(sink);
    this.inputFiles = inputFiles;
    this.outputDirectory = outputDirectory;
    this.sinkFactory = sinkFactory;
    this.log = log;
  }
  
  @Override
  public String getTitle() {
    return "Script Result Report";
  }
  
  @Override
  protected void renderBody() {
    startSection("Script Result Report");
    paragraph("The following subpages contain the results of each analysis script.");
    startTable();
    for (File inputFile : inputFiles) {
      // renders a row of the table with link to sub page and brief generic
      // description
      tableRow(new String[] {
          createLinkPatternedText(inputFile.getName(),
              "./" + inputFile.getName() + ".html"),
          "Results of script " + inputFile.getName() });
      // renders the respective sub page
      try {
        new ReportRenderer(sinkFactory.createSink(
            new File(outputDirectory), inputFile.getName().concat(".html")), inputFile,
            inputFile.getName(), "script-report", log).render();
      }
      catch (IOException e) {
        log.error(
            "0xA4070 Could not create sink for ".concat(new File(outputDirectory, inputFile
                .getName().concat(".html")).getPath()), e);
      }
    }
    endTable();
    endSection();
  }
  
}
