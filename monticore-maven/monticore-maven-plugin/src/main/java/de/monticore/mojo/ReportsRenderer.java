/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */
package de.monticore.mojo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.reporting.AbstractMavenReportRenderer;

/**
 * Creates a composite report containing all gathered MontiCore reports per processed grammar.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since 4.1.7
 */
public class ReportsRenderer extends AbstractMavenReportRenderer {
  
  /* The map of processed grammars and created MontiCore reports */
  private Map<String, List<Path>> inputFiles;
  
  /* The output directory where to write reports to (used for the "sub" reports). */
  private String outputDirectory;
  
  /* Maven stuff. */
  private SinkFactory sinkFactory;
  
  /* A piece of wood. */
  private Log log;
  
  /**
   * Constructor for de.monticore.mojo.ReportingRenderer
   * 
   * @param sink Maven stuff
   * @param inputFiles map of grammars and MontiCore reports
   * @param outputDirectory where to store the "sub" reports to
   * @param sinkFactory Maven stuff
   * @param log of wood
   */
  public ReportsRenderer(
      Sink sink,
      Map<String, List<Path>> inputFiles,
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
    return "MontiCore Reports";
  }
  
  @Override
  protected void renderBody() {
    startSection("MontiCore Reports");
    paragraph("The following subpages contain the reports for each processed grammar.");
    
    // for each grammar one table
    for (Entry<String, List<Path>> entry : this.inputFiles.entrySet()) {
      startSection("Reports for " + entry.getKey());
      startTable();
      for (Path report : entry.getValue()) {
        File input = report.toFile();
        try {
          // renders a row of the table with link to sub page and brief generic
          // description
          tableRow(new String[] {
              createLinkPatternedText(input.getName(), "./" + entry.getKey() + "." + input.getName()
                  + ".html"), "Report " + input.getName() });
          // renders the respective sub page
          new ReportRenderer(sinkFactory.createSink(
              new File(outputDirectory), entry.getKey() + "." + input.getName().concat(".html")), input,
              input.getName() + " for " + entry.getKey(), ReportingReport.OUTPUT_NAME, log).render();
        }
        catch (IOException e) {
          log.error(
              "0xA4061 Could not create sink for ".concat(new File(outputDirectory, entry.getKey() + "."
                  + input.getName().concat(".html")).getPath()), e);
        }
      }
      endTable();
      endSection();
    }
    endSection();
  }
  
}
