/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.reporting;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.reporting.commons.ReportLogHook;
import de.monticore.generating.templateengine.reporting.commons.ReportManager.ReportManagerFactory;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.IScope;
import de.se_rwth.commons.logging.Log;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Facade for all reporting activities. Invoking a report method causes all
 * AReporter implementing this method to execute it.
 */
public class Reporting extends Log {

  /* whether reporting is enabled at the moment */
  protected static boolean enabled = false;

  /* the currently active model for which reporting takes place */
  protected static String currentModel;

  public static String getCurrentModel() {
    return currentModel;
  }

  @Deprecated
  public static void init(String outputDirectory, String reportDirectory, ReportManagerFactory factory) {
    if (outputDirectory == null || outputDirectory.isEmpty()) {
      Log.error("0xA4050 Output directory must not be null or empty.");
    }
    if (reportDirectory == null || reportDirectory.isEmpty()) {
      Log.error("0xA4052 ReportDirectory directory must not be null or empty.");
    }
    if (factory == null) {
      Log.error("0xA4051 Report manager factory must not be null.");
    }
    init(reportDirectory, factory);
  }

  public static void init(String reportDirectory, ReportManagerFactory factory) {
    if (Log.getLog() == null) {
      Log.init();
    }
    addReportHook(new ReportLogHook(reportDirectory, factory));
  }

  /**
   * @return whether reporting is currently enabled
   */
  public static boolean isEnabled() {
    return enabled;
  }

  /**
   * Enable reporting for the given model name.
   *
   * @param modelName for which to enable reporting
   * @return whether reporting is enabled or not (reporting will not be enabled
   * if reporting was not previously initialized)
   */
  public static boolean on(String modelName) {
    if (modelName == null || modelName.isEmpty()) {
      Log.error("0xA4109 Must specify valid model name for reporting.");
    }

    currentModel = modelName;
    enabled = true;

    return isEnabled();
  }

  /**
   * Disable reporting entirely.
   *
   * @return the currently active model name for which reporting was active
   */
  public static String off() {
    enabled = false;
    if (currentModel != null) {
      return currentModel;
    }
    return "";
  }

  static List<ReportLogHook> reportHooks = new ArrayList<>();

  protected static List<ReportLogHook> getReportHooks() {
    return reportHooks;
  }

  public static void addReportHook(ReportLogHook reportHook) {
    reportHooks.add(reportHook);
  }

