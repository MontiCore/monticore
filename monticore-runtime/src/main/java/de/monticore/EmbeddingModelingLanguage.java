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

package de.monticore;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.se_rwth.commons.logging.Log;

/**
 * @author Pedram Mir Nazari
 */
public abstract class EmbeddingModelingLanguage extends CommonModelingLanguage {
  // TODO PN by default, use file extension of host language

  protected ModelingLanguage hostLanguage;
  protected final Collection<ModelingLanguage> embeddedLanguages = new ArrayList<>();

  /**
   * @param name               the name of the modeling grammarlanguage, e.g., "MontiCore Grammar Language"
   * @param fileEnding         the file ending, e.g., ".cd" or "cd"
   *                           {@link Symbol}
   */
  public EmbeddingModelingLanguage(String name, String fileEnding,
      ModelingLanguage hostLanguage, Collection<ModelingLanguage> embeddedLanguages) {
    super(name, fileEnding);

    checkArgument(embeddedLanguages.size() > 0);

    this.hostLanguage = Log.errorIfNull(hostLanguage);
    this.embeddedLanguages.addAll(Log.errorIfNull(embeddedLanguages));

    setModelNameCalculator(hostLanguage.getModelNameCalculator());
  }

  public EmbeddingModelingLanguage(String name, String fileEnding,
      ModelingLanguage hostLanguage, ModelingLanguage embeddedLanguage) {
    this(name, fileEnding, hostLanguage, singletonList(embeddedLanguage));

  }

  public ModelingLanguage getHostLanguage() {
    return hostLanguage;
  }

  public Collection<ModelingLanguage> getEmbeddedLanguages() {
    return ImmutableList.copyOf(embeddedLanguages);
  }

  /**
   * @return all resolvers specified directly in this language, the host language and
   * all embedded languages.
   *
   * @see CommonModelingLanguage#getResolvers()
   */
  @Override
  public Collection<ResolvingFilter<? extends Symbol>> getResolvers() {
    final Set<ResolvingFilter<? extends Symbol>> resolvingFilters = new LinkedHashSet<>();

    // resolving filters directly stored in this (composite) language, e.g., adapters.
    resolvingFilters.addAll(super.getResolvers());

    // Resolving filters of the host language and the embedded language are added
    resolvingFilters.addAll(hostLanguage.getResolvers());

    // If further embedded languages exists, their resolving filters are added too.
    for (ModelingLanguage language : embeddedLanguages) {
      resolvingFilters.addAll(language.getResolvers());
    }

    return resolvingFilters;
  }

  /**
   * @return by default, the {@link ModelingLanguageModelLoader} of the host language
   *
   * @see CommonModelingLanguage#getModelLoader()
   */
  @Override
  public ModelingLanguageModelLoader<? extends ASTNode> getModelLoader() {
    return super.getModelLoader();
  }

  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return getModelLoader();
  }

  /**
   * @return by default, the {@link ModelNameCalculator} of the host language
   *
   * @see CommonModelingLanguage#getModelNameCalculator()
   */
  @Override
  public ModelNameCalculator getModelNameCalculator() {
    return super.getModelNameCalculator();
  }



}
