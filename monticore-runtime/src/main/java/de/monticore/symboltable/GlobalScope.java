/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Pedram Mir Seyed Nazari
 */
public final class GlobalScope extends CommonScope {

  private final ModelPath modelPath;
  private final ResolvingConfiguration resolvingConfiguration;

  private final Set<ModelingLanguage> modelingLanguages = new LinkedHashSet<>();

  private final Map<String, Set<ModelingLanguageModelLoader<? extends ASTNode>>> modelName2ModelLoaderCache = new HashMap<>();

  public GlobalScope(final ModelPath modelPath, final Collection <ModelingLanguage> modelingLanguages,
      final ResolvingConfiguration resolvingConfiguration) {
    super(Optional.empty(), true);

    this.modelPath = Log.errorIfNull(modelPath);
    this.resolvingConfiguration = Log.errorIfNull(resolvingConfiguration);
    this.modelingLanguages.addAll(Log.errorIfNull(modelingLanguages));

    if (modelingLanguages.isEmpty()) {
      Log.warn(GlobalScope.class.getSimpleName() + ": 0xA1044 No model loaders defined. This hampers the "
          + "loading of models.");
    }

    setResolvingFilters(resolvingConfiguration.getDefaultFilters());
  }

  public GlobalScope(final ModelPath modelPath, final ModelingLanguage language,
      ResolvingConfiguration resolvingConfiguration) {
    this(modelPath, Collections.singletonList(language), resolvingConfiguration);
  }

  public GlobalScope(final ModelPath modelPath, final ModelingLanguage language) {
    this(modelPath, language, new ResolvingConfiguration());

    resolvingConfiguration.addDefaultFilters(language.getResolvingFilters());
    setResolvingFilters(resolvingConfiguration.getDefaultFilters());
  }

  public GlobalScope(final ModelPath modelPath, ModelingLanguageFamily languageFamily) {
    this(modelPath, languageFamily.getModelingLanguages(), new ResolvingConfiguration());

    resolvingConfiguration.addDefaultFilters(languageFamily.getAllResolvers());
    setResolvingFilters(resolvingConfiguration.getDefaultFilters());
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
    loadModels(resolvingInfo.getResolvingFilters(), symbolName, kind);

    // Maybe the symbol now exists in this scope (or its sub scopes). So, resolve down, again.
    resolvedSymbol = resolveDownMany(new ResolvingInfo(getResolvingFilters()), symbolName, kind, modifier, predicate);

    return resolvedSymbol;
  }

  protected void loadModels(final Collection<ResolvingFilter<? extends Symbol>> resolvingFilters,
      final String symbolName, final SymbolKind kind) {

    for (final ModelingLanguage lang : modelingLanguages) {
      final ModelNameCalculator modelNameCalculator = lang.getModelNameCalculator();
      final ModelingLanguageModelLoader<? extends ASTNode> modelLoader = lang.getModelLoader();

      final Set<SymbolKind> possibleSymbolKinds = calculatePossibleSymbolKinds(resolvingFilters, kind);

      for (final SymbolKind kindForCalc : possibleSymbolKinds) {
        final Set<String> calculatedModelNames = modelNameCalculator.calculateModelNames(symbolName, kindForCalc);

        for (String calculatedModelName : calculatedModelNames) {
          if (continueWithModelLoader(calculatedModelName, modelLoader)) {
            modelLoader.loadModelsIntoScope(calculatedModelName, modelPath, this, resolvingConfiguration);
            cache(modelLoader, calculatedModelNames.iterator().next());
          }
          else {
            Log.debug("Already tried to load model for '" + symbolName + "'. If model exists, continue with cached version.", GlobalScope.class.getSimpleName());
          }
        }
      }


    }
  }

  protected Set<SymbolKind> calculatePossibleSymbolKinds(Collection<ResolvingFilter<? extends Symbol>> resolvingFilters, SymbolKind kind) {
    final Collection<ResolvingFilter<? extends Symbol>> resolversForKind = ResolvingFilter
        .getFiltersForTargetKind(resolvingFilters, kind);

    return resolversForKind.stream()
        .map(resolvingFilter -> getSymbolKindByResolvingFilter(kind, resolvingFilter))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

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
   * Model loading continues with the given <code>modelLoader</code>, if
   * <code>calculatedModelName</code> has not been already loaded with that loader.
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
