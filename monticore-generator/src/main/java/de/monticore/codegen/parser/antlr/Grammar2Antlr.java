/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser.antlr;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.DirectLeftRecursionDetector;
import de.monticore.grammar.MCGrammarInfo;
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
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static de.monticore.codegen.mc2cd.TransformationHelper.getQualifiedName;
import static de.monticore.codegen.parser.ParserGeneratorHelper.getDefaultValue;
import static de.monticore.codegen.parser.ParserGeneratorHelper.printIteration;

public class Grammar2Antlr implements GrammarVisitor2, GrammarHandler {

  protected MCGrammarSymbol grammarEntry;

  GrammarTraverser traverser;

  /**
   * This list is used for the detection of the left recursion
   */
  protected ArrayList<ASTAlt> altList = new ArrayList<>();

  protected DirectLeftRecursionDetector leftRecursionDetector = new DirectLeftRecursionDetector();

  protected SourcePositionActions positionActions;

  protected AttributeCardinalityConstraint attributeConstraints;

  protected ASTConstructionActions astActions;

  protected List<String> productionAntlrCode = Lists.newArrayList();

  protected StringBuilder codeSection;

  protected StringBuilder action = new StringBuilder();

  protected MCGrammarInfo grammarInfo;

  protected ParserGeneratorHelper parserHelper;

  protected boolean embeddedJavaCode;

  protected Map<ASTProd, Map<ASTNode, String>> tmpNameDict = new LinkedHashMap<>();

  public Grammar2Antlr(
          ParserGeneratorHelper parserGeneratorHelper,
          MCGrammarInfo grammarInfo) {
    Preconditions.checkArgument(parserGeneratorHelper.getGrammarSymbol() != null);
    this.grammarEntry = parserGeneratorHelper.getGrammarSymbol();
    this.grammarInfo = grammarInfo;
    this.parserHelper = parserGeneratorHelper;

    astActions = new ASTConstructionActions(parserGeneratorHelper);
    attributeConstraints = new AttributeCardinalityConstraint(parserGeneratorHelper);
    positionActions = new SourcePositionActions(parserGeneratorHelper);

    this.embeddedJavaCode = true;
  }

