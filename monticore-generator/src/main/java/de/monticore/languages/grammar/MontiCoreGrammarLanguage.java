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

package de.monticore.languages.grammar;

import java.util.Optional;

import javax.annotation.Nullable;

import de.monticore.CommonModelingLanguage;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParserFactory;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.languages.grammar.visitors.MCGrammarSymbolTableCreator;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

public class MontiCoreGrammarLanguage extends CommonModelingLanguage {
  
  public static final String FILE_ENDING = "mc4";
  
  private final Grammar_WithConceptsPrettyPrinter prettyPrinter;
  
  public MontiCoreGrammarLanguage() {
    super("Grammar Language", FILE_ENDING, MCGrammarSymbol.KIND);
    
    prettyPrinter = new Grammar_WithConceptsPrettyPrinter(new IndentPrinter());

    addResolver(new CommonResolvingFilter<>(MCGrammarSymbol.class, MCGrammarSymbol.KIND));
    addResolver(new CommonResolvingFilter<>(MCTypeSymbol.class, MCTypeSymbol.KIND));
    addResolver(new CommonResolvingFilter<>(MCAttributeSymbol.class, MCAttributeSymbol.KIND));
    addResolver(new CommonResolvingFilter<>(MCRuleSymbol.class, MCRuleSymbol.KIND));
    addResolver(new CommonResolvingFilter<>(MCRuleComponentSymbol.class, MCRuleComponentSymbol.KIND));
  }
  
  @Override
  public MCConcreteParser getParser() {
    return Grammar_WithConceptsParserFactory.createMCGrammarMCParser();
  }
  
  @Override
  public Optional<MCGrammarSymbolTableCreator> getSymbolTableCreator(
      ResolverConfiguration resolverConfiguration, @Nullable MutableScope enclosingScope) {
    return Optional.of(new MCGrammarSymbolTableCreator(resolverConfiguration, enclosingScope,
        prettyPrinter));
  }

  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new MontiCoreGrammarModelLoader(this);
  }
}
