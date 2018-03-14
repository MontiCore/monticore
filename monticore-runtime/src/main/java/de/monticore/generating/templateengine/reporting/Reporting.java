/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportManager.ReportManagerFactory;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * Facade for all reporting activities. Invoking a report method causes all
 * AReporter implementing this method to execute it.
 *
 */
public class Reporting extends Slf4jLog {

  /**
   * Map of model names to actual report managers.
   */
  private Map<String, ReportManager> reportManagers = new HashMap<>();

  /**
   * Where reports will be written to.
   */
  private String outputDirectory;

  /**
   * For creating report managers on-demand for newly processed models.
   */
  private ReportManagerFactory factory;

  /**
   * Constructor for de.monticore.generating.templateengine.reporting.Reporting
   *
   * @param outputDirectory for storing the reports
   * @param factory for creating specific report manager configurations
   */
  private Reporting(String outputDirectory, ReportManagerFactory factory) {
    this.outputDirectory = outputDirectory;
    this.factory = factory;
  }

  private Map<String, ReportManager> getReportManagers() {
    return this.reportManagers;
  }

  private String getOutputDirectory() {
    return this.outputDirectory;
  }

  private ReportManagerFactory getFactory() {
    return this.factory;
  }

  private ReportManager getReportManager(String modelName) {
    if (!this.getReportManagers().containsKey(modelName)) {
      ReportManager repoMan = this.getFactory().provide(modelName);
      this.getReportManagers().put(modelName, repoMan);
    }
    return this.getReportManagers().get(modelName);
  }

  // #########################
  // some log overriding magic

  /**
   * @see de.se_rwth.commons.logging.ILog#warn(java.lang.String)
   */
  @Override
  public void doWarn(String msg) {
    reportWarning(msg);
    super.doWarn(msg);
  }

  /**
   * @see de.se_rwth.commons.logging.ILog#warn(java.lang.String,
   * java.lang.Throwable)
   */
  @Override
  public void doWarn(String msg, Throwable t) {
    reportWarning(msg);
    super.doWarn(msg, t);
  }

  /**
   * @see de.se_rwth.commons.logging.ILog#error(java.lang.String)
   */
  @Override
  public void doError(String msg) {
    this.doError(msg, Optional.empty());
  }

  /**
   * @see de.se_rwth.commons.logging.ILog#error(java.lang.String,
   * java.lang.Throwable)
   */
  @Override
  public void doError(String msg, Throwable t) {
    this.doError(msg, Optional.ofNullable(t));
  }

  private void doError(String msg, Optional<Throwable> t) {
    // we need to know whether we wanted to fail immediately
    boolean wantsToFailQuick = Log.isFailQuickEnabled();
    if (wantsToFailQuick) {
      // if so, then temporary deactivate fail quick to allow proper logging
      Log.enableFailQuick(false);
    }
    // report the error
    reportError(msg);
    // and log the error
    if (t.isPresent()) {
      super.doError(msg, t.get());
    }
    else {
      super.doError(msg);
    }

    // now if we wanted to fail quick, we need to flush the reports without
    // causing a crash, i.e., we need to catch exceptions
    if (wantsToFailQuick) {
      try {
        flush(null);
      }
      catch (Exception e) {
        // this is rather generic but it'll probably do for now
        super.doError("0xA4055 Error during error reporting. Enable debug for more details.");
        super.doDebug("Error during error reporting", e, ReportManager.class.getName());
      }
    }

    // eventually, if we wanted to fail quick we do it now
    if (wantsToFailQuick) {
      Log.enableFailQuick(true);
    }
  }

  // end of the log overriding magic
  // #########################

  /* the singleton */
  private static Reporting singleton;

  /* whether reporting is enabled at the moment */
  private static boolean enabled = false;

  /* the currently active model for which reporting takes place */
  private static String currentModel;

  /**
   * @return the single reporting instance
   */
  private static Reporting get() {
    return singleton;
  }

  public static void init(String outputDirectory, ReportManagerFactory factory) {
    if (outputDirectory == null || outputDirectory.isEmpty()) {
      Log.error("0xA4050 Output directory must not be null or empty.");
    }
    if (factory == null) {
      Log.error("0xA4051 Report manager factory must not be null.");
    }
    singleton = new Reporting(outputDirectory, factory);
    Log.setLog(singleton);
  }

