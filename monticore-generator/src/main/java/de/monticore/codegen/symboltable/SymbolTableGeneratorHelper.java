/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisScope;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._ast.ASTCard;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTMethod;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsScope;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.GlobalScope;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static de.se_rwth.commons.Names.getQualifier;

public class SymbolTableGeneratorHelper extends GeneratorHelper {

  public static final String NAME_NONTERMINAL = "Name";

  private final String qualifiedGrammarName;

  private final ASTMCGrammar astGrammar;

  private final MCGrammarSymbol grammarSymbol;

  // TODO PN refactor
  public SymbolTableGeneratorHelper(
          Grammar_WithConceptsGlobalScope mcGlobalScope,
          ASTMCGrammar ast,
          CD4AnalysisGlobalScope cd4AnalysisGlobalScope,
          ASTCDCompilationUnit astCd) {
    super(astCd, cd4AnalysisGlobalScope);
    Log.errorIfNull(ast);
    this.astGrammar = ast;
    this.qualifiedGrammarName = astGrammar.getPackageList().isEmpty()
        ? astGrammar.getName()
        : Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackageList()),
        astGrammar.getName());

    grammarSymbol = mcGlobalScope.resolveMCGrammar(
        qualifiedGrammarName).orElse(null);
    Log.errorIfNull(grammarSymbol, "0xA4036 Grammar " + qualifiedGrammarName
        + " can't be resolved in the scope " + cd4AnalysisGlobalScope);

    checkState(qualifiedGrammarName.equals(grammarSymbol.getFullName()));
  }

  public MCGrammarSymbol getGrammarSymbol() {
    return grammarSymbol;
  }

  /**
   * @return the package for the generated symbol table files
   */
  public String getTargetPackage() {
    return getQualifiedGrammarName().toLowerCase() + "." + SymbolTableGenerator.PACKAGE;
  }
  
  /**
   * @return the package for the generated symbol table files
   */
  public String getSerializationTargetPackage() {
    return getTargetPackage() + "." + SymbolTableGenerator.SERIALIZATION_PACKAGE;
  }

  /**
   * @return the qualified grammar's name
   */
  public String getQualifiedGrammarName() {
    return qualifiedGrammarName;
  }

  public static String getQualifiedSymbolType(ProdSymbol symbol) {
    return SymbolTableGeneratorHelper.getQualifiedSymbolType(
        getQualifier(symbol.getFullName()).toLowerCase(),
        Names.getSimpleName(symbol.getName() + SYMBOL));
  }

  public static String getQualifiedSymbolType(String packageName, String symbolName) {
    return getPackageName(packageName, SymbolTableGenerator.PACKAGE) + "." + symbolName;
  }

  public static String getQualifiedASTType(String packageName, String astName) {
    return getPackageName(packageName, AST_PACKAGE_SUFFIX) + "." + astName;
  }

  /**
   * @return the name of the top ast, i.e., the ast of the start rule.
   */
  public String getQualifiedStartRuleName() {
    if (grammarSymbol.getStartProd().isPresent()) {
      return MCGrammarSymbolTableHelper
          .getQualifiedName(grammarSymbol.getStartProd().get());
    }
    return "";
  }

  public boolean isStartRule(ProdSymbol ruleSymbol) {
    return grammarSymbol.getStartProd().isPresent()
        && grammarSymbol.getStartProd().get().equals(ruleSymbol);
  }

  /**
   * @return all rules using the nonterminal <code>Name</code>. If a usage name
   * is specified, it must be <code>name</code> (case insenstive), e.g.
   * <code>name:Name</code> or <code>Name:Name</code>.
   */
  public Collection<ProdSymbol> getAllSymbolDefiningRules() {
    return getAllSymbolDefiningRules(grammarSymbol);
  }

  public Collection<ProdSymbol> getAllSymbolDefiningRulesInSuperGrammar() {
    final Set<ProdSymbol> ruleSymbolsWithName = new LinkedHashSet<>();

    for (final ProdSymbol superRule : grammarSymbol.getProdsWithInherited().values()) {
      if (superRule.isSymbolDefinition() && superRule.getName().equals(superRule.getSymbolDefinitionKind().get())) {
        ruleSymbolsWithName.add(superRule);
      }
    }

    return ImmutableList.copyOf(ruleSymbolsWithName);
  }

  public Collection<ProdSymbol> getAllOverwrittenSymbolProductions() {
    final Set<ProdSymbol> ruleSymbolsWithName = new LinkedHashSet<>();

    for (final ProdSymbol rule : grammarSymbol.getProds()) {
      if (!rule.isSymbolDefinition()) {
        Optional<ProdSymbol> overwrittenSymbol = grammarSymbol.getInheritedProd(rule.getName());
        if(overwrittenSymbol.isPresent() && overwrittenSymbol.get().isSymbolDefinition()){
          ruleSymbolsWithName.add(overwrittenSymbol.get());
        }
      }
    }

    return ImmutableList.copyOf(ruleSymbolsWithName);
  }


  public Collection<ProdSymbol> getAllScopeSpanningRules() {
    final Set<ProdSymbol> rules = new LinkedHashSet<>();

    for (final ProdSymbol rule : grammarSymbol.getProds()) {
      if (spansScope(rule)) {
        rules.add(rule);
      }
    }

    return ImmutableList.copyOf(rules);
  }

  public Map<String, String> ruleComponents2JavaFields(ProdSymbol ruleSymbol) {
    Log.errorIfNull(ruleSymbol);
    // fieldName -> fieldType
    final Map<String, String> fields = new HashMap<>();

    for (RuleComponentSymbol componentSymbol : ruleSymbol.getProdComponents()) {

      checkArgument(componentSymbol.getAstNode().isPresent());
      if (componentSymbol.isNonterminal()) {
        nonterminal2JavaField(componentSymbol, fields);
      } else if (componentSymbol.isConstant()) {
        constant2JavaField(componentSymbol, fields);
      }
      // Ignore other fields
      /*
      else if (componentSymbol.isConstantGroup()) {
        String attrName = MCGrammarSymbolTableHelper.getConstantName(componentSymbol).orElse("");
        if (canBeTransformedToValidJavaName(attrName)) {
          boolean iterated = MCGrammarSymbolTableHelper.isConstGroupIterated(componentSymbol);
          String constGroupType = iterated? "int" : "boolean";
          fields.put(attrName, constGroupType);
        }
      }
      else if (componentSymbol.isTerminal()) {
        // ignore terminals
      }
      */
    }

    return fields;
  }

  private void nonterminal2JavaField(RuleComponentSymbol componentSymbol,
                                     Map<String, String> fields) {
    final Optional<String> componentName = getRuleComponentName(componentSymbol);
    if (componentName.isPresent()) {
      if (componentSymbol.isSymbolReference()) {
        // the case: Transition = from:Name@State ..., i.e., a reference to
        // another symbol
        fields.put(componentName.get(), componentSymbol.getReferencedSymbolName().get() + "Symbol");
      }
    }
    // TODO PN else, do something?
  }

  private void constant2JavaField(RuleComponentSymbol componentSymbol,
                                  Map<String, String> fields) {
    final Optional<String> componentName = getRuleComponentName(componentSymbol);
    if (componentName.isPresent()) {
      fields.put(componentName.get(), "boolean");
    }
  }

  private Optional<String> getRuleComponentName(RuleComponentSymbol componentSymbol) {
    if (canBeTransformedToValidJavaName(componentSymbol.getName())) {
      return Optional.of(componentSymbol.getName());
    } else if (canBeTransformedToValidJavaName(componentSymbol.getUsageName())) {
      return Optional.of(componentSymbol.getUsageName());
    }

    return Optional.empty();
  }

  public Map<String, String> symbolRuleComponents2JavaFields(ProdSymbol ruleSymbol) {
    Log.errorIfNull(ruleSymbol);

    // fieldName -> fieldType
    final Map<String, String> fields = new HashMap<>();

    for (RuleComponentSymbol componentSymbol : ruleSymbol.getProdComponents()) {
      checkArgument(componentSymbol.getAstNode().isPresent());
      if (componentSymbol.isNonterminal()) {
        symbolNonTerminal2JavaField(componentSymbol, fields);
      }
    }

    return fields;
  }

  private void symbolNonTerminal2JavaField(RuleComponentSymbol componentSymbol,
                                           Map<String, String> fields) {
    final Optional<String> componentName = getRuleComponentName(componentSymbol);
    if (componentName.isPresent() && componentSymbol.getReferencedProd().isPresent()) {
      // the case: Automaton = Name ... State* ..., i.e., the containment of
      // another symbol
      final Optional<ProdSymbol> referencedRule = grammarSymbol
          .getProd(componentSymbol.getReferencedProd().get().getName());
      if (referencedRule.isPresent() && referencedRule.get().isSymbolDefinition()) {
        fields.put(componentName.get(), referencedRule.get().getName() + "Symbol");
      }
    }
  }

  public Map<String, String> symbolReferenceRuleComponents2JavaFields(ProdSymbol ruleSymbol) {
    Log.errorIfNull(ruleSymbol);
    // fieldName -> fieldType
    final Map<String, String> fields = new HashMap<>();

    for (RuleComponentSymbol componentSymbol : ruleSymbol.getProdComponents()) {
      checkArgument(componentSymbol.getAstNode().isPresent());
      if (componentSymbol.isNonterminal()) {
        nonterminal2JavaField(componentSymbol, fields);
      }
    }

    return fields;
  }

  public Map<String, String> ruleComponentsWithoutSymbolReferences2JavaFields(
      final ProdSymbol ruleSymbol) {
    final Map<String, String> all = ruleComponents2JavaFields(ruleSymbol);
    final Map<String, String> symbolReferences = symbolReferenceRuleComponents2JavaFields(
        ruleSymbol);

    final Map<String, String> withoutSymbolReferences = new LinkedHashMap<>();

    for (Map.Entry<String, String> entry : all.entrySet()) {
      if (!symbolReferences.containsKey(entry.getKey())) {
        withoutSymbolReferences.put(entry.getKey(), entry.getValue());
      }
    }

    return withoutSymbolReferences;
  }

  public Map<String, String> nonSymbolFields(ProdSymbol ruleSymbol) {
    return null; // TODO PN implement
  }

  public static String getterPrefix(final String type) {
    if ("boolean".equals(type) || "Boolean".equals(type)) {
      return "is";
    }
    return "get";
  }
  
  public static String getDefaultInitValue(final String type) {
    switch(type) {
      case "boolean":
        return "false";
      case "int":
        return "0";
      case "float":
        return "0.0f";
      case "double":
        return "0.0";
      case "long":
        return "0L";
      case "byte":
        return "0";
      case "short":
        return "0";
      case "char":
        return "0";
      default:
        return "null";
    }
  }

  public static String getDeserializationType(final String type) {
    switch (type) {
      case "boolean":
        return "Boolean";
      case "int":
        return "Int";
      case "float":
        return "Double";
      case "double":
        return "Double";
      case "long":
        return "Long";
      case "byte":
        return "Int";
      case "short":
        return "Int";
      case "char":
        return "Int";
      case "String":
        return "String";
      default:
        return "";
    }
  }
  
  public static String getDeserializationCastString(final String type) {
    switch(type) {
      case "float":
        return "(double)";
      case "long":
        return "(long)";
      case "byte":
        return "(byte)";
      case "short":
        return "(short)";
      case "char":
        return "(char)";
      default:
        return "";
    }
  }

  /**
   * Returns true, if <code>name</code> is a valid Java name or can be
   * transformed to a valid Java name using
   * {@link JavaNamesHelper#getNonReservedName(String)}. For example,
   * <code>final</code> is no valid Java name, but can be transformed to one,
   * e.g., <code>r__final</code>. However, <code>+-</code> is neither a valid
   * Java nor can it be transformed to one.
   *
   * @param name
   * @return true, if <code>name</code> is a valid Java name or can be
   * transformed to a valid Java name using
   * {@link JavaNamesHelper#getNonReservedName(String)}.
   */
  private boolean canBeTransformedToValidJavaName(String name) {
    return isValidName(nonReservedName(name));
  }

  public static String nonReservedName(final String name) {
    return JavaNamesHelper.getNonReservedName(name);
  }

  // TODO PN move to JavaNamesHelper

  private static final Set<String> KEYWORDS = new LinkedHashSet<>(Arrays.asList(
      "abstract", "continue", "for", "new", "switch", "assert", "default", "goto",
      "package", "synchronized", "boolean", "do", "if", "private", "this", "break",
      "double", "implements", "protected", "throw", "byte", "else", "import", "public",
      "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends",
      "int", "short", "try", "char", "final", "interface", "static", "void", "class",
      "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while"));

  private static final Pattern JAVA_SIMPLE_NAME_PATTERN = Pattern
      .compile("[A-Za-z_$]+[a-zA-Z0-9_$]*");

  public static boolean isValidName(final String name) {
    if ("".equals(nullToEmpty(name))) {
      return false;
    }

    return !KEYWORDS.contains(name) && JAVA_SIMPLE_NAME_PATTERN.matcher(name).matches();
  }

  public static String getGeneratedErrorCode(ASTNode ast) {
    return GeneratorHelper.getGeneratedErrorCode(ast);
  }

  // TODO refactor
  public String getVisitorType() {
    return VisitorGeneratorHelper.getVisitorType(getCdName());
  }

  // TODO refactor
  public String getDelegatorVisitorType() {
    return VisitorGeneratorHelper.getDelegatorVisitorType(getCdName());
  }

  // TODO refactor
  public static String getQualifiedSymTabCreatorType(String packageName, String cdName) {
    return getPackageName(packageName, getVisitorPackageSuffix()) + "."
        + getSymTabCreatorType(cdName);
  }

  // TODO refactor
  public static String getSymTabCreatorType(String cdName) {
    return cdName + "SymbolTableCreator";
  }

  // TODO refactor
  public String getQualifiedVisitorNameAsJavaName(CDDefinitionSymbol cd) {
    return VisitorGeneratorHelper.qualifiedJavaTypeToName(getQualifiedVisitorType(cd));
  }

  // TODO refactor
  public static String getQualifiedSymTabCreator(String qualifiedLanguageName) {
    String packageName = getCdPackage(qualifiedLanguageName);
    String cdName = getCdName(qualifiedLanguageName);
    return getQualifiedSymTabCreatorType(packageName, cdName);
  }

  // TODO refactor
  public String getQualifiedVisitorType(CDDefinitionSymbol cd) {
    return VisitorGeneratorHelper.getQualifiedVisitorType(cd.getFullName());
  }
  
  public String getQualifiedScopeVisitorType(CDDefinitionSymbol cd) {
    return VisitorGeneratorHelper.getQualifiedScopeVisitorType(cd.getFullName());
  }
  
  public String getQualifiedScopeVisitorType(String symbol) {
    Optional<CDDefinitionSymbol> cdSymbol = this.cdSymbol.getEnclosingScope().resolveCDDefinition(symbol);
    if (cdSymbol.isPresent()) {
      return getQualifiedScopeVisitorType(cdSymbol.get());
    }
    return "";
  }

  public boolean spansScope(final ProdSymbol rule) {
    return rule.isScopeDefinition();
  }

  public boolean isSymbol(final ProdSymbol rule) {
    return rule.isSymbolDefinition();
  }

  public boolean isScopeSpanningSymbol(final ProdSymbol rule) {
    return isSymbol(rule) && spansScope(rule);
  }

  public boolean isNamed(final ProdSymbol rule) {
    for (RuleComponentSymbol comp : rule.getProdComponents()) {
      // TODO check full name?
      if ((comp.getName().equals(NAME_NONTERMINAL) &&
          isNullOrEmpty(comp.getUsageName())
          || comp.getUsageName().equalsIgnoreCase(NAME_NONTERMINAL))) {
        return true;
      }
    }
    return false;
  }

  public boolean isOptionalNamed(final ProdSymbol rule) {
    for (RuleComponentSymbol comp : rule.getProdComponents()) {
      // TODO check full name?
      if (comp.getName().equals(NAME_NONTERMINAL) &&
          (isNullOrEmpty(comp.getUsageName())
              || comp.getUsageName().equalsIgnoreCase(NAME_NONTERMINAL)) && comp.isOptional()) {
        return true;
      }
    }
    return false;
  }

  public boolean existsHandwrittenSymbolClass(ProdSymbol ruleSymbol, IterablePath handCodedPath) {
    return existsHandwrittenClass(Names.getSimpleName(ruleSymbol.getName() + "Symbol"),
        getTargetPackage(), handCodedPath);
  }

  public String getQualifiedASTName(String name) {
    Optional<ProdSymbol> prod = grammarSymbol.getProdWithInherited(name);
    if (prod.isPresent()) {
      return AST_PREFIX + prod.get().getName();
    }
    return name;
  }

  public String printMethod(ASTMethod meth) {
    Grammar_WithConceptsPrettyPrinter pp = new Grammar_WithConceptsPrettyPrinter(new IndentPrinter());
    String code = pp.prettyprint(meth);
    code = code.replaceFirst("method", "");
    return code;
  }

  public String getQualifiedProdName(ProdSymbol prod) {
    String prodName = prod.getFullName();
    prodName = SymbolTableGeneratorHelper
        .getQualifiedSymbolType(getQualifier(prodName)
            .toLowerCase(), Names.getSimpleName(prodName));
    return prodName;
  }

  public String getQualifiedASTName(ProdSymbol prod) {
    String prodName = prod.getFullName();
    prodName = SymbolTableGeneratorHelper
        .getQualifiedASTType(getQualifier(prodName)
            .toLowerCase(), "AST"+ Names.getSimpleName(prodName));
    return prodName;
  }

  public String getQualifiedScopeInterfaceType(CDDefinitionSymbol cdSymbol) {
    String packageName = getCdPackage(cdSymbol.getFullName());
    String cdName = getCdName(cdSymbol.getFullName());
    return getQualifiedScopeInterfaceType(packageName, cdName);
  }

  public String getQualifiedScopeInterfaceType(String symbol) {
    Optional<CDDefinitionSymbol> cdSymbol = this.cdSymbol.getEnclosingScope().resolveCDDefinition(symbol);
    if (cdSymbol.isPresent()) {
      return getQualifiedScopeInterfaceType(cdSymbol.get());
    }
    return "";
  }

  public String getQualifiedScopeInterfaceType(String packageName, String cdName) {
    return getPackageName(packageName, SymbolTableGenerator.PACKAGE) + "." 
        + getScopeInterfaceType(cdName);
  }
  
  public String getScopeInterfaceType(String cdName) {
    return "I" + cdName + SCOPE;
  }
  
  public String getScopeInterfaceType(CDDefinitionSymbol cdSymbol) {
    String cdName = getCdName(cdSymbol.getFullName());
    return getScopeInterfaceType(cdName);
  }
  
  public String getSymbolNameFromQualifiedSymbol (String qualifiedSymbol) {
	  return qualifiedSymbol.substring(qualifiedSymbol.lastIndexOf(".") + 1);
  }
  
  public Set<String> getAllQualifiedSymbols() {
    Set<String> qualifiedSymbols = new LinkedHashSet<>();
    
    // add local and inherited qualified symbols
    qualifiedSymbols.addAll(getQualifiedSymbolsFromGrammar(grammarSymbol));
    qualifiedSymbols.addAll(getQualifiedInheritedSymbols());
    return qualifiedSymbols;
  }

  /**
   * Computes a set of qualified symbols for all super grammars.
   *
   * @return a set of qualified inherited symbols
   */
  public Set<String> getQualifiedInheritedSymbols() {
    Set<String> inheritedSymbols = new LinkedHashSet<>();
    for (CDDefinitionSymbol superCd : getAllSuperCds(cdSymbol)) {
      // resolve super grammar and retrieve qualified symbols
      MCGrammarSymbol grammarSymbol = getGrammarSymbol().getEnclosingScope().resolveMCGrammar(superCd.getFullName()).orElse(null);
      inheritedSymbols.addAll(getQualifiedSymbolsFromGrammar(grammarSymbol));
    }
    return inheritedSymbols;
  }

  /**
   * Retrieves the qualified symbols for a given grammar.
   *
   * @param grammarSymbol The given grammar symbol.
   * @return a set of qualified symbols.
   */
  public Set<String> getQualifiedSymbolsFromGrammar(MCGrammarSymbol grammarSymbol) {
    Set<String> qualifiedSymbols = new LinkedHashSet<>();
    if (grammarSymbol != null && grammarSymbol.getAstGrammar().isPresent()) {
      String packageName = GeneratorHelper.getPackageName(grammarSymbol.getAstGrammar().get(), GeneratorHelper.SYMBOLTABLE_PACKAGE_SUFFIX);
      // store qualified symbols
      for (ProdSymbol symbol : SymbolTableGeneratorHelper.getAllSymbolDefiningRules(grammarSymbol)) {
        qualifiedSymbols.add(GeneratorHelper.getPackageName(packageName, symbol.getName() + GeneratorHelper.SYMBOL));
      }
    }
    return qualifiedSymbols;
  }

  public boolean hasSymbolDefiningRule(String symbol) {
    Optional<MCGrammarSymbol> grammarSymbol = getGrammarSymbol().getEnclosingScope().resolveMCGrammar(symbol);
    if (grammarSymbol.isPresent()) {
      for (ProdSymbol prodSymbol : grammarSymbol.get().getProds()) {
        if (prodSymbol.isSymbolDefinition()) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean hasScopeSpanningRule(String symbol) {
    Optional<MCGrammarSymbol> grammarSymbol = getGrammarSymbol().getEnclosingScope().resolveMCGrammar(symbol);
    if (grammarSymbol.isPresent()) {
      for (ProdSymbol prodSymbol : grammarSymbol.get().getProds()) {
        if (prodSymbol.isScopeDefinition()) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isComponentGrammar(String grammarName) {
    Optional<MCGrammarSymbol> grammarSymbol = getGrammarSymbol().getEnclosingScope().resolveMCGrammar(grammarName);
    if (grammarSymbol.isPresent() && grammarSymbol.get().isComponent()) {
        return true;
    }
    return false;
  }

  /**
   * @param grammarSymbol The grammar that contains the symbol defining rule
   * @return all rules using the nonterminal <code>Name</code>. If a usage name
   *         is specified, it must be <code>name</code> (case insenstive), e.g.
   *         <code>name:Name</code> or <code>Name:Name</code>.
   */
  public static Collection<ProdSymbol> getAllSymbolDefiningRules(MCGrammarSymbol grammarSymbol) {
    final Set<ProdSymbol> ruleSymbolsWithName = new LinkedHashSet<>();
    
    for (final ProdSymbol rule : grammarSymbol.getProds()) {
      if (rule.isSymbolDefinition() && rule.getName().equals(rule.getSymbolDefinitionKind().get())) {
        ruleSymbolsWithName.add(rule);
      }
    }

    return ImmutableList.copyOf(ruleSymbolsWithName);
  }
  
  /**
   * Checks whether the language related to the given class diagram symbol
   * generates a symbol table.
   *
   * @param cdSymbol The input class diagram symbol.
   * @return A boolean value if the language has a symbol table.
   */
  public boolean hasSymbolTable(CDDefinitionSymbol cdSymbol) {
    Optional<MCGrammarSymbol> grammarSymbol = getGrammarSymbol().getEnclosingScope().resolveMCGrammar(cdSymbol.getFullName());
    if (grammarSymbol.isPresent() && grammarSymbol.get().getStartProd().isPresent()) {
      return true;
    }
    return false;
  }

  /**
   * Checks whether the language related to the given name of a class diagram
   * symbol generates a symbol table.
   *
   * @param cdSymbolName The name of the input class diagram symbol.
   * @return A boolean value if the language has a symbol table.
   */
  public boolean hasSymbolTable(String cdSymbolName) {
    Optional<CDDefinitionSymbol> cdSymbolOpt = resolveCd(cdSymbolName);
    if (cdSymbolOpt.isPresent()) {
      return hasSymbolTable(cdSymbolOpt.get());
    } else {
      return false;
    }
  }

	/**
	 * Computes the fully qualified ISymbolDelegator for a given symbol.
	 * 
	 * @param symbol The input symbol.
	 * @return A String containing the qualified path of ISymbolDelegator.
	 */
	public static String getDelegatorForSymbol(ProdSymbol symbol) {
	  return SymbolTableGeneratorHelper.getQualifiedSymbolType(
        getQualifier(symbol.getFullName()).toLowerCase(),
        Names.getSimpleName("I" + symbol.getName() + SYMBOL + DELEGATE));
	}
	
  /**
   * Checks whether the given additional attribute has star cardinality.
   * 
   * @param attr The input attribute.
   * @return true if the attribute has star cardinality, false otherwise.
   */
  public static boolean isAdditionalAttributeTypeList(ASTAdditionalAttribute attr) {
    if (attr.isPresentCard()) {
      ASTCard card = attr.getCard();
      if (card.isPresentMax()) {
        String max = card.getMax();
        // check for * cardinality
        if (max.equals("*")) {
          return true;
        }
        // check for any number greater 1
        try {
          int maxNumber = Integer.parseInt(max);
          if (maxNumber > 1) {
            return true;
          }
        }
        catch (NumberFormatException e) {
          return false;
        }
      }
    }
    return false;
  }
  
  /**
   * Checks whether the given additional attribute is optional.
   * 
   * @param attr The input attribute.
   * @return true if the attribute is optional, false otherwise.
   */
  public static boolean isAdditionalAttributeTypeOptional(ASTAdditionalAttribute attr) {
    if (attr.isPresentCard()) {
      ASTCard card = attr.getCard();
      if (card.isPresentMax() && card.isPresentMin()) {
        if (card.getMin().equals("0") && card.getMax().equals("1")) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Converts the given primitive type in its corresponding object data type.
   * 
   * @param primitive The input primitive type.
   * @return The computed object data type or the input otherwise.
   */
  public static String convertToObjectDataType(String primitive) {
    switch (primitive) {
      case "boolean":
        return "Boolean";
      case "int":
        return "Integer";
      case "float":
        return "Float";
      case "double":
        return "Double";
      case "long":
        return "Long";
      case "byte":
        return "Byte";
      case "short":
        return "Short";
      case "char":
        return "Character";
      default:
        return primitive;
    }
  }
  
  /**
   * Computes the type of an additional attribute concerning the cardinality
   * value.
   * 
   * @param attr The input attribute.
   * @return The qualified type of the attribute as String.
   */
  public String deriveAdditionalAttributeTypeWithMult(ASTAdditionalAttribute attr) {
    String defaultType = getQualifiedASTName(attr.getMCType().getBaseName());
    if (isAdditionalAttributeTypeList(attr)) {
      return "java.util.List<" + convertToObjectDataType(defaultType) + ">";
    }
    if (isAdditionalAttributeTypeOptional(attr)) {
      return "java.util.Optional<" + convertToObjectDataType(defaultType) + ">";
    }
    return defaultType;
  }
}
