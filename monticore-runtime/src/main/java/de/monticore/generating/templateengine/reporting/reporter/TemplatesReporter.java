/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.*;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

/**
 */
public class TemplatesReporter extends AReporter {
  
  public static final String SIMPLE_FILE_NAME = "04_Templates";
  
  static final String INDENT = Layouter.getSpaceString(10);
  
  protected SortedMap<String, Integer> templateCount = new TreeMap<String, Integer>();
  
  protected SortedMap<String, Integer> hwTemplateCount = new TreeMap<String, Integer>();
  
  protected Set<String> realTemplateNames = new LinkedHashSet<>();
  
  protected Set<String> realHWTemplateNames = new LinkedHashSet<>();
  
  protected ReportingRepository repository;
  
  public TemplatesReporter(String outputDir, String modelName, ReportingRepository repository) {
    super(outputDir
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);
    this.repository = repository;
  }
  
  @Override
  protected void writeHeader() {
    // Write empty header
  }
  
  protected void writeContent() {
    writeUsedTemplates();
    writeUsedUSTemplates();
    writeUnusedUSTemplates();
  }
  
  protected void writeUsedTemplates() {
    writeLine("========================================================== Used Templates");
    writeLine("#Calls:   Template Name");
    for (Entry<String, Integer> entry : templateCount.entrySet()) {
      String countString = entry.getValue() + "x";
      writeLine(countString + getIndentAfterCount(countString) + entry.getKey());
    }
  }
  
  protected void writeUsedUSTemplates() {
    writeLine("========================================================== Used USTemplates");
    writeLine("#Calls:   Template Name");
    for (Entry<String, Integer> entry : hwTemplateCount.entrySet()) {
      String countString = entry.getValue() + "x";
      writeLine(countString + getIndentAfterCount(countString) + entry.getKey());
    }
  }
  
  protected void writeUnusedUSTemplates() {
    writeLine("========================================================== Unused USTemplates");
    SortedSet<String> unusedHWTemplateNames = new TreeSet<String>(
        repository.getAllHWTemplateNames());
    unusedHWTemplateNames.removeAll(realHWTemplateNames);
    for (String t : unusedHWTemplateNames) {
      writeLine(t);
    }
  }
  
  protected void writeFooter() {
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
  
  protected String getIndentAfterCount(String countString) {
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
    String templateNameSanitized = templatename.replaceAll("\\.", "/").concat(".")
            .concat(ReportingConstants.TEMPLATE_FILE_EXTENSION);
    if (hwTemplates.contains(templateNameSanitized)) {
      realHWTemplateNames.add(templateNameSanitized);
      templatename = ReportingHelper.getTemplateName(templatename);
      hwTemplateCount.merge(templatename, 1, Integer::sum);
    }
    else {
      realTemplateNames.add(templateNameSanitized);
      templatename = ReportingHelper.getTemplateName(templatename);
      templateCount.merge(templatename, 1, Integer::sum);
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
