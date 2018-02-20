/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;

/**
 */
public class TemplatesReporter extends AReporter {
  
  final static String SIMPLE_FILE_NAME = "04_Templates";
  
  final static String INDENT = Layouter.getSpaceString(10);
  
  private SortedMap<String, Integer> templateCount = new TreeMap<String, Integer>();
  
  private SortedMap<String, Integer> hwTemplateCount = new TreeMap<String, Integer>();
  
  private Set<String> realTemplateNames = new LinkedHashSet<>();
  
  private Set<String> realHWTemplateNames = new LinkedHashSet<>();
  
  private ReportingRepository repository;
  
  public TemplatesReporter(String outputDir, String modelName, ReportingRepository repository) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);
    this.repository = repository;
  }
  
  @Override
  protected void writeHeader() {
    // Write empty header
  }
  
  private void writeContent() {
    writeUsedTemplates();
    writeUnusedTemplates();
    // TODO: see #1738 properly deal with handwritten template inclusion
    writeUsedUSTemplates();
    writeUnusedUSTemplates();
  }
  
  private void writeUsedTemplates() {
    writeLine("========================================================== Used Templates");
    writeLine("#Calls:   Template Name");
    for (Entry<String, Integer> entry : templateCount.entrySet()) {
      String countString = entry.getValue() + "x";
      writeLine(countString + getIndentAfterCount(countString) + entry.getKey());
    }
  }
  
  private void writeUnusedTemplates() {
    writeLine("========================================================== Unused Templates");
    SortedSet<String> unusedTemplateNames = new TreeSet<String>(repository.getAllTemplateNames());
    unusedTemplateNames.removeAll(realTemplateNames);
    for (String t : unusedTemplateNames) {
      writeLine(t);
    }
  }
  
  private void writeUsedUSTemplates() {
    writeLine("========================================================== Used USTemplates");
    writeLine("#Calls:   Template Name");
    for (Entry<String, Integer> entry : hwTemplateCount.entrySet()) {
      String countString = entry.getValue() + "x";
      writeLine(countString + getIndentAfterCount(countString) + entry.getKey());
    }
  }
  
  private void writeUnusedUSTemplates() {
    writeLine("========================================================== Unused USTemplates");
    SortedSet<String> unusedHWTemplateNames = new TreeSet<String>(
        repository.getAllHWTemplateNames());
    unusedHWTemplateNames.removeAll(realHWTemplateNames);
    for (String t : unusedHWTemplateNames) {
      writeLine(t);
    }
  }
  
  private void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("Used Templates: the list of standard template being used.");
    writeLine("Used USTemplates: the list of user specifc templates being used.");
    writeLine("- #Executions: how often the template was called/included");
    writeLine("Unused Templates: the list of templates which have not been executed");
    writeLine("Unused USTemplates: the list of user specifc templates which");
    writeLine("                    have not been executed.");
    writeLine("All lists are sorted");
    writeLine("(EOF)");
  }
  
  private String getIndentAfterCount(String countString) {
    String indentString = Layouter.getSpaceString(2);
    if (countString.length() < INDENT.length() + 1) {
      indentString = INDENT.substring(countString.length());
    }
    
    return indentString;
  }
  
  @Override
  public void reportTemplateStart(String templatename, ASTNode ast) {
    Set<String> hwTemplates = repository.getAllHWTemplateNames();
    // if template is handwritten
    if (hwTemplates.contains(templatename.replaceAll("\\.", "/").concat(".")
        .concat(ReportingConstants.TEMPLATE_FILE_EXTENSION))) {
      realHWTemplateNames.add(templatename.replaceAll("\\.", "/").concat(".")
          .concat(ReportingConstants.TEMPLATE_FILE_EXTENSION));
      templatename = ReportingHelper.getTemplateName(templatename);
      
      if (hwTemplateCount.containsKey(templatename)) {
        Integer actualCount = hwTemplateCount.get(templatename);
        hwTemplateCount.put(templatename, actualCount + 1);
      }
      else {
        hwTemplateCount.put(templatename, 1);
      }
    }
    else {
      realTemplateNames.add(templatename.replaceAll("\\.", "/").concat(".")
          .concat(ReportingConstants.TEMPLATE_FILE_EXTENSION));
      templatename = ReportingHelper.getTemplateName(templatename);
      
      if (templateCount.containsKey(templatename)) {
        Integer actualCount = templateCount.get(templatename);
        templateCount.put(templatename, actualCount + 1);
      }
      else {
        templateCount.put(templatename, 1);
      }
    }
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeContent();
    writeFooter();
    templateCount.clear();
    super.flush(ast);
  }
  
}
