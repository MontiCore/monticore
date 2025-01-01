/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser.antlr;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.parser.MCGrammarInfo;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.PredicatePair;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar._visitor.GrammarHandler;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static de.monticore.codegen.mc2cd.TransformationHelper.getQualifiedName;
import static de.monticore.codegen.parser.ParserGeneratorHelper.getDefaultValue;
import static de.monticore.codegen.parser.ParserGeneratorHelper.printIteration;

public class Grammar2Antlr implements GrammarVisitor2, GrammarHandler {

  protected MCGrammarSymbol grammarEntry;

  protected GrammarTraverser traverser;

  /**
   * This list is used for the detection of the left recursion
   */
  protected ArrayList<ASTAlt> altList = new ArrayList<>();

  protected AttributeCardinalityConstraint attributeConstraints;

  protected StringBuilder codeSection;

  protected MCGrammarInfo grammarInfo;

  protected ParserGeneratorHelper parserHelper;

  protected boolean embeddedJavaCode;

  protected Map<ASTProd, Map<ASTNode, String>> tmpNameDict = new LinkedHashMap<>();

  public Grammar2Antlr(
      ParserGeneratorHelper parserGeneratorHelper,
      MCGrammarInfo grammarInfo) {
      new Grammar2Antlr(parserGeneratorHelper, grammarInfo, true);
  }

  public Grammar2Antlr(
      ParserGeneratorHelper parserGeneratorHelper,
      MCGrammarInfo grammarInfo,
      boolean embeddedJavaCode) {
    Preconditions.checkArgument(parserGeneratorHelper.getGrammarSymbol() != null);
    this.attributeConstraints = new AttributeCardinalityConstraint(parserGeneratorHelper);
    this.grammarEntry = parserGeneratorHelper.getGrammarSymbol();
    this.grammarInfo = grammarInfo;
    this.parserHelper = parserGeneratorHelper;
    this.codeSection = new StringBuilder();

    this.embeddedJavaCode = embeddedJavaCode;
  }

  /**
   * Prints Lexer rule
   *
   * @param ast - lexer production
   */
  @Override
  public void handle(ASTLexProd ast) {
    addToCodeSection("// ASTLexProd ",ast.getName(), "\n");

    if (ast.isFragment()) {
      addToCodeSection("fragment ");
    }

    addToCodeSection(ast.getName(), " ");

    // Print option
    if (ast.isPresentLexOption()) {
      ast.getLexOption().accept(getTraverser());
    }

    addToCodeSection("\n:");

    if (embeddedJavaCode && ast.isPresentInitAction()) {
      // Add init action
      addToCodeSection("{", ParserGeneratorHelper.getText(ast.getInitAction()), "\n}");
    }

    createAntlrCodeForLexAlts(ast.getAltList());

    if (embeddedJavaCode && ast.isPresentEndAction()) {
      addToCodeSection("{", ParserGeneratorHelper.getText(ast.getEndAction()), "\n}");
    }

    if (ast.isPresentLexerCommand()) {
      addToCodeSection("->", ast.getLexerCommand());
      if (!ast.isEmptyParameter()) {
        addToCodeSection("(");
        String sep = "";
        for (String s: ast.getParameterList()) {
          addToCodeSection(sep, s);
          sep = ", ";
        }
        addToCodeSection(")");
      }
    }

    addToCodeSection(";\n");
  }

