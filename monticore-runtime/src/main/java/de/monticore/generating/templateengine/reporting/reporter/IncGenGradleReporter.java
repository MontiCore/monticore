/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.reporting.reporter;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.ReportingHelper;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static de.monticore.generating.templateengine.reporting.reporter.InputOutputFilesReporter.GEN_ERROR;
import static de.monticore.generating.templateengine.reporting.reporter.InputOutputFilesReporter.MISSING;

public class IncGenGradleReporter extends IncGenReporter {

  static final String SIMPLE_FILE_NAME = "IncGenGradleCheck";
  protected final String fileExtension;

  public IncGenGradleReporter(String outputDir, String modelName) {
    this(outputDir, modelName, "mc4");
  }

  public IncGenGradleReporter(String outputDir, String modelName, String fileExtension) {
    super(outputDir + File.separator + modelName.replaceAll("\\.", "/"), SIMPLE_FILE_NAME, "txt");
    this.outputDir = outputDir;
    this.fileExtension = fileExtension;
  }

  @Override
  protected boolean isModelFile(String fileName) {
    return fileName.endsWith("." + fileExtension);
  }

  @Override
  public void flush(ASTNode node) {
    openFile();

    for (Path lateOne : filesThatMatterButAreNotThereInTime) {
      if (modelToArtifactMap.keySet().contains(lateOne)) {
        String toAdd = Paths.get(modelToArtifactMap.get(lateOne).toString(), lateOne.toString()).toString();
        if (!modelFiles.contains(toAdd)) {
          modelFiles.add(toAdd);
        }
      }
    }


    if (inputFile != null && !inputFile.isEmpty()) {
      String checkSum;
      if (node != null) {
        checkSum = ReportingHelper.getChecksum(inputFile);
      } else {
        checkSum = GEN_ERROR;
      }
      writeLine(fileExtension + ":" + inputFile.replaceAll("\\\\", "/") + " " + checkSum);
      for (String s : modelFiles) {
        //only local files are important
        if (!s.contains(".jar")) {
          File inputFile = new File(s);
          if (inputFile.exists()) {
            checkSum = ReportingHelper.getChecksum(inputFile.toString());
          } else {
            checkSum = MISSING;
          }
          writeLine(fileExtension + ":" + s.replaceAll("\\\\", "/") + " " + checkSum);
        }
      }
    }
    //create check: user templates changed or deleted?
    for (String s : userTemplates) {
      //only local files are important
      if (!s.contains(".jar")) {
        File inputFile = new File(s);
        String checkSum;
        if (inputFile.exists()) {
          checkSum = ReportingHelper.getChecksum(inputFile.toString());
        }else{
          checkSum = MISSING;
        }
        writeLine("ftl:" + s.replaceAll("\\\\", "/") + " " + checkSum);
      }
    }
    // create check: used file deleted?
    for (String p : usedHWCFiles) {
//      writeLine("if (-not (Test-Path " + p + ")) { echo \"" + p + " removed!\"; exit}");
      writeLine("hwc:" + p);
    }
    // create check: relevant file added?
    for (String p : notExistentHWCFiles) {
//        writeLine("if (Test-Path " + p + ") { echo \"" + p + " added!\"; exit}");
      writeLine("gen:" + p);
    }

    for (String p : outputFiles) {
      writeLine("out:" + p);
    }
    super.flush(node);
  }
}
