/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2016, MontiCore, All rights reserved.
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

import de.monticore.CommonModelingLanguage;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class EssentialMontiCoreGrammarLanguage extends CommonModelingLanguage {

  public static final String FILE_ENDING = "mc4";

  private final Grammar_WithConceptsPrettyPrinter prettyPrinter;

  public EssentialMontiCoreGrammarLanguage() {
    super("Essential Grammar Language", FILE_ENDING);
    
    prettyPrinter = new Grammar_WithConceptsPrettyPrinter(new IndentPrinter());

    addResolver(CommonResolvingFilter.create(MontiCoreGrammarSymbol.KIND));
    addResolver(CommonResolvingFilter.create(MCProdSymbol.KIND));
    addResolver(CommonResolvingFilter.create(MCProdComponentSymbol.KIND));
  }
  
  @Override
  public MCConcreteParser getParser() {
    return new Grammar_WithConceptsParser();
  }
  
  @Override
  public Optional<EssentialMontiCoreGrammarSymbolTableCreator> getSymbolTableCreator(
      ResolverConfiguration resolverConfiguration, @Nullable MutableScope enclosingScope) {
    return Optional.of(new EssentialMontiCoreGrammarSymbolTableCreator(
        resolverConfiguration, enclosingScope, prettyPrinter));
  }

  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new EssentialMontiCoreGrammarModelLoader(this);
  }
}
