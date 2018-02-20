/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.ReportCreator;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;

/**
 * It reports the name of all input and output files as well as the name of
 * files which existence was checked.
 *
 */
public class InvolvedFilesReporter extends AReporter {
  
  public static final String SIMPLE_FILE_NAME = "18_InvolvedFiles";
  
  public final static String INDENT = Layouter.getSpaceString(40);
  
  public static final String PARENT_FILE_SEPARATOR = "!/";
  
  private List<String> inputFiles = Lists.newArrayList();
  
  private List<String> outputFiles = Lists.newArrayList();
  
  private List<String> checkedFiles = Lists.newArrayList();
  
  private Map<Path, Path> modelToArtifactMap = new HashMap<>();
  
  private String outputDirectory = "";
  
  private Optional<String> inputFile = Optional.empty();
  
  private Optional<Path> qualifiedInputFile = Optional.empty();
  
  /**
   * Constructor for de.monticore.generating.templateengine.reporting.reporter.
   * FilesReporter
   * 
   * @param outputDir
   */
  public InvolvedFilesReporter(String outputDir) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator,
        SIMPLE_FILE_NAME, ReportingConstants.REPORT_FILE_EXTENSION);
    this.outputDirectory = outputDir;
  }
  
  public static final String INPUT_FILE_HEADING = "================================= Parsed Input file ==================";
  
  protected void writeInputFileHeading() {
    writeLine(INPUT_FILE_HEADING);
  }
  
  public static final String INPUT_FILES_HEADING = "\n================================= Input files ========================";
  
  protected void writeInputFilesHeading() {
    writeLine(INPUT_FILES_HEADING);
  }
  
  public static final String OUTPUT_FILE_HEADING = "\n================================= Output files =======================";
  
  protected void writeOutputFileHeading() {
    writeLine(OUTPUT_FILE_HEADING);
  }
  
  public static final String EXISTS_FILE_HEADING = "\n================================= Check existence of files ============";
  
  protected void writeExistsFileHeading() {
    writeLine(EXISTS_FILE_HEADING);
  }
  
  public static final String EOF = "\n================================= EOF =================================";
  
  private void writeFooter() {
    writeLine(EOF);
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportFileCreation(java.lang.String,
   * java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileCreation(String fileName) {
    if (!outputFiles.contains(fileName)) {
      outputFiles.add(fileName);
    }
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportFileCreation(java.lang.String,
   * java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileExistenceChecking(List<Path> parentPath, Path file) {
    parentPath.forEach(p -> checkedFiles
        .add(p != null ? p.toString() + PARENT_FILE_SEPARATOR + file.toString() : file.toString()));
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportOpenInputFile(java.nio.file.Path)
   */
  @Override
  public void reportOpenInputFile(Optional<Path> parentPath, Path file) {
    if (qualifiedInputFile.isPresent() && qualifiedInputFile.get().compareTo(file) == 0) {
      return;
    }
    String toAdd = "";
    if (parentPath.isPresent()) {
      toAdd = parentPath.get().toString() + PARENT_FILE_SEPARATOR + file.toString();
      modelToArtifactMap.put(file, parentPath.get());
    }
    else if (modelToArtifactMap.keySet().contains(file)) {
      toAdd = modelToArtifactMap.get(file).toString() + PARENT_FILE_SEPARATOR
          + file.toString();
    }
    toAdd = format(toAdd);
    if (!toAdd.isEmpty() && !inputFiles.contains(toAdd)) {
      inputFiles.add(toAdd);
    }
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportOpenInputFile(java.nio.file.Path)
   */
  @Override
  public void reportOpenInputFile(String fileName) {
    fileName = format(fileName);
    if (!inputFiles.contains(fileName)) {
      inputFiles.add(fileName);
    }
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportParseInputFile(java.nio.file.Path,
   * java.lang.String)
   */
  @Override
  public void reportParseInputFile(Path inputFilePath, String modelName) {
    this.reportingHelper = new ReportCreator(outputDirectory + File.separator
        + ReportingConstants.REPORTING_DIR + File.separator + modelName);
    inputFiles.clear();
    outputFiles.clear();
    checkedFiles.clear();
    modelToArtifactMap.clear();
    inputFile = Optional.of(inputFilePath.toString());
    
    qualifiedInputFile = Optional.of(Paths.get(modelName.replaceAll("\\.", "/") + "."
        + Files.getFileExtension(inputFilePath.getFileName().toString())));
    
    Path parent = inputFilePath.subpath(0,
        inputFilePath.getNameCount() - qualifiedInputFile.get().getNameCount());
    parent = inputFilePath.getRoot().resolve(parent);
    modelToArtifactMap.put(qualifiedInputFile.get(), parent);
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeContent();
    writeFooter();
    super.flush(ast);
  }
  
  @Override
  protected void writeHeader() {
    writeInputFileHeading();
    if (inputFile.isPresent()) {
      writeLine(inputFile.get());
    }
  }
  
  private String format(String fileName) {
    fileName = fileName.replace('\\', '/');
    return fileName.startsWith("file:/") ? fileName.substring(6) : fileName;
  }
  
  private void writeContent() {
    writeInputFilesHeading();
    inputFiles.forEach(f -> writeLine(f));
    
    Collections.sort(outputFiles);
    writeOutputFileHeading();
    outputFiles.forEach(f -> writeLine(f));
    
    writeExistsFileHeading();
    checkedFiles.forEach(f -> writeLine(f));
  }
  
}
