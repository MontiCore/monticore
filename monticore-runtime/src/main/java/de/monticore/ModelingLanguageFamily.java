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

import com.google.common.collect.ImmutableList;
import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Base class for language families. Provides access to language-related
 * functionality, like parsing, model analysis and code generation.
 *
 * @author  Pedram Mir Seyed Nazari
 *
 */
// TODO PN extract interface, analogous to ModelingLanguage
public class ModelingLanguageFamily {

  // TODO PN adding of other modeling language families should be possible, too.
  // TODO PN add name for language family?

  private final Collection<ModelingLanguage> modelingLanguages = new LinkedHashSet<>();

  /**
   * All resolvers added directly to this language family (not those of the single modeling
   * languages).
   */
  private final Collection<ResolvingFilter<? extends Symbol>> resolvingFilters = new LinkedHashSet<>();

  public Optional<ModelingLanguage> getLanguageByFileExtension(final String fileExtension) {
    checkArgument(!isNullOrEmpty(fileExtension), "File extension may not be null or empty");

    final String cleanedFileExtension = fileExtension.startsWith(".")
        ? fileExtension.substring(1) : fileExtension;

    for (ModelingLanguage modelingLanguage : modelingLanguages) {
      if (modelingLanguage.getFileExtension().equals(cleanedFileExtension)) {
        return Optional.of(modelingLanguage);
      }
    }

    return Optional.empty();
  }

  public void addModelingLanguage(final ModelingLanguage newModelingLanguage) {
    Log.errorIfNull(newModelingLanguage);

    for (ModelingLanguage modelingLanguage : modelingLanguages) {
      if (modelingLanguage.getFileExtension().equals(newModelingLanguage.getFileExtension())) {
        Log.info("0xA1027 The languages \"" + modelingLanguage.getName() + "\" and \"" +
            newModelingLanguage.getName() + "\" use both the file extension \"" + modelingLanguage
            .getFileExtension() + "\".", ModelingLanguageFamily.class.getName());
      }
    }

    modelingLanguages.add(newModelingLanguage);
  }

  public Collection<ModelingLanguage> getModelingLanguages() {
    return ImmutableList.copyOf(modelingLanguages);
  }

  /**
   * @return all resolvers specified in this language family and all its single languages.
   */
  public Collection<ResolvingFilter<? extends Symbol>> getAllResolvers() {
    final Collection<ResolvingFilter<? extends Symbol>> allResolvingFilters = new LinkedHashSet<>();
    allResolvingFilters.addAll(resolvingFilters);

    for (ModelingLanguage language : modelingLanguages) {
      allResolvingFilters.addAll(language.getResolvingFilters());
    }

    return allResolvingFilters;
  }

  /**
   * Adds a {@link de.monticore.symboltable.resolving.ResolvingFilter} directly to this language family.
   * Usually, only {@link de.monticore.symboltable.resolving.CommonAdaptedResolvingFilter}s
   * need to be added, since the modeling languages already define their default resolvers (see
   * {@link ModelingLanguage#getResolvingFilters()}).
   *
   * @param resolvingFilter the resolver to be added
   */
  public void addResolver(ResolvingFilter<? extends Symbol> resolvingFilter) {
    resolvingFilters.add(resolvingFilter);
  }

  public Collection<ModelingLanguageModelLoader<? extends ASTNode>> getAllModelLoaders() {
    final Collection<ModelingLanguageModelLoader<? extends ASTNode>> allModelLoader =
        modelingLanguages.stream().map(ModelingLanguage::getModelLoader).collect(Collectors.toSet());

    return allModelLoader;
  }

}
