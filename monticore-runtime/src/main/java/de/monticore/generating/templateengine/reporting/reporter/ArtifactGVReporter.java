/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;

import de.monticore.generating.templateengine.reporting.artifacts.ArtifactReporter;
import de.monticore.generating.templateengine.reporting.artifacts.formatter.GVFormatter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;


/**
 *
 */
public class ArtifactGVReporter extends ArtifactReporter {
  
  final static String SIMPLE_FILE_NAME = "16_ArtifactGv";  
  
  public ArtifactGVReporter(String outputDir, String modelName) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator + modelName,
        SIMPLE_FILE_NAME, "gv", new GVFormatter());
  }
}
