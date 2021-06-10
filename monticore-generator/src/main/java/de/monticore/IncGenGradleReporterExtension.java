/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.generating.templateengine.reporting.reporter.IncGenGradleReporter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * temporary fix for gradle report - can be deleted after the release of version 7.1.0
 */
@Deprecated
public class IncGenGradleReporterExtension extends IncGenGradleReporter {

  public IncGenGradleReporterExtension(String outputDir, String modelName) {
    super(outputDir, modelName);
  }

  @Override
  public void reportOpenInputFile(Optional<Path> parentPath, Path file) {
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
    if (!toAdd.isEmpty() && toAdd.endsWith(".mc4") && !grammarFiles.contains(toAdd)) {
      grammarFiles.add(toAdd);
    }
  }

  @Override
  public void reportUserSpecificTemplate(Path parentDir, Path fileName) {
    if (parentDir != null) {
      String toAdd = Paths.get(parentDir.toString(), fileName.toString()).toString();
      if(!toAdd.isEmpty() && toAdd.endsWith(".ftl") && !userTemplates.contains(toAdd)) {
        userTemplates.add(toAdd);
      }
    }
  }
}
