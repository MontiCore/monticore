/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.reporting.reporter;

import com.google.common.io.Files;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.ReportCreator;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.misc.OrderedHashSet;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public abstract class IncGenReporter extends AReporter {

  protected Set<String> modelFiles = new OrderedHashSet<>();

  protected Set<String> usedHWCFiles = new OrderedHashSet<>();

  protected Set<String> notExistentHWCFiles = new OrderedHashSet<>();

  protected Set<Path> filesThatMatterButAreNotThereInTime = new LinkedHashSet<>();

  protected Set<String> userTemplates = new OrderedHashSet<>();

  protected Set<String> outputFiles = new OrderedHashSet<>();

  protected static Map<Path, Path> modelToArtifactMap = new HashMap<>();

  protected String inputFile;

  protected String outputDir;

  protected Path qualifiedInputFile;

  protected IncGenReporter(String path, String qualifiedFileName, String fileextension) {
    super(path, qualifiedFileName, fileextension);
  }

  @Override
  public void reportHWCExistenceCheck(MCPath mcp, Path fileName, Optional<URL> result) {

    if (result.isPresent()) {
      String usedHWCFile = null;
      try {
        usedHWCFile = toUnixPath(new File(result.get().toURI()).toString());
        usedHWCFiles.add(usedHWCFile);
      } catch (URISyntaxException e) {
        Log.warn("0xA0136 Cannot report hwc file", e);

      }
    }
    else {
      for (Path p : mcp.getEntries()) {
        String notExistentHWCFile = toUnixPath(p.resolve(fileName).toString());
        notExistentHWCFiles.add(notExistentHWCFile);
      }
    }
  }

  protected String toUnixPath(String file) {
    return file.replaceAll("\\\\", "/");
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
    modelFiles.clear();
    userTemplates.clear();
    filesThatMatterButAreNotThereInTime.clear();
    outputFiles.clear();

    inputFile = inputFilePath.toString();

    qualifiedInputFile = Paths.get(lowerCaseName + "."
        + Files.getFileExtension(inputFilePath.getFileName().toString()));

    Path parent = inputFilePath.subpath(0,
        inputFilePath.getNameCount() - qualifiedInputFile.getNameCount());
    parent = inputFilePath.getRoot().resolve(parent);

    modelToArtifactMap.put(qualifiedInputFile, parent);
  }

  @Override
  public void reportFileCreation(String fileName) {
    outputFiles.add(fileName);
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
    if (!toAdd.isEmpty() && isModelFile(toAdd)) {
      modelFiles.add(toAdd);
    }
  }

  protected boolean isModelFile(String fileName) {
    return fileName.endsWith(".mc4");
  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportUserSpecificTemplate(java.nio.file.Path,
   * java.nio.file.Path)
   */
  @Override
  public void reportUserSpecificTemplate(Path parentDir, Path fileName) {
    if (parentDir != null) {
      String toAdd = Paths.get(parentDir.toString(), fileName.toString()).toString();
      if(!toAdd.isEmpty() && toAdd.endsWith(".ftl")) {
        userTemplates.add(toAdd);
      }
    }
  }

  @Override
  public void writeHeader(){
    //no header
  }
}
