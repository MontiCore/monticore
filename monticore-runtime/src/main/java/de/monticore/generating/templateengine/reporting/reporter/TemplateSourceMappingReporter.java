package de.monticore.generating.templateengine.reporting.reporter;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.source_mapping.DecodedMapping;
import de.monticore.generating.templateengine.source_mapping.DecodedSourceMap;
import de.monticore.generating.templateengine.source_mapping.SourceMapCalculator;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.generating.templateengine.source_mapping.encoding.Encoding.encodDecodedSourceMapToString;

public class TemplateSourceMappingReporter extends AReporter {
  public static boolean FIRST_FILE_WRITE = false;

  List<DecodedMapping> mappings = new ArrayList<>();

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
  public void reportTemplateSourceMapping(String qualifiedTemplateName, List<DecodedMapping> mapping) {
    this.mappings.addAll(mapping);
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
    writeLine(encodDecodedSourceMapToString(new DecodedSourceMap(fileName, mappings)));
  }

  protected void clearVariables() {
    mappings.clear();
  }
}
