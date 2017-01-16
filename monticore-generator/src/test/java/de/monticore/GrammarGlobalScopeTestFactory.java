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

package de.monticore;

import java.nio.file.Paths;

import de.monticore.grammar.symboltable.MontiCoreGrammarLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolverConfiguration;

public class GrammarGlobalScopeTestFactory {

  public static GlobalScope create() {
    return create(new MontiCoreGrammarLanguage());
  }

  public static GlobalScope createUsingEssentialMCLanguage() {
    return create(new MontiCoreGrammarLanguage());
  }


  private static GlobalScope create(ModelingLanguage grammarLanguage) {
    final ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addDefaultFilters(grammarLanguage.getResolvers());

    return  new GlobalScope(new ModelPath(Paths.get("src/test/resources")),
        grammarLanguage, resolverConfiguration);
  }

}