  /**
   * @return whether reporting was properly initialized
   * @see Reporting#init(String, ReportManagerFactory)
   */
  public static boolean isInitialized() {
    return get() != null;
  }

  /**
   * @return whether reporting is currently enabled
   */
  public static boolean isEnabled() {
    if (isInitialized()) {
      return enabled;
    }
    // if it's not initialized it's also not enabled
    return false;
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
      Log.error("0xA4052 Must specify valid model name for reporting.");
    }
    if (!isInitialized()) {
      Log.warn("0xA4053 You must initialize reporting before enabling it.");
      return false;
    }

    currentModel = modelName;
    enabled = true;
    return enabled;
  }

  /**
   * Disable reporting entirely.
   *
   * @return the currently active model name for which reporting was active
   */
  public static String off() {
    if (isInitialized()) {
      enabled = false;
      if (currentModel != null) {
        return currentModel;
      }
    }
    return "";
  }

  /**
   * @return the currently active/responsible report manager instance
   */
  private static ReportManager getReportManager() {
    // must only be used internally and with preceeding checks fo initialization
    return get().getReportManager(currentModel);
  }

  public static void reportTransformationStart(String transformationName) {
    if (isEnabled()) {
      getReportManager().reportTransformationStart(transformationName);
    }
  }

  public static void reportTransformationObjectMatch(String transformationName, ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportTransformationObjectMatch(transformationName, ast);
    }
  }

  public static void reportTransformationOldValue(String transformationName, ASTNode ast){
    if (isEnabled()) {
      getReportManager().reportTransformationOldValue(transformationName, ast);
    }
  }

  public static void reportTransformationNewValue(String transformationName, ASTNode ast){
    if (isEnabled()) {
      getReportManager().reportTransformationNewValue(transformationName, ast);
    }
  }
  public static void reportTransformationOldValue(String transformationName, String value){
    if (isEnabled()) {
      getReportManager().reportTransformationOldValue(transformationName, value);
    }
  }

  public static void reportTransformationNewValue(String transformationName, String value){
    if (isEnabled()) {
      getReportManager().reportTransformationNewValue(transformationName, value);
    }
  }

  public static void reportTransformationOldValue(String transformationName, boolean value){
    if (isEnabled()) {
      getReportManager().reportTransformationOldValue(transformationName, value);
    }
  }

  public static void reportTransformationNewValue(String transformationName, boolean value){
    if (isEnabled()) {
      getReportManager().reportTransformationNewValue(transformationName, value);
    }
  }

  public static void reportTransformationObjectChange(String transformationName, ASTNode ast, String attributeName) {
    if (isEnabled()) {
      getReportManager().reportTransformationObjectChange(transformationName, ast, attributeName);
    }
  }

  public static void reportTransformationObjectCreation(String transformationName, ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportTransformationObjectCreation(transformationName, ast);
    }
  }

  public static void reportTransformationObjectDeletion(String transformationName, ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportTransformationObjectDeletion(transformationName, ast);
    }
  }

  public static void reportModelStart(ASTNode ast, String modelName, String fileName) {
    if (isEnabled()) {
      getReportManager().reportModelStart(ast, modelName, fileName);
    }
  }



  /**
   * Reports the execution of templates
   *
   * @param templateName
   * @param ast
   */
  /* handwritten templates, and templates within Template Hookpoints and AST
   * specific Template Hookpoints */
  public static void reportTemplateStart(String templateName, ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportTemplateStart(templateName, ast);
    }
  }

  /**
   * Reports the execution of a standard template that is wrapped into a
   * template hook point via the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#getTemplateForwardings(String , ASTNode)
   * getTemplateForwardings} method. The template is wrapped into the template
   * hook point only if there is no other template forwarding.
   *
   * @param templateName
   * @param ast
   */
  public static void reportExecuteStandardTemplate(String templateName, ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportExecuteStandardTemplate(templateName, ast);
    }
  }

  /**
   * Reports a template based file creation via the
   * {@link de.monticore.generating.templateengine.TemplateController#writeArgs(String, String, String, ASTNode, List)
   * writeArgs} method of the
   * {@link de.monticore.generating.templateengine.TemplateController
   * TemplateController}.
   *
   * @param templateName
   * @param path
   * @param ast
   */
  public static void reportFileCreation(String templateName, Path path, ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportFileCreation(templateName, path, ast);
    }
  }
  
  /**
   * Reports a file creation
   *
   * @param parentPath
   * @param file
   */
  public static void reportFileCreation(Path parentPath, Path file) {
    if (isEnabled()) {
      getReportManager().reportFileCreation(parentPath, file);
    }
  }
  
  /**
   * Reports a file creation
   *
   * @param fileName
   */
  public static void reportFileCreation(String fileName) {
    if (isEnabled()) {
      getReportManager().reportFileCreation(fileName);
    }
  }

  /**
   * Reports the end of a file creation (file finalization).
   *
   * @param templateName
   * @param path
   * @param ast
   */
  public static void reportFileFinalization(String templateName, Path path, ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportFileFinalization(templateName, path, ast);
    }
  }

  /**
   * Reports a template based file creation.
   *
   * @param templateName
   * @param qualifiedFilename
   * @param fileExtension
   * @param ast
   */
  public static void reportFileCreation(String templateName, String qualifiedFilename,
      String fileExtension, ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportFileCreation(templateName, qualifiedFilename, fileExtension, ast);
    }
  }

  /**
   * Reports the end of a file creation (file finalization).
   *
   * @param templateName
   * @param qualifiedFilename
   * @param fileExtension
   * @param ast
   */
  public static void reportFileFinalization(String templateName, String qualifiedFilename,
      String fileExtension, ASTNode ast) {
    if (isEnabled()) {
      getReportManager()
          .reportFileFinalization(templateName, qualifiedFilename, fileExtension, ast);
    }
  }
  
  /**
   * Reports a checking of file existence 
   *
   * @param parentPath
   * @param file
   */
  public static void reportFileExistenceChecking(List<Path> parentPath, Path file) {
    if (isEnabled()) {
      getReportManager()
          .reportFileExistenceChecking(parentPath, file);
    }
  }
  
  /**
   * Reports the end of a template execution.
   *
   * @param templateName
   * @param ast
   */
  public static void reportTemplateEnd(String templateName, ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportTemplateEnd(templateName, ast);
    }
  }

  public static void reportModelEnd(String modelName, String fileName) {
    if (isEnabled()) {
      getReportManager().reportModelEnd(modelName, fileName);
    }
  }

  public static void reportModelLoad(String qualifiedName) {
    if (isEnabled()) {
      getReportManager().reportModelLoad(qualifiedName);
    }
  }

  public static void reportSetValue(String name, Object value) {
    if (isEnabled()) {
      getReportManager().reportSetValue(name, value);
    }
  }

  public static void reportInstantiate(String className, List<Object> params) {
    if (isEnabled()) {
      getReportManager().reportInstantiate(className, params);
    }
  }

  public static void reportMethodCall(String className, String methodName, List<Object> params) {
    if (isEnabled()) {
      getReportManager().reportMethodCall(className, methodName, params);
    }
  }

  /**
   * Reports the template inclusion via the
   * {@link de.monticore.generating.templateengine.TemplateController#logTemplateCallOrInclude(String, ASTNode)
   * logTemplateCallOrInclude} method called by the
   * {@link de.monticore.generating.templateengine.TemplateController#processTemplate(String, ASTNode, List)
   * processTemplate} method of the
   * {@link de.monticore.generating.templateengine.TemplateController
   * TemplateController} after calculating all forwardings.
   *
   * @param templateName
   * @param ast
   */
  public static void reportTemplateInclude(String templateName, ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportTemplateInclude(templateName, ast);
    }
  }

  /**
   * Reports the template write via the
   * {@link de.monticore.generating.templateengine.TemplateController#logTemplateCallOrInclude(String, ASTNode)
   * logTemplateCallOrInclude} method called by the
   * {@link de.monticore.generating.templateengine.TemplateController#processTemplate(String, ASTNode, List)
   * processTemplate} method of the
   * {@link de.monticore.generating.templateengine.TemplateController
   * TemplateController}. TemplateWrite does not calculate forwardings, it
   * processes the template instantly.
   *
   * @param templateName
   * @param ast
   */
  public static void reportTemplateWrite(String templateName, ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportTemplateWrite(templateName, ast);
    }
  }

  /**
   * Reports the registration of a hook point via the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#setHookPoint(String, HookPoint)
   * setHookPoint} method of the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement
   * GlobalExtensionManagement}.
   *
   * @param hookName
   * @param hp
   */
  public static void reportSetHookPoint(String hookName, HookPoint hp) {
    if (isEnabled()) {
      getReportManager().reportSetHookPoint(hookName, hp);
    }
  }

  /**
   * Reports the execution of a hook point via the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#callHookPoint(TemplateController, String, ASTNode)
   * callHookPoint} method. This does not include the execution of hook points
   * registered by the setBefore, setAfter or replaceTemplate Methods, nor the
   * execution of AST specific hook points.
   *
   * @param oldTemplate
   * @param beforeHps
   * @param ast
   */
  public static void reportCallHookPointStart(String hookName, HookPoint hp, ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportCallHookPointStart(hookName, hp, ast);
    }
  }

  /**
   * Reports the end of the execution of a hook point via the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#callHookPoint(TemplateController, String, ASTNode)
   * callHookPoint} method.
   *
   * @param hookName
   */
  public static void reportCallHookPointEnd(String hookName) {
    if (isEnabled()) {
      getReportManager().reportCallHookPointEnd(hookName);
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
   * registered by setAfter. This method is called in the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#getTemplateForwardings(String , ASTNode)
   * getTemplateForwardings} method triggered by the
   * {@link de.monticore.generating.templateengine.TemplateController#processTemplate(String, ASTNode, List)
   * processTemplate} method.
   *
   * @param oldTemplate
   * @param afterHps
   * @param ast
   */
  public static void reportCallAfterHookPoint(String oldTemplate, Collection<HookPoint> afterHps,
      ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportCallAfterHookPoint(oldTemplate, afterHps, ast);
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
   * registered by setBefore. This method is called in the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#getTemplateForwardings(String , ASTNode)
   * getTemplateForwardings} method triggered by the
   * {@link de.monticore.generating.templateengine.TemplateController#processTemplate(String, ASTNode, List)
   * processTemplate} method.
   *
   * @param oldTemplate
   * @param beforeHps
   * @param ast
   */
  public static void reportCallBeforeHookPoint(String oldTemplate, Collection<HookPoint> beforeHps,
      ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportCallBeforeHookPoint(oldTemplate, beforeHps, ast);
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
   * registered by setReplace. These hook points replace a template. This method
   * is called in the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#getTemplateForwardingsX(String , ASTNode)
   * getTemplateForwardingsX} method triggered by the
   * {@link de.monticore.generating.templateengine.TemplateController#processTemplate(String, ASTNode, List)
   * processTemplate} method.
   *
   * @param oldTemplate
   * @param hps
   * @param ast
   */
  public static void reportCallReplacementHookPoint(String oldTemplate, List<HookPoint> hps,
      ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportCallReplacementHookPoint(oldTemplate, hps, ast);
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
   * registered by setASTSpecificReplacement. This method is called in the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#getTemplateForwardings(String , ASTNode)
   * getTemplateForwardings} method triggered by the
   * {@link de.monticore.generating.templateengine.TemplateController#processTemplate(String, ASTNode, List)
   * processTemplate} method.
   *
   * @param oldTemplate
   * @param hps
   * @param ast
   */
  public static void reportCallSpecificReplacementHookPoint(String oldTemplate,
      List<HookPoint> hps, ASTNode ast) {
    if (isEnabled()) {
      getReportManager().reportCallSpecificReplacementHookPoint(oldTemplate, hps, ast);
    }
  }

  /**
   * Reports the replacement of a template by an AST hook point via the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#replaceTemplate(String , ASTNode , HookPoint )
   * replaceTemplate} method of the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement
   * GlobalExtensionManagement}. This does not include any other assignment or
   * replacement.
   *
   * @param oldTemplate
   * @param node
   * @param newHp
   */
  public static void reportASTSpecificTemplateReplacement(String oldTemplate, ASTNode node,
      HookPoint newHp) {
    if (isEnabled()) {
      getReportManager().reportASTSpecificTemplateReplacement(oldTemplate, node, newHp);
    }
  }

  /**
   * Reports the replacement of a template by hook points via the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#replaceTemplate(String , List )
   * replaceTemplate} method of the
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement
   * GlobalExtensionManagement}. This does not include any other assignment or
   * replacement.
   *
   * @param oldTemplate
   * @param newHps
   */
  public static void reportTemplateReplacement(String oldTemplate, List<? extends HookPoint> newHps) {
    if (isEnabled()) {
      getReportManager().reportTemplateReplacement(oldTemplate, newHps);
    }
  }

  /**
   * Reports the assignment of hook points to a template via
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#setBeforeTemplate(String , List )}
   * . This does not include any other assignment or replacement.
   *
   * @param template
   * @param beforeHps
   */
  public static void reportSetBeforeTemplate(String template, List<? extends HookPoint> beforeHps) {
    if (isEnabled()) {
      getReportManager().reportSetBeforeTemplate(template, beforeHps);
    }
  }

  /**
   * Reports the assignment of hook points to a template via
   * {@link de.monticore.generating.templateengine.GlobalExtensionManagement#setAfterTemplate(String , List )}
   * . This does not include any other assignment or replacement.
   *
   * @param template
   * @param afterHps
   */
  public static void reportSetAfterTemplate(String template, List<? extends HookPoint> afterHps) {
    if (isEnabled()) {
      getReportManager().reportSetAfterTemplate(template, afterHps);
    }
  }

  public static void reportUseHandwrittenCodeFile(Path parentDir, Path fileName) {
    if (isEnabled()) {
      getReportManager().reportUseHandwrittenCodeFile(parentDir, fileName);
    }
  }

  public static void reportUserSpecificTemplate(Path parentDir, Path fileName) {
    if (isEnabled()) {
      getReportManager().reportUserSpecificTemplate(parentDir, fileName);
    }    
  }
  
  public static void reportAddValue(String name, Object value, int size) {
    if (isEnabled()) {
      getReportManager().reportAddValue(name, value, size);
    }
  }

  /**
   * Invoking this method causes a report of value to DetailedReporter.
   *
   * @param line that will be reported in DetailedReporter
   */
  public static void reportToDetailed(String value) {
    if (isEnabled()) {
      getReportManager().reportDetailed(value);
    }
  }

  /**
   * This method is called when an input file is opened which is obtained via
   * model resolution. Such files typically are dependency models (e.g., super
   * grammars, super CDs, ...).
   *
   * @param parentPath
   * @param file
   */
  public static void reportOpenInputFile(Optional<Path> parentPath, Path file) {
    if (isEnabled()) {
      getReportManager().reportOpenInputFile(parentPath, file);
    }
  }
  
  /**
   * This method is called when an input file is opened which is obtained via
   * model resolution. Such files typically are dependency models (e.g., super
   * grammars, super CDs, ...).
   *
   * @param parentPath
   * @param file
   */
  public static void reportOpenInputFile(String fileName) {
    if (isEnabled()) {
      getReportManager().reportOpenInputFile(fileName);
    }
  }

  /**
   * This method is called when an input file is parsed; i.e., this report hook
   * point is designed for the main input artifacts only. E.g., files that are
   * loaded on demand during further processing should not report using this
   * method but {@link #reportOpenInputFile(Optional<Path>, Path)} instead.
   *
   * @param inputFilePath
   * @param modelName
   */
  public static void reportParseInputFile(Path inputFilePath, String modelName) {
    if (isEnabled()) {
      getReportManager().reportParseInputFile(inputFilePath, modelName);
    }
  }

  /**
   * This method is called to report the content of the symbol table. The method
   * should only be called once during the execution of the generator.
   *
   * @param scope
   */
  public static void reportSymbolTableScope(Scope scope) {
    if (isEnabled()) {
      getReportManager().reportSymbolTableScope(scope);
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
      getReportManager().flush(ast);
    }
  }

  public static void reportWarning(String message) {
    if (isEnabled()) {
      getReportManager().reportWarning(message);
    }
  }

  public static void reportError(String msg) {
    if (isEnabled()) {
      getReportManager().reportError(msg);
    }
  }

  public static String getOutputDir() {
    if (isInitialized()) {
      return get().getOutputDirectory();
    }
    return "";
  }

}
