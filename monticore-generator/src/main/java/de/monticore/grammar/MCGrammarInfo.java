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

package de.monticore.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrLexerAction;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrParserAction;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTJavaCodeExt;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._ast.ASTRuleComponent;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.grammar.symboltable.MCProdSymbolReference;
import de.monticore.utils.ASTNodes;
import de.se_rwth.commons.logging.Log;

/**
 * Contains information about a grammar which is required for the parser
 * generation
 */
public class MCGrammarInfo {
  
  /**
   * Keywords of the processed grammar and its super grammars
   */
  private Set<String> keywords = Sets.newLinkedHashSet();
  
  /**
   * Lexer patterns
   */
  private Map<MCGrammarSymbol, List<Pattern>> lexerPatterns = new HashMap<>();
  
  private Collection<String> leftRecursiveRules = new HashSet<>();
  
  /**
   * Additional java code for parser defined in antlr concepts of the processed
   * grammar and its super grammars
   */
  private List<String> additionalParserJavaCode = new ArrayList<String>();
  
  /**
   * Additional java code for lexer defined in antlr concepts of the processed
   * grammar and its super grammars
   */
  private List<String> additionalLexerJavaCode = new ArrayList<String>();
  
  /**
   * Predicates
   */
  private ArrayListMultimap<String, PredicatePair> predicats = ArrayListMultimap.create();
  
  /**
   * Internal: LexNamer for naming lexer symbols in the antlr source code
   */
  private LexNamer lexNamer = new LexNamer();
  
  /**
   * The symbol of the processed grammar
   */
  private MCGrammarSymbol grammarSymbol;
  
  public MCGrammarInfo(MCGrammarSymbol grammarSymbol) {
    this.grammarSymbol = grammarSymbol;
    buildLexPatterns();
    findAllKeywords();
    addSubRules();
    addSubRulesToInterface();
    addHWAntlrCode();
    addLeftRecursiveRules();
  }
  
  // ------------- Handling of the antlr concept -----------------------------
  
  /**
   * Add all sub/superule-relations to the symbol table form the perspective of
   * the super rule by using addSubrule
   *
   * @param classProds Rule
   */
  private void addSubRules() {
    Set<MCGrammarSymbol> grammarsToHandle = Sets
        .newLinkedHashSet(Arrays.asList(grammarSymbol));
    grammarsToHandle.addAll(MCGrammarSymbolTableHelper.getAllSuperGrammars(grammarSymbol));
    for (MCGrammarSymbol grammar : grammarsToHandle) {
      for (ASTClassProd classProd : ((ASTMCGrammar) grammar.getAstNode().get())
          .getClassProds()) {
        for (ASTRuleReference superRule : classProd.getSuperRule()) {
          Optional<MCProdSymbol> prodByName = grammarSymbol
              .getProdWithInherited(superRule.getTypeName());
          if (prodByName.isPresent()) {
            addSubrule(prodByName.get().getName(), HelperGrammar.getRuleName(classProd), superRule);
          }
          else {
            Log.error("0xA2110 Undefined rule: " + superRule.getTypeName(),
                superRule.get_SourcePositionStart());
          }
        }
        
        for (ASTRuleReference ruleref : classProd.getSuperInterfaceRule()) {
          Optional<MCProdSymbol> prodByName = grammarSymbol
              .getProdWithInherited(ruleref.getTypeName());
          if (prodByName.isPresent()) {
            addSubrule(prodByName.get().getName(), HelperGrammar.getRuleName(classProd), ruleref);
          }
          else {
            Log.error("0xA2111 Undefined rule: " + ruleref.getTypeName(),
                ruleref.get_SourcePositionStart());
          }
        }
      }
    }
    
  }
  
  /**
   * Add all sub/superule-realtions to the symboltable form the perspective of
   * the superrule by using addSubrule
   *
   * @param interfaceProdList Rule
   */
  private void addSubRulesToInterface() {
    Set<MCGrammarSymbol> grammarsToHandle = Sets
        .newLinkedHashSet(Arrays.asList(grammarSymbol));
    grammarsToHandle.addAll(MCGrammarSymbolTableHelper.getAllSuperGrammars(grammarSymbol));
    for (MCGrammarSymbol grammar : grammarsToHandle) {
      for (ASTInterfaceProd interfaceProd : ((ASTMCGrammar) grammar.getAstNode().get())
          .getInterfaceProds()) {
        for (ASTRuleReference superRule : interfaceProd.getSuperInterfaceRule()) {
          Optional<MCProdSymbol> prodByName = grammar
              .getProdWithInherited(superRule.getTypeName());
          if (prodByName.isPresent()) {
            addSubrule(prodByName.get().getName(), interfaceProd.getName(), superRule);
          }
          else {
            Log.error("0xA2111 Undefined rule: " + superRule.getTypeName(),
                superRule.get_SourcePositionStart());
          }
        }
      }
    }
  }
  
