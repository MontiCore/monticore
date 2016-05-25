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

package de.monticore.symboltable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.monticore.ModelNameCalculator;
import de.monticore.ModelingLanguage;
import de.monticore.ModelingLanguageFamily;
import de.monticore.ast.ASTNode;
import de.monticore.io.paths.ModelPath;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.AdaptedResolvingFilter;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.se_rwth.commons.logging.Log;

/**
 * @author Pedram Mir Seyed Nazari
 */
public final class GlobalScope extends CommonScope {

  private final ModelPath modelPath;
  private final ResolverConfiguration resolverConfiguration;

  private final Set<ModelingLanguage> modelingLanguages = new LinkedHashSet<>();

  private final Map<String, Set<ModelingLanguageModelLoader<? extends ASTNode>>> modelName2ModelLoaderCache = new HashMap<>();

  public GlobalScope(final ModelPath modelPath, final Collection <ModelingLanguage> modelingLanguages,
      final ResolverConfiguration resolverConfiguration) {
    super(Optional.empty(), true);

    this.modelPath = Log.errorIfNull(modelPath);
    this.resolverConfiguration = Log.errorIfNull(resolverConfiguration);
    this.modelingLanguages.addAll(Log.errorIfNull(modelingLanguages));

    if (modelingLanguages.isEmpty()) {
      Log.warn(GlobalScope.class.getSimpleName() + ": 0xA1044 No model loaders defined. This hampers the "
          + "loading of models.");
    }

    setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());
  }

  public GlobalScope(final ModelPath modelPath, final ModelingLanguage mo,
      ResolverConfiguration resolverConfiguration) {
    this(modelPath, Collections.singletonList(mo), resolverConfiguration);
  }

  public GlobalScope(final ModelPath modelPath, ModelingLanguageFamily languageFamily) {
    this(modelPath, languageFamily.getModelingLanguages(), new ResolverConfiguration());

    resolverConfiguration.addTopScopeResolvers(languageFamily.getAllResolvers());
    setResolvingFilters(resolverConfiguration.getTopScopeResolvingFilters());
  }


  @Override
  public Optional<String> getName() {
    return Optional.empty();
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(final ResolvingInfo resolvingInfo,
      final String symbolName, final SymbolKind kind, final AccessModifier modifier, final Predicate<Symbol> predicate) {
    resolvingInfo.addInvolvedScope(this);

    // First, try to resolve the symbol in the current scope and its sub scopes.
    Collection<T> resolvedSymbol = resolveDownMany(resolvingInfo, symbolName, kind, modifier, predicate);

    if (!resolvedSymbol.isEmpty()) {
      return resolvedSymbol;
    }


    // Symbol not found: try to load corresponding model and build its symbol table
    // TODO PN Optimize: if no further models have been loaded, we can stop here. There is no need
    // to resolveDown again
    loadModels(resolvingInfo.getResolvingFilters(), symbolName, kind);

    // Maybe the symbol now exists in this scope (resp. its sub scopes). So, resolve down, again.
    resolvedSymbol = resolveDownMany(new ResolvingInfo(getResolvingFilters()), symbolName, kind, modifier, predicate);

    return resolvedSymbol;
  }

  protected void loadModels(final Collection<ResolvingFilter<? extends Symbol>> resolvingFilters, final String symbolName, final SymbolKind kind) {

    // TODO PN optimize

    for (final ModelingLanguage modelingLanguage : modelingLanguages) {
      final ModelNameCalculator modelNameCalculator = modelingLanguage.getModelNameCalculator();
      final ModelingLanguageModelLoader<? extends ASTNode> modelLoader = modelingLanguage.getModelLoader();

      final Set<SymbolKind> possibleSymbolKinds = getPossibleSymbolKinds(resolvingFilters, kind);

      for (final SymbolKind kindForCalc : possibleSymbolKinds) {
        final Set<String> calculatedModelName = modelNameCalculator.calculateModelNames(symbolName, kindForCalc);

        if (!calculatedModelName.isEmpty() && continueWithModelLoader(calculatedModelName.iterator().next(), modelLoader)) {
          modelLoader.loadModelsIntoScope(calculatedModelName.iterator().next(), modelPath, this, resolverConfiguration);
          cache(modelLoader, calculatedModelName.iterator().next());
        }
        else {
          Log.debug("Model for '" + symbolName + "' already exists. No need to load it.", GlobalScope.class.getSimpleName());
        }
      }


    }
  }

  protected Set<SymbolKind> getPossibleSymbolKinds(Collection<ResolvingFilter<? extends Symbol>> resolvingFilters, SymbolKind kind) {
    final Collection<ResolvingFilter<? extends Symbol>> resolversForKind = ResolvingFilter
        .getFiltersForTargetKind(resolvingFilters, kind);

    return resolversForKind.stream()
        .map(resolvingFilter -> getSymbolKindByResolvingFilter(kind, resolvingFilter))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  // TODO PN write tests
  public void cache(ModelingLanguageModelLoader<? extends ASTNode> modelLoader, String calculatedModelName) {
    if (modelName2ModelLoaderCache.containsKey(calculatedModelName)) {
      modelName2ModelLoaderCache.get(calculatedModelName).add(modelLoader);
    }
    else {
      final Set<ModelingLanguageModelLoader<? extends ASTNode>> ml = new LinkedHashSet<>();
      ml.add(modelLoader);
      modelName2ModelLoaderCache.put(calculatedModelName, ml);
    }
  }

  /**
   * Adapted resolving filters search for a source symbol kind and adapt it
   * to a symbol of the target kind. E.g., Class -> State means, search for
   * a class symbol and adapt it to a state symbol. In that case, the model
   * name calculator must know the kind of the source symbol (i.e., class symbol).
   *
   * @param kind
   * @param resolvingFilter
   * @return
   */
  private SymbolKind getSymbolKindByResolvingFilter(SymbolKind kind, ResolvingFilter<? extends Symbol> resolvingFilter) {
    SymbolKind kindForCalc;
    if (resolvingFilter instanceof AdaptedResolvingFilter) {
      kindForCalc = ((AdaptedResolvingFilter) resolvingFilter).getSourceKind();
    }
    else {
      kindForCalc = kind;
    }
    return kindForCalc;
  }

  /**
   * // TODO PN update doc. Seems not to be fully correct
   *
   * Only if the model name differs from the symbol name, we need to proceed, since we
   * already handled the symbol name. For example, for class diagrams the symbol name is a.CD.Person
   * but the model name is a.CD. In contrast, the model name of java.lang.String is also
   * java.lang.String.
   *
   * @return true, if it should be continued with the model loader
   */
  protected boolean continueWithModelLoader(final String calculatedModelName, final ModelingLanguageModelLoader<? extends ASTNode> modelLoader) {
    return !modelName2ModelLoaderCache.containsKey(calculatedModelName)
        || !modelName2ModelLoaderCache.get(calculatedModelName).contains(modelLoader);
  }

  @Override
  protected boolean checkIfContinueAsSubScope(String symbolName, SymbolKind kind) {
    return false;
  }
}
