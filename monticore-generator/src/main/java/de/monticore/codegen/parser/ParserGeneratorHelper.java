/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.MCGrammarInfo;
import de.monticore.grammar.PredicatePair;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbolLoader;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.grammar.grammar_withconcepts._ast.ASTExpressionPredicate;
import de.monticore.grammar.grammar_withconcepts._ast.ASTGrammar_WithConceptsNode;
import de.monticore.grammar.grammar_withconcepts._ast.ASTJavaCode;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.javalight._ast.ASTClassMemberDeclaration;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;

/**
 * This is a helper class for the parser generation
 */
public class ParserGeneratorHelper {

  public static final String MONTICOREANYTHING = "MONTICOREANYTHING";

  public static final String RIGHTASSOC = "<assoc=right>";

  private static final String NOKEYWORD = "nokeyword_";

  private static Grammar_WithConceptsPrettyPrinter prettyPrinter;

  private ASTMCGrammar astGrammar;

  private String qualifiedGrammarName;

  private MCGrammarSymbol grammarSymbol;

  private MCGrammarInfo grammarInfo;

  private Map<ASTNode, String> tmpVariables = new HashMap<>();

  private int tmp_counter = 0;

  private boolean embeddedJavaCode;

  private boolean isJava;

  /**
   * Constructor for de.monticore.codegen.parser.ParserGeneratorHelper
   */
  public ParserGeneratorHelper(ASTMCGrammar ast, MCGrammarInfo grammarInfo) {
    Log.errorIfNull(ast);
    this.astGrammar = ast;
    this.qualifiedGrammarName = astGrammar.getPackageList().isEmpty()
            ? astGrammar.getName()
            : Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackageList()),
            astGrammar.getName());
    this.grammarInfo = grammarInfo;
    this.grammarSymbol = grammarInfo.getGrammarSymbol();
    checkState(qualifiedGrammarName.equals(grammarSymbol.getFullName()));
    this.embeddedJavaCode = true;
    this.isJava = true;
  }

  public ParserGeneratorHelper(ASTMCGrammar ast, MCGrammarInfo grammarInfo, boolean embeddedJavaCode, Languages lang) {
    Log.errorIfNull(ast);
    this.astGrammar = ast;
    this.qualifiedGrammarName = astGrammar.getPackageList().isEmpty()
            ? astGrammar.getName()
            : Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackageList()),
            astGrammar.getName());
    this.grammarInfo = grammarInfo;
    this.grammarSymbol = grammarInfo.getGrammarSymbol();
    checkState(qualifiedGrammarName.equals(grammarSymbol.getFullName()));
    this.embeddedJavaCode = embeddedJavaCode;
    this.isJava = Languages.JAVA.equals(lang);
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
    if (grammarSymbol.getStartProd().isPresent()) {
      return grammarSymbol.getStartProd().get().getName();
    }
    for (MCGrammarSymbol g: grammarSymbol.getSuperGrammarSymbols()) {
      if (g.getStartProd().isPresent()) {
        return g.getStartProd().get().getName();
      }
    }
    return "";
  }

  /**
   * @return the qualified name of the top ast, i.e., the ast of the start rule.
   */
  public String getQualifiedStartRuleName() {
    if (grammarSymbol.getStartProd().isPresent()) {
      return MCGrammarSymbolTableHelper
              .getQualifiedName(grammarSymbol.getStartProd().get());
    }
    for (MCGrammarSymbol g: grammarSymbol.getSuperGrammarSymbols()) {
      if (g.getStartProd().isPresent()) {
        return MCGrammarSymbolTableHelper
                .getQualifiedName(g.getStartProd().get());
      }
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
   * @return the name for a lexsymbol that should be used in an Antlr-File
   */
  public String getLexSymbolName(String constName) {
    Log.errorIfNull(constName);
    if (grammarInfo.getSplitRules().containsKey(constName)) {
      return grammarInfo.getSplitRules().get(constName);
    } else {
      return grammarInfo.getLexNamer().getLexName(grammarSymbol, constName);
    }
  }

  /**
   * @return the name for a rule replacing a keyword
   */
  public String getKeyRuleName(String key) {
    return NOKEYWORD + key + Integer.toUnsignedString(key.hashCode());
  }

  /**
   * Get all used LexSymbols, different form information in AST, as inherited
   * ones are integrated as well
   *
   * @return Keys of all lex symbols
   */
  public Set<String> getLexSymbolsWithInherited() {
    return grammarInfo.getLexNamer().getLexnames();
  }

  /**
   * Get all splitted LexSymbols
   *
   * @return list of rules for all splitted LexSymbols
   */
  public List<String> getSplitLexSymbolsWithInherited() {
    List<String> retList = Lists.newArrayList();
    for (Entry<String, String> e: grammarInfo.getSplitRules().entrySet()) {
      StringBuilder sb = new StringBuilder();
      sb.append(e.getValue());
      sb.append(" : ");
      // Split the token
      String sep = "";
      sb.append("{noSpace(");
      for (int i = 2; i <= e.getKey().length(); i++) {
        sb.append(sep);
        sep = ", ";
        sb.append(i);
      }
      sb.append(")}? ");
      for (char c : e.getKey().toCharArray()) {
        sb.append(grammarInfo.getLexNamer().getLexName(grammarSymbol, String.valueOf(c)));
        sb.append(" ");
      }
      sb.append(";");
      retList.add(sb.toString());
    }
    return retList;
  }

  /**
   * Get all keyords replaced by name
   *
   * @return list of keywords
   */
  public List<String> getNoKeyordsWithInherited() {
    List<String> retList = Lists.newArrayList();
    for (String s: grammarSymbol.getKeywordRulesWithInherited()) {
      String r = getKeyRuleName(s) + " : {next(\"" + s + "\")}? Name;";
      retList.add(r);
    }
    return retList;
  }

  /**
   * checks if parser must be generated for this rule
   *
   * @param rule
   * @return
   */
  public boolean generateParserForRule(ProdSymbol rule) {
    boolean generateParserForRule = false;
    String ruleName = rule.getName();

    if (rule.isClass()) {
      if (!grammarInfo.isProdLeftRecursive(rule.getName())) {
        generateParserForRule = true;
      }
    }

    if (rule.isIsAbstract() || rule.isIsInterface()) {
      List<PredicatePair> subRules = grammarInfo.getSubRulesForParsing(ruleName);
      generateParserForRule = !subRules.isEmpty();
    }
    return generateParserForRule;
  }

  /**
   * Gets all interface rules which were not excluded from the generation
   *
   * @return List of interface rules
   */
  public List<ProdSymbol> getInterfaceRulesToGenerate() {
    List<ProdSymbol> interfaceRules = Lists.newArrayList();
    for (ProdSymbol ruleSymbol : grammarSymbol.getProdsWithInherited()
            .values()) {
      if (ruleSymbol.isIsAbstract() || ruleSymbol.isIsInterface()) {
        interfaceRules.add(ruleSymbol);
      }
    }
    return interfaceRules;
  }

  /**
   * Gets all non external idents
   *
   * @return List of ident types
   */
  public List<ProdSymbol> getIdentsToGenerate() {
    return embeddedJavaCode ? grammarSymbol.getProdsWithInherited().values().stream()
            .filter(r -> r.isIsLexerProd() && !r.isIsExternal()).collect(Collectors.toList()) : Collections.emptyList();
  }

  public static String getConvertFunction(ProdSymbol symbol) {
    if (symbol.isPresentAstNode() && symbol.isIsLexerProd()) {
      return HelperGrammar.createConvertFunction((ASTLexProd) symbol.getAstNode(), getPrettyPrinter());
    }
    return "";
  }

  /**
   * Gets parser rules
   *
   * @return List of ident types
   */
  public List<ASTProd> getParserRulesToGenerate() {
    // Iterate over all Rules
    List<ASTProd> l = Lists.newArrayList();
    for (ProdSymbol prod: grammarSymbol.getProdsWithInherited().values()) {
      if ((prod.isParserProd() || prod.isIsEnum()) && prod.isPresentAstNode()) {
        l.add(prod.getAstNode());
      }
    }
    return l;
  }

  public List<ASTLexProd> getLexerRulesToGenerate() {
    // Iterate over all LexRules
    List<ASTLexProd> prods = Lists.newArrayList();
    ProdSymbol mcanything = null;
    final Map<String, ProdSymbol> rules = new LinkedHashMap<>();

    // Don't use grammarSymbol.getRulesWithInherited because of changed order
    for (final ProdSymbol ruleSymbol : grammarSymbol.getProds()) {
      rules.put(ruleSymbol.getName(), ruleSymbol);
    }
    for (int i = grammarSymbol.getSuperGrammars().size() - 1; i >= 0; i--) {
      rules.putAll(grammarSymbol.getSuperGrammarSymbols().get(i).getProdsWithInherited());
    }

    for (Entry<String, ProdSymbol> ruleSymbol : rules.entrySet()) {
      if (ruleSymbol.getValue().isIsLexerProd()) {
        ProdSymbol lexProd = ruleSymbol.getValue();
        // MONTICOREANYTHING must be last rule
        if (lexProd.getName().equals(MONTICOREANYTHING)) {
          mcanything = lexProd;
        }
        else {
          prods.add((ASTLexProd) lexProd.getAstNode());
        }
      }
    }
    if (mcanything != null) {
      prods.add((ASTLexProd) mcanything.getAstNode());
    }
    return prods;
  }

  public String getConstantNameForConstant(ASTConstant x) {
    String name;
    if (x.isPresentUsageName()) {
      name = x.getUsageName();
    }
    else {
      name = grammarInfo.getLexNamer().getConstantName(x.getName());
    }

    return name.toUpperCase();
  }

  public String getTmpVarName(ASTNode a) {
    if (!tmpVariables.containsKey(a)) {
      tmpVariables.put(a, getNewTmpVar());
    }
    return tmpVariables.get(a);
  }

  private String getNewTmpVar() {
    return "tmp" + (Integer.valueOf(tmp_counter++)).toString();
  }

  public void resetTmpVarNames() {
    tmpVariables.clear();
    tmp_counter = 0;
  }

  // ----------------------------------------------------

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

  public String getTmpVarNameForAntlrCode(ASTNonTerminal node) {
    Optional<ProdSymbol> prod = MCGrammarSymbolTableHelper.getEnclosingRule(node);
    if (!prod.isPresent()) {
      Log.error("0xA1006 ASTNonterminal " + node.getName() + "(usageName: " + node.getUsageName()
              + ") can't be resolved.");
      return "";
    }
    return getTmpVarName(node);
  }

  public Optional<ASTAlt> getAlternativeForFollowOption(String prodName) {
    return astGrammar.isPresentGrammarOption()
            ? astGrammar.getGrammarOption().getFollowOptionList().stream()
            .filter(f -> f.getProdName().equals(prodName)).map(ASTFollowOption::getAlt).findFirst()
            : Optional.empty();
  }

  public List<ASTAlt> getAlternatives(ASTClassProd ast) {
    if (!ast.getAltList().isEmpty()) {
      return ast.getAltList();
    }
    for (MCGrammarSymbolLoader g : grammarSymbol.getSuperGrammars()) {
      final Optional<ProdSymbol> ruleByName = g.getLoadedSymbol().getProdWithInherited(ast.getName());
      if (ruleByName.isPresent() && ruleByName.get().isClass()) {
        if (ruleByName.get().isPresentAstNode() && ruleByName.get().getAstNode() instanceof ASTClassProd) {
          return ((ASTClassProd)ruleByName.get().getAstNode()).getAltList();
        }
      }
    }
    return Lists.newArrayList();
  }

  public static String getASTClassName(ProdSymbol rule) {
    return MCGrammarSymbolTableHelper.getQualifiedName(rule);
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
      StringBuilder buffer = new StringBuilder();
      for (ASTMCBlockStatement action : ((ASTAction) node).getMCBlockStatementList()) {
        buffer.append(getPrettyPrinter().prettyprint(action));
      }
      return buffer.toString();
    }
    if (node instanceof ASTJavaCode) {
      StringBuilder buffer = new StringBuilder();
      for (ASTClassMemberDeclaration action : ((ASTJavaCode) node).getClassMemberDeclarationList()) {
        buffer.append(getPrettyPrinter().prettyprint(action));

      }
      return buffer.toString();
    }
    if (node instanceof ASTExpressionPredicate) {
      String exprPredicate = getPrettyPrinter()
              .prettyprint(((ASTExpressionPredicate) node).getExpression());
      Log.debug("ASTExpressionPredicate:\n" + exprPredicate, ParserGenerator.LOG);
      return exprPredicate;
    }
    if (node instanceof ASTGrammar_WithConceptsNode) {
      String output = getPrettyPrinter().prettyprint((ASTGrammar_WithConceptsNode) node);
      Log.debug("ASTGrammar_WithConceptsNode:\n" + output, ParserGenerator.LOG);
      return output;
    }
    return "";
  }

  public static String getParseRuleName(ProdSymbol rule) {
    return StringTransformations.uncapitalize(rule.getName());
  }

  public static String getParseRuleNameJavaCompatible(ProdSymbol rule) {
    return JavaNamesHelper.getNonReservedName(StringTransformations.uncapitalize(rule.getName()));
  }

  /**
   * @return the qualified name for this type
   */
  // TODO GV: change implementation
  public static String getQualifiedName(ProdSymbol symbol) {
    if (!symbol.isPresentAstNode()) {
      return "UNKNOWN_TYPE";
    }
    if (symbol.isPresentAstNode() && symbol.isIsLexerProd()) {
      return getLexType( symbol.getAstNode());
    }
    if (symbol.isIsEnum()) {

      return "int";
      // TODO GV:
      // return getConstantType();
    }
    return MCGrammarSymbolTableHelper.getQualifiedName(symbol.getAstNode(), symbol,
        AST_PREFIX, "");
  }

  public static String getDefaultValue(ProdSymbol symbol) {
    String name = getQualifiedName(symbol);
    if ("int".equals(name)) {
      return "0";
    }
    if ("boolean".equals(name)) {
      return "false";
    }
    return "null";
  }

  private static String getLexType(ASTNode node) {
      if (node instanceof ASTLexProd) {
        return HelperGrammar.createConvertType((ASTLexProd) node);
      }
      if (node instanceof ASTLexActionOrPredicate) {
        return "String";
      }
    return "UNKNOWN_TYPE";

  }

  public static String formatAttributeValue(Optional<Integer> value) {
    if (!value.isPresent()) {
      return "undef";
    } else if (value.get() == TransformationHelper.STAR) {
      return "*";
    }
    return Integer.toString(value.get());
  }

  public boolean isEmbeddedJavaCode() {
    return this.embeddedJavaCode;
  }

  public boolean isJava() {
    return this.isJava;
  }

  public static String getDotPackageName(String packageName) {
    if (packageName.isEmpty() || packageName.endsWith(".")) {
      return packageName;
    }
    return packageName + ".";
  }

  public static String getPackageName(ASTMCGrammar astGrammar, String suffix) {
    String qualifiedGrammarName = astGrammar.getPackageList().isEmpty()
        ? astGrammar.getName()
        : Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackageList()),
        astGrammar.getName());
    return Joiner.on('.').join(qualifiedGrammarName.toLowerCase(), suffix);
  }
}