  private void addSubrule(String superrule, String subrule, ASTRuleReference ruleReference) {
    PredicatePair subclassPredicatePair = new PredicatePair(subrule, ruleReference);
    predicats.put(superrule, subclassPredicatePair);
  }
  

  private boolean isProdLeftRecursive(MCProdSymbol ruleByName, ASTClassProd ast) {
    List<ASTProd> superProds = GeneratorHelper.getAllSuperProds(ast);
    Collection<String> names = new ArrayList<>();
    superProds.forEach(s -> names.add(s.getName()));
    DirectLeftRecursionDetector detector = new DirectLeftRecursionDetector();
    for (ASTAlt alt : ast.getAlts()) {
      if (detector.isAlternativeLeftRecursive(alt, names)) {
        return true;
      }
    }
    return false;
  }
  
  private void addLeftRecursiveRules() {
    Set<MCGrammarSymbol> grammarsToHandle = Sets
        .newLinkedHashSet(Arrays.asList(grammarSymbol));
    grammarsToHandle.addAll(MCGrammarSymbolTableHelper.getAllSuperGrammars(grammarSymbol));
    Collection<MCProdSymbol> basicRecursiveRules = new HashSet<>();
    for (MCGrammarSymbol grammar : grammarsToHandle) {
      for (ASTClassProd classProd : ((ASTMCGrammar) grammar.getAstNode().get()).getClassProds()) {
        MCProdSymbol rule = (MCProdSymbol) classProd.getSymbol().get();
        if (isProdLeftRecursive((MCProdSymbol) classProd.getSymbol().get(), classProd)) {
          basicRecursiveRules.add(rule);
        }
      }
      
      for (MCProdSymbol rule: basicRecursiveRules) {
        leftRecursiveRules.add(rule.getName());
        rule.getSuperInterfaceProds().stream().forEach(s -> leftRecursiveRules.add(s.getName()));
        rule.getSuperProds().stream().forEach(s -> leftRecursiveRules.add(s.getName()));
      }
    }
    
  }
  
  /**
   * @return grammarSymbol
   */
  public MCGrammarSymbol getGrammarSymbol() {
    return this.grammarSymbol;
  }
  
  /**
   * @param grammarSymbol the grammarSymbol to set
   */
  public void setGrammarSymbol(MCGrammarSymbol grammarSymbol) {
    this.grammarSymbol = grammarSymbol;
  }
  
  /**
   * @return java code
   */
  public List<String> getAdditionalParserJavaCode() {
    return this.additionalParserJavaCode;
  }
  
  /**
   * @return java code
   */
  public List<String> getAdditionalLexerJavaCode() {
    return this.additionalLexerJavaCode;
  }
  
  private void addHWAntlrCode() {
    // Get Antlr hwc
    Set<MCGrammarSymbol> grammarsToHandle = Sets
        .newLinkedHashSet(Arrays.asList(grammarSymbol));
    grammarsToHandle.addAll(MCGrammarSymbolTableHelper.getAllSuperGrammars(grammarSymbol));
    for (MCGrammarSymbol grammar : grammarsToHandle) {
      if (grammar.getAstNode().isPresent()) {
        // Add additional java code for lexer and parser
        ASTNodes.getSuccessors(grammar.getAstNode().get(), ASTAntlrParserAction.class).forEach(
            a -> addAdditionalParserJavaCode(a.getText()));
        ASTNodes.getSuccessors(grammar.getAstNode().get(), ASTAntlrLexerAction.class).forEach(
            a -> addAdditionalLexerJavaCode(a.getText()));
      }
    }
  }
  
  /**
   * @param action the java code to add
   */
  private void addAdditionalParserJavaCode(ASTJavaCodeExt action) {
    additionalParserJavaCode.add(ParserGeneratorHelper.getText(action));
  }
  
  /**
   * @param action the java code to add
   */
  private void addAdditionalLexerJavaCode(ASTJavaCodeExt action) {
    additionalLexerJavaCode.add(ParserGeneratorHelper.getText(action));
  }
  
