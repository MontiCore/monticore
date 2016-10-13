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

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import de.monticore.ModelingLanguage;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.grammar._ast.ASTLexActionOrPredicate;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.grammar.symboltable.EssentialMCGrammarSymbol;
import de.monticore.grammar.symboltable.EssentialMontiCoreGrammarLanguage;
import de.monticore.grammar.symboltable.EssentialMontiCoreGrammarSymbolTableCreator;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.languages.grammar.lexpatterns.RegExpBuilder;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.Util;
import de.se_rwth.commons.logging.Log;

public class EssentialMCGrammarSymbolTableHelper {
  
  public static void initializeSymbolTable(ASTMCGrammar rootNode, ModelPath modelPath) {
    ModelingLanguage grammarLanguage = new EssentialMontiCoreGrammarLanguage();
    
    ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addDefaultFilters(grammarLanguage.getResolvers());
    
    Grammar_WithConceptsPrettyPrinter prettyPrinter = new Grammar_WithConceptsPrettyPrinter(
        new IndentPrinter());
    
    MutableScope globalScope = new GlobalScope(modelPath, grammarLanguage, resolverConfiguration);
    EssentialMontiCoreGrammarSymbolTableCreator symbolTableCreator = new EssentialMontiCoreGrammarSymbolTableCreator(
        resolverConfiguration, globalScope, prettyPrinter);
    
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
        .map(symbol -> Util.preOrder(symbol, EssentialMCGrammarSymbol::getSuperGrammarSymbols)
            .stream())
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
  
  public static Optional<EssentialMCGrammarSymbol> getMCGrammarSymbol(
      Optional<MCProdSymbol> symbol) {
    return symbol.isPresent()
        ? symbol.get().getSpannedScope().getSpanningSymbol()
            .filter(EssentialMCGrammarSymbol.class::isInstance)
            .map(EssentialMCGrammarSymbol.class::cast)
        : Optional.empty();
  }
  
  public static Optional<MCProdSymbol> getEnclosingRule(ASTNode astNode) {
    return getAllScopes(astNode).stream()
        .map(Scope::getSpanningSymbol)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(MCProdSymbol.class::isInstance)
        .map(MCProdSymbol.class::cast)
        .findFirst();
  }
  
  public static Optional<MCProdSymbol> getEnclosingRule(MCProdComponentSymbol prod) {
    return prod.getEnclosingScope().getSpanningSymbol().filter(MCProdSymbol.class::isInstance)
        .map(MCProdSymbol.class::cast);
  }
  
  /**
   * Returns a set of all super grammars of the given grammar (transitively)
   *
   * @return
   */
  public static Set<EssentialMCGrammarSymbol> getAllSuperGrammars(
      EssentialMCGrammarSymbol grammarSymbol) {
    Set<EssentialMCGrammarSymbol> allSuperGrammars = new LinkedHashSet<>();
    Set<EssentialMCGrammarSymbol> tmpList = new LinkedHashSet<>();
    allSuperGrammars.addAll(grammarSymbol.getSuperGrammarSymbols());
    boolean modified = false;
    do {
      for (EssentialMCGrammarSymbol curGrammar : allSuperGrammars) {
        tmpList.addAll(curGrammar.getSuperGrammarSymbols());
      }
      modified = allSuperGrammars.addAll(tmpList);
      tmpList.clear();
    } while (modified);
    
    return ImmutableSet.copyOf(allSuperGrammars);
  }
  
  public static boolean isFragment(Optional<ASTNode> astNode) {
    return !astNode.isPresent() || !(astNode.get() instanceof ASTLexProd)
        || ((ASTLexProd) astNode.get()).isFragment();
  }
  
  private static Set<Scope> getAllScopes(ASTNode astNode) {
    return getAllSubSymbols(astNode).stream()
        .map(Symbol::getEnclosingScope)
        .flatMap(
            scope -> listTillNull(scope, childScope -> childScope.getEnclosingScope().orElse(null))
                .stream())
        .collect(Collectors.toSet());
  }
  
  private static String getLexString(EssentialMCGrammarSymbol grammar, ASTLexProd lexNode) {
    StringBuilder builder = new StringBuilder();
    RegExpBuilder regExp = new RegExpBuilder(builder, grammar);
    regExp.handle(lexNode);
    return builder.toString();
  }
  
  public static Optional<Pattern> calculateLexPattern(EssentialMCGrammarSymbol grammar,
      Optional<ASTNode> lexNode) {
    Optional<Pattern> ret = Optional.empty();
    
    if (!lexNode.isPresent() || !(lexNode.get() instanceof ASTLexProd)) {
      return ret;
    }
    final String lexString = getLexString(grammar, (ASTLexProd) lexNode.get());
    try {
      if ("[[]".equals(lexString)) {
        return Optional.ofNullable(Pattern.compile("[\\[]"));
      }
      else {
        return Optional.ofNullable(Pattern.compile(lexString));
      }
    }
    catch (PatternSyntaxException e) {
      Log.error("0xA0913 Internal error with pattern handling for lex rules. Pattern: " + lexString
          + "\n", e);
    }
    return ret;
  }
  
  private static Set<Symbol> getAllSubSymbols(ASTNode astNode) {
    return Util.preOrder(astNode, ASTNode::get_Children).stream()
        .map(ASTNode::getSymbol)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());
  }
  