  public Grammar2Antlr(
          ParserGeneratorHelper parserGeneratorHelper,
          MCGrammarInfo grammarInfo,
          boolean embeddedJavaCode) {
    Preconditions.checkArgument(parserGeneratorHelper.getGrammarSymbol() != null);
    this.grammarEntry = parserGeneratorHelper.getGrammarSymbol();
    this.grammarInfo = grammarInfo;
    this.parserHelper = parserGeneratorHelper;

    astActions = new ASTConstructionActions(parserGeneratorHelper);
    attributeConstraints = new AttributeCardinalityConstraint(parserGeneratorHelper);
    positionActions = new SourcePositionActions(parserGeneratorHelper);

    this.embeddedJavaCode = embeddedJavaCode;
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
    if (ast.isPresentLexOption()) {
      ast.getLexOption().accept(getTraverser());
    }

    startCodeSection();
    addToCodeSection("\n:");

    if (embeddedJavaCode && ast.isPresentInitAction()) {
      // Add init action
      addToCodeSection("{", ParserGeneratorHelper.getText(ast.getInitAction()), "\n}");
    }

    endCodeSection();

    createAntlrCodeForLexAlts(ast.getAltList());

    // Add Action
    startCodeSection();

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
    String ruleName = getRuleNameForAntlr(ast.getName());
    ProdSymbol ruleByName = ast.getSymbol();
    String classnameFromRulenameorInterfacename = getQualifiedName(ruleByName);

    // Head of Rule
    // Pattern:
    // String tmp =
    // "%name% returns [%uname% ret = %defaultvalue%] %options% ";

    String options = "";

    if (ast.getSymbol().isIsIndirectLeftRecursive()) {
      addToCodeSection("// No code generation because of indirect left recursive rules");
      endCodeSection();
      return;
    }

    // Antlr4: new syntax
    List<ASTAlt> alts = parserHelper.getAlternatives(ast);
    if (alts.isEmpty()) {
      options = "@rulecatch{}";
    }

    // Start code codeSection for rules
    addToCodeSection(ruleName);
    List<PredicatePair> subRules = grammarInfo
            .getSubRulesForParsing(ast.getName());

    if (embeddedJavaCode) {
      addToCodeSection(" returns [", classnameFromRulenameorInterfacename, " ret = ",
              getDefaultValue(ruleByName), "]\n", options);

      // Add actions
      if (ast.isPresentAction() && ast.getAction() instanceof ASTAction) {
        addToAction(ParserGeneratorHelper.getText(ast.getAction()));
      }

      // Action at beginning of rule @init
      addToAction(astActions.getActionForRuleBeforeRuleBody(ast));
      // Action for determining positions
      addToAction(positionActions.startPosition());
      // Action for determining positions of comments (First set position)
      addToAction("setActiveBuilder(_builder);\n");

      addToAction(attributeConstraints.addActionForRuleBeforeRuleBody(ast));

      if (!isActionEmpty()) {
        addToCodeSection("@init");
        addActionToCodeSection();
      }

      // Action at end of rule
      addToAction(positionActions.endPosition());
      addToAction(attributeConstraints.addActionForRuleAfterRuleBody(ast));
      if (subRules != null && !subRules.isEmpty()) {
        addToAction("\nif (_localctx.ret == null)");
      }
      addToAction(astActions.getBuildAction());

      if (!isActionEmpty()) {
        addToCodeSection("\n@after");
        addActionToCodeSection();
      }
    }

    endCodeSection();
    // End code codeSection for rules

    startCodeSection();
    addToCodeSection("\n : ");


    if (subRules != null && !subRules.isEmpty()) {

      addToCodeSection("// Adding subrules");
      endCodeSection();

      int i = 0;
      for (PredicatePair x : subRules) {

        ASTRuleReference ruleRef = x.getRuleReference();
        if (ruleRef.isPresentSemanticpredicateOrAction() && ruleRef.getSemanticpredicateOrAction().isPredicate()) {
          ruleRef.getSemanticpredicateOrAction().accept(getTraverser());
        }


        startCodeSection();
        String subRuleVar = "subRuleVar" + i;
        addToCodeSection("(" + subRuleVar + " = " + getRuleNameForAntlr(x.getClassname()));
        if (embeddedJavaCode) {
          addToCodeSection(" {$ret = $" + subRuleVar + ".ret;}");
        }
        addToCodeSection(") | \n");
        endCodeSection();
        i++;
      }
      addToCodeSection("// end subrules");
      endCodeSection();
    } else {
      endCodeSection();
    }

    // Iterate over all Components
    createAntlrCodeForAlts(alts);

    addDummyRules(ast.getName(), ruleName,
            classnameFromRulenameorInterfacename);

    addToAntlrCode(";");

    endCodeSection(ast);
  }

