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
 * This class is the default implementation of the {@link IReportEventHandler}
 * interface. This class can be used instead of implementing the interface
 * directly if not all methods are overwritten.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class DefaultReportEventHandler implements IReportEventHandler {

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportTemplateStart(java.lang.String,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateStart(String templatename, ASTNode ast) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportFileCreation(java.lang.String,
   * java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileCreation(String templatename, String qualifiedfilename,
      String fileextension, ASTNode ast) {
    // TODO Auto-generated method stub

  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportFileCreation(Path,
   * Path)
   */
  @Override
  public void reportFileCreation(Path parentPath, Path file) {
    // TODO Auto-generated method stub
  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportFileFinalization(java.lang.String,
   * java.lang.String, java.lang.String, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportFileFinalization(String templatename, String qualifiedfilename,
      String fileextension, ASTNode ast) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportTemplateEnd(java.lang.String,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateEnd(String templatename, ASTNode ast) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportModelEnd(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void reportModelEnd(String modelname, String filename) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportModelLoad(java.lang.String)
   */
  @Override
  public void reportModelLoad(String qualifiedName) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportSetValue(java.lang.String,
   * java.lang.Object)
   */
  @Override
  public void reportSetValue(String name, Object value) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportInstantiate(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportInstantiate(String className, List<Object> params) {
    // TODO Auto-generated method stub

  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportFileExistenceChecking(Path,
   * Path)
   */
  @Override
  public void reportFileExistenceChecking(Path parentPath, Path file) {
    // TODO Auto-generated method stub
  }
  
  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportTemplateInclude(java.lang.String,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateInclude(String templateName, ASTNode ast) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportTemplateWrite(java.lang.String,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportTemplateWrite(String templateName, ASTNode ast) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportSetHookPoint(java.lang.String,
   * mc.codegen.HookPoint)
   */
  @Override
  public void reportSetHookPoint(String hookName, HookPoint hp) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportCallHookPointStart(java.lang.String,
   * mc.codegen.HookPoint, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallHookPointStart(String hookName, HookPoint hp, ASTNode ast) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportCallHookPointEnd(java.lang.String)
   */
  @Override
  public void reportCallHookPointEnd(String hookName) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportASTSpecificTemplateReplacement(java.lang.String,
   * de.monticore.ast.ASTNode, mc.codegen.HookPoint)
   */
  @Override
  public void reportASTSpecificTemplateReplacement(String oldTemplate, ASTNode node, HookPoint newHp) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportTemplateReplacement(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportTemplateReplacement(String oldTemplate, List<? extends HookPoint> newHps) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportSetBeforeTemplate(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportSetBeforeTemplate(String template, List<? extends HookPoint> beforeHps) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportSetAfterTemplate(java.lang.String,
   * java.util.List)
   */
  @Override
  public void reportSetAfterTemplate(String template, List<? extends HookPoint> afterHps) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#flush()
   */
  @Override
  public void flush(ASTNode ast) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportUseHandwrittenCodeFile(java.lang.String)
   */
  @Override
  public void reportUseHandwrittenCodeFile(Path parentDir, Path fileName) {
    // TODO Auto-generated method stub

  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportUserSpecificTemplate(java.nio.file.Path, java.nio.file.Path)
   */
  @Override
  public void reportUserSpecificTemplate(Path parentDir, Path fileName) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportWarning(java.lang.String)
   */
  @Override
  public void reportWarning(String message) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportError(java.lang.String)
   */
  @Override
  public void reportError(String message) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportModelStart(de.monticore.ast.ASTNode,
   * java.lang.String, java.lang.String)
   */
  @Override
  public void reportModelStart(ASTNode ast, String modelName, String fileName) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportTransformationStart(java.lang.String)
   */
  @Override
  public void reportTransformationStart(String transformationName) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportTransformationObjectChange(java.lang.String)
   */
  @Override
  public void reportTransformationObjectChange(String transformationName, ASTNode ast,
      String attributeName) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportTransformationObjectCreation(java.lang.String)
   */
  @Override
  public void reportTransformationObjectCreation(String transformationName, ASTNode ast) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportTransformationObjectDeletion(java.lang.String)
   */
  @Override
  public void reportTransformationObjectDeletion(String transformationName, ASTNode ast) {
    // TODO Auto-generated method stub

  }

  /**
   * @see mc.codegen.reporting.commons.IReportEventHandler#reportAddValue(java.lang.String,
   * java.lang.Object, int)
   */
  @Override
  public void reportAddValue(String name, Object value, int size) {
    // TODO Auto-generated method stub

  }

  @Override
  public void reportDetailed(String value) {
    // TODO Auto-generated method stub

  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportCallSpecificReplacementHookPoint(java.lang.String,
   * java.util.List, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallSpecificReplacementHookPoint(String oldTemplate, List<HookPoint> hps,
      ASTNode ast) {
    // TODO Auto-generated method stub

  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportCallReplacementHookPoint(java.lang.String,
   * java.util.List, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallReplacementHookPoint(String oldTemplate, List<HookPoint> hps, ASTNode ast) {
    // TODO Auto-generated method stub

  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportCallBeforeHookPoint(java.lang.String,
   * java.util.Collection, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallBeforeHookPoint(String oldTemplate, Collection<HookPoint> beforeHPs,
      ASTNode ast) {
    // TODO Auto-generated method stub

  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportCallAfterHookPoint(java.lang.String,
   * java.util.Collection, de.monticore.ast.ASTNode)
   */
  @Override
  public void reportCallAfterHookPoint(String oldTemplate, Collection<HookPoint> afterHPs,
      ASTNode ast) {
    // TODO Auto-generated method stub

  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportStandardTemplateStart(java.lang.String,
   * de.monticore.ast.ASTNode)
   */
  @Override
  public void reportExecuteStandardTemplate(String templatename, ASTNode ast) {
    // TODO Auto-generated method stub

  }

  @Override
  public void reportOpenInputFile(Path parentPath, Path file) {
    // TODO Auto-generated method stub

  }

  @Override
  public void reportParseInputFile(Path inputFilePath, String modelName) {
    // TODO Auto-generated method stub

  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportSymbolTable(de.monticore.symboltable.CommonScope)
   */
  @Override
  public void reportSymbolTableScope(Scope scope) {
    // TODO Auto-generated method stub

  }

  /**
   * @see de.monticore.generating.templateengine.reporting.commons.IReportEventHandler#reportMethodCall(java.lang.String, java.lang.String, java.util.List)
   */
  @Override
  public void reportMethodCall(String className, String methodName, List<Object> params) {
    // TODO Auto-generated method stub

  }

  @Override public void reportTransformationObjectMatch(String transformationName, ASTNode ast) {
    // TODO Auto-generated method stub
  }

  @Override
  public void reportTransformationOldValue(String transformationName, ASTNode ast) {
    // TODO Auto-generated method stub
  }

  @Override
  public void reportTransformationNewValue(String transformationName, ASTNode ast) {
      // TODO Auto-generated method stub
  }

  @Override
  public void reportTransformationOldValue(String transformationName, String value) {
    // TODO Auto-generated method stub
  }

  @Override
  public void reportTransformationNewValue(String transformationName, String value) {
    // TODO Auto-generated method stub
  }

  @Override
  public void reportTransformationOldValue(String transformationName, boolean value) {
    // TODO Auto-generated method stub
  }

  @Override
  public void reportTransformationNewValue(String transformationName, boolean value) {
    // TODO Auto-generated method stub
  }

}
