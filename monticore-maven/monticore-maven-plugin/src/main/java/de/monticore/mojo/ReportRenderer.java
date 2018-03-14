/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mojo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.reporting.AbstractMavenReportRenderer;

import com.google.common.io.Files;

/**
 * A very basic report renderer that creates a verbatim report taking the contents of a given file.
 * 
 * @author (last commit) $Author: ahorst $
 */
public class ReportRenderer extends AbstractMavenReportRenderer {
  
  /* Contains what to put into the report. */
  private File inputFile;
  
  /* A nice title for the report. */
  private String title;
  
  /* For creating "back" links. */
  private String parent;
  
  /* Piece of wood. */
  private Log log;
  
  /**
   * Constructor for de.monticore.mojo.extra.ScriptResultRenderer
   * 
   * @param sink Maven stuff
   * @param inputFile which contains what to put into the report
   * @param title fancy name for the report
   * @param parent for creating "back" links
   * @param log
   */
  public ReportRenderer(Sink sink, File inputFile, String title, String parent, Log log) {
    super(sink);
    this.inputFile = inputFile;
    this.title = title;
    this.parent = parent;
    this.log = log;
  }
  
  @Override
  public String getTitle() {
    return inputFile.getName();
  }
  
  @Override
  protected void renderBody() {
    startSection(title);
    linkPatternedText(createLinkPatternedText("Back to parent", "./" + this.parent + ".html"));
    
    try {
      verbatimText(Files.toString(inputFile, Charset.defaultCharset()));
    }
    catch (IOException iOE) {
      log.warn("0xA4071 Unable to read input file " + inputFile.getAbsolutePath(), iOE);
    }
    linkPatternedText(createLinkPatternedText("Back to parent", "./" + this.parent + ".html"));
    endSection();
  }
  
}
