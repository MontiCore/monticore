package de.monticore.generating.templateengine.reporting.reporter;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.sourcemap.DecodedMapping;
import de.monticore.sourcemap.DecodedSourceMap;
import de.monticore.generating.templateengine.sourcemap.SourceMapCalculator;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.sourcemap.Encoding.getEncodeSourceMap;

public class TemplateSourceMappingReporter extends AReporter {
  public static boolean FIRST_FILE_WRITE = false;

  List<DecodedMapping> templateMappings = new ArrayList<>();
  List<DecodedMapping> astMappings = new ArrayList<>();

  public TemplateSourceMappingReporter(String path, String qualifiedFileName, String fileExtension) {
    super(path, qualifiedFileName, fileExtension);
    System.out.println("Creating Template Source Mapping Reporter");
  }

  @Override
  protected void writeHeader() {
    if(FIRST_FILE_WRITE) {
      SourceMapCalculator.reset();
      FIRST_FILE_WRITE = false;
    }
    clearVariables();
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
  public void reportFileCreation(String templateName, String qualifiedFilename, String fileExtension, ASTNode ast) {
    writeContent(qualifiedFilename+"."+fileExtension);
    clearVariables();
  }

  @Override
  public void flush(ASTNode node) {
    super.flush(node);
    clearVariables();
  }

  protected void writeContent(String fileName) {
    writeLine(getEncodeSourceMap(new DecodedSourceMap(fileName, this.templateMappings)));
    writeLine(getEncodeSourceMap(new DecodedSourceMap(fileName, this.astMappings)));
  }

  protected void clearVariables() {
    templateMappings.clear();
    astMappings.clear();
  }
}