  // ------------- Handling of keywords -----------------------------
  
  public Set<String> getKeywords() {
    return Collections.unmodifiableSet(keywords);
  }
  
  /**
   * Checks if the terminal or constant <code>name</code> is a and has to be
   * defined in the parser.
   * 
   * @param name - rule to check
   * @return true, if the terminal or constant <code>name</code> is a and has to
   * be defined in the parser.
   */
  public boolean isKeyword(String name, MCGrammarSymbol grammar) {
    boolean matches = false;
    boolean found = false;
    
    // Check with options
    if (mustBeKeyword(name, grammar)) {
      matches = true;
      found = true;
    }
    
    // Automatically detect if not specified
    if (!found && lexerPatterns.containsKey(grammar)) {
      for (Pattern p : lexerPatterns.get(grammar)) {
        
        if (p.matcher(name).matches()) {
          matches = true;
          Log.debug(name + " is considered as a keyword because it matches " + p + " "
              + "(grammarsymtab)", MCGrammarSymbol.class.getSimpleName());
          break;
        }
        
      }
    }
    
    return matches;
  }
  
  public boolean isProdLeftRecursive(String name) {
    return leftRecursiveRules.contains(name);
  }
  
  public List<PredicatePair> getSubRulesForParsing(String ruleName) {
    // Consider superclass
    Optional<MCProdSymbol> ruleByName = grammarSymbol.getProdWithInherited(ruleName);
    List<PredicatePair> predicateList = Lists.newArrayList();
    if (!ruleByName.isPresent()) {
      return predicateList;
    }
    
    if (predicats.containsKey(ruleName)) {
      predicateList.addAll(predicats.get(ruleName));
    }

    return predicateList;
  }
  
  /**
   * @return lexNamer
   */
  public LexNamer getLexNamer() {
    return this.lexNamer;
  }

  
  /**
   * Iterates over all Rules to find all keywords
   */
  private void findAllKeywords() {
    for (MCProdSymbol ruleSymbol : grammarSymbol.getProdsWithInherited().values()) {
      if (ruleSymbol.isParserProd()) {
        Optional<ASTNode> astProd = ruleSymbol.getAstNode();
        if (astProd.isPresent() && astProd.get() instanceof ASTClassProd) {
          Optional<MCGrammarSymbol> refGrammarSymbol = MCGrammarSymbolTableHelper
              .getMCGrammarSymbol(astProd.get());
          boolean isRefGrammarSymbol = refGrammarSymbol.isPresent();
          for (ASTTerminal keyword : ASTNodes.getSuccessors(astProd.get(), ASTTerminal.class)) {
            if (isKeyword(keyword.getName(), grammarSymbol)
                || (isRefGrammarSymbol && isKeyword(keyword.getName(), refGrammarSymbol.get()))) {
              keywords.add(keyword.getName());
            }
          }
          for (ASTConstant keyword : ASTNodes.getSuccessors(astProd.get(), ASTConstant.class)) {
            if (isKeyword(keyword.getName(), grammarSymbol)
                || (isRefGrammarSymbol && isKeyword(keyword.getName(), refGrammarSymbol.get()))) {
              keywords.add(keyword.getName());
            }
          }
        }
      }
    }
    
  }
  
  private void buildLexPatterns() {
    buildLexPatterns(grammarSymbol);
    grammarSymbol.getSuperGrammarSymbols().forEach(g -> buildLexPatterns(g));
  }
  
  private void buildLexPatterns(MCGrammarSymbol grammar) {
    List<Pattern> patterns = lexerPatterns.get(grammar);
    if (patterns == null) {
      patterns = new ArrayList<>();
      lexerPatterns.put(grammar, patterns);
    }
    
    for (MCProdSymbol rule : grammar.getProdsWithInherited().values()) {
      if (rule.isLexerProd()) {
        if (!MCGrammarSymbolTableHelper.isFragment(rule.getAstNode())) {
          Optional<Pattern> lexPattern = MCGrammarSymbolTableHelper.calculateLexPattern(
              grammar,
              rule.getAstNode());
          
          if (lexPattern.isPresent()) {
            patterns.add(lexPattern.get());
          }
        }
      }
    }
  }
  
  private boolean mustBeKeyword(String rule, MCGrammarSymbol grammar) {
    return keywords.contains(rule);
  }
  
}
