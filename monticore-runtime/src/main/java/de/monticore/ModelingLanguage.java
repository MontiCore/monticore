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

package de.monticore;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolTableCreator;
import de.monticore.symboltable.resolving.ResolvingFilter;

import java.util.Collection;
import java.util.Optional;

/**
 * Super interface for languages. Provides access to language-related functionality,
 * like parsing, symbol table creation, model analysis and code generation.
 *
 * @author  Pedram Mir Seyed Nazari
 * 
 */
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
   * use {@link #getResolvingFilters()} instead
   */
  @Deprecated
  Collection<ResolvingFilter<? extends Symbol>> getResolvers();

  /**
   * @return default resolvering filters for this language
   */
  Collection<ResolvingFilter<? extends Symbol>> getResolvingFilters();

  /**
   *
   *
   * @param resolvingConfiguration the {@link ResolvingConfiguration}
   * @param enclosingScope the enclosing scope of the top level symbol's spanned scope. In other
   *                       words, the scope in which the top level symbol should be defined.
   * @return the {@link de.monticore.symboltable.CommonSymbolTableCreator} for this language.
   */
  // TODO PN change to mandatory
  Optional<? extends SymbolTableCreator> getSymbolTableCreator
  (ResolvingConfiguration resolvingConfiguration, MutableScope enclosingScope);

  ModelingLanguageModelLoader<? extends ASTNode> getModelLoader();

  ModelNameCalculator getModelNameCalculator();

}
