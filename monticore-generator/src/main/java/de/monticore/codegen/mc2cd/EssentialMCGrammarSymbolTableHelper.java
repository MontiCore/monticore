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
import de.monticore.grammar.symboltable.EssentialMCGrammarSymbol;
import de.monticore.grammar.symboltable.EssentialMontiCoreGrammarLanguage;
import de.monticore.grammar.symboltable.EssentialMontiCoreGrammarSymbolTableCreator;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.Util;


public class EssentialMCGrammarSymbolTableHelper {

  public static void initializeSymbolTable(ASTMCGrammar rootNode, ModelPath modelPath) {
    ModelingLanguage grammarLanguage = new EssentialMontiCoreGrammarLanguage();

    ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addDefaultFilters(grammarLanguage.getResolvers());

    Grammar_WithConceptsPrettyPrinter prettyPrinter = new Grammar_WithConceptsPrettyPrinter(new IndentPrinter());

    MutableScope globalScope = new GlobalScope(modelPath, grammarLanguage, resolverConfiguration);
    EssentialMontiCoreGrammarSymbolTableCreator symbolTableCreator = new EssentialMontiCoreGrammarSymbolTableCreator
        (resolverConfiguration, globalScope, prettyPrinter);

    // Create Symbol Table
    symbolTableCreator.createFromAST(rootNode);
  }

  public static Optional<MCProdSymbol> resolveRule(ASTNode astNode, String name) {
    Optional<EssentialMCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(astNode);
    if (!grammarSymbol.isPresent()) {
      return Optional.empty();
    }
    return grammarSymbol.get().getProdWithInherited(name);
  }

  public static Optional<MCProdSymbol> resolveRuleInSupersOnly(ASTNode astNode, String name) {
    Optional<EssentialMCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(astNode);
    Stream<EssentialMCGrammarSymbol> superGrammars = grammarSymbol
        .map(symbol -> Util.preOrder(symbol, EssentialMCGrammarSymbol::getSuperGrammarSymbols).stream())
        .orElse(Stream.empty()).skip(1);
    return superGrammars.map(superGrammar -> superGrammar.getProd(name))
        .filter(mcRuleSymbol -> mcRuleSymbol.isPresent())
        .map(Optional::get)
        .findFirst();
  }

  public static Optional<EssentialMCGrammarSymbol> getMCGrammarSymbol(ASTNode astNode) {
    return getAllScopes(astNode).stream()
        .map(Scope::getSpanningSymbol)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(EssentialMCGrammarSymbol.class::isInstance)
        .map(EssentialMCGrammarSymbol.class::cast)
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
