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

package de.monticore.modelloader;

import de.monticore.AmbiguityException;
import de.monticore.ModelingLanguage;
import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelCoordinates;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.se_rwth.commons.Names;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * This class is responsible for loading models from the model path (
 * {@link de.monticore.io.paths.ModelPath}).
 *
 * @author Pedram Mir Seyed Nazari
 */

// TODO PN update doc
// TODO PN extract CommonModelingLanguageModelLoader
public abstract class ModelingLanguageModelLoader<T extends ASTNode> {

  private final ModelingLanguage modelingLanguage;

  private final AstProvider<T> astProvider;

  public ModelingLanguageModelLoader(ModelingLanguage modelingLanguage) {
    this.modelingLanguage = modelingLanguage;
    this.astProvider = new FileBasedAstProvider<>(modelingLanguage);
  }

  public ModelingLanguageModelLoader(ModelingLanguage modelingLanguage,
      AstProvider<T> astProvider) {
    this.modelingLanguage = modelingLanguage;
    this.astProvider = astProvider;
  }

  /**
   * Loads the model with the <code>qualifiedModelName</code>. If more than one
   * corresponding model is found, an {@link de.monticore.AmbiguityException} is
   * thrown.
   *
   * @param qualifiedModelName the qualified name of the model to be loaded
   * @return An ast of the loaded model.
   * @throws de.monticore.AmbiguityException is thrown, if more than one model
   *                                         with the name <code>qualifiedModelName</code>
   * @see #loadAmbiguousModels(String, de.monticore.io.paths.ModelPath)
   */
  public Optional<T> loadModel(final String qualifiedModelName, final ModelPath modelPath) {
    final Collection<T> models = loadAmbiguousModels(qualifiedModelName, modelPath);
    if (models.size() > 1) {
      throw new AmbiguityException("0xA4092 Multiple models were found with name '"
          + qualifiedModelName + "'");
    }

    return models.stream().findFirst();
  }

  // TODO PN rename loadModels
  public Collection<T> loadAmbiguousModelAndCreateSymbolTable(final String qualifiedModelName,
      final ModelPath modelPath, final MutableScope enclosingScope,
      final ResolverConfiguration resolverConfiguration) {

    final Collection<T> asts = loadAmbiguousModels(qualifiedModelName, modelPath);

    for (T ast : asts) {
      // TODO PN add here a general hook, to allow further processing of the asts
      createSymbolTableFromAST(ast, qualifiedModelName, enclosingScope, resolverConfiguration);
    }

    return asts;
  }

  /**
   * Creates the symbol table for the model in <code>modelName</code> using its
   * <code>ast</code> and adds its top scope (usually
   * {@link de.monticore.symboltable.ArtifactScope}) to the sub scopes.
   *
   * @param ast       the ast of the model in <code>modelName</code>
   * @param modelName name of the model
   */
  protected abstract void createSymbolTableFromAST(T ast, String modelName,
      MutableScope enclosingScope,
      ResolverConfiguration resolverConfiguration);

  /**
   * Loads one or more models with the <code>qualifiedModelName</code>. If only
   * one model is expected, use
   * {@link #loadModel(String, de.monticore.io.paths.ModelPath)} instead.
   *
   * @param qualifiedModelName the qualified name of the model(s) to be loaded
   * @param modelPath
   * @return A collection with an ast for each existing model.
   * @see #loadModel(String, de.monticore.io.paths.ModelPath)
   */
  public Collection<T> loadAmbiguousModels(final String qualifiedModelName, ModelPath modelPath) {
    checkArgument(!isNullOrEmpty(qualifiedModelName));

    final Collection<T> foundModels = new ArrayList<>();

    final ModelCoordinate resolvedCoordinate = resolve(qualifiedModelName, modelingLanguage,
        modelPath);
    if (resolvedCoordinate.hasLocation()) {
      final T ast = astProvider.getRootNode(resolvedCoordinate);
      Reporting.reportOpenInputFile(resolvedCoordinate.getParentDirectoryPath(),
          resolvedCoordinate.getQualifiedPath());
      foundModels.add(ast);
    }

    return foundModels;
  }

  /**
   * @param qualifiedModelName example: "de.mc.statechartOne"
   * @param modelingLanguage   the language to which the model <i>might</i> belong
   * @return the resolved coordinate (the location of the model is set if
   * successful)
   */
  private ModelCoordinate resolve(final String qualifiedModelName,
      final ModelingLanguage modelingLanguage, final ModelPath modelPath) {
    String simpleName = Names.getSimpleName(qualifiedModelName);
    Path qualifiedPath = Paths.get(
        Names.getPathFromQualifiedName(qualifiedModelName)).resolve(
        simpleName + "." + modelingLanguage.getFileExtension());
    ModelCoordinate qualifiedModel = ModelCoordinates.createQualifiedCoordinate(qualifiedPath);

    return modelPath.resolveModel(qualifiedModel);
  }

  public ModelingLanguage getModelingLanguage() {
    return modelingLanguage;
  }

  // TODO PN add equals+hash method
}
