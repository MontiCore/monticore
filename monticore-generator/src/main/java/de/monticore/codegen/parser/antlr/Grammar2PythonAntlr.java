/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.parser.Languages;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.codegen.parser.antlr.ASTConstructionActions;
import de.monticore.codegen.parser.antlr.AttributeCardinalityConstraint;
import de.monticore.codegen.parser.antlr.Grammar2Antlr;
import de.monticore.codegen.parser.antlr.SourcePositionActions;
import de.monticore.grammar.DirectLeftRecursionDetector;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.MCGrammarInfo;
import de.monticore.grammar.PredicatePair;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static de.monticore.codegen.parser.ParserGeneratorHelper.printIteration;
import static de.se_rwth.commons.StringTransformations.uncapitalize;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 */
public class Grammar2PythonAntlr implements Grammar_WithConceptsVisitor {
  private MCGrammarSymbol grammarEntry;

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

  private StringBuilder action = new StringBuilder();

  private MCGrammarInfo grammarInfo;

  private ParserGeneratorHelper parserHelper;

  private boolean embeddedCode;

  //TODO by KP: refactor me, I am no longer required
  private boolean embeddedJavaCode = false;

  public Grammar2PythonAntlr(
          ParserGeneratorHelper parserGeneratorHelper,
          MCGrammarInfo grammarInfo) {
    Preconditions.checkArgument(parserGeneratorHelper.getGrammarSymbol() != null);
    this.grammarEntry = parserGeneratorHelper.getGrammarSymbol();
    this.grammarInfo = grammarInfo;
    this.parserHelper = parserGeneratorHelper;

    astActions = new ASTConstructionActions(parserGeneratorHelper);
    attributeConstraints = new AttributeCardinalityConstraint(parserGeneratorHelper);
    positionActions = new SourcePositionActions(parserGeneratorHelper);
  }

  public Grammar2PythonAntlr(
          ParserGeneratorHelper parserGeneratorHelper,
          MCGrammarInfo grammarInfo,
          boolean embeddedCode) {
    Preconditions.checkArgument(parserGeneratorHelper.getGrammarSymbol() != null);
    this.grammarEntry = parserGeneratorHelper.getGrammarSymbol();
    this.grammarInfo = grammarInfo;
    this.parserHelper = parserGeneratorHelper;

    astActions = new ASTConstructionActions(parserGeneratorHelper);
    attributeConstraints = new AttributeCardinalityConstraint(parserGeneratorHelper);
    positionActions = new SourcePositionActions(parserGeneratorHelper);
    this.embeddedCode = embeddedCode;

  }

