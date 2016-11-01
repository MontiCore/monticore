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

import de.monticore.CommonModelingLanguage;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.languages.grammar.visitors.MCGrammarSymbolTableCreator;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;

import javax.annotation.Nullable;
import java.util.Optional;

public class MontiCoreGrammarLanguage extends CommonModelingLanguage {
  
  public static final String FILE_ENDING = "mc4";
  
  private final Grammar_WithConceptsPrettyPrinter prettyPrinter;
  
  public MontiCoreGrammarLanguage() {
    super("Grammar Language", FILE_ENDING);
    
    prettyPrinter = new Grammar_WithConceptsPrettyPrinter(new IndentPrinter());

    addResolver(new MCGrammarResolvingFilter<>(MCGrammarSymbol.KIND));
    addResolver(new MCGrammarResolvingFilter<>(MCTypeSymbol.KIND));
    addResolver(new MCGrammarResolvingFilter<>(MCAttributeSymbol.KIND));
    addResolver(new MCGrammarResolvingFilter<>(MCRuleSymbol.KIND));
    addResolver(new MCGrammarResolvingFilter<>(MCRuleComponentSymbol.KIND));
  }
  
  @Override
  public MCConcreteParser getParser() {
    return new Grammar_WithConceptsParser();
  }
  
  @Override
  public Optional<MCGrammarSymbolTableCreator> getSymbolTableCreator(
      ResolvingConfiguration resolvingConfiguration, @Nullable MutableScope enclosingScope) {
    return Optional.of(new MCGrammarSymbolTableCreator(resolvingConfiguration, enclosingScope,
        prettyPrinter));
  }

  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new MontiCoreGrammarModelLoader(this);
  }
}
