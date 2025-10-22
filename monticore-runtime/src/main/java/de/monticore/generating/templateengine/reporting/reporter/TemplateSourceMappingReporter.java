package de.monticore.generating.templateengine.reporting.reporter;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler;
import de.monticore.generating.templateengine.reporting.commons.ReportCreator;
import de.monticore.sourcemap.DecodedMapping;
import de.monticore.sourcemap.DecodedSourceMap;
import de.monticore.generating.templateengine.sourcemap.SourceMapCalculator;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.monticore.sourcemap.Encoding.getEncodeSourceMap;

public class TemplateSourceMappingReporter extends DefaultReportEventHandler {

  protected ReportCreator reportingHelper;

  protected String fileextension;

  protected final String TEMPLATE_MAPPING = "TEMPL";
  protected final String AST_MAPPING = "AST";

  protected String qualifiedFileName;

  protected List<DecodedMapping> templateMappings = new ArrayList<>();
  protected List<DecodedMapping> astMappings = new ArrayList<>();

  protected String currentGeneratedFile;
  protected File currentTemplateMappingFile;
  protected File currentASTMappingFile;

  public TemplateSourceMappingReporter(String path, String qualifiedFileName, String fileExtension) {
    reportingHelper = new ReportCreator(path);
    this.qualifiedFileName = qualifiedFileName;
    this.fileextension = fileExtension;
    System.out.println("Creating Template Source Mapping Reporter");
  }

  @Override
  public void reportTemplateSourceMapping(List<DecodedMapping> mapping) {
    this.templateMappings.addAll(mapping);
  }

  @Override
  public void reportASTSourceMapping(List<DecodedMapping> mapping) {
    this.astMappings.addAll(mapping);
  }

  @Override
  public void reportBeforeFileCreation(String templateName, String path, String fileExtension, ASTNode ast) {
    SourceMapCalculator.reset();
    clearVariables();
    currentGeneratedFile = path;
    currentTemplateMappingFile = new File(path.replace(fileExtension, "")+"_"+TEMPLATE_MAPPING+"."+this.fileextension);
    currentASTMappingFile = new File(path.replace(fileExtension, "")+"_"+AST_MAPPING+"."+this.fileextension);
  }

  @Override
  public void reportFileCreation(String templateName, String qualifiedFilename, String fileExtension, ASTNode ast) {
    System.out.println("Reporting file finalized "+qualifiedFilename);
    writeContent(currentGeneratedFile);
  }

  @Override
  public void flush(ASTNode node) {
    super.flush(node);
  }

  protected void writeContent(String fileName) {
    writeLine(currentTemplateMappingFile, getEncodeSourceMap(new DecodedSourceMap(fileName, this.templateMappings)));
    writeLine(currentASTMappingFile, getEncodeSourceMap(new DecodedSourceMap(fileName, this.astMappings)));
  }

  /**
   * Writes a single Line to the corresponding file. The file is opened if it
   * has not been opened before.
   * Flush Buffer after every write to lower memory overhead
   * @param content
   */
  protected void writeLine(File writeToFile, String content) {
    try {
      writeToFile.createNewFile();
      reportingHelper.openFile(writeToFile);
      reportingHelper.writeLineToFile(writeToFile, content);
      reportingHelper.flushBuffer(writeToFile);
      reportingHelper.closeFile(writeToFile);
    } catch (IOException e) {
      Log.warn("0xA0132 Cannot write to log file "+writeToFile.toString(), e);
    }
  }

  protected void clearVariables() {
    templateMappings.clear();
    astMappings.clear();
  }
}
