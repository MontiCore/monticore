/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.monticore.ast.ASTNode;

import com.google.common.collect.Sets;

import de.monticore.generating.templateengine.CodeHookPoint;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.ObjectCountVisitor;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;

/**
 */
public class SummaryReporter extends AReporter {
  
  final static String NUM_TEMPLATE_INCLUDE = "template includes";
  
  final static String NUM_TEMPLATE_WRITE = "template writes";
  
  final static String NUM_GENERATED_FILES = "generated files";
  
  final static String NUM_INSTANTIATIONS = "instantiations";
  
  final static String NUM_VARIABLES = "variables";
  
  final static String NUM_VARIABLE_ASSIGNMENTS = "variable assignments";
  
  final static String NUM_WARNINGS = "warnings";
  
  final static String NUM_ERRORS = "errors";
  
  final static String MAX_TEMPLATE_DEPTH = "max template depth";
  
  final static String MAX_AST_DEPTH = "max AST depth";
  
  final static String NUM_USED_TEMPLATES = "used standard templates";
  
  final static String NUM_UNUSED_TEMPLATES = "unused standard templates";
  
  final static String NUM_USED_HWTEMPLATES = "used handwritten templates";
  
  final static String NUM_UNUSED_HWTEMPLATES = "unused handwritten templates";
  
  final static String NUM_ASTNODE_VISITS = "AST node visits";
  
  final static String NUM_ASTNODE_TYPES = "AST node types";
  
  final static String NUM_ASTNODE_INSTANCES = "AST node instances";
  
  final static String NUM_AST_SPECIFIC_REPLACEMENTS = "AST hook point sets";
  
  final static String NUM_AST_SPECIFIC_CALLS = "AST hook point calls";
  
  final static String NUM_SET_CODE_HOOKPOINTS = "code hook point sets";
  
  final static String NUM_CALL_CODE_HOOKPOINTS = "code hook point calls";
  
  final static String NUM_SET_TEMPLATE_HOOKPOINTS = "template hook point sets";
  
  final static String NUM_CALL_TEMPLATE_HOOKPOINTS = "template hook point calls";
  
  final static String NUM_SET_STRING_HOOKPOINTS = "string hook point sets";
  
  final static String NUM_CALL_STRING_HOOKPOINTS = "string hook point calls";
  
  final static String NUM_CALLS_EMPTY_HOOKPOINTS = "empty hook point calls";
  
  final static String NUM_CALLED_EMPTY_HOOKPOINTS = "empty hook point called";
  
  final static String SIMPLE_FILE_NAME = "01_Summary";
  
  private int numTemplateIncludes;
  
  private int numTemplateWrites;
  
  private int numGeneratedFiles;
  
  private int numInstantiations;
  
  private int numVariableAssignments;
  
  private int numWarnings;
  
  private int numErrors;
  
  private int templateDepth;
  
  private int maxTemplateDepth;
  
  private int numASTNodeVisits;
  
  private int numASTNodeTypes;
  
  private int numASTNodeInstances;
  
  private int numASTSpecificReplacements;
  
  private int numASTSpecificCalls;
  
  private int numSetCodeHookpoints;
  
  private int numCallCodeHookpoints;
  
  private int numSetTemplateHookpoints;
  
  private int numCallTemplateHookpoints;
  
  private int numSetStringHookpoints;
  
  private int numCallStringHookpoints;
  
  private int numCallsUnsetHookpoints;
  
  private int numCalledUnsetHookpoints;
  
  private Set<String> variableNames = new LinkedHashSet<String>();
  
  private Set<String> usedTemplates = Sets.newLinkedHashSet();
  
  private Set<String> usedHWTemplates = Sets.newLinkedHashSet();
  
  private Set<String> calledUnsetHookpoints = Sets.newLinkedHashSet();
  
  private ReportingRepository repository;
  
