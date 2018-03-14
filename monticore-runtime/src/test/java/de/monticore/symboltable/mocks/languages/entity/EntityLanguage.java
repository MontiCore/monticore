/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import de.monticore.CommonModelingLanguage;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

import javax.annotation.Nullable;
import java.util.Optional;

public class EntityLanguage extends CommonModelingLanguage {
  
  public static final String FILE_ENDING = "cla";
  
  public EntityLanguage() {
    super("Entity Language Mock", FILE_ENDING);

    addResolvingFilter(CommonResolvingFilter.create(EntitySymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(ActionSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(PropertySymbol.KIND));
  }

  @Override
  public de.monticore.antlr4.MCConcreteParser getParser() {
    return new EntityParserMock();
  }
  
  @Override
  public Optional<EntityLanguageSymbolTableCreator> getSymbolTableCreator(
      ResolvingConfiguration resolvingConfiguration, @Nullable MutableScope enclosingScope) {
    return Optional.of(new CommonEntityLanguageSymbolTableCreator(resolvingConfiguration, enclosingScope));
  }
  
  @Override
  public EntityLanguageModelLoader getModelLoader() {
    return (EntityLanguageModelLoader) super.getModelLoader();
  }

  @Override
  protected EntityLanguageModelLoader provideModelLoader() {
    return new EntityLanguageModelLoader(this);
  }
}
