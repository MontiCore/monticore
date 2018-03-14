/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;

import de.monticore.generating.templateengine.reporting.artifacts.ArtifactReporter;
import de.monticore.generating.templateengine.reporting.artifacts.formatter.GMLFormatter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;


/**
 */
public class ArtifactGmlReporter extends ArtifactReporter {
  
  final static String SIMPLE_FILE_NAME = "15_ArtifactGml";  
  
  public ArtifactGmlReporter(String outputDir, String modelName) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator + modelName,
        SIMPLE_FILE_NAME, "gml", new GMLFormatter());
  }  
}
