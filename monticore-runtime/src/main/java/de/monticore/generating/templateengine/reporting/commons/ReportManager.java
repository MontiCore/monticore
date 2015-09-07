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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.reporting.artifacts.ReportingNameHelper;
import de.monticore.symboltable.CommonScope;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class ReportManager implements IReportEventHandler {
  
  private Set<IReportEventHandler> reportEventHandlers = new HashSet<IReportEventHandler>();
  
  private String outputDir;
  
  public ReportManager(String outputDir) {
    this.outputDir = outputDir;
  }
  
  public String getOutputDir() {
    return this.outputDir;
  }
  
  public void addReportEventHandler(IReportEventHandler handler) {
    Log.errorIfNull(handler);
    this.reportEventHandlers.add(handler);
  }
  
  public void removeReportEventHandler(IReportEventHandler handler) {
    this.reportEventHandlers.remove(handler);
  }
  
  @Override
  public void reportModelStart(ASTNode ast, String modelName, String fileName) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportModelStart(ast, modelName, fileName);
    }
  }
  
  @Override
  public void reportTemplateStart(String templateName, ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportTemplateStart(templateName, ast);
    }
  }
  
  @Override
  public void reportExecuteStandardTemplate(String templateName, ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportExecuteStandardTemplate(templateName, ast);
    }
  }
  
  public void reportFileCreation(String templateName, Path path, ASTNode ast) {
    String qualifiedName = ReportingNameHelper.getQualifiedName(
        this.getOutputDir(), path);
    String fileExtension = ReportingNameHelper.getFileextension(path);
    
    this.reportFileCreation(templateName, qualifiedName, fileExtension, ast);
  }
  
  public void reportFileFinalization(String templateName, Path path,
      ASTNode ast) {
    String qualifiedName = ReportingNameHelper.getQualifiedName(
        this.getOutputDir(), path);
    String fileExtension = ReportingNameHelper.getFileextension(path);
    
    this.reportFileFinalization(templateName, qualifiedName, fileExtension,
        ast);
  }
  
  @Override
  public void reportFileCreation(String templateName,
      String qualifiedFilename, String fileExtension, ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportFileCreation(templateName, qualifiedFilename,
          fileExtension, ast);
    }
  }
  
  @Override
  public void reportFileFinalization(String templateName,
      String qualifiedFilename, String fileExtension, ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportFileFinalization(templateName, qualifiedFilename,
          fileExtension, ast);
    }
  }
  
  @Override
  public void reportTemplateEnd(String templateName, ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportTemplateEnd(templateName, ast);
    }
  }
  
  @Override
  public void reportModelEnd(String modelName, String fileName) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportModelEnd(modelName, fileName);
    }
  }
  
  @Override
  public void reportModelLoad(String qualifiedName) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportModelLoad(qualifiedName);
    }
  }
  
  @Override
  public void reportSetValue(String name, Object value) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportSetValue(name, value);
    }
  }
  
  @Override
  public void reportAddValue(String name, Object value, int size) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportAddValue(name, value, size);
    }
  }
  
  @Override
  public void reportInstantiate(String className, List<Object> params) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportInstantiate(className, params);
    }
  }
  
  @Override
  public void reportTemplateInclude(String templateName, ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportTemplateInclude(templateName, ast);
    }
  }
  
  @Override
  public void reportTemplateWrite(String templateName, ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportTemplateWrite(templateName, ast);
    }
  }
  
  @Override
  public void reportSetHookPoint(String hookName, HookPoint hp) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportSetHookPoint(hookName, hp);
    }
  }
  
  @Override
  public void reportCallHookPointStart(String hookName, HookPoint hp,
      ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportCallHookPointStart(hookName, hp, ast);
    }
  }
  
  @Override
  public void reportCallHookPointEnd(String hookName) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportCallHookPointEnd(hookName);
    }
  }
  
  @Override
  public void reportTemplateReplacement(String oldTemplate,
      List<? extends HookPoint> newHps) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportTemplateReplacement(oldTemplate, newHps);
    }
  }
  
  @Override
  public void reportASTSpecificTemplateReplacement(String oldTemplate,
      ASTNode node, HookPoint newHp) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportASTSpecificTemplateReplacement(oldTemplate, node,
          newHp);
    }
  }
  
  @Override
  public void reportSetBeforeTemplate(String template,
      List<? extends HookPoint> beforeHps) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportSetBeforeTemplate(template, beforeHps);
    }
  }
  
  @Override
  public void reportCallBeforeHookPoint(String oldTemplate,
      Collection<HookPoint> beforeHPs, ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportCallBeforeHookPoint(oldTemplate, beforeHPs, ast);
    }
  }
  
  @Override
  public void reportCallAfterHookPoint(String oldTemplate,
      Collection<HookPoint> afterHPs, ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportCallAfterHookPoint(oldTemplate, afterHPs, ast);
    }
  }
  
  @Override
  public void reportCallReplacementHookPoint(String oldTemplate,
      List<HookPoint> hps, ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportCallReplacementHookPoint(oldTemplate, hps, ast);
    }
  }
  
  @Override
  public void reportCallSpecificReplacementHookPoint(String oldTemplate,
      List<HookPoint> hps, ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportCallSpecificReplacementHookPoint(oldTemplate,
          hps, ast);
    }
  }
  
  @Override
  public void reportSetAfterTemplate(String template,
      List<? extends HookPoint> afterHps) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportSetAfterTemplate(template, afterHps);
    }
  }
  
  @Override
  public void reportUseHandwrittenCodeFile(Path parentDir, Path fileName) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportUseHandwrittenCodeFile(parentDir, fileName);
    }
  }
  
  @Override
  public void reportTransformationStart(String transformationName) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportTransformationStart(transformationName);
    }
  }
  
  @Override
  public void reportTransformationObjectChange(String transformationName,
      ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportTransformationObjectChange(transformationName,
          ast);
    }
  }
  
  @Override
  public void reportTransformationObjectCreation(String transformationName,
      ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportTransformationObjectCreation(transformationName,
          ast);
    }
  }
  
  @Override
  public void reportTransformationObjectDeletion(String transformationName,
      ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportTransformationObjectDeletion(transformationName,
          ast);
    }
  }
  
  @Override
  public void flush(ASTNode ast) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.flush(ast);
    }
  }
  
  @Override
  public void reportWarning(String message) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportWarning(message);
    }
  }
  
  @Override
  public void reportError(String msg) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportError(msg);
    }
  }
  
  @Override
  public void reportDetailed(String value) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportDetailed(value);
    }
  }
  
  @Override
  public void reportOpenInputFile(Path parentPath, Path file) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportOpenInputFile(parentPath, file);
    }
  }
  
  @Override
  public void reportParseInputFile(Path inputFilePath, String modelName) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportParseInputFile(inputFilePath, modelName);
    }
  }
  
  @Override
  public void reportSymbolTable(CommonScope scope) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportSymbolTable(scope);
    }
  }
  
  /**
   * A factory for providing tool specific report managers.
   *
   * @author (last commit) $Author$
   * @version $Revision$, $Date$
   */
  public static interface ReportManagerFactory {
    
    /**
     * Implementations of this method are responsible for providing an
     * appropriately configured report manager for a potentially given model
     * name (reporting is <b>mainly</b> per model). Implementors must take care
     * of possible <b>null</b> values or provide special constants for dealing
     * with model agnostic reporting etc.
     * 
     * @param modelName the model to which provide a report manager
     * configuration for (use special constants for entirely global, i.e., model
     * agnostic configurations)
     * @return a tool specific configuration/instance of a report manager.
     */
    ReportManager provide(String modelName);
    
  }

  /**
   * @param className
   * @param methodName
   * @param params
   */
  public void reportMethodCall(String className, String methodName, List<Object> params) {
    for (IReportEventHandler handler : this.reportEventHandlers) {
      handler.reportMethodCall(className, methodName, params);
    }
  }
  
}
