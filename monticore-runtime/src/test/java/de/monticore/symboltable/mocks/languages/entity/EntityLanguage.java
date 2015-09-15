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

package de.monticore.symboltable.mocks.languages.entity;

import javax.annotation.Nullable;
import java.util.Optional;

import de.monticore.CommonModelingLanguage;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

public class EntityLanguage extends CommonModelingLanguage {
  
  public static final String FILE_ENDING = "cla";
  
  public EntityLanguage() {
    super("Entity Language Mock", FILE_ENDING, EntitySymbol.KIND);
    
    addResolver(CommonResolvingFilter.create(EntitySymbol.class, EntitySymbol.KIND));
    addResolver(CommonResolvingFilter.create(ActionSymbol.class, ActionSymbol.KIND));
    addResolver(CommonResolvingFilter.create(PropertySymbol.class, PropertySymbol.KIND));
  }

  @Override
  public de.monticore.antlr4.MCConcreteParser getParser() {
    return new EntityParserMock();
  }
  
  @Override
  public Optional<EntityLanguageSymbolTableCreator> getSymbolTableCreator(
      ResolverConfiguration resolverConfiguration, @Nullable MutableScope enclosingScope) {
    return Optional.of(new CommonEntityLanguageSymbolTableCreator(resolverConfiguration, enclosingScope));
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