  public static void reportTransformationStart(String transformationName) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTransformationStart(transformationName);
      }
    }
  }

  public static void reportTransformationObjectMatch(String transformationName, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTransformationObjectMatch(transformationName, ast);
      }
    }
  }

  public static void reportTransformationOldValue(String transformationName, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTransformationOldValue(transformationName, ast);
      }
    }
  }

  public static void reportTransformationNewValue(String transformationName, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTransformationNewValue(transformationName, ast);
      }
    }
  }

  public static void reportTransformationOldValue(String transformationName, String value) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTransformationOldValue(transformationName, value);
      }
    }
  }

  public static void reportTransformationNewValue(String transformationName, String value) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTransformationNewValue(transformationName, value);
      }
    }
  }

  public static void reportTransformationOldValue(String transformationName, boolean value) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTransformationOldValue(transformationName, value);
      }
    }
  }

  public static void reportTransformationNewValue(String transformationName, boolean value) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTransformationNewValue(transformationName, value);
      }
    }
  }

  public static void reportTransformationObjectChange(String transformationName, ASTNode ast, String attributeName) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTransformationObjectChange(transformationName, ast, attributeName);
      }
    }
  }

  public static void reportTransformationObjectCreation(String transformationName, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTransformationObjectCreation(transformationName, ast);
      }
    }
  }

  public static void reportTransformationObjectDeletion(String transformationName, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTransformationObjectDeletion(transformationName, ast);
      }
    }
  }

  public static void reportModelStart(ASTNode ast, String modelName, String fileName) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportModelStart(ast, modelName, fileName);
      }
    }
  }

  /**
   * Reports the execution of templates
   */
  public static void reportTemplateStart(String templateName, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTemplateStart(templateName, ast);
      }
    }
  }

  /**
   * Reports the execution of a standard template that is wrapped into a
   * template hook point,
   */
  public static void reportExecuteStandardTemplate(String templateName, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportExecuteStandardTemplate(templateName, ast);
      }
    }
  }

  /**
   * Reports a template based file creation via the
   * {@link de.monticore.generating.templateengine.TemplateController#writeArgs(String, String, String, ASTNode, List)
   * writeArgs} method of the
   * {@link de.monticore.generating.templateengine.TemplateController
   * TemplateController}.
   */
  public static void reportFileCreation(String templateName, Path path, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportFileCreation(templateName, path, ast);
      }
    }
  }

  /**
   * Reports a file creation
   */
  public static void reportFileCreation(Path parentPath, Path file) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportFileCreation(parentPath, file);
      }
    }
  }

  /**
   * Reports a file creation
   */
  public static void reportFileCreation(String fileName) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportFileCreation(fileName);
      }
    }
  }

  /**
   * Reports the end of a file creation (file finalization).
   */
  public static void reportFileFinalization(String templateName, Path path, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportFileFinalization(templateName, path, ast);
      }
    }
  }

  /**
   * Reports a template based file creation.
   */
  public static void reportFileCreation(String templateName, String qualifiedFilename,
                                        String fileExtension, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportFileCreation(templateName, qualifiedFilename, fileExtension, ast);
      }
    }
  }

  /**
   * Reports the end of a file creation (file finalization).
   */
  public static void reportFileFinalization(String templateName, String qualifiedFilename,
                                            String fileExtension, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportFileFinalization(templateName, qualifiedFilename, fileExtension, ast);
      }
    }
  }

  /**
   * Reports a checking of file existence
   */
  public static void reportFileExistenceChecking(List<Path> parentPath, Path file) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportFileExistenceChecking(parentPath, file);
      }
    }
  }

  /**
   * Reports the end of a template execution.
   */
  public static void reportTemplateEnd(String templateName, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTemplateEnd(templateName, ast);
      }
    }
  }

  public static void reportModelEnd(String modelName, String fileName) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportModelEnd(modelName, fileName);
      }
    }
  }

  public static void reportModelLoad(String qualifiedName) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportModelLoad(qualifiedName);
      }
    }
  }

  public static void reportSetValue(String name, Object value) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportSetValue(name, value);
      }
    }
  }

  public static void reportInstantiate(String className, List<Object> params) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportInstantiate(className, params);
      }
    }
  }

  public static void reportMethodCall(String className, String methodName, List<Object> params) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportMethodCall(className, methodName, params);
      }
    }
  }

  /**
   * Reports template inclusion.
   */
  public static void reportTemplateInclude(String templateName, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTemplateInclude(templateName, ast);
      }
    }
  }

  /**
   * Reports the template write. TemplateWrite does not calculate forwardings, it
   * processes the template instantly.
   */
  public static void reportTemplateWrite(String templateName, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTemplateWrite(templateName, ast);
      }
    }
  }

  /**
   * Reports the registration of a hook point via the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#defineHookPoint(TemplateController, String)} (String, HookPoint)
   * setHookPoint} method of the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement
   * GlobalExtensionManagement}.
   */
  public static void reportSetHookPoint(String hookName, HookPoint hp) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportSetHookPoint(hookName, hp);
      }
    }
  }

  /**
   * Reports the execution of a hook point via the
   * callHookPoint} method. This does not include the execution of hook points
   * registered by the setBefore, setAfter or replaceTemplate Methods, nor the
   * execution of AST specific hook points.
   */
  public static void reportCallHookPointStart(String hookName, HookPoint hp, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportCallHookPointStart(hookName, hp, ast);
      }
    }
  }

  /**
   * Reports the end of the execution of a hook point via the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#bindHookPoint(String, HookPoint)} (TemplateController, String, ASTNode)
   * callHookPoint} method.
   */
  public static void reportCallHookPointEnd(String hookName) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportCallHookPointEnd(hookName);
      }
    }
  }

  /**
   * Reports the execution of hook points via the
   * {@link de.monticore.generating.templateengine.TemplateController#include(List, List)
   * include} or
   * {@link de.monticore.generating.templateengine.TemplateController#includeArgs(String, List)
   * includeArgs} method of the
   * {@link de.monticore.generating.templateengine.TemplateController
   * TemplateController}. This includes the execution of all hook points
   * registered by setAfter.
   */
  public static void reportCallAfterHookPoint(String oldTemplate, Collection<HookPoint> afterHps,
                                              ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportCallAfterHookPoint(oldTemplate, afterHps, ast);
      }
    }
  }

  /**
   * Reports the execution of hook points via the
   * {@link de.monticore.generating.templateengine.TemplateController#include(List, List)
   * include} or
   * {@link de.monticore.generating.templateengine.TemplateController#includeArgs(String, List)
   * includeArgs} method of the
   * {@link de.monticore.generating.templateengine.TemplateController
   * TemplateController}. This includes the execution of all hook points
   * registered by setBefore.
   */
  public static void reportCallBeforeHookPoint(String oldTemplate, Collection<HookPoint> beforeHps,
                                               ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportCallBeforeHookPoint(oldTemplate, beforeHps, ast);
      }
    }
  }

  /**
   * Reports the execution of hook points via the
   * {@link de.monticore.generating.templateengine.TemplateController#include(List, List)
   * include} or
   * {@link de.monticore.generating.templateengine.TemplateController#includeArgs(String, List)
   * includeArgs} method of the
   * {@link de.monticore.generating.templateengine.TemplateController
   * TemplateController}. This includes the execution of all hook points
   * registered by setReplace. These hook points replace a template.
   */
  public static void reportCallReplacementHookPoint(String oldTemplate, List<HookPoint> hps,
                                                    ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportCallReplacementHookPoint(oldTemplate, hps, ast);
      }
    }
  }

  /**
   * Reports the execution of hook points via the
   * {@link de.monticore.generating.templateengine.TemplateController#include(List, List)
   * include} or
   * {@link de.monticore.generating.templateengine.TemplateController#includeArgs(String, List)
   * includeArgs} method of the
   * {@link de.monticore.generating.templateengine.TemplateController
   * TemplateController}. This includes the execution of all hook points
   * registered by setASTSpecificReplacement.
   */
  public static void reportCallSpecificReplacementHookPoint(String oldTemplate,
                                                            List<HookPoint> hps, ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportCallSpecificReplacementHookPoint(oldTemplate, hps, ast);
      }
    }
  }

  /**
   * Reports the replacement of a template by an AST hook point via the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#replaceTemplate(String, ASTNode, HookPoint)
   * replaceTemplate} method of the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement
   * GlobalExtensionManagement}. This does not include any other assignment or
   * replacement.
   */
  public static void reportASTSpecificTemplateReplacement(String oldTemplate, ASTNode node,
                                                          HookPoint newHp) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportASTSpecificTemplateReplacement(oldTemplate, node, newHp);
      }
    }
  }

  /**
   * Reports the replacement of a template by hook points via the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#replaceTemplate(String, List)
   * replaceTemplate} method of the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement
   * GlobalExtensionManagement}. This does not include any other assignment or
   * replacement.
   */
  public static void reportTemplateReplacement(String oldTemplate, List<? extends HookPoint> newHps) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportTemplateReplacement(oldTemplate, newHps);
      }
    }
  }

  /**
   * Reports the assignment of hook points to a template via
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#setBeforeTemplate(String, List)}.
   * This does not include any other assignment or replacement.
   */
  public static void reportSetBeforeTemplate(String template, Optional<ASTNode> ast, List<? extends HookPoint> beforeHps) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportSetBeforeTemplate(template, ast, beforeHps);
      }
    }
  }

  /**
   * Reports the addition of hook points to a template via
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#setAfterTemplate(String, List)}.
   * This does not include any other assignment or replacement.
   */
  public static void reportSetAfterTemplate(String template, Optional<ASTNode> ast, List<? extends HookPoint> afterHps) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportSetAfterTemplate(template, ast, afterHps);
      }
    }
  }

  public static void reportUseHandwrittenCodeFile(Path parentDir, Path fileName) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportUseHandwrittenCodeFile(parentDir, fileName);
      }
    }
  }

  /**
   * Reports the assignment of hook points to a template via
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#setAfterTemplate(String, List)}.
   * This does not include any other assignment or replacement.
   */
  public static void reportAddAfterTemplate(String template, Optional<ASTNode> ast, List<? extends HookPoint> afterHps) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportAddAfterTemplate(template, ast, afterHps);
      }
    }
  }

  /**
   * Reports the assignment of hook points to a template via
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#setAfterTemplate(String, List)}.
   * This does not include any other assignment or replacement.
   */
  public static void reportAddBeforeTemplate(String template, Optional<ASTNode> ast, List<? extends HookPoint> afterHps) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportAddBeforeTemplate(template, ast, afterHps);
      }
    }
  }

  /**
   * Reports the check for existence of an artifact
   */
  public static void reportHWCExistenceCheck(MCPath mcp, Path fileName, Optional<URL> resolvedPath) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportHWCExistenceCheck(mcp, fileName, resolvedPath);
      }
    }
  }

  public static void reportUserSpecificTemplate(Path parentDir, Path fileName) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportUserSpecificTemplate(parentDir, fileName);
      }
    }
  }

  public static void reportAddValue(String name, Object value, int size) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportAddValue(name, value, size);
      }
    }
  }

  /**
   * Invoking this method causes a report of value to DetailedReporter.
   *
   * @param value that will be reported in DetailedReporter
   */
  public static void reportToDetailed(String value) {
    for (ReportLogHook hook : getReportHooks()) {
      hook.reportDetailed(value);
    }
  }

  /**
   * This method is called when an input file is opened which is obtained via
   * model resolution. Such files typically are dependency models (e.g., super
   * grammars, super CDs, ...).
   */
  public static void reportOpenInputFile(Optional<Path> parentPath, Path file) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportOpenInputFile(parentPath, file);
      }
    }
  }

  /**
   * This method is called when an input file is opened which is obtained via
   * model resolution. Such files typically are dependency models (e.g., super
   * grammars, super CDs, ...).
   */
  public static void reportOpenInputFile(String fileName) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportOpenInputFile(fileName);
      }
    }
  }

  /**
   * This method is called when an input file is parsed; i.e., this report hook
   * point is designed for the main input artifacts only. E.g., files that are
   * loaded on demand during further processing should not report using this
   * method but {@link #reportOpenInputFile(Optional, Path)} instead.
   */
  public static void reportParseInputFile(Path inputFilePath, String modelName) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportParseInputFile(inputFilePath, modelName);
      }
    }
  }

  /**
   * This method is called to report the content of the symbol table. The method
   * should only be called once during the execution of the generator.
   */
  public static void reportSymbolTableScope(IScope scope) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportSymbolTableScope(scope);
      }
    }
  }

  /**
   * Invoking this method causes all AReporter to close their files and all
   * OneTimeReporter to write their content into files.
   *
   * @param ast the root node of the reported ast, may be null on error
   */
  public static void flush(ASTNode ast) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.flush(ast);
      }
    }
  }

  public static void reportWarning(String msg) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportWarning(msg);
      }
    }
  }

  public static void reportError(String msg) {
    if (isEnabled()) {
      for (ReportLogHook hook : getReportHooks()) {
        hook.reportError(msg);
      }
    }
  }
}
