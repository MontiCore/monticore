/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.reporting.reporter;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.ReportingHelper;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import static de.monticore.generating.templateengine.reporting.reporter.InputOutputFilesReporter.GEN_ERROR;
import static de.monticore.generating.templateengine.reporting.reporter.InputOutputFilesReporter.MISSING;

public class IncGenGradleReporter extends IncGenReporter {

  static final String SIMPLE_FILE_NAME = "IncGenGradleCheck";
  protected final String fileExtension;
  protected Function<Path, Path> reportPathOutput;

  public IncGenGradleReporter(String outputDir, String modelName) {
    this(outputDir, modelName, "mc4");
  }
  public IncGenGradleReporter(String outputDir, String modelName, String fileExtension) {
    this(outputDir, p->p, modelName, "mc4");
  }
  public IncGenGradleReporter(String outputDir, Function<Path, Path> reportPathOutput, String modelName) {
    this(outputDir, reportPathOutput, modelName, "mc4");
  }

  public IncGenGradleReporter(String outputDir, Function<Path, Path> reportPathOutput, String modelName, String fileExtension) {
    super(outputDir + File.separator + modelName, SIMPLE_FILE_NAME, "txt");

    this.outputDir = Paths.get(outputDir);
    this.fileExtension = fileExtension;
    this.reportPathOutput = reportPathOutput;
  }

  @Override
  protected boolean isModelFile(String fileName) {
    return fileName.endsWith("." + fileExtension);
  }

  protected String printPath(Path p){
    return reportPathOutput.apply(p)
        .toString()
        .replaceAll("\\\\", "/");
  }

  @Override
  public void flush(ASTNode node) {
    openFile();

    for (Path lateOne : filesThatMatterButAreNotThereInTime) {
      if (modelToArtifactMap.keySet().contains(lateOne)) {
        Path toAdd = Paths.get(modelToArtifactMap.get(lateOne).toString(), lateOne.toString());
        if (!modelFiles.contains(toAdd)) {
          modelFiles.add(toAdd);
        }
      }
    }


    if (inputFile != null && !inputFile.toString().isEmpty()) {
      String checkSum;
      if (node != null) {
        checkSum = ReportingHelper.getChecksum(inputFile.toFile().getAbsolutePath());
      } else {
        checkSum = GEN_ERROR;
      }
      writeLine(fileExtension + ":" + printPath(inputFile) + " " + checkSum);
      for (Path s : modelFiles) {
        //only local files are important
        if (!s.endsWith(".jar")) {
          File inputFile = s.toFile();
          if (inputFile.exists()) {
            checkSum = ReportingHelper.getChecksum(inputFile.toString());
          } else {
            checkSum = MISSING;
          }
          writeLine(fileExtension + ":" + printPath(s) + " " + checkSum);
        }
      }
    }
    //create check: user templates changed or deleted?
    for (Path s : userTemplates) {
      //only local files are important
      if (!s.endsWith(".jar")) {
        File inputFile = s.toFile();
        String checkSum;
        if (inputFile.exists()) {
          checkSum = ReportingHelper.getChecksum(inputFile.toString());
        }else{
          checkSum = MISSING;
        }
        writeLine("ftl:" + printPath(s) + " " + checkSum);
      }
    }
    // create check: used file deleted?
    for (Path p : usedHWCFiles) {
//      writeLine("if (-not (Test-Path " + p + ")) { echo \"" + p + " removed!\"; exit}");
      writeLine("hwc:" + printPath(p));
    }
    // create check: relevant file added?
    for (Path p : notExistentHWCFiles) {
//        writeLine("if (Test-Path " + p + ") { echo \"" + p + " added!\"; exit}");
      writeLine("gen:" + printPath(p));
    }

    for (Path p : outputFiles) {
      writeLine("out:" + printPath(p));
    }
    super.flush(node);
  }
}
