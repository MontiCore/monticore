/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.reporter;

import com.google.common.collect.Sets;
import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.CodeHookPoint;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.generating.templateengine.reporting.commons.*;
import de.monticore.visitor.ITraverser;

import java.io.File;
import java.util.*;

/**
 */
public class SummaryReporter extends AReporter {
  
  public static final String NUM_TEMPLATE_INCLUDE = "template includes";
  
  public static final String NUM_TEMPLATE_WRITE = "template writes";
  
  public static final String NUM_GENERATED_FILES = "generated files";
  
  public static final String NUM_INSTANTIATIONS = "instantiations";
  
  public static final String NUM_VARIABLES = "variables";
  
  public static final String NUM_VARIABLE_ASSIGNMENTS = "variable assignments";
  
  public static final String NUM_WARNINGS = "warnings";
  
  public static final String NUM_USER_WARNINGS = "user warnings";
  
  public static final String NUM_ERRORS = "errors";
  
  public static final String NUM_USER_ERRORS = "user errors";
  
  public static final String NUM_INTERNAL_ERRORS = "internal errors";
  
  public static final String MAX_TEMPLATE_DEPTH = "max template depth";
  
  public static final String MAX_AST_DEPTH = "max AST depth";
  
  public static final String NUM_USED_TEMPLATES = "used standard templates";
  
  public static final String NUM_USED_HWTEMPLATES = "used handwritten templates";
  
  public static final String NUM_UNUSED_HWTEMPLATES = "unused handwritten templates";
  
  public static final String NUM_ASTNODE_VISITS = "AST node visits";
  
  public static final String NUM_ASTNODE_TYPES = "AST node types";
  
  public static final String NUM_ASTNODE_INSTANCES = "AST node instances";
  
  public static final String NUM_AST_SPECIFIC_REPLACEMENTS = "AST hook point sets";
  
  public static final String NUM_AST_SPECIFIC_CALLS = "AST hook point calls";
  
  public static final String NUM_SET_CODE_HOOKPOINTS = "code hook point sets";
  
  public static final String NUM_CALL_CODE_HOOKPOINTS = "code hook point calls";
  
  public static final String NUM_SET_TEMPLATE_HOOKPOINTS = "template hook point sets";
  
  public static final String NUM_CALL_TEMPLATE_HOOKPOINTS = "template hook point calls";
  
  public static final String NUM_SET_STRING_HOOKPOINTS = "string hook point sets";
  
  public static final String NUM_CALL_STRING_HOOKPOINTS = "string hook point calls";
  
  public static final String NUM_CALLS_EMPTY_HOOKPOINTS = "empty hook point calls";
  
  public static final String NUM_CALLED_EMPTY_HOOKPOINTS = "empty hook point called";
  
  public static final String SIMPLE_FILE_NAME = "01_Summary";
  
  protected int numTemplateIncludes;
  
  protected int numTemplateWrites;
  
  protected int numGeneratedFiles;
  
  protected int numInstantiations;
  
  protected int numVariableAssignments;
  
  protected int numWarnings;

  protected int numUserWarnings;
  
  protected int numErrors;

  protected int numUserErrors;

  protected int numInternalErrors;
  
  protected int templateDepth;
  
  protected int maxTemplateDepth;
  
  protected int numASTNodeVisits;

  protected int numASTSpecificReplacements;
  
  protected int numASTSpecificCalls;
  
  protected int numSetCodeHookpoints;
  
  protected int numCallCodeHookpoints;
  
  protected int numSetTemplateHookpoints;
  
  protected int numCallTemplateHookpoints;
  
  protected int numSetStringHookpoints;
  
  protected int numCallStringHookpoints;
  
  protected int numCallsUnsetHookpoints;

  protected Set<String> variableNames = new LinkedHashSet<String>();
  
  protected Set<String> usedTemplates = Sets.newLinkedHashSet();
  
  protected Set<String> usedHWTemplates = Sets.newLinkedHashSet();
  
  protected Set<String> calledUnsetHookpoints = Sets.newLinkedHashSet();
  
  protected ReportingRepository repository;

  protected ITraverser traverser;

  protected ObjectCountVisitor ocv;
  
