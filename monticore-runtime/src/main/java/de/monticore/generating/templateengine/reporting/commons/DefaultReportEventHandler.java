/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.symboltable.Scope;

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
  }

  @Override
  public void reportTemplateStart(String templatename, ASTNode ast) {
  }

  @Override
  public void reportExecuteStandardTemplate(String templatename, ASTNode ast) {
  }

  @Override
  public void reportFileCreation(String templatename, String qualifiedfilename,
      String fileextension, ASTNode ast) {
  }

  @Override
  public void reportFileCreation(Path parentPath, Path file) {
  }

  @Override
  public void reportFileFinalization(String templatename, String qualifiedfilename,
      String fileextension, ASTNode ast) {
  }

  @Override
  public void reportTemplateEnd(String templatename, ASTNode ast) {
  }

  @Override
  public void reportModelEnd(String modelname, String filename) {
  }

  @Override
  public void reportModelLoad(String qualifiedName) {
  }

  @Override
  public void reportSetValue(String name, Object value) {
  }

  @Override
  public void reportAddValue(String name, Object value, int size) {
  }

  @Override
  public void reportInstantiate(String className, List<Object> params) {
  }

  @Override
  public void reportTemplateInclude(String templateName, ASTNode ast) {
  }

  @Override
  public void reportTemplateWrite(String templateName, ASTNode ast) {
  }

  @Override
  public void reportSetHookPoint(String hookName, HookPoint hp) {
  }

  @Override
  public void reportCallHookPointStart(String hookName, HookPoint hp, ASTNode ast) {
  }

  @Override
  public void reportCallHookPointEnd(String hookName) {
  }

  @Override
  public void reportASTSpecificTemplateReplacement(String oldTemplate, ASTNode node,
      HookPoint newHp) {
  }

  @Override
  public void reportCallSpecificReplacementHookPoint(String oldTemplate,
      List<HookPoint> hps, ASTNode ast) {
  }

  @Override
  public void reportCallReplacementHookPoint(String oldTemplate, List<HookPoint> hps,
      ASTNode ast) {
  }

  @Override
  public void reportCallBeforeHookPoint(String oldTemplate,
      Collection<HookPoint> beforeHPs, ASTNode ast) {
  }

  @Override
  public void reportCallAfterHookPoint(String oldTemplate, Collection<HookPoint> afterHPs,
      ASTNode ast) {
  }

  @Override
  public void reportTemplateReplacement(String oldTemplate,
      List<? extends HookPoint> newHps) {
  }

  @Override
  public void reportSetBeforeTemplate(String template,
      List<? extends HookPoint> beforeHps) {
  }

  @Override
  public void reportSetAfterTemplate(String template,
      List<? extends HookPoint> afterHps) {
  }

  @Override
  public void reportTransformationStart(String transformationName) {
  }

  @Override
  public void flush(ASTNode ast) {
  }

  @Override
  public void reportUseHandwrittenCodeFile(Path parentDir, Path fileName) {
  }

  @Override
  public void reportUserSpecificTemplate(Path parentDir, Path fileName) {
  }

  @Override
  public void reportWarning(String message) {
  }

  @Override
  public void reportError(String message) {
  }

  @Override
  public void reportTransformationObjectChange(String transformationName, ASTNode ast,
      String attributeName) {
  }

  @Override
  public void reportTransformationObjectCreation(String transformationName, ASTNode ast) {
  }

  @Override
  public void reportTransformationObjectDeletion(String transformationName, ASTNode ast) {
  }

  @Override
  public void reportDetailed(String value) {
  }

  @Override
  public void reportOpenInputFile(Optional<Path> parentPath, Path file) {
  }

  @Override
  public void reportParseInputFile(Path inputFilePath, String modelName) {
  }

  @Override
  public void reportSymbolTableScope(Scope scope) {
  }

  @Override
  public void reportMethodCall(String className, String methodName, List<Object> params) {
  }

  @Override
  public void reportTransformationObjectMatch(String transformationName, ASTNode ast) {
  }

  @Override
  public void reportTransformationOldValue(String transformationName, ASTNode ast) {
  }

  @Override
  public void reportTransformationNewValue(String transformationName, ASTNode ast) {
  }

  @Override
  public void reportTransformationOldValue(String transformationName, String value) {
  }

  @Override
  public void reportTransformationNewValue(String transformationName, String value) {
  }

  @Override
  public void reportTransformationOldValue(String transformationName, boolean value) {
  }

  @Override
  public void reportTransformationNewValue(String transformationName, boolean value) {
  }

  @Override
  public void reportFileCreation(String fileName) {
  }

  @Override
  public void reportOpenInputFile(String fileName) {
  }

  @Override
  public void reportFileExistenceChecking(List<Path> parentPath, Path file) {
  }
}
