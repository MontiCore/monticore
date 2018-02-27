/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser.antlr;

import static de.monticore.codegen.parser.ParserGeneratorHelper.printIteration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.DirectLeftRecursionDetector;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.MCGrammarInfo;
import de.monticore.grammar.PredicatePair;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTAnything;
import de.monticore.grammar.grammar._ast.ASTBlock;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._ast.ASTEof;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
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
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._ast.ASTSemanticpredicateOrAction;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.grammar.grammar._ast.GrammarNodeFactory;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

public class Grammar2Antlr implements Grammar_WithConceptsVisitor {

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

  private boolean embeddedJavaCode;

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
      ast.getLexOption().accept(getRealThis());
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
      addToCodeSection("->", ast.getLexerCommand(), "\n");
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

    if (embeddedJavaCode) {
      addToCodeSection(" returns [", classnameFromRulenameorInterfacename, " ret = ",
              MCGrammarSymbolTableHelper.getDefaultValue(ruleByName.get()), "]\n", options);

      // Add actions
      if (ast.isPresentAction() && ast.getAction() instanceof ASTAction) {
        addToAction(ParserGeneratorHelper.getText(ast.getAction()));
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
        addActionToCodeSection();
      }

      // Action at end of rule
      addToAction(positionActions.endPosition(ast));
      addToAction(attributeConstraints.addActionForRuleAfterRuleBody(ast));

      if (!isActionEmpty()) {
        addToCodeSection("\n@after");
        addActionToCodeSection();
      }
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
        if (ruleRef.isPresentSemanticpredicateOrAction() && ruleRef.getSemanticpredicateOrAction().isPredicate()) {
          ruleRef.getSemanticpredicateOrAction().accept(getRealThis());
        }


        startCodeSection();
        String subRuleVar = "subRuleVar" + i;
        addToCodeSection("(" + subRuleVar + " = "
                + HelperGrammar.getRuleNameForAntlr(x.getClassname()));
        if (embeddedJavaCode) {
          addToCodeSection(" {$ret = $" + subRuleVar + ".ret;}");
        }
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
    for(ASTConstant c : ast.getConstantList()) {
      addToCodeSection(sep);
      if (grammarInfo.isKeyword(c.getName(), grammarEntry)) {
        addToCodeSection("\n'" + c.getName() + "'");
      } else {
        addToCodeSection("\n", parserHelper.getLexSymbolName(c.getName()));
      }

      if (embeddedJavaCode) {
        String temp1 = "";
        temp1 += "$ret = " + MCGrammarSymbolTableHelper.getQualifiedName(ruleByName.get())
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
    if (ast.getSymbol().isPresent() && ast.getSymbol().get() instanceof MCProdComponentSymbol) {
      iterated = MCGrammarSymbolTableHelper
              .isConstGroupIterated((MCProdComponentSymbol) ast.getSymbol().get());
    }

    // One entry leads to boolean isMethods
    if (!iterated) {
      ASTConstant x = ast.getConstantList().get(0);
      if (!grammarInfo.isKeyword(x.getName(), grammarEntry)) {
        addToCodeSection(parserHelper.getLexSymbolName(x.getName()));
      } else {
        addToCodeSection("'" + x.getName() + "'");
      }

      if (embeddedJavaCode) {
        addToAction(astActions.getConstantInConstantGroupSingleEntry(x, ast));
        addActionToCodeSectionWithNewLine();
      }
    }

    // More than one entry leads to an int
    else {
      addToCodeSection("(");
      String del = "";
      for(Iterator<ASTConstant> iter = ast.getConstantList().iterator(); iter
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

        if (embeddedJavaCode) {
          addToAction(astActions.getConstantInConstantGroupMultipleEntries(x, ast));
          addActionToCodeSectionWithNewLine();
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
      ast.getLexChar().accept(getRealThis());
    } else if (ast.isPresentLexString()) {
      ast.getLexString().accept(getRealThis());
    } else if (ast.isPresentLexNonTerminal()) {
      ast.getLexNonTerminal().accept(getRealThis());
    } else if (ast.isPresentLexAnyChar()) {
      ast.getLexAnyChar().accept(getRealThis());
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
    if (a.isPresentOption()) {
      addToCodeSection("\n  options {");
      for(ASTOptionValue x : a.getOption().getOptionValueList()) {
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
    if (grammarInfo.isKeyword(ast.getName(), grammarEntry)) {
      rulename = "'" + ast.getName() + "'";
    } else {
      rulename = parserHelper.getLexSymbolName(ast.getName().intern());
    }

    // No actions in predicates
    // Template engine cannot be used for substition in rare cases
    addToCodeSection(rulename); // + " %initaction% %actions% ) %iteration% ";

    if (embeddedJavaCode) {
      boolean iteratedItself = HelperGrammar.isIterated(ast);
      boolean isAttribute = ast.isPresentUsageName();

      boolean isList = iteratedItself;
      Optional<? extends Symbol> ruleComponent = ast.getSymbol();
      if (ruleComponent.isPresent() && ruleComponent.get() instanceof MCProdComponentSymbol) {
        MCProdComponentSymbol componentSymbol = (MCProdComponentSymbol) ruleComponent.get();
        isList = componentSymbol.isList();
      }
      // Add Actions
      if (isAttribute) {
        if (isList) {
          addToAction(astActions.getActionForTerminalIteratedAttribute(ast));
        } else {
          addToAction(astActions.getActionForTerminalNotIteratedAttribute(ast));
        }
      } else {
        addToAction(astActions.getActionForTerminalIgnore(ast));
      }
    }

    addActionToCodeSection();

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
    if(embeddedJavaCode) {
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
    if (embeddedJavaCode) {
      addToAntlrCode(" returns [" + usageName + " ret]");
    }
    addToAntlrCode(": ");

    List<NodePair> alts = new ArrayList<>();
    String del = "";
    // Get all implementing/extending interfaces
    boolean left = addAlternatives(interfaceRule, alts);

    // Append sorted alternatives
    Collections.sort(alts, (p2, p1) -> new Integer(p1.getPredicatePair().getRuleReference().getPrioOpt().orElse("0")).compareTo(new Integer(p2.getPredicatePair().getRuleReference().getPrioOpt().orElse("0"))));
    for(NodePair entry : alts) {
      addToAntlrCode(del);

      // Append semantic predicates for rules
      if (entry.getPredicatePair().getRuleReference().isPresentSemanticpredicateOrAction()) {
        ASTSemanticpredicateOrAction semPredicate = entry.getPredicatePair().getRuleReference().getSemanticpredicateOrAction();
        if (semPredicate.isPredicate()) {
          semPredicate.accept(getRealThis());
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
          addToAction(positionActions.startPosition(alt));
          // Action for determining positions of comments (First set position)
          addToAction("setActiveASTNode(_aNode);\n");
        }
        alt.accept(getRealThis());
        if (embeddedJavaCode) {
          addActionToCodeSection();
        }
      } else {
        if (left && entry.getAlternative() instanceof ASTClassProd && ((ASTClassProd) entry.getAlternative()).getAltList().size() == 1) {
          ASTAlt alt = ((ASTClassProd) entry.getAlternative()).getAltList().get(0);
          String className = entry.getPredicatePair().getClassname();
          if (embeddedJavaCode) {
            // Generate code for left recursive alternatives
            addToAction(astActions.getActionForAltBeforeRuleBody(className, alt));
            // Action for determining positions
            addToAction(positionActions.startPosition(alt));
            // Action for determining positions of comments (First set position)
            addToAction("setActiveASTNode(_aNode);\n");
            startCodeSection();
            addActionToCodeSection();
            endCodeSection();
          }
          alt.accept(getRealThis());
        } else {
          // normal rule
          startCodeSection();
          String tmpVar = parserHelper.getTmpVarName(entry.getAlternative());
          addToCodeSection(tmpVar + "="
                  + HelperGrammar.getRuleNameForAntlr(entry.getPredicatePair().getClassname()));
          if (embeddedJavaCode) {
            // Action for AntLR4
            addToCodeSection("\n{$ret=$" + tmpVar + ".ret;}");
          }
          endCodeSection();
        }
      }

      del = "|";
    }

    addToAntlrCode(";");

    return getAntlrCode();
  }

  /**
   * @param prodSymbol
   * @param alts
   */
  private boolean addAlternatives(MCProdSymbol prodSymbol, List<NodePair> alts) {
    boolean isLeft = false;
    List<PredicatePair> interfaces = grammarInfo.getSubRulesForParsing(prodSymbol.getName());
    for(PredicatePair interf : interfaces) {
      Optional<MCProdSymbol> symbol = grammarEntry.getSpannedScope().<MCProdSymbol>resolve(interf.getClassname(), MCProdSymbol.KIND);
      if (!symbol.isPresent()) {
        continue;
      }
      MCProdSymbol superSymbol = symbol.get();
      if (!prodSymbol.getAstNode().isPresent()) {
        continue;
      }
      ASTNode astNode = superSymbol.getAstNode().get();
      if (grammarInfo.isProdLeftRecursive(superSymbol.getName())) {
        isLeft = true;
        if (superSymbol.isClass()) {
          List<ASTAlt> localAlts = ((ASTClassProd) astNode).getAltList();
          for(ASTAlt alt : localAlts) {
            alts.add(new NodePair(alt, interf));
          }
        } else if (prodSymbol.isInterface()) {
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

  // ----------------------------------------------------------------------------------------------

  private void addCodeForLexerRule(ASTNonTerminal ast) {
    startCodeSection();

    addToCodeSection("(");

    // AntLR2 -> AntLR4: Replace : by =
    // tmp = "( %tmp%=%rulename% %initaction% %actions%";

    addToCodeSection(parserHelper.getTmpVarName(ast), "=", ast.getName());

    if (embeddedJavaCode) {
      // Add Actions
      Optional<MCProdSymbol> scope = MCGrammarSymbolTableHelper.getEnclosingRule(ast);

      if (scope.isPresent()) {
        addToAction(attributeConstraints.addActionForNonTerminal(ast));
        String attributename = HelperGrammar.getUsuageName(ast);
        if (scope.isPresent() && scope.get().getProdComponent(attributename).isPresent()
                && scope.get().getProdComponent(attributename).get().isList()) {
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

    String tmpVarName = parserHelper.getTmpVarName(ast);

    addToCodeSection(braceopen, " ", tmpVarName, "=", HelperGrammar.getRuleNameForAntlr(ast));

    if (embeddedJavaCode) {
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
      } else {
        addToAction(astActions.getActionForInternalRuleNotIteratedAttribute(ast));
      }

      // TODO GV: replaceAll("..", ".")); is deprecated?
      addActionToCodeSection();
    }

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

  private void addDummyRules(String rulenameInternal, String ruleName,
                             String usageName) {

    addToCodeSection("\n\n", ruleName, "_eof");

    if (embeddedJavaCode) {
      addToCodeSection(" returns [", usageName, " ret = null] :\n  tmp = ",
              ruleName, " {$ret = $tmp.ret;} ");
    }
    else {
      addToCodeSection(" :\n tmp = ", ruleName, " ");
    }

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
   * Adds code for parser action to the current code codeSection
   */
  private void addActionToCodeSection() {
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
  private void addActionToCodeSectionWithNewLine() {
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
  private void clearAction() {
    action.setLength(0);
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