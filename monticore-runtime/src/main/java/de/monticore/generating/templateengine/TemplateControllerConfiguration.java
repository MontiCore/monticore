/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

import java.io.File;
import java.util.Optional;

import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.IterablePath;

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

  private TemplateControllerFactory templateControllerFactory;

  /** the target directory for generated files */
  private File outputDirectory;

  /** the path for the handwritten code */
  private IterablePath handcodedPath;

  private Optional<String> modelName = Optional.empty();

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
  public File getOutputDirectory() {
    return outputDirectory;
  }

  /**
   * @param targetDir the target directory for generated files
   */
  void setOutputDirectory(File targetDir) {
    this.outputDirectory = targetDir;
  }

  public Optional<String> getModelName() {
    return modelName;
  }

  void setModelName(String modelName) {
    this.modelName = Optional.ofNullable(modelName);
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


  /**
   * @return handcoded path
   */
  public IterablePath getHandcodedPath() {
    return this.handcodedPath;
  }

  /**
   * @param handcodedPath the handcoded path to set
   */
  public void setHandcodedPath(IterablePath handcodedPath) {
    this.handcodedPath = handcodedPath;
  }

  void setTemplateControllerFactory(TemplateControllerFactory templateControllerFactory) {
    this.templateControllerFactory = templateControllerFactory;
  }

  public TemplateControllerFactory getTemplateControllerFactory() {
    return this.templateControllerFactory;
  }


}
