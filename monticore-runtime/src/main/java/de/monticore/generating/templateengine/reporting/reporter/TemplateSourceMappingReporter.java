package de.monticore.generating.templateengine.reporting.reporter;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.source_mapping.DecodedMapping;
import de.monticore.generating.templateengine.source_mapping.DecodedSourceMap;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.generating.templateengine.source_mapping.encoding.Encoding.encodDecodedSourceMapToString;

public class TemplateSourceMappingReporter extends AReporter {
  List<DecodedMapping> mappings = new ArrayList<>();

  public TemplateSourceMappingReporter(String path, String qualifiedFileName, String fileExtension) {
    super(path, qualifiedFileName, fileExtension);
    System.out.println("Creating Template Source Mapping Reporter");
  }

  @Override
  protected void writeHeader() {
    clearVariables();
  }

  @Override
  public void reportTemplateSourceMapping(String qualifiedTemplateName, List<DecodedMapping> mapping) {
    System.out.println("Called reportTemplateSourceMapping "+qualifiedTemplateName);
    this.mappings.addAll(mapping);
  }

  @Override
  public void reportFileCreation(String templateName, String qualifiedFilename, String fileExtension, ASTNode ast) {
    System.out.println("Writing Content to File "+templateName);
    writeContent(qualifiedFilename+"."+fileExtension);
    clearVariables();
  }

  @Override
  public void flush(ASTNode node) {
    super.flush(node);
    clearVariables();
  }

  protected void writeContent(String fileName) {
    writeLine(encodDecodedSourceMapToString(new DecodedSourceMap(fileName, mappings)));
  }

  protected void clearVariables() {
    mappings.clear();
  }
}
