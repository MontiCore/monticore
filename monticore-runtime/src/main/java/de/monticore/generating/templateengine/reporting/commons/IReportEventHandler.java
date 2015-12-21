/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.generating.templateengine.reporting.commons;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.symboltable.Scope;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public interface IReportEventHandler {

	public void reportModelStart(ASTNode ast, String modelName, String fileName);

	public void reportTemplateStart(String templatename, ASTNode ast);

	public void reportExecuteStandardTemplate(String templatename, ASTNode ast);

	public void reportFileCreation(String templatename,
			String qualifiedfilename, String fileextension, ASTNode ast);

	public void reportFileFinalization(String templatename,
			String qualifiedfilename, String fileextension, ASTNode ast);

	/**
	 * TODO: Write me!
	 * 
	 * @param templatename
	 * @param ast
	 */
	public void reportTemplateEnd(String templatename, ASTNode ast);

	/**
	 * TODO: Write me!
	 * 
	 * @param modelname
	 * @param filename
	 */
	public void reportModelEnd(String modelname, String filename);

	/**
	 * TODO: Write me!
	 * 
	 * @param qualifiedName
	 */
	public void reportModelLoad(String qualifiedName);

	/**
	 * TODO: Write me!
	 * 
	 * @param name
	 * @param value
	 */
	public void reportSetValue(String name, Object value);

	/**
	 * TODO: Write me!
	 * 
	 * @param name
	 * @param value
	 * @param size
	 */
	public void reportAddValue(String name, Object value, int size);

	/**
	 * TODO: Write me!
	 * 
	 * @param className
	 * @param params
	 */
	public void reportInstantiate(String className, List<Object> params);

	/**
	 * TODO: Write me!
	 * 
	 * @param templateName
	 * @param ast
	 */
	public void reportTemplateInclude(String templateName, ASTNode ast);

	/**
	 * TODO: Write me!
	 * 
	 * @param templateName
	 * @param ast
	 */
	public void reportTemplateWrite(String templateName, ASTNode ast);

	/**
	 * TODO: Write me!
	 * 
	 * @param hookName
	 * @param hp
	 */
	public void reportSetHookPoint(String hookName, HookPoint hp);

	/**
	 * TODO: Write me!
	 * 
	 * @param hookName
	 * @param hp
	 * @param ast
	 */
	public void reportCallHookPointStart(String hookName, HookPoint hp,
			ASTNode ast);

	/**
	 * TODO: Write me!
	 * 
	 * @param hookName
	 */
	public void reportCallHookPointEnd(String hookName);

	/**
	 * TODO: Write me!
	 * 
	 * @param oldTemplate
	 * @param node
	 * @param newHp
	 */
	public void reportASTSpecificTemplateReplacement(String oldTemplate,
			ASTNode node, HookPoint newHp);

	/**
	 * TODO: Write me!
	 * 
	 * @param hp
	 * @param ast
	 */
	public void reportCallSpecificReplacementHookPoint(String oldTemplate,
			List<HookPoint> hps, ASTNode ast);

	/**
	 * TODO: Write me!
	 * 
	 * @param hp
	 * @param ast
	 */
	public void reportCallReplacementHookPoint(String oldTemplate,
			List<HookPoint> hps, ASTNode ast);

	/**
	 * TODO: Write me!
	 * 
	 * @param hp
	 * @param ast
	 */
	public void reportCallBeforeHookPoint(String oldTemplate,
			Collection<HookPoint> beforeHPs, ASTNode ast);

	/**
	 * TODO: Write me!
	 * 
	 * @param hp
	 * @param ast
	 */
	public void reportCallAfterHookPoint(String oldTemplate,
			Collection<HookPoint> afterHPs, ASTNode ast);

	/**
	 * TODO: Write me!
	 * 
	 * @param oldTemplate
	 * @param newHps
	 */
	public void reportTemplateReplacement(String oldTemplate,
			List<? extends HookPoint> newHps);

	/**
	 * TODO: Write me!
	 * 
	 * @param template
	 * @param beforeHps
	 */
	public void reportSetBeforeTemplate(String template,
			List<? extends HookPoint> beforeHps);

	/**
	 * TODO: Write me!
	 * 
	 * @param template
	 * @param afterHps
	 */
	public void reportSetAfterTemplate(String template,
			List<? extends HookPoint> afterHps);

	/**
	 * TODO: Write me!
	 * 
	 * @param transformationName
	 */
	public void reportTransformationStart(String transformationName);

	public abstract void flush(ASTNode ast);

	/**
	 * TODO: Write me!
	 * 
	 * @param fileName
	 */
	public void reportUseHandwrittenCodeFile(Path parentDir, Path fileName);

	/**
	 * TODO: Write me!
	 * 
	 * @param message
	 */
	public void reportWarning(String message);

	/**
	 * TODO: Write me!
	 * 
	 * @param message
	 */
	public void reportError(String message);

	/**
	 * TODO: Write me!
	 * 
	 * @param transformationName
	 */
	public void reportTransformationObjectChange(String transformationName,
			ASTNode ast);

	/**
	 * TODO: Write me!
	 * 
	 * @param transformationName
	 */
	public void reportTransformationObjectCreation(String transformationName,
			ASTNode ast);

	/**
	 * TODO: Write me!
	 * 
	 * @param transformationName
	 * @param ast
	 */
	public void reportTransformationObjectDeletion(String transformationName,
			ASTNode ast);

	public void reportDetailed(String value);

	public void reportOpenInputFile(Path parentPath, Path file);
	
	void reportParseInputFile(Path inputFilePath, String modelName);

  /**
   * TODO: Write me!
   * @param scope
   */
  public void reportSymbolTableScope(Scope scope);

  /**
   * TODO: Write me!
   * @param className
   * @param methodName
   * @param params
   */
  public void reportMethodCall(String className, String methodName, List<Object> params);

}
