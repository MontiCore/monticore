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
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.resolving.ResolvingFilter;

import java.util.Collection;
import java.util.LinkedHashSet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Base class for modeling languages. Provides access to grammarlanguage-related functionality,
 * like parsing, symbol table creation, model analysis and code generation.
 *
 * @author Pedram Mir Seyed Nazari
 */
public abstract class CommonModelingLanguage implements ModelingLanguage {

  private final String fileEnding;
  private final String name;
  private final Collection<ResolvingFilter<? extends Symbol>> resolvingFilters = new LinkedHashSet<>();

  // TODO PN  make it final
  protected ModelingLanguageModelLoader<? extends ASTNode> modelLoader;

  // TODO PN make it final
  private ModelNameCalculator modelNameCalculator;

  /**
   *
   * @param name the name of the modeling grammarlanguage, e.g., "MontiCore Grammar Language"
   * @param fileEnding the file ending, e.g., ".cd" or "cd"
   * @param topLevelSymbolKind the {@link SymbolKind} of the top level
   * {@link de.monticore.symboltable.Symbol}
   *
   * @deprecated use {@link #CommonModelingLanguage(String, String)} instead
   */
  @Deprecated
  public CommonModelingLanguage(
      final String name,
      final String fileEnding,
      final SymbolKind topLevelSymbolKind) {
    this(name, fileEnding);
  }

  /**
   * @param name the name of the modeling grammarlanguage, e.g., "MontiCore Grammar Language"
   * @param fileEnding the file ending, e.g., ".cd" or "cd"
   * {@link de.monticore.symboltable.Symbol}
   */
  public CommonModelingLanguage(final String name, final String fileEnding) {
    checkArgument(!isNullOrEmpty(name));
    checkArgument(!isNullOrEmpty(fileEnding));

    this.name = name;
    this.fileEnding = !fileEnding.startsWith(".") ? fileEnding : fileEnding.substring(1);
    this.modelLoader = provideModelLoader();

    modelNameCalculator = new CommonModelNameCalculator();
  }

  protected abstract ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader();

  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public String getFileExtension() {
    return fileEnding;
  }
  
  @Override
  public String toString() {
    return getName();
  }

  @Override
  public Collection<ResolvingFilter<? extends Symbol>> getResolvers() {
    return getResolvingFilters();
  }

  @Override
  public Collection<ResolvingFilter<? extends Symbol>> getResolvingFilters() {
    return ImmutableList.copyOf(resolvingFilters);
  }

  public void addResolvingFilter(final ResolvingFilter<? extends Symbol> resolvingFilter) {
    resolvingFilters.add(resolvingFilter);
  }

  public void addResolvingFilters(final Collection<ResolvingFilter<? extends Symbol>> resolvingFilters) {
    this.resolvingFilters.addAll(resolvingFilters);
  }

  /**
   * @deprecated use {@link #addResolvingFilter(ResolvingFilter)} instead
   */
  @Deprecated
  public void addResolver(final ResolvingFilter<? extends Symbol> resolvingFilter) {
    addResolvingFilter(resolvingFilter);
  }

  /**
   * @deprecated use {@link #addResolvingFilters(Collection)} instead
   */
  public void addResolvers(final Collection<ResolvingFilter<? extends Symbol>> resolvingFilters) {
    addResolvingFilters(resolvingFilters);
  }

  @Override
  public ModelingLanguageModelLoader<? extends ASTNode> getModelLoader() {
    return modelLoader;
  }

  protected void setModelNameCalculator(ModelNameCalculator modelNameCalculator) {
    this.modelNameCalculator = modelNameCalculator;
  }

  @Override
  public ModelNameCalculator getModelNameCalculator() {
    return modelNameCalculator;
  }
}
