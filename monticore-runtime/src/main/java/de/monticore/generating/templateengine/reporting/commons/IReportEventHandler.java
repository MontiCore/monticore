/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.symboltable.Scope;

public interface IReportEventHandler {

	public void reportModelStart(ASTNode ast, String modelName, String fileName);

	public void reportTemplateStart(String templatename, ASTNode ast);

	public void reportExecuteStandardTemplate(String templatename, ASTNode ast);

	public void reportFileCreation(String templatename,
			String qualifiedfilename, String fileextension, ASTNode ast);
	
	public void reportFileCreation(Path parentPath, Path file);

	public void reportFileFinalization(String templatename,
			String qualifiedfilename, String fileextension, ASTNode ast);

	/**
	 *
	 * @param templatename
	 * @param ast
	 */
	public void reportTemplateEnd(String templatename, ASTNode ast);

	/**
	 *
	 * @param modelname
	 * @param filename
	 */
	public void reportModelEnd(String modelname, String filename);

	/**
	 *
	 * @param qualifiedName
	 */
	public void reportModelLoad(String qualifiedName);

	/**
	 *
	 * @param name
	 * @param value
	 */
	public void reportSetValue(String name, Object value);

	/**
	 *
	 * @param name
	 * @param value
	 * @param size
	 */
	public void reportAddValue(String name, Object value, int size);

	/**
	 *
	 * @param className
	 * @param params
	 */
	public void reportInstantiate(String className, List<Object> params);

	/**
	 *
	 * @param templateName
	 * @param ast
	 */
	public void reportTemplateInclude(String templateName, ASTNode ast);

	/**
	 *
	 * @param templateName
	 * @param ast
	 */
	public void reportTemplateWrite(String templateName, ASTNode ast);

	/**
	 *
	 * @param hookName
	 * @param hp
	 */
	public void reportSetHookPoint(String hookName, HookPoint hp);

	/**
	 *
	 * @param hookName
	 * @param hp
	 * @param ast
	 */
	public void reportCallHookPointStart(String hookName, HookPoint hp,
			ASTNode ast);

	/**
	 *
	 * @param hookName
	 */
	public void reportCallHookPointEnd(String hookName);

	/**
	 *
	 * @param oldTemplate
	 * @param node
	 * @param newHp
	 */
	public void reportASTSpecificTemplateReplacement(String oldTemplate,
			ASTNode node, HookPoint newHp);

	/**
	 *
	 * @param hp
	 * @param ast
	 */
	public void reportCallSpecificReplacementHookPoint(String oldTemplate,
			List<HookPoint> hps, ASTNode ast);

	/**
	 *
	 * @param hp
	 * @param ast
	 */
	public void reportCallReplacementHookPoint(String oldTemplate,
			List<HookPoint> hps, ASTNode ast);

	/**
	 *
	 * @param hp
	 * @param ast
	 */
	public void reportCallBeforeHookPoint(String oldTemplate,
			Collection<HookPoint> beforeHPs, ASTNode ast);

	/**
	 *
	 * @param hp
	 * @param ast
	 */
	public void reportCallAfterHookPoint(String oldTemplate,
			Collection<HookPoint> afterHPs, ASTNode ast);

	/**
	 *
	 * @param oldTemplate
	 * @param newHps
	 */
	public void reportTemplateReplacement(String oldTemplate,
			List<? extends HookPoint> newHps);

	/**
	 *
	 * @param template
	 * @param beforeHps
	 */
	public void reportSetBeforeTemplate(String template,
			List<? extends HookPoint> beforeHps);

	/**
	 *
	 * @param template
	 * @param afterHps
	 */
	public void reportSetAfterTemplate(String template,
			List<? extends HookPoint> afterHps);

	/**
	 *
	 * @param transformationName
	 */
	public void reportTransformationStart(String transformationName);

	public abstract void flush(ASTNode ast);

	/**
	 *
	 * @param fileName
	 */
	public void reportUseHandwrittenCodeFile(Path parentDir, Path fileName);

  /**
   *
   * @param fileName
   */
  public void reportUserSpecificTemplate(Path parentDir, Path fileName);

  /**
	 *
	 * @param message
	 */
	public void reportWarning(String message);

	/**
	 *
	 * @param message
	 */
	public void reportError(String message);

	/**
	 *
	 * @param transformationName
	 * @param attributeName
	 */
	public void reportTransformationObjectChange(String transformationName,
			ASTNode ast, String attributeName);

	/**
	 *
	 * @param transformationName
	 */
	public void reportTransformationObjectCreation(String transformationName,
			ASTNode ast);

	/**
	 *
	 * @param transformationName
	 * @param ast
	 */
	public void reportTransformationObjectDeletion(String transformationName,
			ASTNode ast);

	public void reportDetailed(String value);

	public void reportOpenInputFile(Optional<Path> parentPath, Path file);

	void reportParseInputFile(Path inputFilePath, String modelName);

  /**
   * @param scope
   */
  public void reportSymbolTableScope(Scope scope);

  /**
   * @param className
   * @param methodName
   * @param params
   */
  public void reportMethodCall(String className, String methodName, List<Object> params);

	public void reportTransformationObjectMatch(String transformationName, ASTNode ast);

	public void reportTransformationOldValue(String transformationName, ASTNode ast);

	public void reportTransformationNewValue(String transformationName, ASTNode ast);

	public void reportTransformationOldValue(String transformationName, String value);

	public void reportTransformationNewValue(String transformationName, String value);

	public void reportTransformationOldValue(String transformationName, boolean value);

	public void reportTransformationNewValue(String transformationName, boolean value);

  void reportFileCreation(String fileName);

  void reportOpenInputFile(String fileName);

  void reportFileExistenceChecking(List<Path> parentPath, Path file);
}
