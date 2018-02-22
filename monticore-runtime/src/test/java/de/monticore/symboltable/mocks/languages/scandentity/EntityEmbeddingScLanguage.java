/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.CommonModelingLanguage;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.entity.EntityLanguage;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.PropertySymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateSymbol;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class EntityEmbeddingScLanguage extends CommonModelingLanguage {
  
  public static final String FILE_ENDING = EntityLanguage.FILE_ENDING;
  
  private MCConcreteParser parser;
  
  public EntityEmbeddingScLanguage() {
    super("Entity with embedded Statechart language", FILE_ENDING);
    
    // add default resolvers of the entity language
    addResolvingFilter(CommonResolvingFilter.create(EntitySymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(ActionSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(PropertySymbol.KIND));
    
    // add default resolvers of the statechart language
    addResolvingFilter(CommonResolvingFilter.create(StateChartSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(StateSymbol.KIND));
    
    // add sc2entity resolvers
    addResolvingFilter(new Sc2EntityTransitiveResolvingFilter());
  }
  
  @Override
  public MCConcreteParser getParser() {
    return parser;
  }
  
  @Override
  public Optional<CompositeScAndEntitySymbolTableCreator> getSymbolTableCreator(ResolvingConfiguration resolvingConfiguration, MutableScope enclosingScope) {
    return Optional.of(new CompositeScAndEntitySymbolTableCreator(resolvingConfiguration,
        enclosingScope));
  }
  
  // used for testing
  public void setParser(final MCConcreteParser parser) {
    this.parser = Log.errorIfNull(parser);
  }

  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new EntityEmbeddingScModelLoader(this);
  }
}