  // TODO GV:
  /**
   * Returns the STType associated with this name Use "super." as a prefix to
   * explicitly access the type of supergrammars this grammar overriddes.
   * Native/external types are types that are not defined in the grammar but are
   * refered from it. These types are indicated by the suffix "/" in the grammar
   * and refer to regular Java types. To access these type use the prefix "/"
   * e.g. "/String" or "/int"
   *
   * @param name Name of the type
   * @return Symboltable entry for this type
   */
  public static Optional<MCProdSymbol> getTypeWithInherited(String name,
      EssentialMCGrammarSymbol gramamrSymbol) {
    Optional<MCProdSymbol> ret = Optional.empty();
    if (name.startsWith("super.")) {
      name = name.substring(6);
    }
    else {
      ret = gramamrSymbol.getProd(name);
    }
    
    if (!ret.isPresent()) {
      ret = gramamrSymbol.getProdWithInherited(name);
    }
    return ret;
  }
  
  /**
   * @return the qualified name for this type
   */
  // TODO GV: change implementation
  public static String getQualifiedName(MCProdSymbol symbol) {
    if (!symbol.getAstNode().isPresent()) {
      return "UNKNOWN_TYPE";
    }
    if (symbol.isLexerProd()) {
      return getLexType(symbol.getAstNode());
    }
    if (symbol.isEnum()) {
      return "int";
      //TODO GV:
      //return getConstantType();
    }
    return getQualifiedName(symbol.getAstNode().get(), symbol, GeneratorHelper.AST_PREFIX, "");
  }
  
  private static String getLexType(Optional<ASTNode> node) {
    if (node.isPresent()) {
      if (node.get() instanceof ASTLexProd) {
        return HelperGrammar.createConvertType((ASTLexProd) node.get());
      }
      if (node.get() instanceof ASTLexActionOrPredicate) {
        return "String";
      }
    }
    return "UNKNOWN_TYPE";
    
  }
  
  public static String getQualifiedName(ASTNode astNode, MCProdSymbol symbol, String prefix,
      String suffix) {
    if (symbol.isExternal()) {
      return symbol.getName();
    }
    else {
      Optional<EssentialMCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(astNode);
      String string = grammarSymbol.isPresent()
          ? grammarSymbol.get().getFullName().toLowerCase()
          : ""
              + GeneratorHelper.AST_PACKAGE_SUFFIX_DOT + "." + prefix +
              StringTransformations.capitalize(symbol.getName() + suffix);
      
      if (string.startsWith(".")) {
        string = string.substring(1);
      }
      return string;
    }
  }
  
  public static String getDefaultValue(MCProdSymbol symbol) {
    String name = getQualifiedName(symbol);
    if ("int".equals(name)) {
      return "0";
    }
    if ("boolean".equals(name)) {
      return "false";
    }
    return "null";
  }
  
//  public String getListType() {
//    if (isASTNode()) {
//      return getQualifiedName() + "List";
//    }
//    else {
//      return "java.util.List<" + getQualifiedName() + ">";
//    }
//  }
  
//  private String getConstantType() {
//    return (this.getEnumValues().size() > 1) ? "int" : "boolean";
//  }
  
}
