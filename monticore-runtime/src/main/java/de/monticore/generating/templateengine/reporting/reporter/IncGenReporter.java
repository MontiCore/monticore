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

  protected Set<Path> modelFiles = new OrderedHashSet<>();

  protected Set<Path> usedHWCFiles = new OrderedHashSet<>();

  protected Set<Path> notExistentHWCFiles = new OrderedHashSet<>();

  protected Set<Path> filesThatMatterButAreNotThereInTime = new LinkedHashSet<>();

  protected Set<Path> userTemplates = new OrderedHashSet<>();

  protected Set<Path> outputFiles = new OrderedHashSet<>();

  protected static Map<Path, Path> modelToArtifactMap = new HashMap<>();

  protected Path inputFile;

  protected Path outputDir;

  protected Path qualifiedInputFile;

  protected IncGenReporter(String path, String qualifiedFileName, String fileextension) {
    super(path, qualifiedFileName, fileextension);
  }

  @Override
  public void reportHWCExistenceCheck(MCPath mcp, Path fileName, Optional<URL> result) {

    if (result.isPresent()) {
      String usedHWCFile = null;
      try {
        usedHWCFiles.add(Paths.get(result.get().toURI()));
      } catch (URISyntaxException e) {
        Log.warn("0xA0136 Cannot report hwc file", e);

      }
    }
    else {
      for (Path p : mcp.getEntries()) {
        notExistentHWCFiles.add(p.resolve(fileName));
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
    String lowerCaseName = modelName.toLowerCase();
    this.reportingHelper = new ReportCreator(outputDir + File.separator + lowerCaseName);
    notExistentHWCFiles.clear();
    usedHWCFiles.clear();
    modelFiles.clear();
    userTemplates.clear();
    filesThatMatterButAreNotThereInTime.clear();
    outputFiles.clear();

    inputFile = inputFilePath;

    qualifiedInputFile = Paths.get(lowerCaseName + "."
        + Files.getFileExtension(inputFilePath.getFileName().toString()));

    Path parent = inputFilePath.subpath(0,
        inputFilePath.getNameCount() - qualifiedInputFile.getNameCount());
    parent = inputFilePath.getRoot().resolve(parent);

    modelToArtifactMap.put(qualifiedInputFile, parent);
  }

  @Override
  public void reportFileCreation(String fileName) {
    outputFiles.add(Paths.get(fileName));
  }

  @Override
  public void reportOpenInputFile(Optional<Path> parentPath, Path file){
    if(file.compareTo(qualifiedInputFile) == 0){
      return;
    }
    Path toAdd = null;
    if(parentPath.isPresent()){
      toAdd = Paths.get(parentPath.get().toString(), file.toString());
      modelToArtifactMap.put(file, parentPath.get());
    }
    else {
      if (modelToArtifactMap.keySet().contains(file)) {
        toAdd = Paths.get(modelToArtifactMap.get(file).toString(),
            file.toString());
      }
      else {
        filesThatMatterButAreNotThereInTime.add(file);
      }
    }
    if (toAdd != null && isModelFile(toAdd.toString())) {
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
      Path toAdd = Paths.get(parentDir.toString(), fileName.toString());
      if(!toAdd.toString().isEmpty() && toAdd.endsWith(".ftl")) {
        userTemplates.add(toAdd);
      }
    }
  }

  @Override
  public void writeHeader(){
    //no header
  }
}