  public SummaryReporter(
      String outputDir,
      String modelName,
      ReportingRepository repository,
      ITraverser traverser) {
    super(outputDir
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);
    resetVariables();
    this.repository = repository;
    this.traverser = traverser;
    this.ocv = new ObjectCountVisitor();
    traverser.add4IVisitor(ocv);

  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportWarning(java.lang.String)
   */
  @Override
  public void reportWarning(String message) {
    numWarnings++;
  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportUserWarning(java.lang.String)
   */
  @Override
  public void reportUserWarning(String message) {
    numUserWarnings++;
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportError(java.lang.String)
   */
  @Override
  public void reportError(String message) {
    numErrors++;
  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportErrorUser(java.lang.String)
   */
  @Override
  public void reportErrorUser(String message) {
    numUserErrors++;
  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportErrorInternal(java.lang.String)
   */
  @Override
  public void reportErrorInternal(String message) {
    numInternalErrors++;
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportFileCreation(java.lang.String,
   * java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileCreation(String templatename,
      String qualifiedfilename, String fileextension, ASTNode ast) {
    numGeneratedFiles++;
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportSetValue(java.lang.String,
   * java.lang.Object)
   */
  @Override
  public void reportSetValue(String name, Object value) {
    variableNames.add(name);
    numVariableAssignments++;
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportInstantiate(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportInstantiate(String className, List<Object> params) {
    numInstantiations++;
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportTemplateInclude(java.lang.String,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateInclude(String templateName, ASTNode ast) {
    numTemplateIncludes++;
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportTemplateWrite(java.lang.String,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateWrite(String templateName, ASTNode ast) {
    numTemplateWrites++;
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportTemplateStart(java.lang.String,
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
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportTemplateEnd(java.lang.String,
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
    
    ocv.clear();
    ast.accept(traverser);
    int numASTNodeInstances = ocv.getTotalCount();
    int numASTNodeTypes = ocv.getObjectCountMap().size();
    int maxASTDepth = ocv.getMaxDepth();
    int numCalledUnsetHookpoints = calledUnsetHookpoints.size();
    int numUsedTemplates = usedTemplates.size();
    int numUsedHWTemplates = usedHWTemplates.size();
    int numUnusedHWTemplates = repository.getAllHWTemplateNames().size() -
        numUsedHWTemplates;
    int numVariables = variableNames.size();
    writeSummaryLine(NUM_ERRORS, numErrors);
    writeSummaryLine(NUM_USER_ERRORS, numUserErrors);
    writeSummaryLine(NUM_INTERNAL_ERRORS, numInternalErrors);
    writeSummaryLine(NUM_WARNINGS, numWarnings);
    writeSummaryLine(NUM_USER_WARNINGS, numUserWarnings);
    writeSummaryLine(NUM_GENERATED_FILES, numGeneratedFiles);
    writeSummaryLine(NUM_INSTANTIATIONS, numInstantiations);
    writeSummaryLine(NUM_TEMPLATE_INCLUDE, numTemplateIncludes);
    writeSummaryLine(NUM_TEMPLATE_WRITE, numTemplateWrites);
    writeSummaryLine(NUM_USED_TEMPLATES, numUsedTemplates);
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
  
  protected void writeSummaryLine(String string, int number) {
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
    numASTNodeVisits = 0;
    numErrors = 0;
    numUserErrors = 0;
    numInternalErrors = 0;
    numWarnings = 0;
    numUserWarnings = 0;
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
  
  protected void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("Summary of all reports:");
    writeLine(" -" + NUM_ERRORS + ": " + "Number of errors during the process");
    writeLine(" -" + NUM_USER_ERRORS + ": " + "Number of user errors during the process");
    writeLine(" -" + NUM_INTERNAL_ERRORS + ": " + "Number of internal errors during the process");
    writeLine(" -" + NUM_WARNINGS + ": " + "Number of warnings during the process");
    writeLine(" -" + NUM_USER_WARNINGS + ": " + "Number of user warnings during the process");
    writeLine(" -" + NUM_GENERATED_FILES + ": " + "Number of generated files");
    writeLine(" -" + NUM_INSTANTIATIONS + ": " + "Number of instantiated objects");
    writeLine(" -" + NUM_TEMPLATE_INCLUDE + ": " + "Number of templates being included");
    writeLine(" -" + NUM_TEMPLATE_WRITE + ": " + "Number of templates being used");
    writeLine(" -" + NUM_USED_TEMPLATES + ": " + "Number of templates being used");
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
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportSetHookPoint(java.lang.String,
   * de.monticore.generating.templateengine.HookPoint)
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
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportCallHookPointStart(java.lang.String,
   * de.monticore.generating.templateengine.HookPoint, de.monticore.ast.ASTNode)
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
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportASTSpecificTemplateReplacement(java.lang.String,
   * de.monticore.ast.ASTNode, de.monticore.generating.templateengine.HookPoint)
   */
  @Override
  public void reportASTSpecificTemplateReplacement(String oldTemplate, ASTNode node, HookPoint newHp) {
    numASTSpecificReplacements++;
  }
  
  /**
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportSetBeforeTemplate(java.lang.String,
   * Optional, java.util.List)
   */
  @Override
  public void reportSetBeforeTemplate(String template, Optional<ASTNode> ast, List<? extends HookPoint> beforeHps) {
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
   * @see de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler#reportSetAfterTemplate(java.lang.String,
   * Optional, java.util.List)
   */
  @Override
  public void reportSetAfterTemplate(String template, Optional<ASTNode> ast, List<? extends HookPoint> afterHps) {
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
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportTemplateReplacement(java.lang.String,
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
