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

/**
 * TODO: Write me!
 * 
 * @author (last commit) $Author: ahorst $
 */
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
