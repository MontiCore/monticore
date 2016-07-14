/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.templateclassgenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Modelfinder {
  
  /**
   * Finds all models with a certain file ending in the given model path
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
      System.out.println("[Modelfinder] found model: " + model);
      models.add(model);
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
