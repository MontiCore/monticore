/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import com.google.common.collect.Maps;
import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.se_rwth.commons.Names;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 */
public class InstantiationsReporter extends AReporter {
  
  public static final String SIMPLE_FILE_NAME = "06_Instantiations";
  
  protected Map<String, Integer> instantiateCount = Maps.newTreeMap();
  
  public InstantiationsReporter(String outputDir, String modelName) {
    super(outputDir
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);    
  }
  
  @Override
  protected void writeHeader() {
    writeLine("========================================================== Instantiations");
    writeLine("#Instantiations  JavaType");
  }
  
  protected void writeContent() {
    for (Entry<String, Integer> entry : instantiateCount.entrySet()) {
      String s = entry.getValue() + "x";
      writeLine(s + Layouter.getSpaceString(17 - s.length()) + entry.getKey());
    }
  }
  
  protected void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("This is the list of instantiated java type (triggered by the TC).");
    writeLine("- #Instantiations: how often an object of the corresponding type has");
    writeLine("                   been instantiated.");
    writeLine("(EOF)");
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportInstantiate(java.lang.String,
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
