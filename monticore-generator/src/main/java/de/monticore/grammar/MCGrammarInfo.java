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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;

import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrCodeExt;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrLexerAction;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrParserAction;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.languages.grammar.MCClassRuleSymbol;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCLexRuleSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.languages.grammar.MCRuleSymbol.KindSymbolRule;
import de.monticore.languages.grammar.lexpatterns.LexPatternHelper;
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
  private Set<String> keywords = Sets.newHashSet();
  
  /**
   * Lexer patterns
   */
  private Map<MCGrammarSymbol, List<Pattern>> lexerPatterns = new HashMap<>();
  
  /**
   * Lexer actions defined in antlr concepts of the processed grammar and its
   * super grammars
   */
  private List<String> lexerActions = new ArrayList<String>();
  
  /**
   * Parser actions defined in antlr concepts of the processed grammar and its
   * super grammars
   */
  private List<String> parserActions = new ArrayList<String>();
  
  /**
   * The symbol of the processed grammar
   */
  private MCGrammarSymbol grammarSymbol;
  
  public MCGrammarInfo(MCGrammarSymbol grammarSymbol) {
    this.grammarSymbol = grammarSymbol;
    buildLexPatterns();
    findAllKeywords();
    addHWAntlrCode();
  }
  
  // ------------- Handling of the antlr concept -----------------------------
  
  private void addHWAntlrCode() {
    // Get Antlr hwc
    Set<MCGrammarSymbol> grammarsToHandle = Sets.newHashSet(grammarSymbol);
    grammarsToHandle.addAll(grammarSymbol.getAllSuperGrammars());
    for (MCGrammarSymbol grammar: grammarsToHandle) {
      if (grammar.getAstNode().isPresent()) {
        ASTNodes.getSuccessors(grammar.getAstNode().get(), ASTAntlrParserAction.class).forEach(
            a -> addParserAction(a.getText()));
        ASTNodes.getSuccessors(grammar.getAstNode().get(), ASTAntlrLexerAction.class).forEach(
            a -> addLexerAction(a.getText()));
      }
    }
  }
  
  /**
   * @return lexerActions
   */
  public List<String> getLexerActions() {
    return this.lexerActions;
  }
  
  /**
   * @return parserActions
   */
  public List<String> getParserActions() {
    return this.parserActions;
  }
  
  /**
   * @param lexerAction the lexerAction to add
   */
  private void addLexerAction(ASTAntlrCodeExt action) {
    lexerActions.add(ParserGeneratorHelper.getText(action));
  }
  
  /**
   * @param parserAction the parserAction to add
   */
  private void addParserAction(ASTAntlrCodeExt action) {
    parserActions.add(ParserGeneratorHelper.getText(action));
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
  
  /**
   * Iterates over all Rules to find all keywords
   */
  private void findAllKeywords() {
    for (MCRuleSymbol ruleSymbol : grammarSymbol.getRulesWithInherited().values()) {
      if (ruleSymbol.getKindSymbolRule().equals(KindSymbolRule.PARSERRULE)) {
        Optional<ASTClassProd> astProd = ((MCClassRuleSymbol) ruleSymbol).getRuleNode();
        if (astProd.isPresent()) {
          for (ASTTerminal keyword: ASTNodes.getSuccessors(astProd.get(), ASTTerminal.class)) {
            if (isKeyword(keyword.getName(), grammarSymbol) || isKeyword(keyword.getName(), ruleSymbol.getGrammarSymbol())) {
              keywords.add(keyword.getName());
            }
          }
          for (ASTConstant keyword: ASTNodes.getSuccessors(astProd.get(), ASTConstant.class)) {
            if (isKeyword(keyword.getName(), grammarSymbol) || isKeyword(keyword.getName(), ruleSymbol.getGrammarSymbol())) {
              keywords.add(keyword.getName());
            }
          }
        }
      }
    }
    
  }
  
  private void buildLexPatterns() {
    buildLexPatterns(grammarSymbol);
    grammarSymbol.getSuperGrammars().forEach(g -> buildLexPatterns(g));
  }

  
  private void buildLexPatterns(MCGrammarSymbol grammar) {
    List<Pattern> patterns = lexerPatterns.get(grammar);
    if (patterns == null) {
      patterns = new ArrayList<>();
      lexerPatterns.put(grammar, patterns);
    }

    for (MCRuleSymbol rule : grammar.getRulesWithInherited().values()) {
      if (rule.getKindSymbolRule().equals(MCRuleSymbol.KindSymbolRule.LEXERRULE)) {
        final MCLexRuleSymbol lexRule = (MCLexRuleSymbol) rule;
        if (!lexRule.isFragment()) {
          Optional<Pattern> lexPattern = LexPatternHelper.calculateLexPattern(grammar,
              lexRule.getRuleNode());

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
