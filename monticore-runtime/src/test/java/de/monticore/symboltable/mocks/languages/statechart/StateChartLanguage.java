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

package de.monticore.symboltable.mocks.languages.statechart;

import java.util.Optional;

import de.monticore.CommonModelingLanguage;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.se_rwth.commons.logging.Log;

public class StateChartLanguage extends CommonModelingLanguage {
  
  public StateChartLanguage() {
    super("StateChart Language Mock", "sc", StateChartSymbol.KIND);
    
    addResolver(CommonResolvingFilter.create(StateChartSymbol.class, StateChartSymbol.KIND));
    addResolver(CommonResolvingFilter.create(StateSymbol.class, StateSymbol.KIND));

  }
  
  @Override
  public MCConcreteParser getParser() {
    return new StateChartParserMock();
  }


  @Override
  public Optional<StateChartLanguageSymbolTableCreator> getSymbolTableCreator(
      final ResolverConfiguration resolverConfiguration, final MutableScope enclosingScope) {
    return Optional.of(new CommonStateChartSymbolTableCreator(Log.errorIfNull(resolverConfiguration),
        Log.errorIfNull(enclosingScope)));
  }

  @Override
  public StateChartLanguageModelLoader getModelLoader() {
    return (StateChartLanguageModelLoader) super.getModelLoader();
  }

  @Override
  protected StateChartLanguageModelLoader provideModelLoader() {
    return new StateChartLanguageModelLoader(this);
  }
}