  public SummaryReporter(
      String outputDir,
      String modelName,
      ReportingRepository repository) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);
    resetVariables();
    this.repository = repository;
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportWarning(java.lang.String)
   */
  @Override
  public void reportWarning(String message) {
    numWarnings++;
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportError(java.lang.String)
   */
  @Override
  public void reportError(String message) {
    numErrors++;
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportFileCreation(java.lang.String,
   * java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileCreation(String templatename,
      String qualifiedfilename, String fileextension, ASTNode ast) {
    numGeneratedFiles++;
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportSetValue(java.lang.String,
   * java.lang.Object)
   */
  @Override
  public void reportSetValue(String name, Object value) {
    variableNames.add(name);
    numVariableAssignments++;
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportInstantiate(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportInstantiate(String className, List<Object> params) {
    numInstantiations++;
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportTemplateInclude(java.lang.String,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateInclude(String templateName, ASTNode ast) {
    numTemplateIncludes++;
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportTemplateWrite(java.lang.String,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateWrite(String templateName, ASTNode ast) {
    numTemplateWrites++;
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportTemplateStart(java.lang.String,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateStart(String templatename, ASTNode ast) {
    numASTNodeVisits++;
    if (repository.getAllHWTemplateNames().contains(templatename)) {
      usedHWTemplates.add(templatename);
    }
    else {
      usedTemplates.add(templatename);
    }
    templateDepth++;
    if (templateDepth > maxTemplateDepth) {
      maxTemplateDepth = templateDepth;
    }
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportTemplateEnd(java.lang.String,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateEnd(String templatename, ASTNode ast) {
    templateDepth--;
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallAfterHookPoint(java.lang.String,
   * java.util.Collection, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallAfterHookPoint(String oldTemplate, Collection<HookPoint> afterHPs,
      ASTNode ast) {
    for (HookPoint hp : afterHPs) {
      if (hp != null && hp instanceof CodeHookPoint) {
        numCallCodeHookpoints++;
      }
      else if (hp != null && hp instanceof TemplateHookPoint) {
        numCallTemplateHookpoints++;
      }
      else if (hp != null && hp instanceof StringHookPoint) {
        numCallStringHookpoints++;
      }
    }
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallBeforeHookPoint(java.lang.String,
   * java.util.Collection, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallBeforeHookPoint(String oldTemplate, Collection<HookPoint> beforeHPs,
      ASTNode ast) {
    for (HookPoint hp : beforeHPs) {
      if (hp != null && hp instanceof CodeHookPoint) {
        numCallCodeHookpoints++;
      }
      else if (hp != null && hp instanceof TemplateHookPoint) {
        numCallTemplateHookpoints++;
      }
      else if (hp != null && hp instanceof StringHookPoint) {
        numCallStringHookpoints++;
      }
    }
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallReplacementHookPoint(java.lang.String,
   * java.util.List, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallReplacementHookPoint(String oldTemplate, List<HookPoint> hps, ASTNode ast) {
    for (HookPoint hp : hps) {
      if (hp != null && hp instanceof CodeHookPoint) {
        numCallCodeHookpoints++;
      }
      else if (hp != null && hp instanceof TemplateHookPoint) {
        numCallTemplateHookpoints++;
      }
      else if (hp != null && hp instanceof StringHookPoint) {
        numCallStringHookpoints++;
      }
    }
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallSpecificReplacementHookPoint(java.lang.String,
   * java.util.List, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallSpecificReplacementHookPoint(String oldTemplate, List<HookPoint> hps,
      ASTNode ast) {
    for (HookPoint hp : hps) {
      numASTSpecificCalls++;
      if (!(hp instanceof TemplateHookPoint)) {
        numASTNodeVisits++;
      }
    }
  }
  
  protected void writeContent(ASTNode ast) {
    if (ast == null) {
      return;
    }
    
    ObjectCountVisitor ocv = new ObjectCountVisitor();
    ocv.handle(ast);
    numASTNodeInstances = ocv.getTotalCount();
    numASTNodeTypes = ocv.getObjectCountMap().size();
    numCalledUnsetHookpoints = calledUnsetHookpoints.size();
    int numUsedTemplates = usedTemplates.size();
    int numUsedHWTemplates = usedHWTemplates.size();
    int numUnusedTemplates = repository.getAllTemplateNames().size() -
        numUsedTemplates;
    int numUnusedHWTemplates = repository.getAllHWTemplateNames().size() -
        numUsedHWTemplates;
    int numVariables = variableNames.size();
    int maxASTDepth = ReportingHelper.getASTDepth(ast);
    writeSummaryLine(NUM_ERRORS, numErrors);
    writeSummaryLine(NUM_WARNINGS, numWarnings);
    writeSummaryLine(NUM_GENERATED_FILES, numGeneratedFiles);
    writeSummaryLine(NUM_INSTANTIATIONS, numInstantiations);
    writeSummaryLine(NUM_TEMPLATE_INCLUDE, numTemplateIncludes);
    writeSummaryLine(NUM_TEMPLATE_WRITE, numTemplateWrites);
    writeSummaryLine(NUM_USED_TEMPLATES, numUsedTemplates);
    writeSummaryLine(NUM_UNUSED_TEMPLATES, numUnusedTemplates);
    writeSummaryLine(NUM_USED_HWTEMPLATES, numUsedHWTemplates);
    writeSummaryLine(NUM_UNUSED_HWTEMPLATES, numUnusedHWTemplates);
    writeSummaryLine(MAX_TEMPLATE_DEPTH, maxTemplateDepth);
    writeSummaryLine(MAX_AST_DEPTH, maxASTDepth);
    writeSummaryLine(NUM_ASTNODE_INSTANCES, numASTNodeInstances);
    writeSummaryLine(NUM_ASTNODE_TYPES, numASTNodeTypes);
    writeSummaryLine(NUM_ASTNODE_VISITS, numASTNodeVisits);
    writeSummaryLine(NUM_VARIABLES, numVariables);
    writeSummaryLine(NUM_VARIABLE_ASSIGNMENTS, numVariableAssignments);
    writeSummaryLine(NUM_AST_SPECIFIC_REPLACEMENTS, numASTSpecificReplacements);
    writeSummaryLine(NUM_AST_SPECIFIC_CALLS, numASTSpecificCalls);
    writeSummaryLine(NUM_SET_CODE_HOOKPOINTS, numSetCodeHookpoints);
    writeSummaryLine(NUM_CALL_CODE_HOOKPOINTS, numCallCodeHookpoints);
    writeSummaryLine(NUM_SET_TEMPLATE_HOOKPOINTS, numSetTemplateHookpoints);
    writeSummaryLine(NUM_CALL_TEMPLATE_HOOKPOINTS, numCallTemplateHookpoints);
    writeSummaryLine(NUM_SET_STRING_HOOKPOINTS, numSetStringHookpoints);
    writeSummaryLine(NUM_CALL_STRING_HOOKPOINTS, numCallStringHookpoints);
    writeSummaryLine(NUM_CALLED_EMPTY_HOOKPOINTS, numCalledUnsetHookpoints);
    writeSummaryLine(NUM_CALLS_EMPTY_HOOKPOINTS, numCallsUnsetHookpoints);
  }
  
  private void writeSummaryLine(String string, int number) {
    writeLine(string + ":" + Layouter.getSpaceString(40 - string.length())
        + number);
  }
  
  protected void resetVariables() {
    numTemplateIncludes = 0;
    numTemplateWrites = 0;
    numGeneratedFiles = 0;
    numInstantiations = 0;
    variableNames.clear();
    numVariableAssignments = 0;
    numASTNodeInstances = 0;
    numASTNodeTypes = 0;
    numASTNodeVisits = 0;
    numErrors = 0;
    numWarnings = 0;
    templateDepth = 0;
    maxTemplateDepth = 0;
    numCallCodeHookpoints = 0;
    numSetCodeHookpoints = 0;
    numCallTemplateHookpoints = 0;
    numSetTemplateHookpoints = 0;
    numCallStringHookpoints = 0;
    numSetStringHookpoints = 0;
    numCallsUnsetHookpoints = 0;
    numASTSpecificReplacements = 0;
    usedTemplates.clear();
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeContent(ast);
    resetVariables();
    writeFooter();
    super.flush(ast);
  }
  
  private void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("Summary of all reports:");
    writeLine(" -" + NUM_ERRORS + ": " + "Number of errors during the process");
    writeLine(" -" + NUM_WARNINGS + ": " + "Number of warnings during the process");
    writeLine(" -" + NUM_GENERATED_FILES + ": " + "Number of generated files");
    writeLine(" -" + NUM_INSTANTIATIONS + ": " + "Number of instantiated objects");
    writeLine(" -" + NUM_TEMPLATE_INCLUDE + ": " + "Number of templates being included");
    writeLine(" -" + NUM_TEMPLATE_WRITE + ": " + "Number of templates being used");
    writeLine(" -" + NUM_USED_TEMPLATES + ": " + "Number of templates being used");
    writeLine(" -" + NUM_UNUSED_TEMPLATES + ": " + "Number of templates being unused");
    writeLine(" -" + NUM_USED_HWTEMPLATES + ": " + "Number of handwritten templates being used");
    writeLine(" -" + NUM_UNUSED_HWTEMPLATES + ": " + "Number of handwritten templates being unused");
    writeLine(" -" + MAX_TEMPLATE_DEPTH + ": " + "Maximal depth of the template call hierarchy");
    writeLine(" -" + MAX_AST_DEPTH + ": " + "Maximal depth of the AST");
    writeLine(" -" + NUM_ASTNODE_INSTANCES + ": " + "Number of instantiated AST nodes");
    writeLine(" -" + NUM_ASTNODE_TYPES + ": " + "Number of used AST node types");
    writeLine(" -" + NUM_ASTNODE_VISITS + ": " + "Number of AST node visits");
    writeLine(" -" + NUM_VARIABLES + ": " + "Variable names that got a value during the process");
    writeLine(" -" + NUM_VARIABLE_ASSIGNMENTS + ": " + "Number of values assigned to variables");
    writeLine(" -" + NUM_AST_SPECIFIC_REPLACEMENTS + ": " + "Number of AST specific replacements");
    writeLine(" -" + NUM_AST_SPECIFIC_CALLS + ": " + "Number of AST hook point calls");
    writeLine(" -" + NUM_SET_CODE_HOOKPOINTS + ": "
        + "Number of code hook points set including");
    writeLine("    template replacements, set before and set after template statements");
    writeLine(" -" + NUM_CALL_CODE_HOOKPOINTS + ": " + "Number of code hook point calls");
    writeLine(" -" + NUM_SET_TEMPLATE_HOOKPOINTS + ": "
        + "Number of template hook points set including");
    writeLine("    template replacements, set before and set after template statements");
    writeLine(" -" + NUM_CALL_TEMPLATE_HOOKPOINTS + ": " + "Number of template hook point calls");
    writeLine(" -" + NUM_SET_STRING_HOOKPOINTS + ": "
        + "Number of string hook points set including");
    writeLine("    template replacements, set before and set after template statements");
    writeLine(" -" + NUM_CALL_STRING_HOOKPOINTS + ": " + "Number of string hook point calls");
    writeLine(" -" + NUM_CALLED_EMPTY_HOOKPOINTS + ": " + "Number of empty hook point called");
    writeLine(" -" + NUM_CALLS_EMPTY_HOOKPOINTS + ": " + "Number of empty hook point calls");
    
    writeLine("(EOF)");
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportSetHookPoint(java.lang.String,
   * mc.codegen.HookPoint)
   */
  @Override
  public void reportSetHookPoint(String hookName, HookPoint hp) {
    if (hp != null && hp instanceof CodeHookPoint) {
      numSetCodeHookpoints++;
    }
    else if (hp != null && hp instanceof TemplateHookPoint) {
      numSetTemplateHookpoints++;
    }
    else if (hp != null && hp instanceof StringHookPoint) {
      numSetStringHookpoints++;
    }
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportCallHookPointStart(java.lang.String,
   * mc.codegen.HookPoint, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallHookPointStart(String hookName, HookPoint hp, ASTNode ast) {
    if (hp != null && hp instanceof CodeHookPoint) {
      numCallCodeHookpoints++;
    }
    else if (hp != null && hp instanceof TemplateHookPoint) {
      numCallTemplateHookpoints++;
    }
    else if (hp != null && hp instanceof StringHookPoint) {
      numCallStringHookpoints++;
    }
    calledUnsetHookpoints.add(ReportingHelper.getHookPointName(hookName));
    numCallsUnsetHookpoints++;
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportASTSpecificTemplateReplacement(java.lang.String,
   * de.monticore.ast.ASTNode, mc.codegen.HookPoint)
   */
  @Override
  public void reportASTSpecificTemplateReplacement(String oldTemplate, ASTNode node, HookPoint newHp) {
    numASTSpecificReplacements++;
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportSetBeforeTemplate(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportSetBeforeTemplate(String template, List<? extends HookPoint> beforeHps) {
    for (HookPoint hp : beforeHps) {
      if (hp != null && hp instanceof CodeHookPoint) {
        numSetCodeHookpoints++;
      }
      else if (hp != null && hp instanceof TemplateHookPoint) {
        numSetTemplateHookpoints++;
      }
      else if (hp != null && hp instanceof StringHookPoint) {
        numSetStringHookpoints++;
      }
    }
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportSetAfterTemplate(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportSetAfterTemplate(String template, List<? extends HookPoint> afterHps) {
    for (HookPoint hp : afterHps) {
      if (hp != null && hp instanceof CodeHookPoint) {
        numSetCodeHookpoints++;
      }
      else if (hp != null && hp instanceof TemplateHookPoint) {
        numSetTemplateHookpoints++;
      }
      else if (hp != null && hp instanceof StringHookPoint) {
        numSetStringHookpoints++;
      }
    }
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportTemplateReplacement(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportTemplateReplacement(String oldTemplate, List<? extends HookPoint> newHps) {
    for (HookPoint hp : newHps) {
      if (hp != null && hp instanceof CodeHookPoint) {
        numSetCodeHookpoints++;
      }
      else if (hp != null && hp instanceof TemplateHookPoint) {
        numSetTemplateHookpoints++;
      }
      else if (hp != null && hp instanceof StringHookPoint) {
        numSetStringHookpoints++;
      }
    }
  }
  
  @Override
  protected void writeHeader() {
    writeLine("========================================================== Summary");
  }
  
}
