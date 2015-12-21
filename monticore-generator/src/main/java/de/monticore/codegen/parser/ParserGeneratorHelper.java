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

package de.monticore.codegen.parser;

import static com.google.common.base.Preconditions.checkState;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTBlock;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._ast.ASTConstantsGrammar;
import de.monticore.grammar.grammar._ast.ASTLexNonTerminal;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.grammar.grammar_withconcepts._ast.ASTExpressionPredicate;
import de.monticore.grammar.grammar_withconcepts._ast.ASTJavaCode;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.java.javadsl._ast.ASTBlockStatement;
import de.monticore.java.javadsl._ast.ASTClassMemberDeclaration;
import de.monticore.languages.grammar.MCClassRuleSymbol;
import de.monticore.languages.grammar.MCEnumRuleSymbol;
import de.monticore.languages.grammar.MCExternalTypeSymbol;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCInterfaceOrAbstractRuleSymbol;
import de.monticore.languages.grammar.MCLexRuleSymbol;
import de.monticore.languages.grammar.MCRuleComponentSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.languages.grammar.MCRuleSymbol.KindSymbolRule;
import de.monticore.languages.grammar.MCTypeSymbol;
import de.monticore.languages.grammar.MCTypeSymbol.KindType;
import de.monticore.languages.grammar.PredicatePair;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

/**
 * This is a helper class for the parser generation
 */
public class ParserGeneratorHelper {
  
  public static final String MONTICOREANYTHING = "MONTICOREANYTHING";
  
  public static final String RIGHTASSOC = "<assoc=right>";

  public static final String ANTLR_CONCEPT = "antlr";
  
  private static Grammar_WithConceptsPrettyPrinter prettyPrinter;
  
  private ASTMCGrammar astGrammar;
  
  private String qualifiedGrammarName;
  
  private MCGrammarSymbol grammarSymbol;
  
