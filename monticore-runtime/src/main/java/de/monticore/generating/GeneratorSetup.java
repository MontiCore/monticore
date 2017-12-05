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

package de.monticore.generating;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.io.FileReaderWriter;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.freemarker.TemplateAutoImport;
import de.monticore.io.paths.IterablePath;

// TODO Optional values werden inkonsistent gesetzt:
// glex wird weder ein default gesezt, noch als parameter im
// Konstruktor verlangt, aber beim getter ein Optional geliefert
// additionalTemplatePaths: hat einen default
// In den comments wird Defalt gespeichert als Optional.absent
// etc.
// ausserdem fehlt eine Begründung warum das alles hier hängen muss
// und nicht zB Kommentare values in glex sind.

/**
 * Setup for generator (see {@link GeneratorEngine}).
 */
public class GeneratorSetup {

  /**
   * Where to store all files (e.g. "gen" or "out")
   */
  private File outputDirectory = "out";

  /**
   * Used for handling variables and hook points;
   * Default is only created with first get-access.
   */
  private GlobalExtensionManagement glex = null;

  /**
   * The path for the handwritten code
   * Default is only created with first get-access.
   */
  private IterablePath handcodedPath;

  /**
   * Additional path as the source of templates
   */
  private List<File> additionalTemplatePaths = new ArrayList<>();

  /**
   * Template to include automatically at beginning.
   */
  private List<TemplateAutoImport> autoImports = new ArrayList<>();

  /**
   * Defines if tracing infos are added to the result as comments
   */
  private boolean tracing = true;

  /**
   * The characters for the start of a comment.
   * Usually these are the comments of the target language.
   */
  private String commentStart = "/*";

  /**
   * The characters for the end of a comment.
   * Usually these are the comments of the target language.
   */
  private String commentEnd = "*/";

  // TODO MB: Unklar wieso ein FileReaderWriter und ein ClassLoader 
  // notwendig sind. Kann der ClassLoader gestrichen werden?
  
  /**
   * Used for loading all sorts of files (mainly templates)
   */
  private ClassLoader classLoader;

  /**
   * The model name
   * (if the arftifacts are generated from one model, this could 
   * be an identifier of this model)
   * By default the model name is absent -- 
   * and then the according tracing info is not printed at all.
   */
  private Optional<String> modelName = Optional.empty();

  /**
   * The handler for File IO (also manages reporting)
   */
  private FileReaderWriter fileHandler;

  /**
   * The real engine provided by FreeMarker
   */
  private FreeMarkerTemplateEngine freeMarkerTemplateEngine;

  /**
   * Each template receives its own controller.
   * The controlle factory instantiates TemplateControllers 
   * on demand.
   * Overriding TemplateControllers should be done by 
   * providing a different factory
   */
  private TemplateControllerFactory templateControllerFactory;

  /**
   * Desired default file extension, e.g. "java"
   */
  private String defaultFileExtension = "java";


  /*******************************************************/
  /**
   * Sets the default file extension used for the generated files, e.g. java or
   * .java (with leading dot).
   *
   * @param o the file extension, e.g. java or .java (with leading
   * dot)
   */
  public void setDefaultFileExtension(String o) {
    if (o.startsWith(".")) {
      this.defaultFileExtension = o.substring(1);
    }
    else {
      this.defaultFileExtension = o;
    }
  }

  public String getDefaultFileExtension() {
    return defaultFileExtension;
  }

  /*******************************************************/
  public void setFileHandler(FileReaderWriter o) {
    this.fileHandler = o;
  }

  public FileReaderWriter getFileHandler() {
    if (this.fileHandler == null) 
        this.fileHandler = new FileReaderWriter(); //default
    return fileHandler;
  }

  /*******************************************************/
  public void setFreeMarkerTemplateEngine(FreeMarkerTemplateEngine o) {
    this.freeMarkerTemplateEngine = o;
  }

  public FreeMarkerTemplateEngine getFreeMarkerTemplateEngine() {
    // KEIN default
    return freeMarkerTemplateEngine;
  }

  /*******************************************************/
  public void setTemplateControllerFactory(TemplateControllerFactory o) {
    this.templateControllerFactory = o;
  }

  public TemplateControllerFactory getTemplateControllerFactory() {
    if (this.templateControllerFactory == null) 
        this.templateControllerFactory =
			new TemplateControllerFactory(); //default
    return templateControllerFactory;
  }


  /*******************************************************/
  /*******************************************************/

  /**
   * Construtor
   */
  public GeneratorSetup() {
  }

  /*******************************************************/

  public void setOutputDirectory(File outputDirectory) {
    this.outputDirectory = outputDirectory;
  }

  public File getOutputDirectory() {
    return outputDirectory;
  }

  public void setClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  public ClassLoader getClassLoader() {
    if (this.classLoader == null) 
        this.classLoader = getClass().getClassLoader(); //default
    return classLoader;
  }

  public void setGlex(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  public GlobalExtensionManagement getGlex() {
    if (this.glex == null) 
    	this.glex = new GlobalExtensionManagement();  //default
    return this.glex;
  }

  public void setAdditionalTemplatePaths(List<File> additionalTemplatePaths) {
    this.additionalTemplatePaths = new ArrayList<>(additionalTemplatePaths);
  }

  public List<File> getAdditionalTemplatePaths() {
    return additionalTemplatePaths;
  }

  public void setAutoImports(List<TemplateAutoImport> autoImports) {
    this.autoImports = new ArrayList<>(autoImports);
  }

  /**
   * @return the templates to include automatically at the beginning
   */
  public List<TemplateAutoImport> getAutoTemplateImports() {
    return ImmutableList.copyOf(autoImports);
  }

  /**
   * @return targetPath
   */
  public IterablePath getHandcodedPath() {
    if (this.handcodedPath == null) 
    	this.handcodedPath = IterablePath.empty();  //default
    return this.handcodedPath;
  }

  /**
   * @param hwcpath the handcoded path to set
   */
  public void setHandcodedPath(IterablePath hwcPath) {
    this.handcodedPath = hwcPath;
  }

  /**
   * @param tracing defines if tracing infos are added to the result as comments.
   */
  public void setTracing(boolean tracing) {
    this.tracing = tracing;
  }

  /**
   * @return true, if tracing infos are added to the result as comments.
   */
  public boolean isTracing() {
    return tracing;
  }

  /**
   * @return the characters for the start of a comment. Usually same as the target language.
   */
  public String getCommentStart() {
    return commentStart;
  }

  /**
   * @param commentStart the characters for the start of a comment. Usually same as the target
   * language.
   */
  public void setCommentStart(String commentStart) {
    this.commentStart = commentStart;
  }

  /**
   * @return the characters for the end of a comment. Usually same as the target language.
   */
  public String getCommentEnd() {
    return commentEnd;
  }

  /**
   * @param commentEnd the characters for the end of a comment. Usually same as the target language.
   */
  public void setCommentEnd(String commentEnd) {
    this.commentEnd = commentEnd;
  }
  
  /**
   * @return modelName
   */
  public Optional<String> getModelName() {
    return this.modelName;
  }

  /**
   * @param modelName the modelName to set
   */
  public void setModelName(String modelName) {
    this.modelName = Optional.ofNullable(modelName);
  }
}
