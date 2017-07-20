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

package de.monticore.grammar.symboltable;

import java.util.Optional;

import javax.annotation.Nullable;

import de.monticore.CommonModelingLanguage;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class MontiCoreGrammarLanguage extends CommonModelingLanguage {

  public static final String FILE_ENDING = "mc4";

  private final Grammar_WithConceptsPrettyPrinter prettyPrinter;

  public MontiCoreGrammarLanguage() {
    super("Essential Grammar Language", FILE_ENDING);
    
    prettyPrinter = new Grammar_WithConceptsPrettyPrinter(new IndentPrinter());

    addResolvingFilter(CommonResolvingFilter.create(MCGrammarSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(MCProdSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(MCProdComponentSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(MCProdAttributeSymbol.KIND));
  }
  
  @Override
  public MCConcreteParser getParser() {
    return new Grammar_WithConceptsParser();
  }
  
  @Override
  public Optional<MontiCoreGrammarSymbolTableCreator> getSymbolTableCreator(
      ResolvingConfiguration resolvingConfiguration, @Nullable MutableScope enclosingScope) {
    return Optional.of(new MontiCoreGrammarSymbolTableCreator(
        resolvingConfiguration, enclosingScope, prettyPrinter));
  }

  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new MontiCoreGrammarModelLoader(this);
  }
}
