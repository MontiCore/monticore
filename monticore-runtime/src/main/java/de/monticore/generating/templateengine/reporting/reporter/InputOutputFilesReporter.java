/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;

import de.monticore.ast.ASTNode;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.ReportCreator;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.incremental.IncrementalChecker;
import de.se_rwth.commons.logging.Log;

/**
 * This report is used to enable incremental generation. It reports the name of all input and output
 * files.
 *
 */
public class InputOutputFilesReporter extends AReporter {
  
  public static final String SIMPLE_FILE_NAME = "17_InputOutputFiles";
  
  final static String INDENT = Layouter.getSpaceString(40);
  
  private List<String> inputFiles = Lists.newArrayList();
  
  private List<String> hwcFiles = Lists.newArrayList();
  
  private List<String> outputFiles = Lists.newArrayList();
  
  private List<String> userTemplates = Lists.newArrayList();
  
  /**
   * Constructor for de.monticore.generating.templateengine.reporting.reporter.
   * InputOutputFilesReporter
   * 
   * @param outputDir
   */
  public InputOutputFilesReporter(String outputDir) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator,
        SIMPLE_FILE_NAME, ReportingConstants.REPORT_FILE_EXTENSION);
    this.outputDirectory = outputDir;
  }
  
  final String outputDirectory;
  
  public static final String INPUT_FILE_HEADING = "========================================================== Input files";
  
  protected void writeInputFileHeading() {
    writeLine(INPUT_FILE_HEADING);
  }
  
  public static final String USER_TEMPLATE_HEADING = "====================================================== User templates";
  
  protected void writeUserTemplateHeading() {
    writeLine(USER_TEMPLATE_HEADING);
  }
  
  public static final String HWC_FILE_HEADING = "====================================================== Handwritten files";

  protected void writeHWCFileHeading() {
    writeLine(HWC_FILE_HEADING);
  }
  
  public static final String OUTPUT_FILE_HEADING = "========================================================== Output files";
  
  protected void writeOutputFileHeading() {
    writeLine(OUTPUT_FILE_HEADING);
  }
  
  public static final String FOOTER_HEADING = "========================================================== Explanation";
  
  private void writeFooter() {
    writeLine(FOOTER_HEADING);
    writeLine("This report is used to enable incremental generation");
    writeLine("Input files: the list of input files ordered by their paths.");
    writeLine("Types of input files are:");
    writeLine("- Model files");
    writeLine("- Handwritten sourcecode files");
    writeLine("- User specific script files");
    writeLine("- User specific template files");
    writeLine("Output files: the list of generated output files ordered by their paths.");
    writeLine("(EOF)");
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportFileCreation(java.lang.String,
   * java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileCreation(String templatename,
      String qualifiedfilename, String fileextension, ASTNode ast) {
    String filePath = qualifiedfilename.replaceAll("\\.", Matcher.quoteReplacement(File.separator));
    outputFiles.add(filePath + "." + fileextension);
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportFileCreation(java.lang.String,
   * java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileCreation(Path parentPath, Path file) {
    if (file.compareTo(qualifiedInputFile) == 0) {
      return;
    }
    String filePath = parentPath != null
        ? parentPath.toString() + PARENT_FILE_SEPARATOR + file.toString()
        : file.toString();
    outputFiles.add(filePath);
  }
  
  public static final String PARENT_FILE_SEPARATOR = " sub ";
  
  public static final String INPUT_STATE_SEPARATOR = " state ";
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportOpenInputFile(java.nio.file.Path)
   */
  @Override
  public void reportOpenInputFile(Optional<Path> parentPath, Path file) {
    if (file.compareTo(qualifiedInputFile) == 0) {
      return;
    }
    String toAdd = "";
    if (parentPath.isPresent()) {
      toAdd = parentPath.get().toString() + PARENT_FILE_SEPARATOR + file.toString();
      modelToArtifactMap.put(file, parentPath.get());
    }
    else {
      if (modelToArtifactMap.keySet().contains(file)) {
        toAdd = modelToArtifactMap.get(file).toString() + PARENT_FILE_SEPARATOR
            + file.toString();
      }
      else {
        filesThatMatterButAreNotThereInTime.add(file);
      }
    }
    if (!toAdd.isEmpty() && !inputFiles.contains(toAdd)) {
      inputFiles.add(toAdd);
    }
  }
  
  private Set<Path> filesThatMatterButAreNotThereInTime = new LinkedHashSet<>();
  
  // TODO: think about when to clean this up
  private static Map<Path, Path> modelToArtifactMap = new HashMap<>();
  
  // TODO: see todo above :-)
  public static void resetModelToArtifactMap() {
    modelToArtifactMap = new HashMap<>();
  }
  
  private String inputFile;
  
  private Path qualifiedInputFile;
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportParseInputFile(java.nio.file.Path,
   * java.lang.String)
   */
  @Override
  public void reportParseInputFile(Path inputFilePath, String modelName) {
    // IMPORTANT
    // this entirely resets the gathered information, hence the corresponding
    // event reportParseInputFile must only be called once for each actual input
    // file, i.e., the things that are parsed
    this.reportingHelper = new ReportCreator(outputDirectory + File.separator
        + ReportingConstants.REPORTING_DIR + File.separator + modelName);
    inputFiles.clear();
    hwcFiles.clear();
    outputFiles.clear();
    userTemplates.clear();
    filesThatMatterButAreNotThereInTime.clear();
    inputFile = inputFilePath.toString();
    
    qualifiedInputFile = Paths.get(modelName.replaceAll("\\.", "/") + "."
        + Files.getFileExtension(inputFilePath.getFileName().toString()));
    
    Path parent = inputFilePath.subpath(0,
        inputFilePath.getNameCount() - qualifiedInputFile.getNameCount());
    parent = inputFilePath.getRoot().resolve(parent);
    
    modelToArtifactMap.put(qualifiedInputFile, parent);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportUseHandwrittenCodeFile(java.nio.file.Path,
   * java.nio.file.Path)
   */
  @Override
  public void reportUseHandwrittenCodeFile(Path parentDir, Path fileName) {
    Optional<Path> parent = Optional.ofNullable(parentDir);
    String hwcLine = "";
    if (parent.isPresent()) {
      Path parentPath = parent.get().subpath(0,
          parent.get().getNameCount() - fileName.getNameCount());
      parentPath = parent.get().getRoot().resolve(parentPath);
      hwcLine = parentPath.toString();
    }
    hwcLine = hwcLine.concat(PARENT_FILE_SEPARATOR).concat(fileName.toString());
    hwcFiles.add(hwcLine);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportUserSpecificTemplate(java.nio.file.Path,
   * java.nio.file.Path)
   */
  @Override
  public void reportUserSpecificTemplate(Path parentDir, Path fileName) {
    if (parentDir != null) {
      userTemplates.add(parentDir.toString() + PARENT_FILE_SEPARATOR + fileName.toString());
    }
  }
  
  public static final String MISSING = "not found";
  
  public static final String GEN_ERROR = "error during generation";
  
  private void writeContent(ASTNode ast) {
    
    // the magic TODO AHo: document
    for (Path lateOne : filesThatMatterButAreNotThereInTime) {
      if (modelToArtifactMap.keySet().contains(lateOne)) {
        String toAdd = modelToArtifactMap.get(lateOne).toString() + PARENT_FILE_SEPARATOR
            + lateOne.toString();
        if (!inputFiles.contains(toAdd)) {
          inputFiles.add(toAdd);
        }
      }
    }
    
    Collections.sort(inputFiles);
    Collections.sort(outputFiles);
    
    if (inputFile != null && !inputFile.isEmpty()) {
      String checkSum;
      if (ast != null) {
        checkSum = IncrementalChecker.getChecksum(inputFile);
        writeLine(inputFile + INPUT_STATE_SEPARATOR + checkSum);
      }
      else {
        writeLine(inputFile + INPUT_STATE_SEPARATOR + GEN_ERROR);
      }
      
      for (String s : inputFiles) {
        if (s.contains(PARENT_FILE_SEPARATOR)) {
          String[] elements = s.split(PARENT_FILE_SEPARATOR);
          if (elements[0].endsWith(".jar")) {
            String inputFile = elements[0].concat("!" + File.separator).concat(elements[1]);
            try {
              String url = "jar:file:" + inputFile;
              url = url.replaceAll("\\" + File.separator, "/");
              URL input = new URL(url);
              String inputModel = CharStreams.toString(new InputStreamReader(input.openStream()));
              
              MessageDigest md = MessageDigest.getInstance("MD5");
              md.update(inputModel.getBytes());
              String digest = Hashing.md5().hashString(inputModel, Charset.forName("UTF-8"))
                  .toString();
              
              writeLine(s + INPUT_STATE_SEPARATOR + digest);
            }
            catch (IOException | NoSuchAlgorithmException e) {
              Log.warn("0xA0134 Cannot write to log file", e);
            }
            
          }
          else {
            File inputFile = new File(elements[0].concat(File.separator).concat(elements[1]));
            if (inputFile.exists()) {
              checkSum = IncrementalChecker.getChecksum(inputFile.toString());
              writeLine(s + INPUT_STATE_SEPARATOR + checkSum);
            }
            else {
              writeLine(s + INPUT_STATE_SEPARATOR + MISSING);
            }
          }
        }
        else {
          checkSum = IncrementalChecker.getChecksum(s);
          writeLine(s + INPUT_STATE_SEPARATOR + checkSum);
        }
      }
      
      writeUserTemplateHeading();
      for (String s : userTemplates) {
        if (s.contains(PARENT_FILE_SEPARATOR)) {
          String[] elements = s.split(PARENT_FILE_SEPARATOR);
          File inputFile = new File(elements[0].concat(File.separator).concat(elements[1]));
          if (inputFile.exists()) {
            checkSum = IncrementalChecker.getChecksum(inputFile.toString());
            writeLine(s + INPUT_STATE_SEPARATOR + checkSum);
          }
          else {
            writeLine(s + INPUT_STATE_SEPARATOR + MISSING);
          }
        }
        else {
          writeLine(s + INPUT_STATE_SEPARATOR + MISSING);
        }
      }
      
      writeHWCFileHeading();
      for (String hwc : hwcFiles) {
        writeLine(hwc);
      }
      
      writeOutputFileHeading();
      for (String s : outputFiles) {
        writeLine(s);
      }
    }
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeContent(ast);
    writeFooter();
    super.flush(ast);
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.AReporter#writeHeader()
   */
  @Override
  protected void writeHeader() {
    writeInputFileHeading();
  }
  
}
