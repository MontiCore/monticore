package de.monticore.generating.templateengine.reporting.reporter;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler;
import de.monticore.generating.templateengine.reporting.commons.ReportCreator;
import de.monticore.generating.templateengine.sourcemap.IncludeSpan;
import de.monticore.sourcemap.DecodedMapping;
import de.monticore.sourcemap.DecodedSourceMap;
import de.monticore.generating.templateengine.sourcemap.SourceMapCalculator;
import de.monticore.symboltable.serialization.json.*;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.monticore.sourcemap.Encoding.getEncodeSourceMap;

public class TemplateSourceMappingReporter extends DefaultReportEventHandler {

  protected ReportCreator reportingHelper;

  protected String fileextension;

  protected final String AST_MAPPING = "AST";
  protected final String INCLUDE_EXTENSION = ".incl.json";

  protected String qualifiedFileName;

  protected List<DecodedMapping> templateMappings = new ArrayList<>();
  protected List<DecodedMapping> astMappings = new ArrayList<>();
  protected List<IncludeSpan> includeSpans = new ArrayList<>();

  protected String currentGeneratedFile;
  protected File currentTemplateMappingFile;
  protected File currentASTMappingFile;
  protected File currentIncludeSpanFile;

  public TemplateSourceMappingReporter(String path, String qualifiedFileName, String fileExtension) {
    reportingHelper = new ReportCreator(path);
    this.qualifiedFileName = qualifiedFileName;
    this.fileextension = fileExtension;
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
  public void reportTemplateIncludeSpan(List<IncludeSpan> spans){ this.includeSpans.addAll(spans); }

  @Override
  public void reportBeforeFileCreation(String templateName, String path, String fileExtension, ASTNode ast) {
    SourceMapCalculator.clearMappings();
    clearVariables();
    currentGeneratedFile = path;

    currentTemplateMappingFile = new File(path + "." + this.fileextension);
    currentASTMappingFile = new File(path.replace("." + fileExtension, "")+"_"+AST_MAPPING+"."+this.fileextension);
    currentIncludeSpanFile = new File(path + INCLUDE_EXTENSION);
  }

  @Override
  public void reportFileCreation(String templateName, String qualifiedFilename, String fileExtension, ASTNode ast) {
    SourceMapCalculator.flushMappings();
    writeContent(currentGeneratedFile);
  }

  @Override
  public void flush(ASTNode node) {
    super.flush(node);
  }

  protected void writeContent(String fileName) {
    writeLine(currentTemplateMappingFile, getEncodeSourceMap(new DecodedSourceMap(fileName, this.templateMappings)));
    writeLine(currentASTMappingFile, getEncodeSourceMap(new DecodedSourceMap(fileName, this.astMappings)));
    writeLine(currentIncludeSpanFile, getIncludeSourceMap(fileName, this.includeSpans));
  }

  /**
   * Writes a single Line to the corresponding file. The file is opened if it
   * has not been opened before.
   * Flush Buffer after every write to lower memory overhead
   * @param content
   */
  protected void writeLine(File writeToFile, String content) {
    try {
      // Create possible subdirectories (for first file as the source map is written before the actual file)
      File parentDir = writeToFile.getParentFile();
      if(parentDir != null && !parentDir.exists())
        parentDir.mkdirs();
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
    includeSpans.clear();
  }

  private String getIncludeSourceMap(String fileName, List<IncludeSpan> includeSpans) {
    JsonObject content = new JsonObject();
    content.putMember("file", new UserJsonString(fileName));
    List<String> targetFiles = new ArrayList<>();
    List<String> sourceFiles = new ArrayList<>();
    JsonArray includedFiles = new JsonArray();
    JsonArray includes = new JsonArray();
    JsonArray sources = new JsonArray();
    // Build list of all included files on the fly to avoid iterating twice,
    // keep list of plain strings for faster lookup
    for (IncludeSpan span : includeSpans) {
      int posTarget = targetFiles.indexOf(span.targetTemplate);
      if(posTarget == -1) {
        // First occurrence
        targetFiles.add(span.targetTemplate);
        includedFiles.add(new UserJsonString(span.targetTemplate));
        posTarget = targetFiles.size() - 1;
      }
      int posSource = sourceFiles.indexOf(span.start.getFileName().get());
      if(posSource == -1){
        // First occurrence
        sourceFiles.add(span.start.getFileName().get());
        sources.add(new UserJsonString(span.start.getFileName().get()));
        posSource = sourceFiles.size() - 1;
      }

      JsonObject include = new JsonObject();
      JsonObject start = new JsonObject();
      start.putMember("line", new JsonNumber(span.start.getLine() + ""));
      start.putMember("col", new JsonNumber(span.start.getColumn() + ""));
      JsonObject end = new JsonObject();
      end.putMember("line", new JsonNumber(span.end.getLine() + ""));
      end.putMember("col", new JsonNumber(span.end.getColumn() + ""));
      include.putMember("start", start);
      include.putMember("end", end);
      include.putMember("source", new JsonNumber(posSource + ""));
      include.putMember("target", new JsonNumber(posTarget + ""));
      includes.add(include);
    }
    content.putMember("sources", sources);
    content.putMember("includedFiles", includedFiles);
    content.putMember("includes", includes);
    return content.toString();
  }
}