  /**
   * Prints Parser rule
   *
   * @param ast parser production
   */
  @Override
  public void handle(ASTClassProd ast) {
    addToCodeSection("\n// ASTClassProd ", ast.getName(), "\n");

    // Create eof and dummy rules
    String ruleName = getRuleNameForAntlr(ast.getName());

    if (ast.getSymbol().isIsIndirectLeftRecursive()) {
      addToCodeSection("// No code generation because of indirect left recursive rules");
      return;
    }

    // Antlr4: new syntax
    List<ASTAlt> alts = parserHelper.getAlternatives(ast);

    // Start code codeSection for rules
    addToCodeSection(ruleName);
    List<PredicatePair> subRules = grammarInfo
        .getSubRulesForParsing(ast.getName());

    if (embeddedJavaCode) {
      // Add actions
      String action = "";
      if (ast.isPresentAction()) {
        action += ParserGeneratorHelper.getText(ast.getAction()) + "\n";
      }

      // Action at beginning of rule @init
      action += attributeConstraints.addActionForRuleBeforeRuleBody(ast);
      if (!action.isEmpty()) {
        addToCodeSection("\n@init", " {", action, " " +
            "}\n");
      }

      // Action at end of rule
      action = attributeConstraints.addActionForRuleAfterRuleBody(ast);
      if (!action.isEmpty()) {
        addToCodeSection("\n@after", "{", action, "}\n");
      }
    }
    addToCodeSection(" : \n");

    if (subRules != null && !subRules.isEmpty()) {

      addToCodeSection("// Adding subrules\n");

      int i = 0;
      for (PredicatePair x : subRules) {

        ASTRuleReference ruleRef = x.getRuleReference();
        if (ruleRef.isPresentSemanticpredicateOrAction() && ruleRef.getSemanticpredicateOrAction().isPredicate()) {
          ruleRef.getSemanticpredicateOrAction().accept(getTraverser());
        }

        String subRuleVar = "subRuleVar" + i;
        addToCodeSection("(" + subRuleVar + " = " + getRuleNameForAntlr(x.getClassname()));
        addToCodeSection(") | \n");
        i++;
      }
      addToCodeSection("// end subrules\n");
    }

    // Iterate over all Components
    createAntlrCodeForAlts(alts);

    addDummyRules(ast.getName());

    addToCodeSection(";\n");
  }

  @Override
  public void handle(ASTEnumProd ast) {
     addToCodeSection("\n// ASTEnumProd ", ast.getName(), "\n");

    // Create rule
    String ruleName = getRuleNameForAntlr(ast.getName());
    ProdSymbol ruleByName = ast.getSymbol();

    // Head of Rule
    addToCodeSection(ruleName + " returns ["
        + getQualifiedName(ruleByName) + " ret = "
        + getDefaultValue(ruleByName) + "] ");

    addToCodeSection("\n: ");

    String sep = "";
    int index = 0;
    for (ASTConstant c : ast.getConstantList()) {
      addToCodeSection(sep);
      addToCodeSection("\n", "e_" + index++ + "=" + parserHelper.getOrComputeLexSymbolName(c.getName()));

      if (embeddedJavaCode) {
        String temp1 = "";
        temp1 += "$ret = " + getQualifiedName(ruleByName)
            + "."
            + parserHelper.getConstantNameForConstant(c) + ";";

        addToCodeSection("\n{" + temp1 + "}");

      }
      sep = "|";
    }
    addToCodeSection(";\n");
  }

  /**
   * Handles a ConstantGroup (something in [] )
   *
   * @param ast
   */
  @Override
  public void handle(ASTConstantGroup ast) {

    boolean iterated = false;
    if (ast.isPresentSymbol()) {
      iterated = TransformationHelper
          .isConstGroupIterated(ast.getSymbol());
    }

    addToCodeSection("(");
    String del = "";
    String tmpName = parserHelper.getTmpVarName(ast);
    String label = "=";

    for (ASTConstant x: ast.getConstantList()) {
      addToCodeSection(del);

      if (iterated) {
        tmpName = parserHelper.getTmpVarName(x);
      }

      if (x.isPresentKeyConstant()) {
        addToCodeSection(createKeyPredicate(x.getKeyConstant().getStringList(), tmpName + label));
      } else if (!grammarInfo.isKeyword(x.getName())) {
        addToCodeSection(tmpName + label + parserHelper.getOrComputeLexSymbolName(x.getName()));
      } else {
        addToCodeSection(tmpName + label + parserHelper.getOrComputeLexSymbolName(x.getName()));
      }

      del = "|\n";
    }
    addToCodeSection(")", printIteration(ast.getIteration()));
  }

