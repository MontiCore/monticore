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

package de.monticore.generating.templateengine;

import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

/**
 * Holds settings for {@link TemplateController}. These settings are usually set
 * once and remain unchanged.
 *
 * @author Pedram Nazari
 */
public class TemplateControllerConfiguration {
  
  public static final String ALIASES_TEMPLATE = "de.monticore.generating.templateengine.freemarker.Aliases";
  
  private final GlobalExtensionManagement glex;
  
  private FileReaderWriter fileHandler;
  
  private FreeMarkerTemplateEngine freeMarkerTemplateEngine;
  
  private TemplateLogger log = new TemplateLogger();
  
  private ITemplateControllerFactory templateControllerFactory;
  
  /** the target directory for generated files */
  private File targetDir;
  
  private String modelName;
  
  /**
   * Defines if tracing infos are added to the result as comments
   */
  private boolean tracing;
  
  /**
   * The characters for the start of a comment. Usually same as the target
   * language.
   */
  private String commentStart;

  /**
   * The characters for the end of a comment. Usually same as the target
   * language.
   */
  private String commentEnd;
  
  private String defaultFileExtension;
  
  private ClassLoader classLoader;
  
  /**
   * Additional path as the source of templates
   */
  private File[] externalTemplatePath;
  
  TemplateControllerConfiguration(GlobalExtensionManagement glex, FileReaderWriter fileHandler) {
    this.glex = glex;
    this.fileHandler = fileHandler;
  }
  
  /**
   * Defines if tracing infos are added to the result as comments.
   * 
   * @param tracing set true to switch on tracing and false to switch of
   */
  void setTracing(boolean tracing) {
    this.tracing = tracing;
  }
  
  public boolean isTracing() {
    return tracing;
  }
  
  /**
   * @return the characters for the start of a comment. Usually same as the
   * target language.
   */
  public String getCommentStart() {
    return commentStart;
  }
  
  /**
   * @param commentStart the characters for the start of a comment. Usually same
   * as the target language.
   */
  void setCommentStart(String commentStart) {
    this.commentStart = nullToEmpty(commentStart);
  }
  
  /**
   * @return the characters for the end of a comment. Usually same as the target
   * language.
   */
  public String getCommentEnd() {
    return commentEnd;
  }
  
  /**
   * @param commentEnd the characters for the end of a comment. Usually same as
   * the target language.
   */
  void setCommentEnd(String commentEnd) {
    this.commentEnd = nullToEmpty(commentEnd);
  }
  
  /**
   * @return the default file extension used for the generated files (e.g.
   * java).
   */
  public String getDefaultFileExtension() {
    return defaultFileExtension;
  }
  
  /**
   * Sets the default file extension used for the generated files, e.g. java or
   * .java (with leading dot).
   * 
   * @param fileExtension the file extension, e.g. java or .java (with leading
   * dot)
   */
  public void setDefaultFileExtension(String fileExtension) {
    checkArgument(!isNullOrEmpty(fileExtension));
    
    if (fileExtension.startsWith(".")) {
      this.defaultFileExtension = fileExtension.substring(1);
    }
    else {
      this.defaultFileExtension = fileExtension;
    }
  }
  
  public ClassLoader getClassLoader() {
    return classLoader;
  }
  
  void setClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }
  
  /**
   * @return the target directory for generated files
   */
  public File getTargetDir() {
    return targetDir;
  }
  
  /**
   * @param targetDir the target directory for generated files
   */
  void setTargetDir(File targetDir) {
    this.targetDir = targetDir;
  }
  
  public String getModelName() {
    return modelName;
  }
  
  void setModelName(String modelName) {
    this.modelName = modelName;
  }
  
  public File[] getExternalTemplatePath() {
    return externalTemplatePath;
  }
  
  void setExternalTemplatePath(File[] externalTemplatePath) {
    this.externalTemplatePath = externalTemplatePath;
  }
  
  public GlobalExtensionManagement getGlEx() {
    return glex;
  }
  
  void setFreeMarkerTemplateEngine(FreeMarkerTemplateEngine freeMarkerTemplateEngine) {
    this.freeMarkerTemplateEngine = freeMarkerTemplateEngine;
  }
  
  public FreeMarkerTemplateEngine getFreeMarkerTemplateEngine() {
    return freeMarkerTemplateEngine;
  }
  
  void setFileHandler(FileReaderWriter fileHandler) {
    this.fileHandler = fileHandler;
  }
  
  public FileReaderWriter getFileHandler() {
    return this.fileHandler;
  }
  
  void setTemplateControllerFactory(ITemplateControllerFactory templateControllerFactory) {
    this.templateControllerFactory = templateControllerFactory;
  }
  
  public ITemplateControllerFactory getTemplateControllerFactory() {
    return this.templateControllerFactory;
  }
  
  /* Assigns the template logger to use. */
  void setTemplateLogger(TemplateLogger log) {
    if (log == null) {
      throw new IllegalArgumentException("0xA4091 Assigned template logger must not be null.");
    }
    this.log = log;
  }
  
  /**
   * Getter for the template logger of this configuration.
   * 
   * @return
   */
  protected TemplateLogger getTemplateLogger() {
    return this.log;
  }
  
  /**
   * Provides logging for templates.
   *
   * @since 4.0.1
   */
  public class TemplateLogger {
    
    private TemplateLogger() {
    }
    
    /**
     * @see de.se_rwth.commons.logging.Log#trace(String, String)
     */
    public void trace(String msg, String logName) {
      Log.trace(msg, logName);
    }
    
    /**
     * @see de.se_rwth.commons.logging.Log#debug(String, String)
     */
    public void debug(String msg, String logName) {
      Log.debug(msg, logName);
    }
    
    /**
     * @see de.se_rwth.commons.logging.Log#info(String, String)
     */
    public void info(String msg, String logName) {
      Log.info(msg, logName);
    }
    
    /**
     * @see de.se_rwth.commons.logging.Log#warn(String)
     */
    public void warn(String msg) {
      Log.warn(msg);
    }
    
    /**
     * @see de.se_rwth.commons.logging.Log#error(String)
     */
    public void error(String msg) {
      Log.error(msg);
    }

  }
  
}
