/* (c) https://github.com/MontiCore/monticore */
package de.monticore.templateclassgenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.monticore.templateclassgenerator.codegen.TemplateClassGeneratorConstants;
import de.se_rwth.commons.logging.Log;

/**
 * Class that finds all models with a given file extension in a modelpath.
 *
 * @author Jerome Pfeiffer
 */
public class Modelfinder {
  
  /**
   * Finds all models with a certain {@code fileExtension} in the given
   * {@code modelPath}
   * 
   * @param modelPath
   * @param fileExtension
   * @return List of all found models
   */
  public static List<String> getModelsInModelPath(File modelPath, String fileExtension) {
    List<String> models = new ArrayList<String>();
    String[] extension = { fileExtension };
    Collection<File> files = FileUtils.listFiles(modelPath, extension, true);
    for (File f : files) {
      String model = getDotSeperatedFQNModelName(modelPath.getPath(), f.getPath(), fileExtension);
      if (model.startsWith(TemplateClassGeneratorConstants.TEMPLATE_CLASSES_SETUP_PACKAGE)) {
        Log.error("0xA700 ATemplate '" + model + "' must not lay in topfolder '"
            + TemplateClassGeneratorConstants.TEMPLATE_CLASSES_SETUP_PACKAGE + "'");
      }
      
      else {
        Log.info("Found model: " + model, "Modelfinder");
        models.add(model);
      }
    }
    return models;
  }
  
  private static String getDotSeperatedFQNModelName(String FQNModelPath, String FQNFilePath,
      String fileExtension) {
    if (FQNFilePath.contains(FQNModelPath)) {
      String fqnModelName = FQNFilePath.substring(FQNModelPath.length() + 1);
      fqnModelName = fqnModelName.replace("." + fileExtension, "");
      if (fqnModelName.contains("\\")) {
        fqnModelName = fqnModelName.replaceAll("\\\\", ".");
      }
      else if (fqnModelName.contains("/")) {
        fqnModelName = fqnModelName.replaceAll("/", ".");
      }
      
      return fqnModelName;
    }
    return FQNFilePath;
  }
  
}