  /**
   * Print alternatives
   *
   * @param alts
   */
  public void createAntlrCodeForAlts(List<ASTAlt> alts) {
    String del = "";
    for (ASTAlt alt: alts) {
      addToCodeSection(del);

      alt.accept(getTraverser());

      del = "|";
    }
  }

  public void createAntlrCodeForLexAlts(List<ASTLexAlt> ast) {
    String del = "";
    for (ASTLexAlt anAst : ast) {
      addToCodeSection(del);

      anAst.accept(getTraverser());

      del = "|";
    }
  }

  @Override
  public void handle(ASTLexBlock ast) {
    if (ast.isNegate()) {
      addToCodeSection("~");
    }

    // Start of Block
    addToCodeSection("(");
    if (ast.isPresentOption()) {
      addToCodeSection("\noptions {", ast.getOption().getID(), "=", ast.getOption()
          .getValue(), ";}");
    }

    // Visit all alternatives
    createAntlrCodeForLexAlts(ast.getLexAltList());

    // print iteration
    addToCodeSection(")\n", printIteration(ast.getIteration()));
  }

  @Override
  public void handle(ASTLexSimpleIteration ast) {
    addToCodeSection("(");

    // Visit all alternatives
    if (ast.isPresentLexChar()) {
      ast.getLexChar().accept(getTraverser());
    } else if (ast.isPresentLexString()) {
      ast.getLexString().accept(getTraverser());
    } else if (ast.isPresentLexNonTerminal()) {
      ast.getLexNonTerminal().accept(getTraverser());
    } else if (ast.isPresentLexAnyChar()) {
      ast.getLexAnyChar().accept(getTraverser());
    }

    // Close block and print iteration
    addToCodeSection(")\n", printIteration(ast.getIteration()));

    if (ast.isQuestion()) {
      addToCodeSection("?");
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
    addToCodeSection("(");
    // Print options
    if (a.isPresentOption()) {
      addToCodeSection("\n  options {");
      for (ASTOptionValue x : a.getOption().getOptionValueList()) {
        addToCodeSection("\n  " + x.getKey() + "=" + x.getValue() + ";");
      }
      addToCodeSection("\n }");
    }

    ruleIteratedStack.push((!ruleIteratedStack.isEmpty() && ruleIteratedStack.peek()) || isIterated(a.getIteration()));

    // Visit all alternatives
    createAntlrCodeForAlts(a.getAltList());

    ruleIteratedStack.pop();

    // print iteration
    addToCodeSection(")", printIteration(a.getIteration()), "\n");
  }

  @Override
  public void visit(ASTTerminal ast) {
    String rulename;
    if (ast.getName().isEmpty()) {
      rulename = "";
    } else {
      rulename = parserHelper.getOrComputeLexSymbolName(ast.getName().intern());
    }

    // No actions in predicates
    // Template engine cannot be used for substition in rare cases
    boolean isAttribute = ast.isPresentUsageName();
    boolean isListOrOpt = ast.isPresentSymbol() && (ast.getSymbol().isIsList() || ast.getSymbol().isIsOptional());
    if (isAttribute && isListOrOpt) {
      addToCodeSection("(");
    }
    Map<String, Collection<String>> keywords = grammarEntry.getReplacedKeywordsWithInherited();
    if (keywords.containsKey(ast.getName())) {
      addToCodeSection("(");
      String seperator = "";
      int nokeywordindex = 0; // we use an index
      for (String replaceString: keywords.get(ast.getName())) {
        addToCodeSection(seperator);
        addToCodeSection(parserHelper.getTmpVarName(ast) + "_nk" + nokeywordindex++);
        addToCodeSection(ast.isPresentSymbol() && ast.getSymbol().isIsList() ? "+=" : "=");

        addToCodeSection(parserHelper.getOrComputeLexSymbolName(replaceString));
        seperator = " | ";
      }
      addToCodeSection(")");
    } else {
      addToCodeSection(" ", parserHelper.getTmpVarName(ast));
      addToCodeSection((ast.isPresentSymbol() && ast.getSymbol().isIsList() ? "+=" : "="));
      addToCodeSection(rulename);
    }

    if (isAttribute && isListOrOpt) {
      addToCodeSection(")");
    }
    addToCodeSection(printIteration(ast.getIteration()));


  }

  protected String createKeyPredicate(List<String> stringList, String tmpNamePlusLbl) {
    String rulename = "(";
    String sep = "";
    for (String key : stringList) {
      rulename += sep;
      sep = " | ";
      rulename += tmpNamePlusLbl + parserHelper.getOrComputeLexSymbolName(key);

    }
    rulename += ")";
    return rulename;
  }

  @Override
  public void visit(ASTKeyTerminal ast) {

    addToCodeSection("(");

    String labelAssignment = ast.isPresentSymbol() && ast.getSymbol().isIsList()  ? "+=" : "=";

    String tmpVarName = parserHelper.getTmpVarName(ast);
    var replacedAdditionalKeywords = grammarEntry.getReplacedKeywordsWithInherited();
    if (replacedAdditionalKeywords.containsKey(ast.getName())) {
      handleTerminal(labelAssignment, tmpVarName, replacedAdditionalKeywords, ast.getName());
    } else if (ast.getKeyConstant().sizeStrings() == 1) {
      // Extra case for one-long strings
      addToCodeSection(tmpVarName + labelAssignment + parserHelper.getOrComputeLexSymbolName(ast.getKeyConstant().getString(0)));
    } else {
      // replace keyword is currently not implemented for multiple key
      addToCodeSection("(");
      String seperator = "";
      int nkIndex = 0;
      for (String keyString : ast.getKeyConstant().getStringList()) {
        addToCodeSection(seperator);
        addToCodeSection(tmpVarName + "_key" + nkIndex++ + labelAssignment + parserHelper.getOrComputeLexSymbolName(keyString));
        seperator = "|";
      }
      addToCodeSection(")");
    }

    addToCodeSection(")");
    addToCodeSection(printIteration(ast.getIteration()));

  }

  void handleTerminal(String labelAssignment, String tmpVarName, Map<String, Collection<String>> replacedAdditionalKeywords, String name) {
    addToCodeSection("(");
    String seperator = "";
    int nokeywordindex = 0;
    for (String replaceString: replacedAdditionalKeywords.get(name)) {
      addToCodeSection(seperator);
      addToCodeSection(tmpVarName + "_nk" + nokeywordindex++);
      addToCodeSection(labelAssignment);
      addToCodeSection(parserHelper.getOrComputeLexSymbolName(replaceString));
      seperator = " | ";
    }
    addToCodeSection(")");
  }

  @Override
  public void visit(ASTTokenTerminal ast) {
    addToCodeSection("(");

    String labelAssignment = ast.isPresentSymbol() && ast.getSymbol().isIsList()  ? "+=" : "=";
    String tmpVarName = parserHelper.getTmpVarName(ast);
    var replacedAdditionalKeywords = grammarEntry.getReplacedKeywordsWithInherited();
    if (replacedAdditionalKeywords.containsKey(ast.getName())) {
      handleTerminal(labelAssignment, tmpVarName, replacedAdditionalKeywords, ast.getName());
    } else {
      addToCodeSection(tmpVarName + labelAssignment + parserHelper.getOrComputeLexSymbolName(ast.getTokenConstant().getString()));
    }

    addToCodeSection(")");
    addToCodeSection(printIteration(ast.getIteration()));
  }

  @Override
  public void visit(ASTLexCharRange ast) {
    if (ast.isNegate()) {
      addToCodeSection("~");
    }
    addToCodeSection("'", ast.getLowerChar(), "'..'", ast.getUpperChar(), "'  ");
  }

  @Override
  public void visit(ASTLexChar a) {
    if (a.isNegate()) {
      addToCodeSection("~");
    }
    addToCodeSection("'", a.getChar(), "' ");
  }

  @Override
  public void visit(ASTLexAnyChar a) {
    addToCodeSection(".");
  }

  @Override
  public void visit(ASTLexString a) {
    addToCodeSection("'");
    addToCodeSection(a.getString(), "' ");
  }

  @Override
  public void visit(ASTLexActionOrPredicate a) {
    addToCodeSection("{");
    addToCodeSection(ParserGeneratorHelper.getText(a.getExpressionPredicate()), "}");
    if (a.isPredicate()) {
      addToCodeSection("?");
    }
  }

  @Override
  public void visit(ASTLexNonTerminal ast) {
    addToCodeSection(" ");
    addToCodeSection(ast.getName(), " ");
  }

  @Override
  public void visit(ASTLexOption ast) {
    addToCodeSection("options {" + ast.getID() + " = " + ast.getValue() + "; } \n");
  }

  /**
   * Print anything in {} which can be an Action or a semantic predicate for
   * antlr, depending on a ? at the end of the block
   *
   * @param ast
   */
  @Override
  public void visit(ASTSemanticpredicateOrAction ast) {
    if (embeddedJavaCode) {
      addToCodeSection("{");
      if (ast.isPresentExpressionPredicate()) {
        addToCodeSection(ParserGeneratorHelper.getText(ast.getExpressionPredicate()), "}");
      } else if (ast.isPresentAction()) {
        addToCodeSection(ParserGeneratorHelper.getText(ast.getAction()), "}");
      } else {
        Log.error("0xA0327 neither expression predicate nor action is set.");
      }
      if (ast.isPredicate()) {
        addToCodeSection("?");
      }
    }
  }

  /**
   * Handles a non-terminal thats stated in a grammar
   *
   * @param ast
   */
  @Override
  public void visit(ASTNonTerminal ast) {
    Optional<ProdSymbol> prod = grammarEntry.getProdWithInherited(ast.getName());
    if (!prod.isPresent()) {
      Log.error("0xA2201 Production symbol for " + ast.getName() + " couldn't be resolved.",
          ast.get_SourcePositionStart());
    }
    // Lexer Rule
    if (prod.get().isIsLexerProd()) {
      addCodeForLexerRule(ast);
    }
    // Other Rule called
    else if (prod.get().isParserProd()
        ||
        prod.get().isIsInterface()
        ||
        prod.get().isIsAbstract()
        ||
        prod.get().isIsEnum()) {

      addCodeForRuleReference(ast);
    }
  }

  @Override
  public void visit(ASTAlt alt) {
    altList.add(alt);
    if (alt.isRightAssoc()) {
      addToCodeSection(ParserGeneratorHelper.RIGHTASSOC);
    }
    if (embeddedJavaCode && alt.isPresentGrammarAnnotation() && alt.getGrammarAnnotation().isDeprecated()) {
      String t = alt.getGrammarAnnotation().isPresentMessage() ? alt.getGrammarAnnotation().getMessage() : "";
      String message = "Deprecated syntax: " + t;
      addToCodeSection("{", "de.se_rwth.commons.logging.Log.warn(\"" + message + "\");", "{\n");
    }
  }

  @Override
  public void endVisit(ASTAlt alt) {
    if (!altList.isEmpty()) {
      altList.remove(altList.size() - 1);
    }
  }

  public void traverse(ASTAbstractProd node) {
    //no parser generation for abstract classes
  }

  // ----------------- End of visit methods
  // ---------------------------------------------

  public List<String> createAntlrCode(ASTProd ast) {
    clearAntlrCode();
    parserHelper.resetTmpVarNames();
    ast.accept(getTraverser());
    tmpNameDict.put(ast, new LinkedHashMap<>(parserHelper.getTmpVariables()));
    return getAntlrCode();
  }

  class NodePair {
    ASTGrammarNode alternative;
    PredicatePair pp;

    /**
     * Constructor for de.monticore.codegen.parser.antlr.NodePair.
     *
     * @param alternative
     * @param pp
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
  public List<String> createAntlrCodeForInterface(ProdSymbol interfaceRule) {

    clearAntlrCode();
    parserHelper.resetTmpVarNames();

    String interfacename = interfaceRule.getName();

    addToCodeSection("\n// ASTInterface ", interfaceRule.getName(), "\n");
    addToCodeSection(getRuleNameForAntlr(interfacename), ":", "\n");

    List<NodePair> alts = new ArrayList<>();
    String del = "";
    // Get all implementing/extending interfaces
    boolean left = addAlternatives(interfaceRule, alts);

    // Append sorted alternatives
    Collections.sort(alts, (p2, p1) ->
        Integer.valueOf(p1.getPredicatePair().getRuleReference().isPresentPrio() ? p1.getPredicatePair().getRuleReference().getPrio() : "0").compareTo(
            Integer.valueOf(p2.getPredicatePair().getRuleReference().isPresentPrio() ? p2.getPredicatePair().getRuleReference().getPrio() : "0")));

    for (NodePair entry : alts) {
      addToCodeSection(del);

      // Append semantic predicates for rules
      if (entry.getPredicatePair().getRuleReference().isPresentSemanticpredicateOrAction()) {
        ASTSemanticpredicateOrAction semPredicate = entry.getPredicatePair().getRuleReference().getSemanticpredicateOrAction();
        if (semPredicate.isPredicate()) {
          semPredicate.accept(getTraverser());
        }
      }

      if (entry.getAlternative() instanceof ASTAlt) {
        // Left recursive rule
        ASTAlt alt = (ASTAlt) entry.getAlternative();

        alt.accept(getTraverser());
      } else {
        if (left && entry.getAlternative() instanceof ASTClassProd && ((ASTClassProd) entry.getAlternative()).getAltList().size() == 1) {
          ASTAlt alt = ((ASTClassProd) entry.getAlternative()).getAltList().get(0);
          alt.accept(getTraverser());
        } else {
          // normal rule
          String tmpVar = parserHelper.getTmpVarName(entry.getAlternative());
          addToCodeSection(tmpVar + "="
              + getRuleNameForAntlr(entry.getPredicatePair().getClassname()));
        }
      }

      del = " |\n";
    }

    addDummyRules(interfacename);

    addToCodeSection(";\n");

    tmpNameDict.put(interfaceRule.getAstNode(), new LinkedHashMap<>(parserHelper.getTmpVariables()));

    return getAntlrCode();
  }

  /**
   * @param prodSymbol
   * @param alts
   */
  protected boolean addAlternatives(ProdSymbol prodSymbol, List<NodePair> alts) {
    boolean isLeft = false;
    List<PredicatePair> interfaces = grammarInfo.getSubRulesForParsing(prodSymbol.getName());
    for (PredicatePair interf : interfaces) {
      Optional<ProdSymbol> symbol = grammarEntry.getSpannedScope().resolveProd(interf.getClassname());
      if (!symbol.isPresent()) {
        continue;
      }
      ProdSymbol superSymbol = symbol.get();
      if (!prodSymbol.isPresentAstNode()) {
        continue;
      }
      ASTGrammarNode astNode = superSymbol.getAstNode();
      if (superSymbol.isIsIndirectLeftRecursive()) {
        isLeft = true;
        if (superSymbol.isClass()) {
          List<ASTAlt> localAlts = ((ASTClassProd) astNode).getAltList();
          for (ASTAlt alt : localAlts) {
            alts.add(new NodePair(alt, interf));
          }
        } else if (prodSymbol.isIsInterface()) {
          addAlternatives(superSymbol, alts);
        }
      } else {
        alts.add(new NodePair( astNode, interf));
      }
    }
    return isLeft;
  }

  public List<String> getHWParserJavaCode() {
    return grammarInfo.getAdditionalParserJavaCode();
  }

  public List<String> getHWLexerJavaCode() {
    return grammarInfo.getAdditionalLexerJavaCode();
  }

  public Map<ASTProd, Map<ASTNode, String>> getTmpNameDict() {
    return tmpNameDict;
  }

  // ----------------------------------------------------------------------------------------------

  protected void addCodeForLexerRule(ASTNonTerminal ast) {
    addToCodeSection(" ( ");

    // AntLR2 -> AntLR4: Replace : by =
    // tmp = "( %tmp%=%rulename% %initaction% %actions%";

    // In case the AST has a list of this rule (*/+), we use += instead of = in the g4 rule
    boolean isRuleIterated = getASTMax(ast);

    String tmpName = parserHelper.getTmpVarName(ast);
    if ("Name".equals(ast.getName())) {
      // Due to special no-keyword handling, Name lexer-rules might be substituted with an intermediate rule
      // This (sh/c)ould be extended to other lexer rules
      String nameRule = ast.isPlusKeywords() ? "name" + ParserGeneratorHelper.WITH_KEYWORDS_RULE_SUFFIX : "name" + ParserGeneratorHelper.INCL_NO_KEYWORDS_RULE_SUFFIX;
      addToCodeSection(tmpName, isRuleIterated ? "+=" : "=", nameRule);
    } else {
      addToCodeSection(tmpName, isRuleIterated ? "+=" : "=", ast.getName());
    }

    if (embeddedJavaCode) {
      // Add Actions
      Optional<ProdSymbol> scope = MCGrammarSymbolTableHelper.getEnclosingRule(ast);

      if (scope.isPresent()) {
        String action = attributeConstraints.addActionForNonTerminal(ast);
        if (!action.isEmpty()) {
          addToCodeSection("{", action, "}\n");
        }
      }
    }

    // Legacy-workaround AddKeywordsTest: B = INT& ;
    if (ast.isPlusKeywords() && !ast.getName().equals("Name")) {
      addToCodeSection("/* Automatically added keywords " + grammarInfo.getKeywords()
                               + " */\n");

      ArrayList<String> keys = Lists.newArrayList(grammarInfo.getKeywords());
      keys.removeAll(grammarInfo.getKeywordRules());
      for (String y : keys) {
        addToCodeSection(" | ");
        ASTTerminal term = Grammar_WithConceptsMill.terminalBuilder()
                .setName(y).build();

        if (ast.isPresentSymbol()) {
          RuleComponentSymbol componentSymbol = ast.getSymbol();
          Optional<ProdSymbol> rule = MCGrammarSymbolTableHelper
                  .getEnclosingRule(componentSymbol);
          term.setUsageName(ParserGeneratorHelper.getUsageName(ast));

          if (rule.isPresent()) {
            addActionForKeyword(term, rule.get(), componentSymbol.isIsList(), tmpName + (isRuleIterated?"+=":"="));
          }
        }
      }
    }

    addToCodeSection(") " + printIteration(ast.getIteration()));
  }


  // Stack for Block iterations (Prod)* => should result in tmp +=
  protected Stack<Boolean> ruleIteratedStack = new Stack<>();

  /**
   * Print code for references to other rules in the same grammar
   *
   * @param ast
   * @return
   */
  protected void addCodeForRuleReference(ASTNonTerminal ast) {
    Optional<ProdSymbol> scope = MCGrammarSymbolTableHelper.getEnclosingRule(ast);
    if (!scope.isPresent()) {
      return;
    }

    // In star enviroment use add-method, else use set methods
    // Do not build up ast in predicates

    String iteration = printIteration(ast.getIteration());
    String braceopen = iteration.isEmpty() ? "" : "(";
    String braceclose = iteration.isEmpty() ? "" : ")";

    String tmpVarName = parserHelper.getTmpVarName(ast);

    // In case the AST has a list of this rule (*/+), we use += instead of = in the g4 rule
    boolean isRuleIterated = getASTMax(ast);

    String label = isRuleIterated ? "+=":"=";
    addToCodeSection(braceopen, " ", tmpVarName, label, getRuleNameForAntlr(ast.getName()));

    if (embeddedJavaCode) {
      String action = attributeConstraints.addActionForNonTerminal(ast);
      if (!action.isEmpty()) {
        addToCodeSection("{", action, "}\n");
      }
    }

    addToCodeSection(braceclose, " ", iteration, " ");

  }

  boolean getASTMax(ASTNonTerminal ast) {
    String usageName = ParserGeneratorHelper.getUsageName(ast);
    Optional<Integer> max = ast.getEnclosingScope().getLocalAdditionalAttributeSymbols()
        .stream()
        .filter(e -> e.getName().equals(usageName))
        .map(TransformationHelper::getMax)
        .map(e->e.orElse(1))
        .findFirst();

    boolean isRuleIterated = (!ruleIteratedStack.isEmpty() && ruleIteratedStack.peek()) || isIterated(ast.getIteration());

    if (max.isPresent() && max.get() == 1)
      return false; // an astrule forcefully set the cardinality
    return isRuleIterated;
  }

  protected void addActionForKeyword(ASTTerminal keyword, ProdSymbol rule, boolean isList, String tmpNamePlusLbl) {
    addToCodeSection("(");
    String rulename = "";
    if (grammarInfo.isKeyword(keyword.getName())) {
      rulename = parserHelper.getOrComputeLexSymbolName(keyword.getName());
    }

    // No actions in predicates
    // Template engine cannot be used for substition in rare cases
    addToCodeSection(tmpNamePlusLbl + rulename); // + " %initaction% %actions% ) %iteration% ";
    addToCodeSection(")", printIteration(keyword.getIteration()));
  }

  protected void addDummyRules(String rulenameInternal) {
    Optional<ASTAlt> follow2 = parserHelper.getAlternativeForFollowOption(rulenameInternal);
    if (!follow2.isPresent()) {
      return;
    }
    follow2.get().accept(getTraverser());

  }

  public boolean isEmbeddedJavaCode() {
    return this.embeddedJavaCode;
  }


  // ----------------------------------------------------------

  /**
   * Returns Human-Readable, antlr conformed name for a rulename
   *
   * @param rulename rule name
   * @return Human-Readable, antlr conformed rule name
   */
  public static String getRuleNameForAntlr(String rulename) {
    return JavaNamesHelper.getNonReservedName(StringTransformations.uncapitalize(rulename));
  }

  /**
   * Gets the antlr code (for printing)
   *
   * @return
   */
  protected List<String> getAntlrCode() {
    return Arrays.asList(codeSection.toString().split("\\n"));
  }

  /**
   * Clears antlr code
   */
  protected void clearAntlrCode() {
    codeSection = new StringBuilder();
  }


  /**
   * Adds the given code to the current codeSection
   */
  protected void addToCodeSection(String... code) {
    Arrays.asList(code).forEach(s -> codeSection.append(s));
  }


  @Override
  public GrammarTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(GrammarTraverser traverser) {
    this.traverser = traverser;
  }

  public boolean isIterated(int iteration) {
    return iteration == ASTConstantsGrammar.STAR || iteration == ASTConstantsGrammar.PLUS;
  }
}