  @Override
  public void handle(ASTEnumProd ast) {
    // Check if user excluded this rule from the code generation

    startCodeSection("ASTEnumProd " + ast.getName());

    // Create eof and dummy rules
    String ruleName = getRuleNameForAntlr(ast.getName());
    ProdSymbol ruleByName = ast.getSymbol();

    // Head of Rule
    addToCodeSection(ruleName + " returns ["
            + getQualifiedName(ruleByName) + " ret = "
            + getDefaultValue(ruleByName) + "] ");

    addToCodeSection("\n: ");

    String sep = "";
    for (ASTConstant c : ast.getConstantList()) {
      addToCodeSection(sep);
      addToCodeSection("\n", parserHelper.getLexSymbolName(c.getName()));

      if (embeddedJavaCode) {
        String temp1 = "";
        temp1 += "$ret = " + getQualifiedName(ruleByName)
                + "."
                + parserHelper.getConstantNameForConstant(c) + ";";

        if (!temp1.isEmpty()) {
          addToCodeSection("\n{" + temp1 + "}");
        }
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
    if (ast.isPresentSymbol()) {
      iterated = TransformationHelper
              .isConstGroupIterated(ast.getSymbol());
    }

    // One entry leads to boolean isMethods
    if (!iterated) {
      ASTConstant x = ast.getConstantList().get(0);
      addToCodeSection("(");
      if (x.isPresentKeyConstant()) {
        addToCodeSection(createKeyPredicate(x.getKeyConstant().getStringList()));
      } else if (x.isPresentTokenConstant()) {
        addToCodeSection(parserHelper.getLexSymbolName(x.getTokenConstant().getString()));
      } else if (!grammarInfo.isKeyword(x.getName(), grammarEntry)) {
        addToCodeSection(parserHelper.getLexSymbolName(x.getName()));
      } else if (grammarInfo.getKeywordRules().contains(x.getName())) {
        addToCodeSection(parserHelper.getKeyRuleName(x.getName()));
      } else {
        addToCodeSection(parserHelper.getLexSymbolName(x.getName()));
      }

      if (embeddedJavaCode) {
        addToAction(astActions.getConstantInConstantGroupSingleEntry(x, ast));
        addActionToCodeSectionWithNewLine();
      }
      addToCodeSection(")", printIteration(ast.getIteration()));

    }

    // More than one entry leads to an int
    else {
      addToCodeSection("(");
      String del = "";
      for (Iterator<ASTConstant> iter = ast.getConstantList().iterator(); iter
              .hasNext(); ) {
        addToCodeSection(del);
        ASTConstant x = iter.next();

        if (x.isPresentKeyConstant()) {
          addToCodeSection(createKeyPredicate(x.getKeyConstant().getStringList()));
        } else if (!grammarInfo.isKeyword(x.getName(), grammarEntry)) {
          addToCodeSection(parserHelper.getLexSymbolName(x.getName()));
        } else if (grammarInfo.getKeywordRules().contains(x.getName())) {
          addToCodeSection(parserHelper.getKeyRuleName(x.getName()));
        } else {
          addToCodeSection(parserHelper.getLexSymbolName(x.getName()));
        }

        if (embeddedJavaCode) {
          addToAction(astActions.getConstantInConstantGroupMultipleEntries(x, ast));
          addActionToCodeSectionWithNewLine();
        }

        del = "|\n";
      }

      addToCodeSection(")", printIteration(ast.getIteration()));
    }

    endCodeSection(ast);
  }

  /**
   * Print alternatives
   *
   * @param alts
   */
  public void createAntlrCodeForAlts(List<ASTAlt> alts) {
    String del = "";
    for (Iterator<ASTAlt> iter = alts.iterator(); iter.hasNext(); ) {
      addToAntlrCode(del);

      iter.next().accept(getTraverser());

      del = "|";
    }
  }

  public void createAntlrCodeForLexAlts(List<ASTLexAlt> ast) {
    String del = "";
    for (ASTLexAlt anAst : ast) {
      addToAntlrCode(del);

      anAst.accept(getTraverser());

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
    if (ast.isPresentOption()) {
      addToCodeSection("\noptions {", ast.getOption().getID(), "=", ast.getOption()
              .getValue(), ";}");
    }
    if (embeddedJavaCode && ast.isPresentInitAction()) {
      addToCodeSection("{", ParserGeneratorHelper.getText(ast.getInitAction()), "}");
    }
    endCodeSection();

    // Visit all alternatives
    createAntlrCodeForLexAlts(ast.getLexAltList());

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
    addActionToCodeSection();

    addToCodeSection("(");
    // Print options
    if (a.isPresentOption()) {
      addToCodeSection("\n  options {");
      for (ASTOptionValue x : a.getOption().getOptionValueList()) {
        addToCodeSection("\n  " + x.getKey() + "=" + x.getValue() + ";");
      }
      addToCodeSection("\n }");
    }

    // Print init actions
    if (embeddedJavaCode && a.isPresentInitAction()) {
      addToCodeSection("{" + ParserGeneratorHelper.getText(a.getInitAction()) + "}");
    }

    endCodeSection();

    // Visit all alternatives
    createAntlrCodeForAlts(a.getAltList());

    // Start of Block with iteration
    startCodeSection();
    addToCodeSection("\n)" + printIteration(a.getIteration()));
    endCodeSection();
  }

  @Override
  public void visit(ASTTerminal ast) {

    startCodeSection("ASTTerminal " + ast.getName());

    String rulename;
    if (ast.getName().isEmpty()) {
      rulename = "";
    } else if (grammarInfo.isKeyword(ast.getName(), grammarEntry) && grammarInfo.getKeywordRules().contains(ast.getName())) {
      rulename = parserHelper.getKeyRuleName(ast.getName());
    } else {
      rulename = parserHelper.getLexSymbolName(ast.getName().intern());
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
      for (String replaceString: keywords.get(ast.getName())) {
        addToCodeSection(seperator);
        if (grammarInfo.getKeywordRules().contains(replaceString)) {
          addToCodeSection(parserHelper.getKeyRuleName(replaceString));
        } else {
          addToCodeSection(parserHelper.getLexSymbolName(replaceString));
        }
        seperator = " | ";
      }
      addToCodeSection(")");
    } else {
      addToCodeSection(rulename);
    }

    if (embeddedJavaCode) {
      // Add Actions
      if (isAttribute) {
        if (ast.getSymbol().isIsList()) {
          addToAction(astActions.getActionForTerminalIteratedAttribute(ast));
        } else {
          addToAction(astActions.getActionForTerminalNotIteratedAttribute(ast));
        }
      } else {
        addToAction(astActions.getActionForTerminalIgnore(ast));
      }
    }

    addActionToCodeSection();

    if (isAttribute && isListOrOpt) {
      addToCodeSection(")");
    }
    addToCodeSection(printIteration(ast.getIteration()));

    endCodeSection();

  }

  protected String createKeyPredicate(List<String> stringList) {
    String rulename = "(";
    String sep = "";
    for (String key : stringList) {
      rulename += sep;
      sep = " | ";
      rulename += parserHelper.getKeyRuleName(key);

    }
    rulename += ")";
    return rulename;
  }

  @Override
  public void visit(ASTKeyTerminal ast) {

    startCodeSection("ASTKeyTerminal " + ast.getName());
    addToCodeSection("(");
    String rulename = createKeyPredicate(ast.getKeyConstant().getStringList());

    if (grammarEntry.getReplacedKeywordsWithInherited().containsKey(ast.getName())) {
      addToCodeSection("(");
      String seperator = "";
      for (String replaceString: grammarEntry.getAdditionalKeywords().get(ast.getName())) {
        addToCodeSection(seperator);
        if (grammarInfo.getKeywordRules().contains(replaceString)) {
          addToCodeSection(parserHelper.getKeyRuleName(replaceString));
        } else {
          addToCodeSection(parserHelper.getLexSymbolName(replaceString));
        }
        seperator = " | ";
      }
      addToCodeSection(")");
    } else {
      addToCodeSection(rulename);
    }

    if (embeddedJavaCode) {
      boolean isAttribute = ast.isPresentUsageName();
      boolean isList = ast.isPresentSymbol() && ast.getSymbol().isIsList();
      // Add Actions
      if (isAttribute) {
        if (isList) {
          addToAction(astActions.getActionForKeyTerminalIteratedAttribute(ast));
        } else {
          addToAction(astActions.getActionForKeyTerminalNotIteratedAttribute(ast));
        }
      }
    }

    addActionToCodeSection();
    addToCodeSection(")");
    addToCodeSection(printIteration(ast.getIteration()));

    endCodeSection(ast);

  }

  @Override
  public void visit(ASTTokenTerminal ast) {

    startCodeSection("ASTTokenTerminal " + ast.getName());
    addToCodeSection("(");

    if (grammarEntry.getReplacedKeywordsWithInherited().containsKey(ast.getName())) {
      addToCodeSection("(");
      String seperator = "";
      for (String replaceString: grammarEntry.getAdditionalKeywords().get(ast.getName())) {
        addToCodeSection(seperator);
        if (grammarInfo.getKeywordRules().contains(replaceString)) {
          addToCodeSection(parserHelper.getKeyRuleName(replaceString));
        } else {
          addToCodeSection(parserHelper.getLexSymbolName(replaceString));
        }
        seperator = " | ";
      }
      addToCodeSection(")");
    } else {
      addToCodeSection(parserHelper.getLexSymbolName(ast.getTokenConstant().getString()));
    }

    if (embeddedJavaCode) {
      boolean isAttribute = ast.isPresentUsageName();
      boolean isList = ast.isPresentSymbol() && ast.getSymbol().isIsList();
      // Add Actions
      if (isAttribute) {
        if (isList) {
          addToAction(astActions.getActionForTerminalIteratedAttribute(ast));
        } else {
          addToAction(astActions.getActionForTerminalNotIteratedAttribute(ast));
        }
      }
    }

    addActionToCodeSection();
    addToCodeSection(")");
    addToCodeSection(printIteration(ast.getIteration()));

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
    if (embeddedJavaCode) {
      startCodeSection();

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
    // external rule called (first+second version)
    else {

      addToCodeSection(embedded(ast));
    }

    endCodeSection();
  }

  @Override
  public void visit(ASTAlt alt) {
    altList.add(alt);
    if (alt.isRightAssoc()) {
      addToAntlrCode(ParserGeneratorHelper.RIGHTASSOC);
    }
    if (alt.isPresentGrammarAnnotation() && alt.getGrammarAnnotation().isDeprecated()) {
      String t = alt.getGrammarAnnotation().isPresentMessage() ? alt.getGrammarAnnotation().getMessage() : "";
      String message = "Deprecated syntax: " + t;
      addToAction("de.se_rwth.commons.logging.Log.warn(\"" + message + "\");");
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

    String interfacename = interfaceRule.getName();
    // Dummy rules
    String ruleName = getRuleNameForAntlr(interfacename);
    String usageName = getQualifiedName(interfaceRule);

    startCodeSection(interfaceRule.getName());

    addToAntlrCode(getRuleNameForAntlr(interfacename));
    if (embeddedJavaCode) {
      addToAntlrCode(" returns [" + usageName + " ret]");
    }
    addToAntlrCode(": ");

    List<NodePair> alts = new ArrayList<>();
    String del = "";
    // Get all implementing/extending interfaces
    boolean left = addAlternatives(interfaceRule, alts);

    // Append sorted alternatives
    Collections.sort(alts, (p2, p1) ->
            new Integer(p1.getPredicatePair().getRuleReference().isPresentPrio() ? p1.getPredicatePair().getRuleReference().getPrio() : "0").compareTo(
                    new Integer(p2.getPredicatePair().getRuleReference().isPresentPrio() ? p2.getPredicatePair().getRuleReference().getPrio() : "0")));

    for (NodePair entry : alts) {
      addToAntlrCode(del);

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
        String className = entry.getPredicatePair().getClassname();
        if (embeddedJavaCode) {
          // Generate code for left recursive alternatives
          addToAction(astActions.getActionForAltBeforeRuleBody(className, alt));
          // Action for determining positions
          addToAction(positionActions.startPosition());
          // Action for determining positions of comments (First set position)
          addToAction("setActiveBuilder(_builder);\n");
          if (!leftRecursionDetector.isAlternativeLeftRecursive(alt, interfacename)) {
            // If the alternative is left-recursive, the code may only
            // be saved later. With left-recursive rules, Antlr expects the first
            // element to be the recursive element.
            // Code is also not allowed at this point.
            // But for the other rules it is necessary that the code
            // is saved first
            addActionToCodeSection();
            endCodeSection();
          }
        }
        alt.accept(getTraverser());
        if (embeddedJavaCode) {
          addToAction(positionActions.endPosition());
          addToAction(astActions.getBuildAction());
          addActionToCodeSection();
          endCodeSection();
        }
      } else {
        if (left && entry.getAlternative() instanceof ASTClassProd && ((ASTClassProd) entry.getAlternative()).getAltList().size() == 1) {
          ASTAlt alt = ((ASTClassProd) entry.getAlternative()).getAltList().get(0);
          String className = entry.getPredicatePair().getClassname();
          if (embeddedJavaCode) {
            // Generate code for left recursive alternatives
            addToAction(astActions.getActionForAltBeforeRuleBody(className, alt));
            // Action for determining positions
            addToAction(positionActions.startPosition());
            // Action for determining positions of comments (First set position)
            addToAction("setActiveBuilder(_builder);\n");
            startCodeSection();
            addActionToCodeSection();
            endCodeSection();
          }
          alt.accept(getTraverser());
          addToAction(positionActions.endPosition());
          addToAction(astActions.getBuildAction());
          addActionToCodeSectionWithNewLine();
          endCodeSection();
        } else {
          // normal rule
          startCodeSection();
          String tmpVar = parserHelper.getTmpVarName(entry.getAlternative());
          addToCodeSection(tmpVar + "="
                  + getRuleNameForAntlr(entry.getPredicatePair().getClassname()));
          if (embeddedJavaCode) {
            // Action for AntLR4
            addToCodeSection("\n{$ret=$" + tmpVar + ".ret;}");
          }
          endCodeSection();
        }
      }

      del = "|";
    }

    addDummyRules(interfacename, ruleName, usageName);

    addToAntlrCode(";");

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
      ASTNode astNode = superSymbol.getAstNode();
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
        alts.add(new NodePair((ASTGrammarNode) astNode, interf));
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
    startCodeSection();

    addToCodeSection("(");

    // AntLR2 -> AntLR4: Replace : by =
    // tmp = "( %tmp%=%rulename% %initaction% %actions%";

    addToCodeSection(parserHelper.getTmpVarName(ast), "=", ast.getName());

    if (embeddedJavaCode) {
      // Add Actions
      Optional<ProdSymbol> scope = MCGrammarSymbolTableHelper.getEnclosingRule(ast);

      if (scope.isPresent()) {
        addToAction(attributeConstraints.addActionForNonTerminal(ast));
        String attributename = ast.isPresentUsageName() ? ast.getUsageName() : StringTransformations.uncapitalize(ast.getName());
        List<RuleComponentSymbol> rcs = scope.get().getSpannedScope().resolveRuleComponentDownMany(attributename);
        if (!rcs.isEmpty()
                && rcs.get(0).isIsList()) {
          addToAction(astActions.getActionForLexerRuleIteratedAttribute(ast));
        } else {
          addToAction(astActions.getActionForLexerRuleNotIteratedAttribute(ast));
        }
      }

      addActionToCodeSection();
    }

    addToCodeSection("\n");

    endCodeSection();

    if (ast.isPlusKeywords()) {
      addToAntlrCode("/* Automatically added keywords " + grammarInfo.getKeywords()
              + " */");

      ArrayList<String> keys = Lists.newArrayList(grammarInfo.getKeywords());
      keys.removeAll(grammarInfo.getKeywordRules());
      for (String y : keys) {
        addToAntlrCode(" | ");
        ASTTerminal term = Grammar_WithConceptsMill.terminalBuilder()
                .setName(y).build();

        if (ast.isPresentSymbol()) {
          RuleComponentSymbol componentSymbol = ast.getSymbol();
          Optional<ProdSymbol> rule = MCGrammarSymbolTableHelper
                  .getEnclosingRule(componentSymbol);
          term.setUsageName(ParserGeneratorHelper.getUsageName(ast));

          if (rule.isPresent()) {
            addActionForKeyword(term, rule.get(), componentSymbol.isIsList());
          }
        }
      }
    }

    addToAntlrCode(") " + printIteration(ast.getIteration()));
  }

  /**
   * print code for references to embedded rules
   */
  protected String embedded(ASTNonTerminal ast) {
    return "";
  }

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
    boolean isLeftRecursive = false;
    if (scope.get().getName().equals(ast.getName())
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

    addToCodeSection(braceopen, " ", tmpVarName, "=", getRuleNameForAntlr(ast.getName()));

    if (embeddedJavaCode) {
      if (isLeftRecursive) {
        addToAction(astActions
                .getActionForInternalRuleNotIteratedLeftRecursiveAttribute(ast));
      }
      addToAction(attributeConstraints.addActionForNonTerminal(ast));
      String attributename = ast.isPresentUsageName() ? ast.getUsageName() : StringTransformations.uncapitalize(ast.getName());
      List<RuleComponentSymbol> rcs = scope.get().getSpannedScope().resolveRuleComponentDownMany(attributename);
      if (!rcs.isEmpty() && rcs.get(0).isIsList()) {
        addToAction(astActions.getActionForInternalRuleIteratedAttribute(ast));
      } else {
        addToAction(astActions.getActionForInternalRuleNotIteratedAttribute(ast));
      }

      addActionToCodeSection();
    }

    addToCodeSection(braceclose, " ", iteration, " ");

    endCodeSection();

  }

  protected void addActionForKeyword(ASTTerminal keyword, ProdSymbol rule, boolean isList) {

    startCodeSection();

    addToCodeSection("(");
    String rulename = "";
    if (grammarInfo.isKeyword(keyword.getName(), grammarEntry)) {
      rulename = parserHelper.getLexSymbolName(keyword.getName());
    }

    // No actions in predicates
    // Template engine cannot be used for substition in rare cases
    addToCodeSection(rulename); // + " %initaction% %actions% ) %iteration% ";

    if (embeddedJavaCode) {
      boolean isAttribute = (keyword.isPresentUsageName());

      // Add Actions
      if (isAttribute) {
        if (isList) {
          addToAction(astActions.getActionForTerminalIteratedAttribute(keyword));
        } else {
          addToAction(astActions.getActionForTerminalNotIteratedAttribute(keyword));
        }
      } else {
        addToAction(astActions.getActionForTerminalIgnore(keyword));
      }

      addActionToCodeSection();
    }

    addToCodeSection(")", printIteration(keyword.getIteration()));

    endCodeSection();

  }

  protected void addDummyRules(String rulenameInternal, String ruleName,
                               String usageName) {
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
      if (embeddedJavaCode) {
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
      if (embeddedJavaCode) {
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

  @Override
  public GrammarTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(GrammarTraverser traverser) {
    this.traverser = traverser;
  }
}