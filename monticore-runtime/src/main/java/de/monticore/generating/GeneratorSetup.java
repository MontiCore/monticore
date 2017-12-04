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
   * Used for handling variables and hook points
   */
  private GlobalExtensionManagement glex;

  /**
   * The path for the handwritten code
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
  private Optional<String> commentStart = Optional.empty();

  /**
   * The characters for the end of a comment.
   * Usually these are the comments of the target language.
   */
  private Optional<String> commentEnd = Optional.empty();

  /**
   * Used for loading all sorts of files (mainly templates)
   */
  private ClassLoader classLoader;

  /**
   * The model name
   */
  private Optional<String> modelName = Optional.empty();
  
  /*******************************************************/

  /**
   * Construtor
   */
  public GeneratorSetup() {
    this.classLoader = getClass().getClassLoader();
  }

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
    return classLoader;
  }

  public void setGlex(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  public Optional<GlobalExtensionManagement> getGlex() {
    return Optional.ofNullable(glex);
  }

  public void setAdditionalTemplatePaths(List<File> additionalTemplatePaths) {
    this.additionalTemplatePaths = new ArrayList<>(additionalTemplatePaths);
  }

  public List<File> getAdditionalTemplatePaths() {
    return ImmutableList.copyOf(additionalTemplatePaths);
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
  public Optional<String> getCommentStart() {
    return commentStart;
  }

  /**
   * @param commentStart the characters for the start of a comment. Usually same as the target
   * language.
   */
  public void setCommentStart(Optional<String> commentStart) {
    this.commentStart = commentStart;
  }

  /**
   * @return the characters for the end of a comment. Usually same as the target language.
   */
  public Optional<String> getCommentEnd() {
    return commentEnd;
  }

  /**
   * @param commentEnd the characters for the end of a comment. Usually same as the target language.
   */
  public void setCommentEnd(Optional<String> commentEnd) {
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