  /**
   * Constructor for de.monticore.codegen.parser.ParserGeneratorHelper
   */
  public ParserGeneratorHelper(ASTMCGrammar ast, MCGrammarSymbol grammarSymbol) {
    Log.errorIfNull(ast);
    this.astGrammar = ast;
    this.qualifiedGrammarName = astGrammar.getPackage().isEmpty() ? astGrammar.getName() :
        Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackage()),
            astGrammar.getName());
    
    checkState(qualifiedGrammarName.equals(grammarSymbol.getFullName()));
    this.grammarSymbol = grammarSymbol;
  }
  
  /**
   * @return grammarSymbol
   */
  public MCGrammarSymbol getGrammarSymbol() {
    return this.grammarSymbol;
  }
  
  /**
   * @return the qualified grammar's name
   */
  public String getQualifiedGrammarName() {
    return qualifiedGrammarName;
  }
  
  /**
   * @return the name of the start rule
   */
  public String getStartRuleName() {
    if (grammarSymbol.getStartRule().isPresent()) {
      return grammarSymbol.getStartRule().get().getName();
    }

    return "";
  }

  /**
   * @return the qualified name of the top ast, i.e., the ast of the start rule.
   */
  public String getQualifiedStartRuleName() {
    if (grammarSymbol.getStartRule().isPresent()) {
      return getASTClassName(grammarSymbol.getStartRule().get());
    }
    return "";
  }

  /**
   * @return the package for the generated parser files
   */
  public String getParserPackage() {
    return getQualifiedGrammarName().toLowerCase() + "." + ParserGenerator.PARSER_PACKAGE;
  }
  
  /**
   * checks if parser must be generated for this rule
   * 
   * @param rule
   * @return
   */
  public boolean generateParserForRule(MCRuleSymbol rule) {
    boolean generateParserForRule = false;
    String ruleName = rule.getName();
    
    if (rule instanceof MCClassRuleSymbol) {
      MCClassRuleSymbol classRule = (MCClassRuleSymbol) rule;
      generateParserForRule = classRule.getNoParam() == 0;
    }
    
    if (rule instanceof MCInterfaceOrAbstractRuleSymbol) {
      List<PredicatePair> subRules = grammarSymbol.getSubRulesForParsing(ruleName);
      generateParserForRule = subRules != null && subRules.size() > 0;
    }
    return generateParserForRule;
  }
  
  /**
   * Gets all interface rules which were not excluded from the generation
   * 
   * @return List of interface rules
   */
  public List<MCRuleSymbol> getInterfaceRulesToGenerate() {
    List<MCRuleSymbol> interfaceRules = Lists.newArrayList();
    
    for (MCRuleSymbol ruleSymbol : grammarSymbol.getRulesWithInherited()
        .values()) {
      if (ruleSymbol.getKindSymbolRule().equals(KindSymbolRule.INTERFACEORABSTRACTRULE)) {
        
        List<PredicatePair> subRules = grammarSymbol
            .getSubRulesForParsing(ruleSymbol.getName());
        
        if (subRules != null && !subRules.isEmpty()) {
          interfaceRules.add(ruleSymbol);
        }
      }
    }
    
    return interfaceRules;
  }
  
  /**
   * Gets all non external idents
   * 
   * @return List of ident types
   */
  public List<MCTypeSymbol> getIdentsToGenerate() {
    List<MCTypeSymbol> idents = Lists.newArrayList();
    for (MCTypeSymbol typeSymbol : grammarSymbol.getTypesWithInherited()
        .values()) {
      if (typeSymbol.getKindOfType().equals(KindType.IDENT)
          && !(typeSymbol instanceof MCExternalTypeSymbol)) {
        idents.add(typeSymbol);
      }
    }
    return idents;
  }
  
  /**
   * Gets parser rules
   * 
   * @return List of ident types
   */
  public List<ASTProd> getParserRulesToGenerate() {
    // Iterate over all Rules
    List<ASTProd> prods = Lists.newArrayList();
    for (MCRuleSymbol ruleSymbol : grammarSymbol.getRulesWithInherited()
        .values()) {
      if (ruleSymbol.getKindSymbolRule().equals(
          KindSymbolRule.PARSERRULE)) {
        Optional<ASTClassProd> astProd = ((MCClassRuleSymbol) ruleSymbol)
            .getRuleNode();
        if (astProd.isPresent()) {
          prods.add(astProd.get());
        }
      }
      else if (ruleSymbol.getKindSymbolRule().equals(
          KindSymbolRule.ENUMRULE)) {
        prods.add(((MCEnumRuleSymbol) ruleSymbol).getRule());
      }
    }
    return prods;
  }
  
  public List<ASTLexProd> getLexerRulesToGenerate() {
    // Iterate over all LexRules
    List<ASTLexProd> prods = Lists.newArrayList();
    MCLexRuleSymbol mcanything = null;
    final Map<String, MCRuleSymbol> rules = new LinkedHashMap<>();
    
    // Don't use grammarSymbol.getRulesWithInherited because of changed order
    for (final MCRuleSymbol ruleSymbol : grammarSymbol.getRules()) {
      rules.put(ruleSymbol.getName(), ruleSymbol);
    }
    for (int i = grammarSymbol.getSuperGrammars().size() - 1; i >= 0; i--) {
      rules.putAll(grammarSymbol.getSuperGrammars().get(i).getRulesWithInherited());
    }

    for (Entry<String, MCRuleSymbol> ruleSymbol :rules.entrySet()) {
      if (ruleSymbol.getValue().getKindSymbolRule().equals(KindSymbolRule.LEXERRULE)) {
        MCLexRuleSymbol lexRule = ((MCLexRuleSymbol) ruleSymbol.getValue());
        
        // MONTICOREANYTHING must be last rule
        if (lexRule.getName().equals(MONTICOREANYTHING)) {
          mcanything = lexRule;
        }
        else {
          prods.add(lexRule.getRuleNode());
        }
      }
    }
    if (mcanything != null) {
      prods.add(mcanything.getRuleNode());
    }
    return prods;
  }
  
  
  // ----------------------------------------------------
  
  /**
   * The result is true iff ASTTerminal is iterated
   * 
   * @param ast ASTConstantGroup to be evaluated
   * @return true iff ASTConstantGroup is iterated
   */
  public static boolean isIterated(ASTTerminal ast) {
    return ast.getIteration() == ASTConstantsGrammar.PLUS || ast
        .getIteration() == ASTConstantsGrammar.STAR;
  }
  
  /**
   * The result is true iff ASTOrGroup is iterated
   * 
   * @param ast ASTOrGroup to be evaluated
   * @return true iff ASTOrGroup is iterated
   */
  public static boolean isIterated(ASTBlock ast) {
    return ast.getIteration() == ASTConstantsGrammar.PLUS || ast
        .getIteration() == ASTConstantsGrammar.STAR;
  }
  
  /**
   * Returns the name of a rule
   * 
   * @param ast rule
   * @return Name of a rule
   */
  public static String getRuleName(ASTClassProd ast) {
    return ast.getName();
  }
  
  /**
   * Creates usage name from a NtSym usually from its attribute or creates name
   * 
   * @param ast
   * @return
   */
  
  public static String getUsuageName(ASTNonTerminal ast) {
    // Use Nonterminal name as attribute name starting with lower case latter
    if (ast.getUsageName().isPresent()) {
      return ast.getUsageName().get();
    }
    else {
      return StringTransformations.uncapitalize(ast.getName());
    }
  }
  
  public static boolean isIterated(ASTNonTerminal ast) {
    return ast.getIteration() == ASTConstantsGrammar.PLUS || ast
        .getIteration() == ASTConstantsGrammar.STAR;
  }
  
  public static String getTypeNameForEnum(String surroundtype, ASTConstantGroup ast) {
    return new StringBuilder("[enum.").append(surroundtype).append(".")
        .append(ast.getUsageName()).toString();
  }
  
  /**
   * Printable representation of iteration
   * 
   * @param i Value from AST
   * @return String representing value i
   */
  public static String printIteration(int i) {
    switch (i) {
      case ASTConstantsGrammar.PLUS:
        return "+";
      case ASTConstantsGrammar.STAR:
        return "*";
      case ASTConstantsGrammar.QUESTION:
        return "?";
      default:
        return "";
    }
  }
  
  public static String getDefinedType(ASTClassProd rule) {
    return rule.getName();
  }
  
  /**
   * Returns Human-Readable, antlr conformed name for a rulename
   * 
   * @param ast rule name
   * @return Human-Readable, antlr conformed rule name
   */
  public static String getRuleNameForAntlr(ASTNonTerminal ast) {
    return getRuleNameForAntlr(ast.getName());
  }
  
  /**
   * Returns Human-Readable, antlr conformed name for a rulename
   * 
   * @param rulename rule name
   * @return Human-Readable, antlr conformed rule name
   */
  public static String getRuleNameForAntlr(String rulename) {
    return JavaNamesHelper.getNonReservedName(rulename
        .toLowerCase());
  }
  
  /**
   * Returns Human-Readable, antlr conformed name for a rulename
   * 
   * @param rule rule name
   * @return Human-Readable, antlr conformed rule name
   */
  public static String getRuleNameForAntlr(ASTClassProd rule) {
    return getRuleNameForAntlr(getRuleName(rule));
  }
  
  public static String getTmpVarNameForAntlrCode(ASTNonTerminal node) {
    Optional<MCRuleSymbol> prod = getMCRuleForThisComponent(node);
    if (!prod.isPresent()) {
      Log.error("0xA1006 ASTNonterminal " + node.getName() + "(usageName: " + node.getUsageName()
          + ") can't be resolved.");
      return "";
    }
    return prod.get().getTmpVarName(node);
  }
  
  public static String getTmpVarNameForAntlrCode(ASTLexNonTerminal node) {
    Optional<MCRuleSymbol> prod = getMCRuleForThisComponent(node);
    if (!prod.isPresent()) {
      Log.error("0xA1007 ASTNonterminal " + node.getName() + "(usageName: " + node.getVariable()
          + ") can't be resolved.");
      return "";
    }
    return prod.get().getTmpVarName(node);
  }
  
  public static String getTmpVarNameForAntlrCode(String name, ASTNode node) {
    Optional<MCRuleSymbol> prod = getMCRuleForThisComponent(name, node);
    if (!prod.isPresent()) {
      Log.error("0xA1008 ASTNonterminal " + name + " can't be resolved.");
      return "";
    }
    return prod.get().getTmpVarName(node);
  }
  
  public static Grammar_WithConceptsPrettyPrinter getPrettyPrinter() {
    if (prettyPrinter == null) {
      prettyPrinter = new Grammar_WithConceptsPrettyPrinter(new IndentPrinter());
    }
    return prettyPrinter;
  }
  
  /**
   * Neue Zwischenknoten: Action = Statements; ExpressionPredicate = Expression;
   * ASTScript = MCStatement; ASTAntlrCode = MemberDeclarations; ASTActionAntlr
   * = MemberDeclarations;
   * 
   * @param node
   * @return
   */
  public static String getText(ASTNode node) {
    Log.errorIfNull(node);

    if (node instanceof ASTAction) {
      StringBuffer buffer = new StringBuffer();
      for (ASTBlockStatement action: ((ASTAction) node).getBlockStatements()) {
        buffer.append(getPrettyPrinter().prettyprint(action));
      }
      return buffer.toString();
    }
    if (node instanceof ASTJavaCode) {
      StringBuffer buffer = new StringBuffer();
      for (ASTClassMemberDeclaration action: ((ASTJavaCode) node).getClassMemberDeclarations()) {
        buffer.append(getPrettyPrinter().prettyprint(action));

      }
      return buffer.toString();
    }
    if (node instanceof ASTExpressionPredicate) {
      String exprPredicate = getPrettyPrinter().prettyprint(((ASTExpressionPredicate) node).getExpression());
      Log.debug("ASTExpressionPredicate:\n" + exprPredicate, ParserGenerator.LOG);
      return exprPredicate;
    }
    // TODO MB
    // getPrettyPrinter().prettyPrint(node, buffer);
    return "";
  }
  
  
  public static String getParseRuleName(MCRuleSymbol rule) {
    return JavaNamesHelper.getNonReservedName(StringTransformations.uncapitalize(rule.getName()));
  }
  
  public static String getMCParserWrapperName(MCRuleSymbol rule) {
    return StringTransformations.capitalize(JavaNamesHelper.
        getNonReservedName(rule.getName()));
  }
  
  public static String getASTClassName(MCRuleSymbol rule) {
    return rule.getType().getQualifiedName();
  }
  
  public static Optional<MCRuleSymbol> getMCRuleForThisComponent(String name, ASTNode node) {
    Optional<? extends Symbol> ruleComponent = node.getSymbol();
    if (ruleComponent.isPresent() && ruleComponent.get() instanceof MCRuleComponentSymbol) {
      return Optional.ofNullable(((MCRuleComponentSymbol) ruleComponent.get()).getEnclosingRule());
    }
    return Optional.<MCRuleSymbol> empty();
  }
  
  public static Optional<MCRuleSymbol> getMCRuleForThisComponent(ASTNonTerminal node) {
    Optional<? extends Symbol> ruleComponent = node.getSymbol();
    if (ruleComponent.isPresent() && ruleComponent.get() instanceof MCRuleComponentSymbol) {
      return Optional.ofNullable(((MCRuleComponentSymbol) ruleComponent.get()).getEnclosingRule());
    }
    return Optional.<MCRuleSymbol> empty();
  }
  
  public static Optional<MCRuleSymbol> getMCRuleForThisComponent(ASTLexNonTerminal node) {
    Optional<? extends Symbol> ruleComponent = node.getSymbol();
    if (ruleComponent.isPresent() && ruleComponent.get() instanceof MCRuleComponentSymbol) {
      return Optional.ofNullable(((MCRuleComponentSymbol) ruleComponent.get()).getEnclosingRule());
    }
    return Optional.<MCRuleSymbol> empty();
  }
  
}
