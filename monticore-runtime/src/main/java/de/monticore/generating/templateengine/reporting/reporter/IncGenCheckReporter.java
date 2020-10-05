/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.reporting.reporter;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportCreator;
import de.monticore.incremental.IncrementalChecker;
import de.monticore.io.paths.IterablePath;
import org.antlr.v4.runtime.misc.OrderedHashSet;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static de.monticore.generating.templateengine.reporting.reporter.InputOutputFilesReporter.MISSING;

public class IncGenCheckReporter extends AReporter {

  final static String SIMPLE_FILE_NAME = "IncGenCheck";

  List<String> grammarFiles = Lists.newArrayList();

  Set<String> usedHWCFiles = new OrderedHashSet<>();

  Set<String> notExistentHWCFiles = new OrderedHashSet<>();

  private Set<Path> filesThatMatterButAreNotThereInTime = new LinkedHashSet<>();

  private static Map<Path, Path> modelToArtifactMap = new HashMap<>();

  private String inputFile;

  String outputDir;

  Path qualifiedInputFile;

  public IncGenCheckReporter(String outputDir, String modelName) {
    super(outputDir + File.separator + modelName.replaceAll("\\.", "/"), SIMPLE_FILE_NAME, "sh");
    this.outputDir = outputDir;
  }

  @Override
  public void reportHWCExistenceCheck(IterablePath parentDirs, Path fileName,
                                      Optional<Path> result) {
    if (result.isPresent()) {
      String usedHWCFile = toUnixPath(result.get().toString());
      usedHWCFiles.add(usedHWCFile);
    }
    else {
      for (Path p : parentDirs.getPaths()) {
        String notExistentHWCFile = toUnixPath(p.resolve(fileName).toString());
        notExistentHWCFiles.add(notExistentHWCFile);
      }
    }
  }

  private String toUnixPath(String file) {
    return file.replaceAll("\\\\", "/");
  }

  private String toAbsoluteUnixPath(String relativePath) {
    Path filePath = Paths.get(relativePath);
    String file = filePath.toAbsolutePath().toString();
    file = file.replaceAll("\\\\", "/");
    return file;
  }

  @Override
  public void reportParseInputFile(Path inputFilePath, String modelName){
    // IMPORTANT
    // this entirely resets the gathered information, hence the corresponding
    // event reportParseInputFile must only be called once for each actual input
    // file, i.e., the things that are parsed
    String lowerCaseName = modelName.replaceAll("\\.", "/").toLowerCase();
    this.reportingHelper = new ReportCreator(outputDir + File.separator + lowerCaseName);
    notExistentHWCFiles.clear();
    usedHWCFiles.clear();
    grammarFiles.clear();
    filesThatMatterButAreNotThereInTime.clear();
    inputFile = inputFilePath.toString();

    qualifiedInputFile = Paths.get(lowerCaseName + "."
        + Files.getFileExtension(inputFilePath.getFileName().toString()));

    Path parent = inputFilePath.subpath(0,
        inputFilePath.getNameCount() - qualifiedInputFile.getNameCount());
    parent = inputFilePath.getRoot().resolve(parent);

    modelToArtifactMap.put(qualifiedInputFile, parent);
  }

  @Override
  public void reportOpenInputFile(Optional<Path> parentPath, Path file){
    if(file.compareTo(qualifiedInputFile) == 0){
      return;
    }
    String toAdd = "";
    if(parentPath.isPresent()){
      toAdd = Paths.get(parentPath.get().toString(), file.toString()).toString();
      modelToArtifactMap.put(file, parentPath.get());
    }
    else {
      if (modelToArtifactMap.keySet().contains(file)) {
        toAdd = Paths.get(modelToArtifactMap.get(file).toString(),
            file.toString()).toString();
      }
      else {
        filesThatMatterButAreNotThereInTime.add(file);
      }
    }
    if (!toAdd.isEmpty() && !grammarFiles.contains(toAdd)) {
      grammarFiles.add(toAdd);
    }
  }

  @Override
  protected void writeHeader() {
    // No header
  }

  @Override
  public void flush(ASTNode node) {
    openFile();
    //create check: grammar changed?
    for (Path lateOne : filesThatMatterButAreNotThereInTime) {
      if (modelToArtifactMap.keySet().contains(lateOne)) {
        String toAdd = Paths.get(modelToArtifactMap.get(lateOne).toString(), lateOne.toString()).toString();
        if (!grammarFiles.contains(toAdd)) {
          grammarFiles.add(toAdd);
        }
      }
    }

    Collections.sort(grammarFiles);

    if (inputFile != null && !inputFile.isEmpty()) {
      String checkSum;
      if (node != null) {
        checkSum = IncrementalChecker.getChecksum(inputFile);
        //test if file was removed
        String file = inputFile.replaceAll("\\\\", "/");
        writeLine("[ -e " + file + " ] || (touch $1; echo " + file + " removed!; exit 0;)");
        //test if file was changed by comparing its hash to its previous hash
        writeLine("md5sum -c <<<\"" +checkSum +" *" + file + "\" || (touch $1; echo " + file + " changed!; exit 0;)");
      }
      for (String s : grammarFiles) {
        String digest;
        if (!s.contains(".jar")) {
          File inputFile = new File(s);
          if (inputFile.exists()) {
            digest = IncrementalChecker.getChecksum(inputFile.toString());
          }
        } else {
          digest = MISSING;
        }
        //test if file was removed
        String file = s.replaceAll("\\\\", "/");
        writeLine("[ -e " + file + " ] || (touch $1; echo " + file + " removed!; exit 0;)");
        //test if file was changed by comparing its hash to its previous hash
        writeLine("md5sum -c <<<\"" +digest +" *" + file + "\" || (touch $1; echo " + file + " changed!; exit 0;)");
      }
    }
    // create check: used file deleted?
    for (String p : usedHWCFiles) {
      writeLine("[ -e " + p + " ] || (touch $1; echo " + p + " removed!; exit 0;)");
    }
    // create check: relevant file added?
    for (String p : notExistentHWCFiles) {
      writeLine("[ -e " + p + " ] && (touch $1; echo " + p + " added!; exit 0;)");
    }

    super.flush(node);
  }
}
