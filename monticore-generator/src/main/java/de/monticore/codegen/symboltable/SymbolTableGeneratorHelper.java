/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTMethod;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;
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
      ASTMCGrammar ast,
      GlobalScope globalScope,
      ASTCDCompilationUnit astCd) {
    super(astCd, globalScope);
    Log.errorIfNull(ast);
    this.astGrammar = ast;
    this.qualifiedGrammarName = astGrammar.getPackageList().isEmpty()
        ? astGrammar.getName()
        : Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackageList()),
        astGrammar.getName());

    grammarSymbol = globalScope.<MCGrammarSymbol>resolve(
        qualifiedGrammarName, MCGrammarSymbol.KIND).orElse(null);
    Log.errorIfNull(grammarSymbol, "0xA4036 Grammar " + qualifiedGrammarName
        + " can't be resolved in the scope " + globalScope);

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
   * @return the qualified grammar's name
   */
  public String getQualifiedGrammarName() {
    return qualifiedGrammarName;
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

  public boolean isStartRule(MCProdSymbol ruleSymbol) {
    return grammarSymbol.getStartProd().isPresent()
        && grammarSymbol.getStartProd().get().equals(ruleSymbol);
  }

  /**
   * @return all rules using the nonterminal <code>Name</code>. If a usage name
   * is specified, it must be <code>name</code> (case insenstive), e.g.
   * <code>name:Name</code> or <code>Name:Name</code>.
   */
  public Collection<MCProdSymbol> getAllSymbolDefiningRules() {
    return getAllSymbolDefiningRules(grammarSymbol);
  }

  public Collection<MCProdSymbol> getAllSymbolDefiningRulesInSuperGrammar() {
    final Set<MCProdSymbol> ruleSymbolsWithName = new LinkedHashSet<>();

    for (final MCProdSymbol superRule : grammarSymbol.getProdsWithInherited().values()) {
      if (superRule.isSymbolDefinition() && superRule.getName().equals(superRule.getSymbolDefinitionKind().get())) {
        ruleSymbolsWithName.add(superRule);
      }
    }

    return ImmutableList.copyOf(ruleSymbolsWithName);
  }

  public Collection<MCProdSymbol> getAllOverwrittenSymbolProductions() {
    final Set<MCProdSymbol> ruleSymbolsWithName = new LinkedHashSet<>();

    for (final MCProdSymbol rule : grammarSymbol.getProds()) {
      if (!rule.isSymbolDefinition()) {
        Optional<MCProdSymbol> overwrittenSymbol = grammarSymbol.getInheritedProd(rule.getName());
        if(overwrittenSymbol.isPresent() && overwrittenSymbol.get().isSymbolDefinition()){
          ruleSymbolsWithName.add(overwrittenSymbol.get());
        }
      }
    }

    return ImmutableList.copyOf(ruleSymbolsWithName);
  }


  public Collection<MCProdSymbol> getAllScopeSpanningRules() {
    final Set<MCProdSymbol> rules = new LinkedHashSet<>();

    for (final MCProdSymbol rule : grammarSymbol.getProds()) {
      if (spansScope(rule)) {
        rules.add(rule);
      }
    }

    return ImmutableList.copyOf(rules);
  }

  public Map<String, String> ruleComponents2JavaFields(MCProdSymbol ruleSymbol) {
    Log.errorIfNull(ruleSymbol);
    // fieldName -> fieldType
    final Map<String, String> fields = new HashMap<>();

    for (MCProdComponentSymbol componentSymbol : ruleSymbol.getProdComponents()) {

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

  private void nonterminal2JavaField(MCProdComponentSymbol componentSymbol,
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

  private void constant2JavaField(MCProdComponentSymbol componentSymbol,
                                  Map<String, String> fields) {
    final Optional<String> componentName = getRuleComponentName(componentSymbol);
    if (componentName.isPresent()) {
      fields.put(componentName.get(), "boolean");
    }
  }

  private Optional<String> getRuleComponentName(MCProdComponentSymbol componentSymbol) {
    if (canBeTransformedToValidJavaName(componentSymbol.getName())) {
      return Optional.of(componentSymbol.getName());
    } else if (canBeTransformedToValidJavaName(componentSymbol.getUsageName())) {
      return Optional.of(componentSymbol.getUsageName());
    }

    return Optional.empty();
  }

  public Map<String, String> symbolRuleComponents2JavaFields(MCProdSymbol ruleSymbol) {
    Log.errorIfNull(ruleSymbol);

    // fieldName -> fieldType
    final Map<String, String> fields = new HashMap<>();

    for (MCProdComponentSymbol componentSymbol : ruleSymbol.getProdComponents()) {
      checkArgument(componentSymbol.getAstNode().isPresent());
      if (componentSymbol.isNonterminal()) {
        symbolNonTerminal2JavaField(componentSymbol, fields);
      }
    }

    return fields;
  }

  private void symbolNonTerminal2JavaField(MCProdComponentSymbol componentSymbol,
                                           Map<String, String> fields) {
    final Optional<String> componentName = getRuleComponentName(componentSymbol);
    if (componentName.isPresent() && componentSymbol.getReferencedProd().isPresent()) {
      // the case: Automaton = Name ... State* ..., i.e., the containment of
      // another symbol
      final Optional<MCProdSymbol> referencedRule = grammarSymbol
          .getProd(componentSymbol.getReferencedProd().get().getName());
      if (referencedRule.isPresent() && referencedRule.get().isSymbolDefinition()) {
        fields.put(componentName.get(), referencedRule.get().getName() + "Symbol");
      }
    }
  }

  public Map<String, String> symbolReferenceRuleComponents2JavaFields(MCProdSymbol ruleSymbol) {
    Log.errorIfNull(ruleSymbol);
    // fieldName -> fieldType
    final Map<String, String> fields = new HashMap<>();

    for (MCProdComponentSymbol componentSymbol : ruleSymbol.getProdComponents()) {
      checkArgument(componentSymbol.getAstNode().isPresent());
      if (componentSymbol.isNonterminal()) {
        nonterminal2JavaField(componentSymbol, fields);
      }
    }

    return fields;
  }

  public Map<String, String> ruleComponentsWithoutSymbolReferences2JavaFields(
      final MCProdSymbol ruleSymbol) {
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

  public Map<String, String> nonSymbolFields(MCProdSymbol ruleSymbol) {
    return null; // TODO PN implement
  }

  public static String getterPrefix(final String type) {
    if ("boolean".equals(type) || "Boolean".equals(type)) {
      return "is";
    }
    return "get";
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
  public String getQualifiedVisitorNameAsJavaName(CDSymbol cd) {
    return VisitorGeneratorHelper.qualifiedJavaTypeToName(getQualifiedVisitorType(cd));
  }

  // TODO refactor
  public static String getQualifiedSymTabCreator(String qualifiedLanguageName) {
    String packageName = getCdPackage(qualifiedLanguageName);
    String cdName = getCdName(qualifiedLanguageName);
    return getQualifiedSymTabCreatorType(packageName, cdName);
  }

  // TODO refactor
  public String getQualifiedVisitorType(CDSymbol cd) {
    return VisitorGeneratorHelper.getQualifiedVisitorType(cd.getFullName());
  }
  
  public String getQualifiedScopeVisitorType(CDSymbol cd) {
    return VisitorGeneratorHelper.getQualifiedScopeVisitorType(cd.getFullName());
  }
  
  public String getQualifiedScopeVisitorType(String symbol) {
    Optional<CDSymbol> cdSymbol = this.cdSymbol.getEnclosingScope().resolve(symbol, CDSymbol.KIND);
    if (cdSymbol.isPresent()) {
      return getQualifiedScopeVisitorType(cdSymbol.get());
    }
    return "";
  }

  public boolean spansScope(final MCProdSymbol rule) {
    return rule.isScopeDefinition();
  }

  public boolean isSymbol(final MCProdSymbol rule) {
    return rule.isSymbolDefinition();
  }

  public boolean isScopeSpanningSymbol(final MCProdSymbol rule) {
    return isSymbol(rule) && spansScope(rule);
  }

  public boolean isNamed(final MCProdSymbol rule) {
    for (MCProdComponentSymbol comp : rule.getProdComponents()) {
      // TODO check full name?
      if ((comp.getName().equals(NAME_NONTERMINAL) &&
          isNullOrEmpty(comp.getUsageName())
          || comp.getUsageName().equalsIgnoreCase(NAME_NONTERMINAL))) {
        return true;
      }
    }
    return false;
  }

  public boolean isOptionalNamed(final MCProdSymbol rule) {
    for (MCProdComponentSymbol comp : rule.getProdComponents()) {
      // TODO check full name?
      if (comp.getName().equals(NAME_NONTERMINAL) &&
          (isNullOrEmpty(comp.getUsageName())
              || comp.getUsageName().equalsIgnoreCase(NAME_NONTERMINAL)) && comp.isOptional()) {
        return true;
      }
    }
    return false;
  }

  public boolean existsHandwrittenSymbolClass(MCProdSymbol ruleSymbol, IterablePath handCodedPath) {
    return existsHandwrittenClass(Names.getSimpleName(ruleSymbol.getName() + "Symbol"),
        getTargetPackage(), handCodedPath);
  }

  public String getQualifiedASTName(String name) {
    Optional<MCProdSymbol> prod = grammarSymbol.getProdWithInherited(name);
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

  public String getQualifiedProdName(MCProdSymbol prod) {
    String prodName = prod.getFullName();
    prodName = SymbolTableGeneratorHelper
        .getQualifiedSymbolType(getQualifier(prodName)
            .toLowerCase(), Names.getSimpleName(prodName));
    return prodName;
  }

  public String getQualifiedASTName(MCProdSymbol prod) {
    String prodName = prod.getFullName();
    prodName = SymbolTableGeneratorHelper
        .getQualifiedASTType(getQualifier(prodName)
            .toLowerCase(), "AST"+ Names.getSimpleName(prodName));
    return prodName;
  }

  public String getQualifiedScopeInterfaceType(CDSymbol cdSymbol) {
    String packageName = getCdPackage(cdSymbol.getFullName());
    String cdName = getCdName(cdSymbol.getFullName());
    return getQualifiedScopeInterfaceType(packageName, cdName);
  }

  public String getQualifiedScopeInterfaceType(String symbol) {
    Optional<CDSymbol> cdSymbol = this.cdSymbol.getEnclosingScope().resolve(symbol, CDSymbol.KIND);
    if (cdSymbol.isPresent()) {
      return getQualifiedScopeInterfaceType(cdSymbol.get());
    }
    return "";
  }

  public String getQualifiedScopeInterfaceType(String packageName, String cdName) {
    return getPackageName(packageName, SymbolTableGenerator.PACKAGE) + ".I"
        + cdName + SCOPE;
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
    for (CDSymbol superCd : getAllSuperCds(cdSymbol)) {
      // resolve super grammar and retrieve qualified symbols
      MCGrammarSymbol grammarSymbol = cdSymbol.getEnclosingScope().<MCGrammarSymbol> resolve(superCd.getFullName(), MCGrammarSymbol.KIND).orElse(null);
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
      for (MCProdSymbol symbol : SymbolTableGeneratorHelper.getAllSymbolDefiningRules(grammarSymbol)) {
        qualifiedSymbols.add(GeneratorHelper.getPackageName(packageName, symbol.getName() + GeneratorHelper.SYMBOL));
      }
    }
    return qualifiedSymbols;
  }

  public boolean hasSymbolDefiningRule(String symbol) {
    Optional<MCGrammarSymbol> grammarSymbol = cdSymbol.getEnclosingScope().resolve(symbol, MCGrammarSymbol.KIND);
    if (grammarSymbol.isPresent()) {
      for (MCProdSymbol prodSymbol : grammarSymbol.get().getProds()) {
        if (prodSymbol.isSymbolDefinition()) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean hasScopeSpanningRule(String symbol) {
    Optional<MCGrammarSymbol> grammarSymbol = cdSymbol.getEnclosingScope().resolve(symbol, MCGrammarSymbol.KIND);
    if (grammarSymbol.isPresent()) {
      for (MCProdSymbol prodSymbol : grammarSymbol.get().getProds()) {
        if (prodSymbol.isScopeDefinition()) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isComponentGrammar(String grammarName) {
    Optional<MCGrammarSymbol> grammarSymbol = cdSymbol.getEnclosingScope().resolve(grammarName, MCGrammarSymbol.KIND);
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
  public static Collection<MCProdSymbol> getAllSymbolDefiningRules(MCGrammarSymbol grammarSymbol) {
    final Set<MCProdSymbol> ruleSymbolsWithName = new LinkedHashSet<>();
    
    for (final MCProdSymbol rule : grammarSymbol.getProds()) {
      if (rule.isSymbolDefinition() && rule.getName().equals(rule.getSymbolDefinitionKind().get())) {
        ruleSymbolsWithName.add(rule);
      }
    }

    return ImmutableList.copyOf(ruleSymbolsWithName);
  }

}
