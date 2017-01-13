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

package de.monticore.codegen.parser.antlr;

import static de.monticore.codegen.parser.ParserGeneratorHelper.printIteration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.EssentialMCGrammarSymbolTableHelper;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.DirectLeftRecursionDetector;
import de.monticore.grammar.EssentialMCGrammarInfo;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTAnything;
import de.monticore.grammar.grammar._ast.ASTBlock;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._ast.ASTEof;
import de.monticore.grammar.grammar._ast.ASTLexActionOrPredicate;
import de.monticore.grammar.grammar._ast.ASTLexAlt;
import de.monticore.grammar.grammar._ast.ASTLexAnyChar;
import de.monticore.grammar.grammar._ast.ASTLexBlock;
import de.monticore.grammar.grammar._ast.ASTLexChar;
import de.monticore.grammar.grammar._ast.ASTLexCharRange;
import de.monticore.grammar.grammar._ast.ASTLexNonTerminal;
import de.monticore.grammar.grammar._ast.ASTLexOption;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTLexSimpleIteration;
import de.monticore.grammar.grammar._ast.ASTLexString;
import de.monticore.grammar.grammar._ast.ASTMCAnything;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTOptionValue;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._ast.ASTSemanticpredicateOrAction;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.grammar.grammar._ast.GrammarNodeFactory;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.grammar.symboltable.EssentialMCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.languages.grammar.PredicatePair;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class Grammar2Antlr implements Grammar_WithConceptsVisitor {
  
  private EssentialMCGrammarSymbol grammarEntry;
  
  /**
   * This list is used for the detection of the left recursion
   */
  private ArrayList<ASTAlt> altList = new ArrayList<>();
  
  private DirectLeftRecursionDetector leftRecursionDetector = new DirectLeftRecursionDetector();
  
  private SourcePositionActions positionActions;
  
  private AttributeCardinalityConstraint attributeConstraints;
  
  private ASTConstructionActions astActions;
  
  private List<String> productionAntlrCode = Lists.newArrayList();
  
  private StringBuilder codeSection;
  
  private StringBuilder action;
  
  private EssentialMCGrammarInfo grammarInfo;
  
  private ParserGeneratorHelper parserHelper;
  
  public Grammar2Antlr(
      ParserGeneratorHelper parserGeneratorHelper,
      EssentialMCGrammarInfo grammarInfo) {
    Preconditions.checkArgument(parserGeneratorHelper.getGrammarSymbol() != null);
    this.grammarEntry = parserGeneratorHelper.getGrammarSymbol();
    this.grammarInfo = grammarInfo;
    this.parserHelper = parserGeneratorHelper;
    
    astActions = new ASTConstructionActions(parserGeneratorHelper);
    attributeConstraints = new AttributeCardinalityConstraint(parserGeneratorHelper);
    positionActions = new SourcePositionActions(parserGeneratorHelper);
    
  }
  
  /**
   * Prints Lexer rule
   *
   * @param ast - lexer production
   */
  @Override
  public void handle(ASTLexProd ast) {
    startCodeSection("ASTLexProd " + ast.getName());
    
    if (ast.isFragment()) {
      addToCodeSection("fragment ");
    }
    
    addToCodeSection(ast.getName(), " ");
    
    endCodeSection();
    
    // Print option
    if (ast.getLexOption().isPresent()) {
      ast.getLexOption().get().accept(getRealThis());
    }
    
    startCodeSection();
    addToCodeSection("\n:");
    
    // Add init action
    if (ast.getInitAction().isPresent()) {
      addToCodeSection("{", ParserGeneratorHelper.getText(ast.getInitAction().get()), "\n}");
    }
    endCodeSection();
    
    createAntlrCodeForLexAlts(ast.getAlts());
    
    // Add Action
    startCodeSection();
    
    if (ast.getEndAction().isPresent()) {
      if (ast.getEndAction().isPresent()) {
        addToCodeSection("{", ParserGeneratorHelper.getText(ast.getEndAction().get()), "\n}");
      }
    }
    addToCodeSection(";");
    
    endCodeSection(ast);
  }
  
  /**
   * Prints Parser rule
   *
   * @param ast parser production
   */
  @Override
  public void handle(ASTClassProd ast) {
    startCodeSection("ASTClassProd " + ast.getName());
    
    // Create eof and dummy rules
    String ruleName = HelperGrammar.getRuleNameForAntlr(ast);
    Optional<MCProdSymbol> ruleByName = grammarEntry
        .getProdWithInherited(HelperGrammar.getRuleName(ast));
    // String classnameFromRulenameorInterfacename = ruleByName.getType()
    // .getQualifiedName();
    String classnameFromRulenameorInterfacename = EssentialMCGrammarSymbolTableHelper
        .getQualifiedName(ruleByName.get());
    
    // Head of Rule
    // Pattern:
    // String tmp =
    // "%name% returns [%uname% ret = %defaultvalue%] %options% ";
    
    // TODO: Antlr4 Dies war die Alternative, wenn es keine Parameter
    // gibt.
    // Ist aber wahrscheinlich so korrekt,
    // erzeugt bestimmt Default für ret ...
    addDummyRules(HelperGrammar.getRuleName(ast), ruleName,
        classnameFromRulenameorInterfacename);
    
    String options = "";
    // Antlr4: new syntax
    if (ast.getAlts().isEmpty()) {
      options = "@rulecatch{}";
    }
    
    // Start code codeSection for rules
    addToCodeSection(ruleName, " returns [", classnameFromRulenameorInterfacename, " ret = ",
        EssentialMCGrammarSymbolTableHelper.getDefaultValue(ruleByName.get()), "]\n", options);
    
    startAction();
    // Add actions
    if (ast.getAction().isPresent() && ast.getAction().get() instanceof ASTAction) {
      addToAction(ParserGeneratorHelper.getText(ast.getAction().get()));
    }
    
    // Action at beginning of rule @init
    addToAction(astActions.getActionForRuleBeforeRuleBody(ast));
    // Action for determining positions
    addToAction(positionActions.startPosition(ast));
    // Action for determining positions of comments (First set position)
    addToAction("setActiveASTNode(_aNode);\n");
    
    addToAction(attributeConstraints.addActionForRuleBeforeRuleBody(ast));
    
    if (!isActionEmpty()) {
      addToCodeSection("@init");
      endAction();
    }
    
    // Action at end of rule
    startAction();
    addToAction(positionActions.endPosition(ast));
    addToAction(attributeConstraints.addActionForRuleAfterRuleBody(ast));
    
    if (!isActionEmpty()) {
      addToCodeSection("\n@after");
      endAction();
    }
    
    endCodeSection();
    // End code codeSection for rules
    
    startCodeSection();
    addToCodeSection("\n : ");
    
    List<PredicatePair> subRules = grammarInfo
        .getSubRulesForParsing(HelperGrammar.getRuleName(ast));
    if (subRules != null && !subRules.isEmpty()) {
      
      addToCodeSection("// Adding subrules");
      endCodeSection();
      
      int i = 0;
      for (PredicatePair x : subRules) {
        
        if (x.getComponent().isPresent()) {
          x.getComponent().get().accept(getRealThis());
        }
        
        startCodeSection();
        String subRuleVar = "subRuleVar" + i;
        addToCodeSection("(" + subRuleVar + " = "
            + HelperGrammar.getRuleNameForAntlr(x.getClassname())
            + "{$ret = $" + subRuleVar + ".ret;}) |  ");
        addToCodeSection("\n// end subrules");
        endCodeSection();
      }
    }
    else {
      endCodeSection();
    }
    
    // Iterate over all Components
    createAntlrCodeForAlts(ast.getAlts());
    
    addToAntlrCode(";");
    
    endCodeSection(ast);
  }
  
  @Override
  public void handle(ASTEnumProd ast) {
    // Check if user excluded this rule from the code generation
    
    startCodeSection("ASTEnumProd " + ast.getName());
    
    // Create eof and dummy rules
    String ruleName = HelperGrammar.getRuleNameForAntlr(ast.getName());
    Optional<MCProdSymbol> ruleByName = grammarEntry.getProdWithInherited(ast
        .getName());
    
    // Head of Rule
    addToCodeSection(ruleName + " returns ["
        + EssentialMCGrammarSymbolTableHelper.getQualifiedName(ruleByName.get()) + " ret = "
        + EssentialMCGrammarSymbolTableHelper.getDefaultValue(ruleByName.get()) + "] ");
    
    addToCodeSection("\n: ");
    
    String sep = "";
    for (ASTConstant c : ast.getConstants()) {
      addToCodeSection(sep);
      if (grammarInfo.isKeyword(c.getName(), grammarEntry)) {
        addToCodeSection("\n'" + c.getName() + "'");
      }
      else {
        addToCodeSection("\n", parserHelper.getLexSymbolName(c.getName()));
      }
      
      String temp1 = "";
      temp1 += "$ret = " + EssentialMCGrammarSymbolTableHelper.getQualifiedName(ruleByName.get())
          + "."
          + parserHelper.getConstantNameForConstant(c) + ";";
      
      if (!temp1.isEmpty()) {
        addToCodeSection("\n{" + temp1 + "}");
      }
      sep = "|";
    }
    addToCodeSection(";\n");
    
    endCodeSection(ast);
  }
  
  /**
   * Handles a ConstantGroup (something in [] )
   *
   * @param ast
   */
  @Override
  public void handle(ASTConstantGroup ast) {
    startCodeSection(ast);
    
    boolean iterated = false;
    // TODO GV:
    // if (ast.getUsageName().isPresent()) {
    // Optional<MCProdSymbol> ruleSymbol =
    // EssentialMCGrammarSymbolTableHelper.getEnclosingRule(ast);
    // if (ruleSymbol.isPresent() && ruleSymbol.get().getDefinedType() != null)
    // {
    // MCAttributeSymbol attribute = ruleSymbol.get().getDefinedType()
    // .getAttribute(ast.getUsageName().get());
    // MCTypeSymbol type = attribute.getType();
    // if ("int".equals(type.getQualifiedName()) ||
    // (type.getKindOfType().equals(KindType.CONST) &&
    // type.getEnumValues().size() > 1)) {
    // iterated = true;
    // }
    // }
    // }
    iterated = ast.getConstants().size() > 1;
    
    // One entry leads to boolean isMethods
    if (!iterated) {
      ASTConstant x = ast.getConstants().get(0);
      if (!grammarInfo.isKeyword(x.getName(), grammarEntry)) {
        addToCodeSection(parserHelper.getLexSymbolName(x.getName()));
      }
      else {
        addToCodeSection("'" + x.getName() + "'");
      }
      
      startAction();
      addToAction(astActions.getConstantInConstantGroupSingleEntry(x, ast));
      endActionNewLine();
    }
    
    // More than one entry leads to an int
    else {
      addToCodeSection("(");
      String del = "";
      for (Iterator<ASTConstant> iter = ast.getConstants().iterator(); iter
          .hasNext();) {
        addToCodeSection(del);
        ASTConstant x = iter.next();
        
        if (!grammarInfo.isKeyword(x.getName(), grammarEntry)) {
          /* // Template //
           * a.set%namegroup%(%astconstclassname%.%constantname%); tmp =
           * "%name% {%actions%}";
           *
           * // Replace values tmp = tmp.replaceAll("%astconstclassname%",
           * constClassName); tmp = tmp.replaceAll("%name%",
           * grammarEntry.getLexSymbolName(x.getName())); tmp =
           * tmp.replaceAll("%namegroup%",
           * NameHelper.firstToUpper(a.getUsageName()));
           *
           * if (x.getHumanName() == null) { tmp =
           * tmp.replaceAll("%constantname%",
           * grammarEntry.getLexSymbolName(x.getName())); } else { tmp =
           * tmp.replaceAll("%constantname%", (x.getHumanName().toUpperCase()));
           * } */
          addToCodeSection(parserHelper.getLexSymbolName(x.getName()));
        }
        else {
          /* // Template //
           * a.set%namegroup%(%astconstclassname%.%constantname%); tmp =
           * "'%name%' {%actions%}";
           *
           * tmp = tmp.replaceAll("%astconstclassname%", constClassName); tmp =
           * tmp.replaceAll("%name%", (x.getName())); tmp =
           * tmp.replaceAll("%namegroup%",
           * NameHelper.firstToUpper(a.getUsageName()));
           *
           * tmp = tmp.replaceAll("%constantname%",
           * (x.getName().toUpperCase())); */
          addToCodeSection("'" + x.getName() + "'");
        }
        
        startAction();
        addToAction(astActions.getConstantInConstantGroupMultipleEntries(x, ast));
        endActionNewLine();
        
        del = "|\n";
      }
      
      addToCodeSection(")");
    }
    
    endCodeSection(ast);
  }
  
  /**
   * Print alternatives
   *
   * @param ast
   */
  public void createAntlrCodeForAlts(List<ASTAlt> ast) {
    String del = "";
    for (Iterator<ASTAlt> iter = ast.iterator(); iter.hasNext();) {
      addToAntlrCode(del);
      
      iter.next().accept(getRealThis());
      
      del = "|";
    }
  }
  
  public void createAntlrCodeForLexAlts(List<ASTLexAlt> ast) {
    String del = "";
    for (ASTLexAlt anAst : ast) {
      addToAntlrCode(del);
      
      anAst.accept(getRealThis());
      
      del = "|";
    }
  }
  
  @Override
  public void handle(ASTLexBlock ast) {
    startCodeSection();
    
    if (ast.isNegate()) {
      addToCodeSection("~");
    }
    
    // Start of Block
    addToCodeSection("(");
    if (ast.getOption().isPresent()) {
      addToCodeSection("\noptions {", ast.getOption().get().getID(), "=", ast.getOption().get()
          .getValue(), ";}");
    }
    if (ast.getInitAction().isPresent()) {
      addToCodeSection("{", ParserGeneratorHelper.getText(ast.getInitAction().get()), "}");
    }
    endCodeSection();
    
    // Visit all alternatives
    createAntlrCodeForLexAlts(ast.getLexAlts());
    
    // Start of Block with iteration
    startCodeSection();
    
    addToCodeSection(")\n", printIteration(ast.getIteration()));
    
    endCodeSection();
    
  }
  
  @Override
  public void handle(ASTLexSimpleIteration ast) {
    startCodeSection();
    
    // Start of Block
    addToAntlrCode("(");
    
    // Visit all alternatives
    if (ast.getLexChar().isPresent()) {
      ast.getLexChar().get().accept(getRealThis());
    }
    else if (ast.getLexString().isPresent()) {
      ast.getLexString().get().accept(getRealThis());
    }
    else if (ast.getLexNonTerminal().isPresent()) {
      ast.getLexNonTerminal().get().accept(getRealThis());
    }
    else if (ast.getLexAnyChar().isPresent()) {
      ast.getLexAnyChar().get().accept(getRealThis());
    }
    
    // Close block and print iteration
    addToCodeSection(")\n", printIteration(ast.getIteration()));
    endCodeSection();
    
    if (ast.isQuestion()) {
      addToAntlrCode("?");
    }
  }
  
  /**
   * Print Block structure, 1:1 copy to Antlr Differences occurr, if it is a
   * syntatic predicate, which uses the same syntax but ends with "=>" Turn
   * extra code generation off in these block and use antlr syntax only
   * (indicated by inpredicate)
   *
   * @param a Block to be printed
   */
  @Override
  public void handle(ASTBlock a) {
    // Start of Block
    startCodeSection();
    
    addToCodeSection("(");
    // Print options
    if (a.getOption().isPresent()) {
      addToCodeSection("\n  options {");
      for (ASTOptionValue x : a.getOption().get().getOptionValues()) {
        addToCodeSection("\n  " + x.getKey() + "=" + x.getValue() + ";");
      }
      addToCodeSection("\n }");
    }
    
    // Print init actions
    if (a.getInitAction().isPresent()) {
      addToCodeSection("{" + ParserGeneratorHelper.getText(a.getInitAction().get()) + "}");
    }
    
    endCodeSection();
    
    // Visit all alternatives
    createAntlrCodeForAlts(a.getAlts());
    
    // Start of Block with iteration
    startCodeSection();
    addToCodeSection("\n)" + printIteration(a.getIteration()));
    endCodeSection();
  }
  
  @Override
  public void visit(ASTTerminal ast) {
    
    startCodeSection("ASTTerminal " + ast.getName());
    
    addToCodeSection("(");
    
    String rulename;
    if (grammarInfo.isKeyword(ast.getName(), grammarEntry)) {
      rulename = "'" + ast.getName() + "'";
    }
    else {
      rulename = parserHelper.getLexSymbolName(ast.getName().intern());
    }
    
    // No actions in predicates
    // Template engine cannot be used for substition in rare cases
    addToCodeSection(rulename); // + " %initaction% %actions% ) %iteration% ";
    
    boolean iteratedItself = HelperGrammar.isIterated(ast);
    boolean isVariable = (ast.getVariableName().isPresent());
    boolean isAttribute = (ast.getUsageName().isPresent());
    
    boolean isList = iteratedItself;
    Optional<? extends Symbol> ruleComponent = ast.getSymbol();
    if (ruleComponent.isPresent() && ruleComponent.get() instanceof MCProdComponentSymbol) {
      MCProdComponentSymbol componentSymbol = (MCProdComponentSymbol) ruleComponent.get();
      isList = componentSymbol.isList();
    }
    // Add Actions
    startAction();
    if (isVariable) {
      if (iteratedItself) {
        addToAction(astActions.getActionForTerminalIteratedVariable(ast));
      }
      else {
        addToAction(astActions.getActionForTerminalNotIteratedVariable(ast));
      }
    }
    else if (isAttribute) {
      if (isList) {
        addToAction(astActions.getActionForTerminalIteratedAttribute(ast));
      }
      else {
        addToAction(astActions.getActionForTerminalNotIteratedAttribute(ast));
      }
    }
    else {
      addToAction(astActions.getActionForTerminalIgnore(ast));
    }
    
    endAction();
    
    addToCodeSection(")", printIteration(ast.getIteration()));
    
    endCodeSection(ast);
    
  }
  
  @Override
  public void visit(ASTLexCharRange ast) {
    startCodeSection();
    
    if (ast.isNegate()) {
      addToCodeSection("~");
    }
    addToCodeSection("'", ast.getLowerChar(), "'..'", ast.getUpperChar(), "'  ");
    
    endCodeSection();
  }
  
  @Override
  public void visit(ASTLexChar a) {
    startCodeSection();
    
    if (a.isNegate()) {
      addToCodeSection("~");
    }
    addToCodeSection("'", a.getChar(), "' ");
    
    endCodeSection();
  }
  
  @Override
  public void visit(ASTLexAnyChar a) {
    startCodeSection();
    addToCodeSection(".");
    endCodeSection();
  }
  
  @Override
  public void visit(ASTLexString a) {
    startCodeSection();
    
    addToCodeSection("'");
    addToCodeSection(a.getString(), "' ");
    
    endCodeSection();
  }
  
  @Override
  public void visit(ASTLexActionOrPredicate a) {
    startCodeSection();
    
    addToCodeSection("{");
    addToCodeSection(ParserGeneratorHelper.getText(a.getExpressionPredicate()), "}");
    if (a.isPredicate()) {
      addToCodeSection("?");
    }
    
    endCodeSection();
  }
  
  @Override
  public void visit(ASTLexNonTerminal ast) {
    startCodeSection();
    
    addToCodeSection(" ");
    addToCodeSection(ast.getName(), " ");
    
    endCodeSection();
  }
  
  @Override
  public void visit(ASTLexOption ast) {
    addToAntlrCode("options {" + ast.getID() + " = " + ast.getValue() + "; } ");
  }
  
  /**
   * Print anything in {} which can be an Action or an semantic predicate for
   * antlr, depending on a ? at the end of the block
   *
   * @param ast
   */
  @Override
  public void visit(ASTSemanticpredicateOrAction ast) {
    startCodeSection();
    
    addToCodeSection("{");
    if (ast.getExpressionPredicate().isPresent()) {
      addToCodeSection(ParserGeneratorHelper.getText(ast.getExpressionPredicate().get()), "}");
    }
    else if (ast.getAction().isPresent()) {
      addToCodeSection(ParserGeneratorHelper.getText(ast.getAction().get()), "}");
    }
    else {
      Log.error("0xA0327 neither expression predicate nor action is set.");
    }
    if (ast.isPredicate()) {
      addToCodeSection("?");
    }
    
    endCodeSection();
  }
  
  /**
   * Handles an non-terminal thats stated in a grammar
   *
   * @param ast
   */
  @Override
  public void visit(ASTNonTerminal ast) {
    startCodeSection();
    
    Optional<MCProdSymbol> prod = grammarEntry.getProdWithInherited(ast.getName());
    if (!prod.isPresent()) {
      Log.error("0xA2201 Production symbol for  " + ast.getName() + "couldn't be resolved.",
          ast.get_SourcePositionStart());
    }
    
    // Lexer Rule
    if (prod.get().isLexerProd()) {
      addCodeForLexerRule(ast);
    }
    // Other Rule called
    else if (prod.get().isParserProd()
        ||
        prod.get().isInterface()
        ||
        prod.get().isAbstract()
        ||
        prod.get().isEnum()) {
      
      addCodeForRuleReference(ast);
    }
    // external rule called (first+second version)
    else {
      
      addToCodeSection(embedded(ast));
    }
    
    endCodeSection();
  }
  
  /**
   * Print end-of-file token, which is simply an EOF
   *
   * @param a
   */
  @Override
  public void visit(ASTEof a) {
    addToAntlrCode("EOF");
  }
  
  @Override
  public void visit(ASTAnything a) {
    addToAntlrCode(".");
  }
  
  @Override
  public void visit(ASTMCAnything a) {
    addToAntlrCode(ParserGeneratorHelper.MONTICOREANYTHING);
  }
  
  @Override
  public void visit(ASTAlt alt) {
    altList.add(alt);
    if (alt.isRightAssoc()) {
      addToAntlrCode(ParserGeneratorHelper.RIGHTASSOC);
    }
  }
  
  @Override
  public void endVisit(ASTAlt alt) {
    if (!altList.isEmpty()) {
      altList.remove(altList.size() - 1);
    }
  }
  
  // ----------------- End of visit methods
  // ---------------------------------------------
  
  public List<String> createAntlrCode(ASTProd ast) {
    clearAntlrCode();
    parserHelper.resetTmpVarNames();
    ast.accept(getRealThis());
    return getAntlrCode();
  }
  
  /**
   * Write extra Rules for Interfaces Example A implements C = zz ; B implements
   * C = zz ; results in an extra rule C : A | B;
   */
  public List<String> createAntlrCodeForInterface(MCProdSymbol interfaceRule) {
    
    clearAntlrCode();
    
    String interfacename = interfaceRule.getName();
    // Dummy rules
    String ruleName = HelperGrammar.getRuleNameForAntlr(interfacename);
    String usageName = EssentialMCGrammarSymbolTableHelper.getQualifiedName(interfaceRule);
    
    startCodeSection(interfaceRule.getName());
    
    addDummyRules(interfacename, ruleName, usageName);
    
    addToAntlrCode(HelperGrammar.getRuleNameForAntlr(interfacename) + " returns ["
        + usageName + " ret]: (");
    
    String del = "";
    
    int count = 0;
    for (PredicatePair interf : grammarInfo.getSubRulesForParsing(interfacename)) {
      addToAntlrCode(del);
      Optional<MCProdSymbol> prod = grammarEntry.getProdWithInherited(interf.getClassname());
      
      if (interf.getComponent().isPresent()) {
        interf.getComponent().get().accept(getRealThis());
      }
      
      startCodeSection();
      addToCodeSection("tmp" + count + "="
          + HelperGrammar.getRuleNameForAntlr(interf.getClassname()));
      // TODO GV: don't need it anymore?
      // int size = 0;
      // if (prod != null) {
      // size = prod.getNoParam();
      // }
      //
      // if (size > 0) {
      // addToCodeSection("[null");
      // for (int j = 1; j < size; j++) {
      // addToCodeSection(",null");
      // }
      // addToCodeSection("]");
      // }
      
      // Action for AntLR4
      addToCodeSection("\n{$ret=$tmp" + count + ".ret;}");
      
      count++;
      del = " | ";
      
      endCodeSection();
    }
    
    addToAntlrCode(");");
    
    return getAntlrCode();
  }
  
  public List<String> getHWParserJavaCode() {
    return grammarInfo.getAdditionalParserJavaCode();
  }
  
  public List<String> getHWLexerJavaCode() {
    return grammarInfo.getAdditionalLexerJavaCode();
  }
  
  // ----------------------------------------------------------------------------------------------
  
  private void addCodeForLexerRule(ASTNonTerminal ast) {
    startCodeSection();
    
    addToCodeSection("(");
    
    // AntLR2 -> AntLR4: Replace : by =
    // tmp = "( %tmp%=%rulename% %initaction% %actions%";
    
    addToCodeSection(parserHelper.getTmpVarName(ast), "=", ast.getName());
    
    // Add Actions
    startAction();
    
    Optional<MCProdSymbol> scope = EssentialMCGrammarSymbolTableHelper.getEnclosingRule(ast);
    
    if (scope.isPresent()) {
      addToAction(attributeConstraints.addActionForNonTerminal(ast));
      // TODO GV:
      // String attributename = HelperGrammar.getUsuageName(ast);
      
      // if
      // (scope.get().getDefinedType().getAttribute(attributename).isIterated())
      // {
  //    if (HelperGrammar.isIterated(ast)) {
      String attributename = HelperGrammar.getUsuageName(ast);
        if (scope.isPresent() && scope.get().getProdComponent(attributename).isPresent()
            && scope.get().getProdComponent(attributename).get().isList()) {
        addToAction(astActions.getActionForLexerRuleIteratedAttribute(ast));
      }
      else {
        addToAction(astActions.getActionForLexerRuleNotIteratedAttribute(ast));
      }
    }
    
    endAction();
    
    addToCodeSection("\n");
    
    endCodeSection();
    
    if (ast.isPlusKeywords()) {
      addToAntlrCode("/* Automatically added keywords " + grammarInfo.getKeywords()
          + " */");
      
      // TODO PN, GV
      for (String y : grammarInfo.getKeywords()) {
        addToAntlrCode(" | ");
        ASTTerminal term = GrammarNodeFactory.createASTTerminal();
        ast.get_Children().add(term);
        // term.set_Parent(ast);
        term.setName(y);
        term.setUsageName(HelperGrammar.getUsuageName(ast));
        
        Optional<? extends Symbol> ruleComponent = ast.getSymbol();
        if (ruleComponent.isPresent() && ruleComponent.get() instanceof MCProdComponentSymbol) {
          MCProdComponentSymbol componentSymbol = (MCProdComponentSymbol)ruleComponent.get();
          Optional<MCProdSymbol> rule = EssentialMCGrammarSymbolTableHelper
              .getEnclosingRule(componentSymbol);
          if (rule.isPresent()) {
            addActionForKeyword(term, rule.get(), componentSymbol.isList());
          }
        }
      }
    }
    
    addToAntlrCode(") " + printIteration(ast.getIteration()));
  }
  
  /**
   * print code for references to embedded rules
   */
  private String embedded(ASTNonTerminal ast) {
    return "";
  }
  
  /**
   * Print code for references to other rules in the same grammar
   *
   * @param ast
   * @return
   */
  private void addCodeForRuleReference(ASTNonTerminal ast) {
    Optional<MCProdSymbol> scope = EssentialMCGrammarSymbolTableHelper.getEnclosingRule(ast);
    
    boolean isLeftRecursive = false;
    if (scope.isPresent() && scope.get().getName().equals(ast.getName())
        && !altList.isEmpty()) {
      // Check if rule is left recursive
      isLeftRecursive = leftRecursionDetector
          .isAlternativeLeftRecursive(altList.get(0), ast);
    }
    
    startCodeSection();
    
    // In star enviroment use add-method, else use set methods
    // Do not build up ast in predicates
    
    String iteration = printIteration(ast.getIteration());
    String braceopen = iteration.isEmpty() ? "" : "(";
    String braceclose = iteration.isEmpty() ? "" : ")";
    
    String tmpVarName = parserHelper.getTmpVarName(ast);
    
    addToCodeSection(braceopen, " ", tmpVarName, "=", HelperGrammar.getRuleNameForAntlr(ast));
    
    startAction();
    
    if (isLeftRecursive) {
      addToAction(astActions
          .getActionForInternalRuleNotIteratedLeftRecursiveAttribute(ast));
    }
    addToAction(attributeConstraints.addActionForNonTerminal(ast));
    // TODO GV:
    String attributename = HelperGrammar.getUsuageName(ast);
    if (scope.isPresent() && scope.get().getProdComponent(attributename).isPresent()
        && scope.get().getProdComponent(attributename).get().isList()) {
      addToAction(astActions.getActionForInternalRuleIteratedAttribute(ast));
    }
    else {
      addToAction(astActions.getActionForInternalRuleNotIteratedAttribute(ast));
    }
    
    // TODO GV: replaceAll("..", ".")); is deprecated?
    endAction();
    
    addToCodeSection(braceclose, " ", iteration, " ");
    
    endCodeSection();
    
  }
  
  private void addActionForKeyword(ASTTerminal keyword, MCProdSymbol rule, boolean isList) {
    
    startCodeSection();
    
    addToCodeSection("(");
    String rulename = "";
    if (grammarInfo.isKeyword(keyword.getName(), grammarEntry)) {
      rulename = "'" + keyword.getName() + "'";
    }
    
    // No actions in predicates
    // Template engine cannot be used for substition in rare cases
    addToCodeSection(rulename); // + " %initaction% %actions% ) %iteration% ";
    
    boolean iteratedItself = HelperGrammar.isIterated(keyword);
    boolean isVariable = (keyword.getVariableName().isPresent());
    boolean isAttribute = (keyword.getUsageName().isPresent());
    
    // Add Actions
    startAction();
    if (isVariable) {
      if (iteratedItself) {
        addToAction(astActions.getActionForTerminalIteratedVariable(keyword));
      }
      else {
        addToAction(astActions.getActionForTerminalNotIteratedVariable(keyword));
      }
    }
    else if (isAttribute) {
      if (isList) {
        addToAction(astActions.getActionForTerminalIteratedAttribute(keyword));
      }
      else {
        addToAction(astActions.getActionForTerminalNotIteratedAttribute(keyword));
      }
    }
    else {
      addToAction(astActions.getActionForTerminalIgnore(keyword));
    }
    
    endAction();
    
    addToCodeSection(")", printIteration(keyword.getIteration()));
    
    endCodeSection();
    
  }
  
  private void addDummyRules(String rulenameInternal, String ruleName,
      String usageName) {
    
    addToCodeSection("\n\n", ruleName, "_eof returns [", usageName, " ret = null] :\n  tmp = ",
        ruleName, " {$ret = $tmp.ret;} ");
    String follow = "EOF";
    String end = " ;\n\n";
    
    Optional<ASTAlt> follow2 = parserHelper.getAlternativeForFollowOption(rulenameInternal);
    if (!follow2.isPresent()) {
      addToCodeSection(follow, end);
      endCodeSection();
      return;
    }
    endCodeSection();
    
    follow2.get().accept(getRealThis());
    
    addToAntlrCode(end);
  }
  
  // ----------------------------------------------------------
  
  /**
   * Gets the antlr code (for printing)
   *
   * @return
   */
  private List<String> getAntlrCode() {
    return ImmutableList.copyOf(productionAntlrCode);
  }
  
  /**
   * Adds the given code to antlr code
   *
   * @param code
   */
  private void addToAntlrCode(String code) {
    productionAntlrCode.add(code);
  }
  
  /**
   * Clears antlr code
   */
  private void clearAntlrCode() {
    productionAntlrCode.clear();
  }
  
  /**
   * Adds the given code to antlr code
   *
   * @param code
   */
  private void addToAntlrCode(StringBuilder code) {
    addToAntlrCode(code.toString());
  }
  
  /**
   * Starts codeSection of the parser code
   */
  private void startCodeSection() {
    codeSection = new StringBuilder();
  }
  
  /**
   * Adds the current code codeSection to antlr
   */
  private void endCodeSection() {
    addToAntlrCode(codeSection);
    codeSection = new StringBuilder();
  }
  
  /**
   * Starts antlr code for the given production
   *
   * @param ast
   */
  private void startCodeSection(ASTNode ast) {
    startCodeSection(ast.getClass().getSimpleName());
  }
  
  /**
   * Starts antlr code for the production with the given name
   */
  private void startCodeSection(String text) {
    codeSection = new StringBuilder("\n // Start of '" + text + "'\n");
  }
  
  /**
   * Ends antlr code for the given production
   *
   * @param ast
   */
  private void endCodeSection(ASTNode ast) {
    codeSection.append("// End of '" + ast.getClass().getSimpleName() + "'\n");
    endCodeSection();
  }
  
  /**
   * Adds the given code to the current codeSection
   */
  private void addToCodeSection(String... code) {
    Arrays.asList(code).forEach(s -> codeSection.append(s));
  }
  
  /**
   * @return codeSection
   */
  public StringBuilder getCodeSection() {
    return this.codeSection;
  }
  
  /**
   * Starts parser action
   */
  private void startAction() {
    action = new StringBuilder();
  }
  
  /**
   * Adds code for parser action to the current code codeSection
   */
  private void endAction() {
    if (action.length() != 0) {
      addToCodeSection("{", action.toString(), "}");
      clearAction();
    }
  }
  
  /**
   * Adds code for parser action to the current code codeSection starting and
   * ending with a new line
   */
  private void endActionNewLine() {
    if (action.length() != 0) {
      addToCodeSection("{\n", action.toString(), "\n}");
      clearAction();
    }
  }
  
  /**
   * Clears code for parser action
   */
  private void clearAction() {
    action = new StringBuilder();
  }
  
  private boolean isActionEmpty() {
    return action.length() == 0;
  }
  
  /**
   * @return action
   */
  public StringBuilder getAction() {
    return this.action;
  }
  
  /**
   * Adds code to the parser action
   */
  private void addToAction(String... code) {
    Arrays.asList(code).forEach(s -> action.append(s));
  }
  
}
