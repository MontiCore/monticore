/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import com.google.common.collect.ImmutableList;
import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.singletonList;

/**
 * @author Pedram Mir Nazari
 */
public abstract class EmbeddingModelingLanguage extends CommonModelingLanguage {

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
   * @see CommonModelingLanguage#getResolvingFilters()
   */
  @Override
  public Collection<ResolvingFilter<? extends Symbol>> getResolvingFilters() {
    final Set<ResolvingFilter<? extends Symbol>> resolvingFilters = new LinkedHashSet<>();

    // resolving filters directly stored in this (composite) language, e.g., adapters.
    resolvingFilters.addAll(super.getResolvingFilters());

    // Resolving filters of the host language and the embedded language are added
    resolvingFilters.addAll(hostLanguage.getResolvingFilters());

    // If further embedded languages exists, their resolving filters are added too.
    for (ModelingLanguage language : embeddedLanguages) {
      resolvingFilters.addAll(language.getResolvingFilters());
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
