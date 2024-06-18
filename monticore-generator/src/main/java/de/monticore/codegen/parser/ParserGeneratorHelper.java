/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.PredicatePair;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbolSurrogate;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._ast.ASTAction;
import de.monticore.grammar.grammar_withconcepts._ast.ASTExpressionPredicate;
import de.monticore.grammar.grammar_withconcepts._ast.ASTGrammar_WithConceptsNode;
import de.monticore.grammar.grammar_withconcepts._ast.ASTJavaCode;
import de.monticore.javalight._ast.ASTClassBodyDeclaration;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static de.monticore.codegen.mc2cd.TransformationHelper.getQualifiedName;

/**
 * This is a helper class for the parser generation
 */
public class ParserGeneratorHelper {

  public static final String MONTICOREANYTHING = "MONTICOREANYTHING";

  public static final String RIGHTASSOC = "<assoc=right>";

  protected static final String NOKEYWORD = "nokeyword_";

  protected ASTMCGrammar astGrammar;

  protected String qualifiedGrammarName;

  protected MCGrammarSymbol grammarSymbol;

  protected MCGrammarInfo grammarInfo;

  protected Map<ASTNode, String> tmpVariables = new LinkedHashMap<>();

  protected int tmp_counter = 0;

  protected boolean embeddedJavaCode;

