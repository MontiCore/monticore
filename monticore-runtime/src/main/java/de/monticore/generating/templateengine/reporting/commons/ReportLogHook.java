/* (c) https://github.com/MontiCore/monticore */
package de.monticore.generating.templateengine.reporting.commons;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.artifacts.ReportingNameHelper;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.IScope;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.ILogHook;

import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReportLogHook implements ILogHook, IReportEventHandler {

  /**
   * @param reportDirectory for storing the reports
   * @param factory         for creating specific report manager configurations
   */
  public ReportLogHook(String reportDirectory,
                       ReportManager.ReportManagerFactory factory) {
    this.reportDirectory = reportDirectory;
    this.factory = factory;
  }

  @Override
  public void doPrintln(String msg) {

  }

  @Override
  public void doErrPrint(String msg) {
    this.getReportManager().reportError(msg);
    flush(null);
  }

  @Override
  public void doWarn(Finding warn) {
    this.getReportManager().reportWarning(String.format("[WARN]  %s", warn));
  }

  @Override
  public void doWarn(Finding warn, Throwable t) {
    ILogHook.super.doWarn(warn, t);
  }

  @Override
  public void doPrintStackTrace(Throwable t) {

  }

  @Override
  public void doErrPrintStackTrace(Throwable t) {

  }

  @Override
  public void doPrint(String msg) {

  }

  /**
   * Map of model names to actual report managers.
   */
  protected Map<String, ReportManager> reportManagers = new HashMap<>();

  protected Map<String, ReportManager> getReportManagers() {
    return this.reportManagers;
  }

  protected ReportManager getReportManager(String modelName) {
    if (!this.getReportManagers().containsKey(modelName)) {
      ReportManager repoMan = this.getFactory().provide(modelName);
      this.getReportManagers().put(modelName, repoMan);
    }
    return this.getReportManagers().get(modelName);
  }

  /**
   * @return the currently active/responsible report manager instance
   */
  protected ReportManager getReportManager() {
    return getReportManager(Reporting.getCurrentModel());
  }

  /**
   * For creating report managers on-demand for newly processed models.
   */
  protected ReportManager.ReportManagerFactory factory;

  protected ReportManager.ReportManagerFactory getFactory() {
    return this.factory;
  }

  /**
   * Where reports will be written to.
   */
  protected String reportDirectory;

  protected String getReportDirectory() {
    return this.reportDirectory;
  }

  @Override
  public void reportModelStart(ASTNode ast,
                               String modelName,
                               String fileName) {
    this.getReportManager().reportModelStart(ast, modelName, fileName);
  }

  @Override
  public void reportTemplateStart(String templatename, ASTNode ast) {
    this.getReportManager().reportTemplateStart(templatename, ast);
  }

  @Override
  public void reportExecuteStandardTemplate(String templatename, ASTNode ast) {
    this.getReportManager().reportExecuteStandardTemplate(templatename, ast);
  }

  @Override
  public void reportFileCreation(String templatename,
                                 String qualifiedfilename,
                                 String fileextension, ASTNode ast) {
    this.getReportManager().reportFileCreation(templatename, qualifiedfilename, fileextension, ast);
  }

  public void reportFileCreation(String templateName, Path path, ASTNode ast) {
    this.getReportManager().reportFileCreation(templateName, path, ast);
  }

  @Override
  public void reportFileCreation(Path parentPath, Path file) {
    this.getReportManager().reportFileCreation(parentPath, file);
  }

  @Override
  public void reportFileFinalization(String templatename,
                                     String qualifiedfilename,
                                     String fileextension,
                                     ASTNode ast) {
    this.getReportManager().reportFileFinalization(templatename, qualifiedfilename, fileextension, ast);
  }

  public void reportFileFinalization(String templateName,
                                     Path path,
                                     ASTNode ast) {
    String qualifiedName = ReportingNameHelper.getQualifiedName(this.getReportDirectory(), path);
    String fileExtension = ReportingNameHelper.getFileextension(path);

    this.getReportManager().reportFileFinalization(templateName, qualifiedName, fileExtension, ast);
  }

  @Override
  public void reportTemplateEnd(String templatename, ASTNode ast) {
    this.getReportManager().reportTemplateEnd(templatename, ast);
  }

  @Override
  public void reportModelEnd(String modelname, String filename) {
    this.getReportManager().reportModelEnd(modelname, filename);
  }

  @Override
  public void reportModelLoad(String qualifiedName) {
    this.getReportManager().reportModelLoad(qualifiedName);
  }

  @Override
  public void reportSetValue(String name, Object value) {
    this.getReportManager().reportSetValue(name, value);
  }

  @Override
  public void reportAddValue(String name, Object value, int size) {
    this.getReportManager().reportAddValue(name, value, size);
  }

  @Override
  public void reportInstantiate(String className, List<Object> params) {
    this.getReportManager().reportInstantiate(className, params);
  }

  @Override
  public void reportTemplateInclude(String templateName, ASTNode ast) {
    this.getReportManager().reportTemplateInclude(templateName, ast);
  }

  @Override
  public void reportTemplateWrite(String templateName, ASTNode ast) {
    this.getReportManager().reportTemplateWrite(templateName, ast);
  }

  @Override
  public void reportSetHookPoint(String hookName, HookPoint hp) {
    this.getReportManager().reportSetHookPoint(hookName, hp);
  }

  @Override
  public void reportCallHookPointStart(String hookName, HookPoint hp, ASTNode ast) {
    this.getReportManager().reportCallHookPointStart(hookName, hp, ast);
  }

  @Override
  public void reportCallHookPointEnd(String hookName) {
    this.getReportManager().reportCallHookPointEnd(hookName);
  }

  @Override
  public void reportASTSpecificTemplateReplacement(String oldTemplate, ASTNode node, HookPoint newHp) {
    this.getReportManager().reportASTSpecificTemplateReplacement(oldTemplate, node, newHp);
  }

  @Override
  public void reportCallSpecificReplacementHookPoint(String oldTemplate, List<HookPoint> hps, ASTNode ast) {
    this.getReportManager().reportCallSpecificReplacementHookPoint(oldTemplate, hps, ast);
  }

  @Override
  public void reportCallReplacementHookPoint(String oldTemplate, List<HookPoint> hps, ASTNode ast) {
    this.getReportManager().reportCallReplacementHookPoint(oldTemplate, hps, ast);
  }

  @Override
  public void reportCallBeforeHookPoint(String oldTemplate, Collection<HookPoint> beforeHPs, ASTNode ast) {
    this.getReportManager().reportCallBeforeHookPoint(oldTemplate, beforeHPs, ast);
  }

  @Override
  public void reportCallAfterHookPoint(String oldTemplate, Collection<HookPoint> afterHPs, ASTNode ast) {
    this.getReportManager().reportCallAfterHookPoint(oldTemplate, afterHPs, ast);
  }

  @Override
  public void reportTemplateReplacement(String oldTemplate, List<? extends HookPoint> newHps) {
    this.getReportManager().reportTemplateReplacement(oldTemplate, newHps);
  }

  @Override
  public void reportSetBeforeTemplate(String template, Optional<ASTNode> ast, List<? extends HookPoint> beforeHps) {
    this.getReportManager().reportSetBeforeTemplate(template, ast, beforeHps);
  }

  @Override
  public void reportSetAfterTemplate(String template, Optional<ASTNode> ast, List<? extends HookPoint> afterHps) {
    this.getReportManager().reportSetAfterTemplate(template, ast, afterHps);
  }

  @Override
  public void reportAddAfterTemplate(String template, Optional<ASTNode> ast, List<? extends HookPoint> afterHps) {
    this.getReportManager().reportAddAfterTemplate(template, ast, afterHps);
  }

  @Override
  public void reportAddBeforeTemplate(String template, Optional<ASTNode> ast, List<? extends HookPoint> beforeHps) {
    this.getReportManager().reportAddBeforeTemplate(template, ast, beforeHps);
  }

  @Override
  public void reportTransformationStart(String transformationName) {
    this.getReportManager().reportTransformationStart(transformationName);
  }

  @Override
  public void flush(ASTNode ast) {
    this.getReportManager().flush(ast);
  }

  @Override
  public void reportUseHandwrittenCodeFile(Path parentDir, Path fileName) {
    this.getReportManager().reportUseHandwrittenCodeFile(parentDir, fileName);
  }

  @Override
  public void reportHWCExistenceCheck(MCPath mcp, Path fileName, Optional<URL> exists) {
    this.getReportManager().reportHWCExistenceCheck(mcp, fileName, exists);
  }

  @Override
  public void reportUserSpecificTemplate(Path parentDir, Path fileName) {
    this.getReportManager().reportUserSpecificTemplate(parentDir, fileName);
  }

  @Override
  public void reportWarning(String message) {
    this.getReportManager().reportWarning(message);
  }

  @Override
  public void reportUserWarning(String message) {
    this.getReportManager().reportUserWarning(message);
  }

  @Override
  public void reportError(String message) {
    this.getReportManager().reportError(message);
  }

  @Override
  public void reportErrorUser(String message) {
    this.getReportManager().reportErrorUser(message);
  }

  @Override
  public void reportErrorInternal(String message) {
    this.getReportManager().reportErrorInternal(message);
  }

  @Override
  public void reportTransformationObjectChange(String transformationName, ASTNode ast, String attributeName) {
    this.getReportManager().reportTransformationObjectChange(transformationName, ast, attributeName);
  }

  @Override
  public void reportTransformationObjectCreation(String transformationName, ASTNode ast) {
    this.getReportManager().reportTransformationObjectCreation(transformationName, ast);
  }

  @Override
  public void reportTransformationObjectDeletion(String transformationName, ASTNode ast) {
    this.getReportManager().reportTransformationObjectDeletion(transformationName, ast);
  }

  @Override
  public void reportDetailed(String value) {
    this.getReportManager().reportDetailed(value);
  }

  @Override
  public void reportOpenInputFile(Optional<Path> parentPath, Path file) {
    this.getReportManager().reportOpenInputFile(parentPath, file);
  }

  @Override
  public void reportParseInputFile(Path inputFilePath, String modelName) {
    this.getReportManager().reportParseInputFile(inputFilePath, modelName);
  }

  @Override
  public void reportSymbolTableScope(IScope scope) {
    this.getReportManager().reportSymbolTableScope(scope);
  }

  @Override
  public void reportMethodCall(String className, String methodName, List<Object> params) {
    this.getReportManager().reportMethodCall(className, methodName, params);
  }

  @Override
  public void reportTransformationObjectMatch(String transformationName, ASTNode ast) {
    this.getReportManager().reportTransformationObjectMatch(transformationName, ast);
  }

  @Override
  public void reportTransformationOldValue(String transformationName, ASTNode ast) {
    this.getReportManager().reportTransformationOldValue(transformationName, ast);
  }

  @Override
  public void reportTransformationNewValue(String transformationName, ASTNode ast) {
    this.getReportManager().reportTransformationNewValue(transformationName, ast);
  }

  @Override
  public void reportTransformationOldValue(String transformationName, String value) {
    this.getReportManager().reportTransformationOldValue(transformationName, value);
  }

  @Override
  public void reportTransformationNewValue(String transformationName, String value) {
    this.getReportManager().reportTransformationNewValue(transformationName, value);
  }

  @Override
  public void reportTransformationOldValue(String transformationName, boolean value) {
    this.getReportManager().reportTransformationOldValue(transformationName, value);
  }

  @Override
  public void reportTransformationNewValue(String transformationName, boolean value) {
    this.getReportManager().reportTransformationNewValue(transformationName, value);
  }

  @Override
  public void reportFileCreation(String fileName) {
    this.getReportManager().reportFileCreation(fileName);
  }

  @Override
  public void reportOpenInputFile(String fileName) {
    this.getReportManager().reportOpenInputFile(fileName);
  }

  @Override
  public void reportFileExistenceChecking(List<Path> parentPath, Path file) {
    this.getReportManager().reportFileExistenceChecking(parentPath, file);
  }
}
