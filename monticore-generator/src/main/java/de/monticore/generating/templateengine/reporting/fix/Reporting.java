/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.reporting.fix;

import de.monticore.generating.templateengine.reporting.ReportingFix;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;

/**
 * TODO: Remove after 7.7.0 release
 * Enables the groovy scripts to use "Reporting" instead of having to use
 * "ReportingFix"
 */
@Deprecated(forRemoval = true)
public class Reporting extends ReportingFix {
  @Deprecated
  public static void init(String outputDirectory, String reportDirectory, ReportManager.ReportManagerFactory factory) {
    // Delegate to static
    System.err.println("###### Reporting.init ####");
    ReportingFix.init(outputDirectory, reportDirectory, factory);
  }

}
