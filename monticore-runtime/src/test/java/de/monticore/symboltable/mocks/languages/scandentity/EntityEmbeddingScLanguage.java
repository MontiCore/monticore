/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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
    addResolver(CommonResolvingFilter.create(EntitySymbol.KIND));
    addResolver(CommonResolvingFilter.create(ActionSymbol.KIND));
    addResolver(CommonResolvingFilter.create(PropertySymbol.KIND));
    
    // add default resolvers of the statechart language
    addResolver(CommonResolvingFilter.create(StateChartSymbol.KIND));
    addResolver(CommonResolvingFilter.create(StateSymbol.KIND));
    
    // add sc2entity resolvers
    addResolver(new Sc2EntityTransitiveResolvingFilter());
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
