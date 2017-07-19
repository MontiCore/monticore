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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

import java.io.File;
import java.util.Optional;

import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 */
public class TemplateControllerConfigurationBuilder {

  private GlobalExtensionManagement glex;

  private FreeMarkerTemplateEngine freeMarkerTemplateEngine;

  private ITemplateControllerFactory templateControllerFactory;

  private FileReaderWriter fileHandler;

  public static final String DEFAULT_COMMENT_START = "/*";

  public static final String DEFAULT_COMMENT_END = "*/";

  /**
   * Defines if tracing infos are added to the result as comments
   */
  private boolean tracing = true;

  private String commentStart = DEFAULT_COMMENT_START;

  private String commentEnd = DEFAULT_COMMENT_END;

  private String defaultFileExtension = "java";

  private File outputDirectory;

  private IterablePath handcodedPath;

  private Optional<String> modelName = Optional.empty();

  private ClassLoader classLoader;

  /**
   * Additional path as the source of templates
   */
  private File[] externalTemplatePaths;

  public TemplateControllerConfigurationBuilder glex(GlobalExtensionManagement glex) {
    this.glex = glex;
    return this;
  }

  public TemplateControllerConfigurationBuilder freeMarkerTemplateEngine(
      FreeMarkerTemplateEngine freeMarkerTemplateEngine) {
    this.freeMarkerTemplateEngine = freeMarkerTemplateEngine;
    return this;
  }

  public TemplateControllerConfigurationBuilder templateControllerFactory(
      ITemplateControllerFactory templateControllerFactory) {
    this.templateControllerFactory = templateControllerFactory;
    return this;
  }

  public TemplateControllerConfigurationBuilder tracing(boolean tracing) {
    this.tracing = tracing;
    return this;
  }

  public TemplateControllerConfigurationBuilder modelName(Optional<String> modelName) {
    this.modelName = modelName;
    return this;
  }

  public TemplateControllerConfigurationBuilder commentStart(String commentStart) {
    this.commentStart = commentStart;
    return this;
  }

  public TemplateControllerConfigurationBuilder commentEnd(String commentEnd) {
    this.commentEnd = commentEnd;
    return this;
  }

  public TemplateControllerConfigurationBuilder defaultFileExtension(String defaultFileExtension) {
    this.defaultFileExtension = defaultFileExtension;
    return this;
  }

  public TemplateControllerConfigurationBuilder classLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
    return this;
  }

  public TemplateControllerConfigurationBuilder externalTemplatePaths(
      File[] externalTemplatePaths) {
    this.externalTemplatePaths = externalTemplatePaths;
    return this;
  }

  public TemplateControllerConfigurationBuilder handcodedPath(IterablePath hwcPath) {
    this.handcodedPath = hwcPath;
    return this;
  }

  public TemplateControllerConfigurationBuilder outputDirectory(File targetDir) {
    this.outputDirectory = targetDir;
    return this;
  }

  public TemplateControllerConfigurationBuilder fileHandler(FileReaderWriter fileHandler) {
    this.fileHandler = fileHandler;
    return this;
  }

  public TemplateControllerConfiguration build() {
    Log.errorIfNull(glex);
    Log.errorIfNull(freeMarkerTemplateEngine);
    Log.errorIfNull(fileHandler);
    Log.errorIfNull(classLoader);
    Log.errorIfNull(outputDirectory);

    checkArgument(!isNullOrEmpty(defaultFileExtension));
    if (defaultFileExtension.startsWith(".")) {
      defaultFileExtension = defaultFileExtension.substring(1);
    }

    if (templateControllerFactory == null) {
      templateControllerFactory = TemplateControllerFactory.getInstance();
    }

    commentStart = nullToEmpty(commentStart);
    commentEnd = nullToEmpty(commentEnd);

    TemplateControllerConfiguration config = new TemplateControllerConfiguration(glex, fileHandler);
    config.setFreeMarkerTemplateEngine(freeMarkerTemplateEngine);
    config.setTemplateControllerFactory(templateControllerFactory);
    config.setExternalTemplatePath(externalTemplatePaths);
    config.setOutputDirectory(outputDirectory);
    config.setHandcodedPath(handcodedPath);
    if (modelName.isPresent()) {
      config.setModelName(modelName.get());
    }
    config.setDefaultFileExtension(defaultFileExtension);
    config.setCommentStart(commentStart);
    config.setCommentEnd(commentEnd);
    config.setTracing(tracing);

    return config;
  }

}
