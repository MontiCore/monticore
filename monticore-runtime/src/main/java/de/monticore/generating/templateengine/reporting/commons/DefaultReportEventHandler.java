/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.IScope;

import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This class is the default implementation of the {@link IReportEventHandler}
 * interface. This class can be used instead of implementing the interface
 * directly if not all methods are overwritten.
 */
public class DefaultReportEventHandler implements IReportEventHandler {

  @Override
  public void reportModelStart(ASTNode ast, String modelName, String fileName) {
    // default
  }

  @Override
  public void reportTemplateStart(String templatename, ASTNode ast) {
    // default
  }

  @Override
  public void reportExecuteStandardTemplate(String templatename, ASTNode ast) {
    // default
  }

  @Override
  public void reportFileCreation(String templatename, String qualifiedfilename,
      String fileextension, ASTNode ast) {
    // default
  }

  @Override
  public void reportFileCreation(Path parentPath, Path file) {
    // default
  }

  @Override
  public void reportFileFinalization(String templatename, String qualifiedfilename,
      String fileextension, ASTNode ast) {
    // default
  }

  @Override
  public void reportTemplateEnd(String templatename, ASTNode ast) {
    // default
  }

  @Override
  public void reportModelEnd(String modelname, String filename) {
    // default
  }

  @Override
  public void reportModelLoad(String qualifiedName) {
    // default
  }

  @Override
  public void reportSetValue(String name, Object value) {
    // default
  }

  @Override
  public void reportAddValue(String name, Object value, int size) {
    // default
  }

  @Override
  public void reportInstantiate(String className, List<Object> params) {
    // default
  }

  @Override
  public void reportTemplateInclude(String templateName, ASTNode ast) {
    // default
  }

  @Override
  public void reportTemplateWrite(String templateName, ASTNode ast) {
    // default
  }

  @Override
  public void reportSetHookPoint(String hookName, HookPoint hp) {
    // default
  }

  @Override
  public void reportCallHookPointStart(String hookName, HookPoint hp, ASTNode ast) {
    // default
  }

  @Override
  public void reportCallHookPointEnd(String hookName) {
    // default
  }

  @Override
  public void reportASTSpecificTemplateReplacement(String oldTemplate, ASTNode node,
      HookPoint newHp) {
    // default
  }

  @Override
  public void reportCallSpecificReplacementHookPoint(String oldTemplate,
      List<HookPoint> hps, ASTNode ast) {
    // default
  }

  @Override
  public void reportCallReplacementHookPoint(String oldTemplate, List<HookPoint> hps,
      ASTNode ast) {
    // default
  }

  @Override
  public void reportCallBeforeHookPoint(String oldTemplate,
      Collection<HookPoint> beforeHPs, ASTNode ast) {
    // default
  }

  @Override
  public void reportCallAfterHookPoint(String oldTemplate, Collection<HookPoint> afterHPs,
      ASTNode ast) {
    // default
  }

  @Override
  public void reportTemplateReplacement(String oldTemplate,
      List<? extends HookPoint> newHps) {
    // default
  }

  @Override
  public void reportSetBeforeTemplate(String template,
                                      Optional<ASTNode> ast,
                                      List<? extends HookPoint> beforeHps) {
    // default
  }

  @Override
  public void reportSetAfterTemplate(String template,
                                     Optional<ASTNode> ast,
                                     List<? extends HookPoint> afterHps) {
    // default
  }

  @Override
  public void reportAddBeforeTemplate(String template,
                                     Optional<ASTNode> ast,
                                     List<? extends HookPoint> beforeHps) {
    // default
  }

  @Override
  public void reportAddAfterTemplate(String template,
                                     Optional<ASTNode> ast,
                                     List<? extends HookPoint> afterHps) {
    // default
  }

  @Override
  public void reportTransformationStart(String transformationName) {
    // default
  }

  @Override
  public void flush(ASTNode ast) {
    // default
  }

  @Override
  public void reportUseHandwrittenCodeFile(Path parentDir, Path fileName) {
    // default
  }

  @Override
  public void reportHWCExistenceCheck(MCPath mcp, Path fileName, Optional<URL> exists){
    // default
  }

  @Override
  public void reportUserSpecificTemplate(Path parentDir, Path fileName) {
    // default
  }

  @Override
  public void reportWarning(String message) {
    // default
  }

  @Override
  public void reportUserWarning(String message) {
    // default
  }

  @Override
  public void reportError(String message) {
    // default
  }

  @Override
  public void reportErrorInternal(String message) {
    // default
  }

  @Override
  public void reportErrorUser(String message) {
    // default
  }

  @Override
  public void reportTransformationObjectChange(String transformationName, ASTNode ast,
      String attributeName) {
    // default
  }

  @Override
  public void reportTransformationObjectCreation(String transformationName, ASTNode ast) {
    // default
  }

  @Override
  public void reportTransformationObjectDeletion(String transformationName, ASTNode ast) {
    // default
  }

  @Override
  public void reportDetailed(String value) {
    // default
  }

  @Override
  public void reportOpenInputFile(Optional<Path> parentPath, Path file) {
    // default
  }

  @Override
  public void reportParseInputFile(Path inputFilePath, String modelName) {
    // default
  }

  @Override
  public void reportSymbolTableScope(IScope scope) {
    // default
  }

  @Override
  public void reportMethodCall(String className, String methodName, List<Object> params) {
    // default
  }

  @Override
  public void reportTransformationObjectMatch(String transformationName, ASTNode ast) {
    // default
  }

  @Override
  public void reportTransformationOldValue(String transformationName, ASTNode ast) {
    // default
  }

  @Override
  public void reportTransformationNewValue(String transformationName, ASTNode ast) {
    // default
  }

  @Override
  public void reportTransformationOldValue(String transformationName, String value) {
    // default
  }

  @Override
  public void reportTransformationNewValue(String transformationName, String value) {
    // default
  }

  @Override
  public void reportTransformationOldValue(String transformationName, boolean value) {
    // default
  }

  @Override
  public void reportTransformationNewValue(String transformationName, boolean value) {
    // default
  }

  @Override
  public void reportFileCreation(String fileName) {
    // default
  }

  @Override
  public void reportOpenInputFile(String fileName) {
    // default
  }

  @Override
  public void reportFileExistenceChecking(List<Path> parentPath, Path file) {
    // default
  }
}
