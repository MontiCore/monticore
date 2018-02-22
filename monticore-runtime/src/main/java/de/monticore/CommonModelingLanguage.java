/* (c) https://github.com/MontiCore/monticore */

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
  public Collection<ResolvingFilter<? extends Symbol>> getResolvingFilters() {
    return ImmutableList.copyOf(resolvingFilters);
  }

  public void addResolvingFilter(final ResolvingFilter<? extends Symbol> resolvingFilter) {
    resolvingFilters.add(resolvingFilter);
  }

  public void addResolvingFilters(final Collection<ResolvingFilter<? extends Symbol>> resolvingFilters) {
    this.resolvingFilters.addAll(resolvingFilters);
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
