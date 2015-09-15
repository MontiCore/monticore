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

package de.monticore.codegen.mc2cd;

import static de.se_rwth.commons.Util.listTillNull;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.monticore.ModelingLanguage;
import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.io.paths.ModelPath;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.languages.grammar.MontiCoreGrammarLanguage;
import de.monticore.languages.grammar.visitors.MCGrammarSymbolTableCreator;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.Util;


public class MCGrammarSymbolTableHelper {

  public static void initializeSymbolTable(ASTMCGrammar rootNode, ModelPath modelPath) {
    ModelingLanguage grammarLanguage = new MontiCoreGrammarLanguage();

    ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addTopScopeResolvers(grammarLanguage.getResolvers());

    Grammar_WithConceptsPrettyPrinter prettyPrinter = new Grammar_WithConceptsPrettyPrinter(new IndentPrinter());

    MutableScope globalScope = new GlobalScope(modelPath, grammarLanguage.getModelLoader(),
        resolverConfiguration);
    MCGrammarSymbolTableCreator symbolTableCreator = new MCGrammarSymbolTableCreator
        (resolverConfiguration, globalScope, prettyPrinter);

    // Create Symbol Table
    symbolTableCreator.createFromAST(rootNode);
  }

  public static Optional<MCRuleSymbol> resolveRule(ASTNode astNode, String name) {
    return getMCGrammarSymbol(astNode)
        .map(mcGrammarSymbol -> mcGrammarSymbol.getRuleWithInherited(name));
  }

  public static Optional<MCRuleSymbol> resolveRuleInSupersOnly(ASTNode astNode, String name) {
    Optional<MCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(astNode);
    Stream<MCGrammarSymbol> superGrammars = grammarSymbol
        .map(symbol -> Util.preOrder(symbol, MCGrammarSymbol::getSuperGrammars).stream())
        .orElse(Stream.empty()).skip(1);
    return superGrammars.map(superGrammar -> superGrammar.getRule(name))
        .filter(mcRuleSymbol -> mcRuleSymbol != null)
        .findFirst();
  }

  public static Optional<MCGrammarSymbol> getMCGrammarSymbol(ASTNode astNode) {
    return getAllScopes(astNode).stream()
        .map(Scope::getSpanningSymbol)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(MCGrammarSymbol.class::isInstance)
        .map(MCGrammarSymbol.class::cast)
        .findFirst();
  }

  private static Set<Scope> getAllScopes(ASTNode astNode) {
    return getAllSubSymbols(astNode).stream()
        .map(Symbol::getEnclosingScope)
        .flatMap(scope ->
            listTillNull(scope, childScope -> childScope.getEnclosingScope().orElse(null)).stream())
        .collect(Collectors.toSet());
  }

  private static Set<Symbol> getAllSubSymbols(ASTNode astNode) {
    return Util.preOrder(astNode, ASTNode::get_Children).stream()
        .map(ASTNode::getSymbol)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());
  }
}