  protected boolean isJava;

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
      return getQualifiedName(grammarSymbol.getStartProd().get());
    }
    for (MCGrammarSymbol g: grammarSymbol.getSuperGrammarSymbols()) {
      if (g.getStartProd().isPresent()) {
        return getQualifiedName(g.getStartProd().get());
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
    return NOKEYWORD + key + "_" + Integer.toUnsignedString(key.hashCode());
  }

  /**
   * Get all used LexSymbols, different form information in AST, as inherited
   * ones are integrated as well
   *
   * @return Keys of all lex symbols
   */
  public Set<String> getLexSymbolsWithInherited() {
    Set<String> retSet = Sets.newLinkedHashSet(grammarInfo.getLexNamer().getLexnames());
    retSet.addAll(
        grammarInfo.getKeywords().stream()
            .filter(f -> !grammarSymbol.getKeywordRulesWithInherited().contains(f))
            .collect(Collectors.toList())
    );
    return retSet;
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
      if (!rule.isIsIndirectLeftRecursive()) {
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
      return createConvertFunction((ASTLexProd) symbol.getAstNode());
    }
    return "";
  }

  /**
   * Creates the convert function for a lexrule
   *
   * @param a
   * @return
   */
  public static String createConvertFunction(ASTLexProd a) {
    String name = a.getName();
    // simple String
    if (!a.isPresentVariable()) {
      return createStringConvertFunction(name);
    }

    // default functions
    else if (a.getTypeList() == null || a.getTypeList().isEmpty()) {
      String variable = a.getVariable();

      if ("int".equals(variable)) {
        String function = "private int convert%name%(Token t) {\n"

                + "  return Integer.parseInt(t.getText());\n"
                + " }\n";
        return createConvertFunction(name, function);
      } else if ("boolean".equals(variable)) {
        return createConvertFunction(
                name,
                "private boolean convert"
                        + name
                        + "(Token t) {\n"
                        + "    if (t.getText().equals(\"1\")||t.getText().equals(\"start\")||t.getText().equals(\"on\")||t.getText().equals(\"true\")){return true;}else{return false;} \n"
                        + "}\n");
      } else if ("byte".equals(variable)) {
        String function = "private byte convert%name%(Token t) {\n"
                + "  return Byte.parseByte(t.getText());\n"
                + " }\n";
        return createConvertFunction(name, function);
      } else if ("char".equals(variable)) {
        return createConvertFunction(name, "private char convert" + name + "(Token t) " + "{\n"
                + "  return t.getText().charAt(0); \n" + "}\n");
      } else if ("float".equals(variable)) {
        String function = "private float convert%name%(Token t) {\n"
                + "  return Float.parseFloat(t.getText());\n"
                + " }\n";
        return createConvertFunction(name, function);
      } else if ("double".equals(variable)) {
        String function = "private double convert%name%(Token t) {\n"
                + "  return Double.parseDouble(t.getText());\n"
                + " }\n";
        return createConvertFunction(name, function);
      } else if ("long".equals(variable)) {
        String function = "private long convert%name%(Token t) {\n"
                + "  return Long.parseLong(t.getText());\n"
                + " }\n";
        return createConvertFunction(name, function);
      } else if ("short".equals(variable)) {
        String function = "private short convert%name%(Token t) {\n"
                + "return Short.parseShort(t.getText());\n"
                + " }\n";
        return createConvertFunction(name, function);
      } else if ("card".equals(variable)) {
        String function = "private int convert%name%(Token t) {\n"
                + "   if (t.getText().equals(\"*\")) return -1; else return Integer.parseInt(t.getText());\n"
                + " }\n";
        return createConvertFunction(name, function);
      } else {
        Log.warn(
                "0xA1061 No function for " + a.getVariable() + " registered, will treat it as string!");
        return createStringConvertFunction(name);
      }
    }
    // specific function
    else {
      if (a.isPresentBlock()) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(Grammar_WithConceptsMill.prettyPrint(a.getBlock(), true));
        String createConvertFunction = createConvertFunction(name,
                "private " + Names.getQualifiedName(a.getTypeList()) + " convert" + name
                        + "(Token " + a.getVariable() + ")" + " {\n" + buffer.toString() + "}\n");
        return createConvertFunction;
      }
    }
    return "";

  }

  protected static String createConvertFunction(String name, String function) {
    String f = function.replaceAll("%name%", name);
    return "// convert function for " + name + "\n" + f;
  }

  public static String createStringConvertFunction(String name) {
    String t = "private String convert" + name + "(Token t)  {\n" + "    return t.getText();\n"
            + "}\n";
    return "// convert function for " + name + "\n" + t;
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
    ArrayList<ASTLexProd> prodList = Lists.newArrayList();
    Map<String, Collection<String>> modeMap = grammarSymbol.getTokenModesWithInherited();
    // Default mode
    if (modeMap.containsKey("")) {
      for (String tokenName : modeMap.get("")) {
        Optional<ProdSymbol> localToken = grammarSymbol.getSpannedScope().resolveProdDown(tokenName);
        if (localToken.isPresent() && localToken.get().isIsLexerProd()) {
          prodList.add((ASTLexProd) localToken.get().getAstNode());
        } else {
          grammarSymbol.getSpannedScope().resolveProdMany(tokenName).stream().filter(p -> p.isIsLexerProd()).forEach(p -> prodList.add((ASTLexProd) p.getAstNode()));
        }
      }
    }
    return prodList;
  }

  public Map<String, List<ASTProd>> getLexerRulesForMode() {
    Map<String, List<ASTProd>> retMap = Maps.newLinkedHashMap();
    Map<String, Collection<String>> modeMap = grammarSymbol.getTokenModesWithInherited();
    for (Entry<String, Collection<String>> e: modeMap.entrySet()) {
      ArrayList<ASTProd> prodList = Lists.newArrayList();
      if (!e.getKey().isEmpty()) {
        for (String tokenName : e.getValue()) {
          Optional<ProdSymbol> localToken = grammarSymbol.getSpannedScope().resolveProdDown(tokenName);
          if (localToken.isPresent() && localToken.get().isIsLexerProd()) {
            prodList.add(localToken.get().getAstNode());
          } else {
            grammarSymbol.getSpannedScope().resolveProdMany(tokenName).stream().filter(p -> p.isIsLexerProd()).forEach(p -> prodList.add(p.getAstNode()));
          }
        }
        retMap.put(e.getKey(), prodList);
      }
    }
    return retMap;
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

  protected String getNewTmpVar() {
    return "tmp" + (Integer.valueOf(tmp_counter++)).toString();
  }

  public void resetTmpVarNames() {
    tmpVariables.clear();
    tmp_counter = 0;
  }

  public Map<ASTNode, String> getTmpVariables() {
    return tmpVariables;
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
    for (MCGrammarSymbolSurrogate g : grammarSymbol.getSuperGrammars()) {
      final Optional<ProdSymbol> ruleByName = g.lazyLoadDelegate().getProdWithInherited(ast.getName());
      if (ruleByName.isPresent() && ruleByName.get().isClass()) {
        if (ruleByName.get().isPresentAstNode() && ruleByName.get().getAstNode() instanceof ASTClassProd) {
          return ((ASTClassProd)ruleByName.get().getAstNode()).getAltList();
        }
      }
    }
    return Lists.newArrayList();
  }

  public static String getASTClassName(ProdSymbol rule) {
    return getQualifiedName(rule);
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
        buffer.append(Grammar_WithConceptsMill.prettyPrint(action, true));
      }
      return buffer.toString();
    }
    if (node instanceof ASTJavaCode) {
      StringBuilder buffer = new StringBuilder();
      for (ASTClassBodyDeclaration action : ((ASTJavaCode) node).getClassBodyDeclarationList()) {
        buffer.append(Grammar_WithConceptsMill.prettyPrint(action, true));

      }
      return buffer.toString();
    }
    if (node instanceof ASTExpressionPredicate) {
      String exprPredicate = Grammar_WithConceptsMill.prettyPrint((((ASTExpressionPredicate) node).getExpression()), true);
      Log.debug("ASTExpressionPredicate:\n" + exprPredicate, ParserGenerator.LOG);
      return exprPredicate;
    }
    if (node instanceof ASTGrammar_WithConceptsNode) {
      String output = Grammar_WithConceptsMill.prettyPrint((ASTGrammar_WithConceptsNode) node, true);
      Log.debug("ASTGrammar_WithConceptsNode:\n" + output, ParserGenerator.LOG);
      return output;
    }
    return "";
  }

  public static String getParseRuleName(ProdSymbol rule) {
    return StringTransformations.uncapitalize(rule.getName());
  }

  public static String getDefaultValue(ProdSymbol symbol) {
    if ("int".equals(symbol.getFullName())) {
      return "0";
    }
    if ("boolean".equals(symbol.getFullName())) {
      return "false";
    }
    return "null";
  }

  protected static String getLexType(ASTNode node) {
      if (node instanceof ASTLexProd) {
        return TransformationHelper.createConvertType((ASTLexProd) node);
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

  /**
   * Creates usuage name from a NtSym usually from its attribute or creates name
   *
   * @param a
   * @return
   */
  public static String getUsageName(ASTNonTerminal a) {
    String name;
    if (a.isPresentUsageName()) {
      name = a.getUsageName();
    } else {
      // Use Nonterminal name as attribute name starting with lower case
      // latter
      name = StringTransformations.uncapitalize(a.getName());
    }
    return name;
  }

  /**
   * Fetch the super class for the ANTLR Lexer.
   * By default, null
   */
  @SuppressWarnings("unused")
  @Nullable
  public String getLexerSuperClass() {
    if (!astGrammar.isPresentGrammarOption() || astGrammar.getGrammarOption().isEmptyAntlrOptions()) {
      return null;
    }
    return astGrammar.getGrammarOption().getAntlrOptionList().stream()
            .filter(ASTAntlrOption::isPresentValue)
            .filter(a -> a.getName().equals("LexerSuperClass")).map(ASTAntlrOption::getValue)
            .findFirst().orElse(null);
  }

  /**
   * Fetch the super class for the ANTLR Parser.
   * By default, MCParser
   */
  @SuppressWarnings("unused")
  @Nonnull
  public String getParserSuperClass() {
    if (!astGrammar.isPresentGrammarOption() || astGrammar.getGrammarOption().isEmptyAntlrOptions()) {
      return "MCParser";
    }
    return astGrammar.getGrammarOption().getAntlrOptionList().stream()
            .filter(ASTAntlrOption::isPresentValue)
            .filter(a -> a.getName().equals("ParserSuperClass")).map(ASTAntlrOption::getValue)
            .findFirst().orElse("MCParser");
  }

}
