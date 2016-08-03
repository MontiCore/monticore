/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2016, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */
package de.monticore.templateclassgenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Class that finds all models with a given file extension in a modelpath.
 *
 * @author  Jerome Pfeiffer
 *          
 */
public class Modelfinder {
  
  /**
   * Finds all models with a certain {@code fileExtension} in the given {@code modelPath}
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
