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

public interface IReportEventHandler {

  void reportModelStart(ASTNode ast, String modelName, String fileName);

  void reportTemplateStart(String templatename, ASTNode ast);

  void reportExecuteStandardTemplate(String templatename, ASTNode ast);

  void reportFileCreation(String templatename, String qualifiedfilename, String fileextension,
      ASTNode ast);

  void reportFileCreation(Path parentPath, Path file);

  void reportFileFinalization(String templatename, String qualifiedfilename, String fileextension,
      ASTNode ast);

  /**
   * @param templatename
   * @param ast
   */
  void reportTemplateEnd(String templatename, ASTNode ast);

  /**
   * @param modelname
   * @param filename
   */
  void reportModelEnd(String modelname, String filename);

  /**
   * @param qualifiedName
   */
  void reportModelLoad(String qualifiedName);

  /**
   * @param name
   * @param value
   */
  void reportSetValue(String name, Object value);

  /**
   * @param name
   * @param value
   * @param size
   */
  void reportAddValue(String name, Object value, int size);

  /**
   * @param className
   * @param params
   */
  void reportInstantiate(String className, List<Object> params);

  /**
   * @param templateName
   * @param ast
   */
  void reportTemplateInclude(String templateName, ASTNode ast);

  /**
   * @param templateName
   * @param ast
   */
  void reportTemplateWrite(String templateName, ASTNode ast);

  /**
   * @param hookName
   * @param hp
   */
  void reportSetHookPoint(String hookName, HookPoint hp);

  /**
   * @param hookName
   * @param hp
   * @param ast
   */
  void reportCallHookPointStart(String hookName, HookPoint hp, ASTNode ast);

  /**
   * @param hookName
   */
  void reportCallHookPointEnd(String hookName);

  /**
   * @param oldTemplate
   * @param node
   * @param newHp
   */
  void reportASTSpecificTemplateReplacement(String oldTemplate, ASTNode node, HookPoint newHp);

  /**
   * @param oldTemplate
   * @param hps
   * @param ast
   */
  
  void reportCallSpecificReplacementHookPoint(String oldTemplate, List<HookPoint> hps, ASTNode ast);

  /**
   * @param oldTemplate
   * @param hps
   * @param ast
   */
  
  void reportCallReplacementHookPoint(String oldTemplate, List<HookPoint> hps, ASTNode ast);

  /**
   * @param oldTemplate
   * @param beforeHPs
   * @param ast
   */
  
  void reportCallBeforeHookPoint(String oldTemplate, Collection<HookPoint> beforeHPs, ASTNode ast);

  /**
   * @param oldTemplate
   * @param afterHPs
   * @param ast
   */
  void reportCallAfterHookPoint(String oldTemplate, Collection<HookPoint> afterHPs, ASTNode ast);

  /**
   * @param oldTemplate
   * @param newHps
   */
  void reportTemplateReplacement(String oldTemplate, List<? extends HookPoint> newHps);

  /**
   * @param template
   * @param beforeHps
   */
  void reportSetBeforeTemplate(String template, Optional<ASTNode> ast,
      List<? extends HookPoint> beforeHps);

  /**
   * @param template
   * @param afterHps
   */
  void reportSetAfterTemplate(String template, Optional<ASTNode> ast,
      List<? extends HookPoint> afterHps);

  /**
   * @param template
   * @param ast
   * @param afterHps
   */
  void reportAddAfterTemplate(String template, Optional<ASTNode> ast,
      List<? extends HookPoint> afterHps);

  /**
   * @param template
   * @param ast
   * @param beforeHps
   */
  void reportAddBeforeTemplate(String template, Optional<ASTNode> ast,
      List<? extends HookPoint> beforeHps);

  /**
   * @param transformationName
   */
  void reportTransformationStart(String transformationName);

  void flush(ASTNode ast);

  /**
   * @param fileName
   */
  void reportUseHandwrittenCodeFile(Path parentDir, Path fileName);

  void reportHWCExistenceCheck(MCPath mcp, Path fileName, Optional<URL> exists);

  /**
   * @param fileName
   */
  void reportUserSpecificTemplate(Path parentDir, Path fileName);

  /**
   * @param message
   */
  void reportWarning(String message);

  /**
   * @param message
   */
  void reportUserWarning(String message);

  /**
   * @param message
   */
  void reportError(String message);

  /**
   * @param message
   */
  void reportErrorUser(String message);

  /**
   * @param message
   */
  void reportErrorInternal(String message);

  /**
   * @param transformationName
   * @param attributeName
   */
  void reportTransformationObjectChange(String transformationName, ASTNode ast,
      String attributeName);

  /**
   * @param transformationName
   */
  void reportTransformationObjectCreation(String transformationName, ASTNode ast);

  /**
   * @param transformationName
   * @param ast
   */
  void reportTransformationObjectDeletion(String transformationName, ASTNode ast);

  void reportDetailed(String value);

  void reportOpenInputFile(Optional<Path> parentPath, Path file);

  void reportParseInputFile(Path inputFilePath, String modelName);

  /**
   * @param scope
   */
  void reportSymbolTableScope(IScope scope);
  /**
   * @param className
   * @param methodName
   * @param params
   */
  void reportMethodCall(String className, String methodName, List<Object> params);

  void reportTransformationObjectMatch(String transformationName, ASTNode ast);

  void reportTransformationOldValue(String transformationName, ASTNode ast);

  void reportTransformationNewValue(String transformationName, ASTNode ast);

  void reportTransformationOldValue(String transformationName, String value);

  void reportTransformationNewValue(String transformationName, String value);

  void reportTransformationOldValue(String transformationName, boolean value);

  void reportTransformationNewValue(String transformationName, boolean value);

  void reportFileCreation(String fileName);

  void reportOpenInputFile(String fileName);

  void reportFileExistenceChecking(List<Path> parentPath, Path file);
}
