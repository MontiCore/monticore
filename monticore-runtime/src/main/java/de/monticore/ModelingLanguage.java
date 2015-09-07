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

import java.util.Collection;
import java.util.Optional;

import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolTableCreator;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;

/**
 * Super interface for languages. Provides access to language-related functionality,
 * like parsing, symbol table creation, model analysis and code generation.
 *
 * @author  Pedram Mir Seyed Nazari
 * 
 */
// TODO PN die einzelnen Komponenten wie Parser, SymbolTableCreator etc. sind alle Stateful. Daher sollte die ModelingLanguage immer eine neue Instanz zurück geben...
// TODO PN ...Das hat ggf. Einfluss auf das Design dieser Komponenten, z.B. kann der AST direkt
// TODO PN ...dem Konstruktor des STCreators übergeben werden
// TODO PN DIE oberen Punkte dokumentieren
public interface ModelingLanguage {

  /**
   * @return the name of the modeling language, e.g., "MontiCore Grammar Language"
   */
  String getName();

  /**
   * @return the file ending, e.g., ".cd"
   */
  String getFileExtension();

  /**
   * @return the parser for models of this language
   */
  MCConcreteParser getParser();

  /**
   * @return default resolvers for this language
   */
  // TODO PN rename to getResolvingFilters
  Collection<ResolvingFilter<? extends Symbol>> getResolvers();

  /**
   *
   *
   * @param resolverConfiguration the {@link ResolverConfiguration}
   * @param enclosingScope the enclosing scope of the top level symbol's spanned scope. In other
   *                       words, the scope in which the top level symbol should be defined.
   * @return the {@link de.monticore.symboltable.CommonSymbolTableCreator} for this language.
   */
  Optional<? extends SymbolTableCreator> getSymbolTableCreator
  (ResolverConfiguration resolverConfiguration, MutableScope enclosingScope);

  ModelingLanguageModelLoader<? extends ASTNode> getModelLoader();

  ModelNameCalculator getModelNameCalculator();

}
