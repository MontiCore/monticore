/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import de.monticore.generating.templateengine.reporting.artifacts.ArtifactReporter;
import de.monticore.generating.templateengine.reporting.artifacts.formatter.GMLFormatter;

import java.io.File;


/**
 */
public class ArtifactGmlReporter extends ArtifactReporter {
  
  final static String SIMPLE_FILE_NAME = "15_ArtifactGml";  
  
  public ArtifactGmlReporter(String outputDir, String modelName) {
    super(outputDir + File.separator + modelName,
        SIMPLE_FILE_NAME, "gml", new GMLFormatter());
  }  
}