  /**
   * Should embedded code be used or not.
   * @param useEmbeddedCode
   */
  public void setUseEmbeddedCode(final boolean useEmbeddedCode){
    this.embeddedCode = useEmbeddedCode;
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

    if (embeddedCode && ast.getInitAction().isPresent()) {
      // Add init action
      addToCodeSection("{", ParserGeneratorHelper.getText(ast.getInitAction().get()), "\n}");
    }

    endCodeSection();

    createAntlrCodeForLexAlts(ast.getAlts());

    // Add Action
    startCodeSection();

    if (embeddedCode && ast.getEndAction().isPresent()) {
      addToCodeSection("{", ParserGeneratorHelper.getText(ast.getEndAction().get()), "\n}");
    }

    if (ast.getLexerCommand().isPresent()) {
      addToCodeSection("->", ast.getLexerCommand().get(), "\n");
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
    String classnameFromRulenameorInterfacename = MCGrammarSymbolTableHelper
            .getQualifiedName(ruleByName.get());

    // Head of Rule
    // Pattern:
    // String tmp =
    // "%name% returns [%uname% ret = %defaultvalue%] %options% ";

    String options = "";

    if (grammarInfo.isProdLeftRecursive(ast.getName())) {
      addToCodeSection("// No code generation because of indirect left recursive rules");
      endCodeSection();
      return;
    }

    // Antlr4: new syntax
    List<ASTAlt> alts = parserHelper.getAlternatives(ast);
    if (alts.isEmpty()) {
      options = "@rulecatch{}";
    }

    // TODO: Antlr4 Dies war die Alternative, wenn es keine Parameter
    // gibt.
    // Ist aber wahrscheinlich so korrekt,
    // erzeugt bestimmt Default für ret ...
    addDummyRules(HelperGrammar.getRuleName(ast), ruleName,
            classnameFromRulenameorInterfacename);

    // Start code codeSection for rules
    addToCodeSection(ruleName);
    // Add actions

    if (embeddedCode && ast.getAction().isPresent() && ast.getAction().get() instanceof ASTAction) {
      addToAction(ParserGeneratorHelper.getText(ast.getAction().get()));
    }
    if (!isActionEmpty()) {
      addToCodeSection("@init");
      addActionToCodeSection();
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
      for(PredicatePair x : subRules) {

        ASTRuleReference ruleRef = x.getRuleReference();
        if (ruleRef.getSemanticpredicateOrAction().isPresent() && ruleRef.getSemanticpredicateOrAction().get().isPredicate()) {
          ruleRef.getSemanticpredicateOrAction().get().accept(getRealThis());
        }


        startCodeSection();
        String subRuleVar = "subRuleVar" + i;
        addToCodeSection("(" + subRuleVar + " = "
                + HelperGrammar.getRuleNameForAntlr(x.getClassname()));
        addToCodeSection(") | \n// end subrules");
        endCodeSection();
      }
    } else {
      endCodeSection();
    }

    // Iterate over all Components
    createAntlrCodeForAlts(alts);

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
            + MCGrammarSymbolTableHelper.getQualifiedName(ruleByName.get()) + " ret = "
            + MCGrammarSymbolTableHelper.getDefaultValue(ruleByName.get()) + "] ");

    addToCodeSection("\n: ");

    String sep = "";
    for(ASTConstant c : ast.getConstants()) {
      addToCodeSection(sep);
      if (grammarInfo.isKeyword(c.getName(), grammarEntry)) {
        addToCodeSection("\n'" + c.getName() + "'");
      } else {
        addToCodeSection("\n", parserHelper.getLexSymbolName(c.getName()));
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
    if (ast.getSymbol().isPresent() && ast.getSymbol().get() instanceof MCProdComponentSymbol) {
      iterated = MCGrammarSymbolTableHelper
              .isConstGroupIterated((MCProdComponentSymbol) ast.getSymbol().get());
    }

    // One entry leads to boolean isMethods
    if (!iterated) {
      ASTConstant x = ast.getConstants().get(0);
      if (!grammarInfo.isKeyword(x.getName(), grammarEntry)) {
        addToCodeSection(parserHelper.getLexSymbolName(x.getName()));
      } else {
        addToCodeSection("'" + x.getName() + "'");
      }

    }

    // More than one entry leads to an int
    else {
      addToCodeSection("(");
      String del = "";
      for(Iterator<ASTConstant> iter = ast.getConstants().iterator(); iter
              .hasNext(); ) {
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
        } else {
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
  public void createAntlrCodeForAlts(List<ASTAlt> alts) {
    String del = "";
    for(Iterator<ASTAlt> iter = alts.iterator(); iter.hasNext(); ) {
      addToAntlrCode(del);

      iter.next().accept(getRealThis());

      del = "|";
    }
  }

  public void createAntlrCodeForLexAlts(List<ASTLexAlt> ast) {
    String del = "";
    for(ASTLexAlt anAst : ast) {
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
    if (embeddedCode && ast.getInitAction().isPresent()) {
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
    } else if (ast.getLexString().isPresent()) {
      ast.getLexString().get().accept(getRealThis());
    } else if (ast.getLexNonTerminal().isPresent()) {
      ast.getLexNonTerminal().get().accept(getRealThis());
    } else if (ast.getLexAnyChar().isPresent()) {
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
      for(ASTOptionValue x : a.getOption().get().getOptionValues()) {
        addToCodeSection("\n  " + x.getKey() + "=" + x.getValue() + ";");
      }
      addToCodeSection("\n }");
    }

    // Print init actions
    if (embeddedCode && a.getInitAction().isPresent()) {
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
    } else {
      rulename = parserHelper.getLexSymbolName(ast.getName().intern());
    }

    //regard the usage name

    if(ast.getUsageName().isPresent()){
      rulename = ast.getUsageName().get() + "=" + rulename;
    }else {
      //if (grammarInfo.isKeyword(ast.getName(), grammarEntry)) {
      //  rulename = uncapitalize(ast.getName()) + "=" + rulename;
      //} else {
      //  rulename = uncapitalize(parserHelper.getLexSymbolName(ast.getName().intern())) + "=" + rulename;
      //}
    }

    // No actions in predicates
    // Template engine cannot be used for substition in rare cases
    addToCodeSection(rulename); // + " %initaction% %actions% ) %iteration% ";

    addActionToCodeSection();

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
    if(embeddedCode) {
      startCodeSection();

      addToCodeSection("{");
      if (ast.getExpressionPredicate().isPresent()) {
        addToCodeSection(ParserGeneratorHelper.getText(ast.getExpressionPredicate().get()), "}");
      } else if (ast.getAction().isPresent()) {
        addToCodeSection(ParserGeneratorHelper.getText(ast.getAction().get()), "}");
      } else {
        Log.error("0xA0327 neither expression predicate nor action is set.");
      }
      if (ast.isPredicate()) {
        addToCodeSection("?");
      }

      endCodeSection();
    }
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
      Log.error("0xA2201 Production symbol for " + ast.getName() + " couldn't be resolved.",
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

  class NodePair {
    ASTGrammarNode alternative;
    PredicatePair pp;

    /**
     * Constructor for de.monticore.codegen.parser.antlr.NodePair.
     *
     * @param alternative
     * @param ruleReference
     */
    public NodePair(ASTGrammarNode alternative, PredicatePair pp) {
      this.alternative = alternative;
      this.pp = pp;
    }

    /**
     * @return the alternative
     */
    public ASTGrammarNode getAlternative() {
      return this.alternative;
    }

    /**
     * @return the ruleReference
     */
    public PredicatePair getPredicatePair() {
      return this.pp;
    }
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
    String usageName = MCGrammarSymbolTableHelper.getQualifiedName(interfaceRule);

    startCodeSection(interfaceRule.getName());

    addDummyRules(interfacename, ruleName, usageName);

    addToAntlrCode(HelperGrammar.getRuleNameForAntlr(interfacename));
    addToAntlrCode(": ");

    List<Grammar2PythonAntlr.NodePair> alts = new ArrayList<>();
    String del = "";
    // Get all implementing/extending interfaces
    List<PredicatePair> interfaces = grammarInfo.getSubRulesForParsing(interfacename);
    boolean left = false;
    for(PredicatePair interf : interfaces) {
      Optional<MCProdSymbol> symbol = grammarEntry.getSpannedScope().<MCProdSymbol>resolve(interf.getClassname(), MCProdSymbol.KIND);
      if (!symbol.isPresent()) {
        continue;
      }
      MCProdSymbol prodSymbol = symbol.get();
      if (!prodSymbol.getAstNode().isPresent()) {
        continue;
      }
      ASTNode astNode = prodSymbol.getAstNode().get();
      if (prodSymbol.isClass() && grammarInfo.isProdLeftRecursive(prodSymbol.getName())) {
        left = true;
        List<ASTAlt> localAlts = ((ASTClassProd) astNode).getAlts();
        for(ASTAlt alt : localAlts) {
          alts.add(new Grammar2PythonAntlr.NodePair(alt, interf));
        }
      } else {
        alts.add(new Grammar2PythonAntlr.NodePair((ASTGrammarNode) astNode, interf));
      }
    }
    // Append sorted alternatives
    Collections.sort(alts, (p2, p1) -> new Integer(p1.getPredicatePair().getRuleReference().getPrio().orElse("0")).compareTo(new Integer(p2.getPredicatePair().getRuleReference().getPrio().orElse("0"))));
    for(Grammar2PythonAntlr.NodePair entry : alts) {
      addToAntlrCode(del);

      // Append semantic predicates for rules
      if (entry.getPredicatePair().getRuleReference().getSemanticpredicateOrAction().isPresent()) {
        ASTSemanticpredicateOrAction semPredicate = entry.getPredicatePair().getRuleReference().getSemanticpredicateOrAction().get();
        if (semPredicate.isPredicate()) {
          semPredicate.accept(getRealThis());
        }
      }

      if (entry.getAlternative() instanceof ASTAlt) {
        // Left recursive rule
        ASTAlt alt = (ASTAlt) entry.getAlternative();
        String className = entry.getPredicatePair().getClassname();
        alt.accept(getRealThis());
      } else {
        if (left && entry.getAlternative() instanceof ASTClassProd && ((ASTClassProd) entry.getAlternative()).getAlts().size() == 1) {
          ASTAlt alt = ((ASTClassProd) entry.getAlternative()).getAlts().get(0);
          String className = entry.getPredicatePair().getClassname();
          alt.accept(getRealThis());
        } else {
          // normal rule
          startCodeSection();
          String tmpVar = parserHelper.getTmpVarName(entry.getAlternative());
          addToCodeSection(tmpVar + "="
                  + HelperGrammar.getRuleNameForAntlr(entry.getPredicatePair().getClassname()));
          endCodeSection();
        }
      }

      del = "|";
    }

    addToAntlrCode(";");

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

    if (ast.getUsageName().isPresent()) {
      addToCodeSection(ast.getUsageName().get(), "=", ast.getName());
    }else if(ast.getSymbol().isPresent()){
      addToCodeSection(uncapitalize(ast.getSymbol().get().getName()), "=", ast.getName());
    }else{
      addToCodeSection(ast.getName());
    }

    addToCodeSection("\n");

    endCodeSection();

    if (ast.isPlusKeywords()) {
      addToAntlrCode("/* Automatically added keywords " + grammarInfo.getKeywords()
              + " */");

      // TODO PN, GV
      for(String y : grammarInfo.getKeywords()) {
        addToAntlrCode(" | ");
        ASTTerminal term = GrammarNodeFactory.createASTTerminal();
        ast.get_Children().add(term);
        // term.set_Parent(ast);
        term.setName(y);
        term.setUsageName(HelperGrammar.getUsuageName(ast));

        Optional<? extends Symbol> ruleComponent = ast.getSymbol();
        if (ruleComponent.isPresent() && ruleComponent.get() instanceof MCProdComponentSymbol) {
          MCProdComponentSymbol componentSymbol = (MCProdComponentSymbol) ruleComponent.get();
          Optional<MCProdSymbol> rule = MCGrammarSymbolTableHelper
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
    Optional<MCProdSymbol> scope = MCGrammarSymbolTableHelper.getEnclosingRule(ast);

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

    String tmpVarName;
    if (ast.getUsageName().isPresent()){
      tmpVarName = ast.getUsageName().get();
    }else{
      tmpVarName = ast.getName();
    }

    addToCodeSection(braceopen, " ", tmpVarName, "=", HelperGrammar.getRuleNameForAntlr(ast));

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

    addToCodeSection(")", printIteration(keyword.getIteration()));

    endCodeSection();

  }

  private void addDummyRules(String rulenameInternal, String ruleName,
                             String usageName) {

    addToCodeSection("\n\n", ruleName, "_eof");

    addToCodeSection(" :\n tmp = ", ruleName, " ");

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

  public boolean isEmbeddedJavaCode() {
    return this.embeddedJavaCode;
  }


  // ----------------------------------------------------------

  /**
   * Gets the antlr code (for printing)
   *
   * @return
   */
  protected List<String> getAntlrCode() {
    return ImmutableList.copyOf(productionAntlrCode);
  }

  /**
   * Adds the given code to antlr code
   *
   * @param code
   */
  protected void addToAntlrCode(String code) {
    productionAntlrCode.add(code);
  }

  /**
   * Clears antlr code
   */
  protected void clearAntlrCode() {
    productionAntlrCode.clear();
  }

  /**
   * Adds the given code to antlr code
   *
   * @param code
   */
  protected void addToAntlrCode(StringBuilder code) {
    addToAntlrCode(code.toString());
  }

  /**
   * Starts codeSection of the parser code
   */
  protected void startCodeSection() {
    codeSection = new StringBuilder();
  }

  /**
   * Adds the current code codeSection to antlr
   */
  protected void endCodeSection() {
    addToAntlrCode(codeSection);
    codeSection = new StringBuilder();
  }

  /**
   * Starts antlr code for the given production
   *
   * @param ast
   */
  protected void startCodeSection(ASTNode ast) {
    startCodeSection(ast.getClass().getSimpleName());
  }

  /**
   * Starts antlr code for the production with the given name
   */
  protected void startCodeSection(String text) {
    codeSection = new StringBuilder("\n // Start of '" + text + "'\n");
  }

  /**
   * Ends antlr code for the given production
   *
   * @param ast
   */
  protected void endCodeSection(ASTNode ast) {
    codeSection.append("// End of '" + ast.getClass().getSimpleName() + "'\n");
    endCodeSection();
  }

  /**
   * Adds the given code to the current codeSection
   */
  protected void addToCodeSection(String... code) {
    Arrays.asList(code).forEach(s -> codeSection.append(s));
  }

  /**
   * @return codeSection
   */
  public StringBuilder getCodeSection() {
    return this.codeSection;
  }

  /**
   * Adds code for parser action to the current code codeSection
   */
  protected void addActionToCodeSection() {
    if (action.length() != 0) {
      if (embeddedCode) {
        addToCodeSection("{", action.toString(), "}");
      }
      clearAction();
    }
  }

  /**
   * Adds code for parser action to the current code codeSection starting and
   * ending with a new line
   */
  protected void addActionToCodeSectionWithNewLine() {
    if (action.length() != 0) {
      if (embeddedCode) {
        addToCodeSection("{\n", action.toString(), "\n}");
      }
      clearAction();
    }
  }

  /**
   * Clears code for parser action
   */
  protected void clearAction() {
    action.setLength(0);
  }

  protected boolean isActionEmpty() {
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
  protected void addToAction(String... code) {
    Arrays.asList(code).forEach(s -> action.append(s));
  }


}