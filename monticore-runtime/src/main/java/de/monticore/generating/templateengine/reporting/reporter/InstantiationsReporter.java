/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.monticore.ast.ASTNode;

import com.google.common.collect.Maps;

import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.se_rwth.commons.Names;

/**
 */
public class InstantiationsReporter extends AReporter {
  
  final static String SIMPLE_FILE_NAME = "06_Instantiations";
  
  private Map<String, Integer> instantiateCount = Maps.newTreeMap();
  
  public InstantiationsReporter(String outputDir, String modelName) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);    
  }
  
  @Override
  protected void writeHeader() {
    writeLine("========================================================== Instantiations");
    writeLine("#Instantiations  JavaType");
  }
  
  private void writeContent() {
    for (Entry<String, Integer> entry : instantiateCount.entrySet()) {
      String s = entry.getValue() + "x";
      writeLine(s + Layouter.getSpaceString(17 - s.length()) + entry.getKey());
    }
  }
  
  private void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("This is the list of instantiated java type (triggered by the TC).");
    writeLine("- #Instantiations: how often an object of the corresponding type has");
    writeLine("                   been instantiated.");
    writeLine("(EOF)");
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportInstantiate(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportInstantiate(String className, List<Object> params) {
    className = Names.getSimpleName(className);
    if (instantiateCount.containsKey(className)) {
      Integer actualCount = instantiateCount.get(className);
      instantiateCount.put(className, actualCount + 1);
    }
    else {
      instantiateCount.put(className, 1);
    }
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeContent();
    writeFooter();
    instantiateCount.clear();
    super.flush(ast);
  }
  
}
