/*
 * ******************************************************************************
 * MontiCore Language Workbench
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

package de.monticore.symboltable.mocks.languages.statechart;

import de.monticore.CommonModelingLanguage;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class StateChartLanguage extends CommonModelingLanguage {
  
  public StateChartLanguage() {
    super("StateChart Language Mock", "sc", StateChartSymbol.KIND);
    
    addResolver(CommonResolvingFilter.create(StateChartSymbol.KIND));
    addResolver(CommonResolvingFilter.create(StateSymbol.KIND));

  }
  
  @Override
  public MCConcreteParser getParser() {
    return new StateChartParserMock();
  }


  @Override
  public Optional<StateChartLanguageSymbolTableCreator> getSymbolTableCreator(
      final ResolvingConfiguration resolvingConfiguration, final MutableScope enclosingScope) {
    return Optional.of(new CommonStateChartSymbolTableCreator(Log.errorIfNull(resolvingConfiguration),
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
