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

package de.monticore.languages.grammar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.LexNamer;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConcept;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTGenericType;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTRuleComponent;
import de.monticore.languages.grammar.attributeinfos.MCAttributeInfo;
import de.monticore.languages.grammar.symbolreferences.MCExternalTypeSymbolReference;
import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.references.SymbolReference;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;


/**
 * Symboltable entry for a MontiCore grammar. This symboltable manages two
 * different types of entries: Types and Rules represented by the classes
 * MCTypeEntry and MCProdEntry (and their subclasses). A MCProdEntry represents
 * a rule in the grammar whereas an MCTypeEntry represents a Java type (class
 * and primitives) that are defined or used inside the grammar. Rules usually
 * define and return types. Rules are referred by their unqualified name in this
 * grammar. Note that there is only one namespace for type of rules like lexer
 * and parser rules. Types are identified by their unqualified name in this
 * symboltable if they are defined by a rule or the grammar. Use "super." as a
 * prefix to explicitly access the type of supergrammars this grammar
 * overrides. Native/external types are types that are not defined in the
 * grammar but are referred from it. These types are indicated by the suffix
 * "/" in the grammar and refer to regular Java types. To access these type use
 * the prefix "/" e.g. "/String" or "/int". All names of types and rules are
 * case sensitive although a syntactic check is applied during symboltable
 * building that checks if a user uses ambiguous names like "NAME" and "Name"
 * in the same grammar. <br>
 *
 * @author Krahn, Volkova, Mir Seyed Nazari<br>
 */
public class MCGrammarSymbol extends CommonScopeSpanningSymbol {

  public static final GrammarKind KIND = new GrammarKind();

  /**
   * Direct super grammars of this grammar
   */
  private List<MCGrammarSymbol> superGrammars = new ArrayList<>();

  /**
   * Internal: LexNamer for naming lexer symbols in the antlr source code
   */
  private LexNamer lexNamer = new LexNamer();
  
  /**
   * Predicates
   */
  private ArrayListMultimap<String, PredicatePair> predicats = ArrayListMultimap.create();

  /**
   * Is the grammar abstract?
   */
  private boolean isComponent = false;

  // the start rule of the grammar
  private MCRuleSymbol startRule;

  // -------------------- CONSTRUCTORS -----------------------------------------------------

  protected MCGrammarSymbol() {
    this("");
  }

  protected MCGrammarSymbol(String name) {
    super(name, KIND);
  }

  public List<Symbol> getChildren() {
    List<Symbol> children = new ArrayList<>();
    children.addAll(getTypes());
    children.addAll(getRules());
    return children;
  }

  // ------------- Handling of concepts -----------------------------

  /**
   * Returns all concepts with name name in this grammar
   *
   * @param name Name of the concept
   */
  public List<ASTConcept> getConcept(String name) {
    List<ASTConcept> ret = Lists.newArrayList();
    // TODO: deser.
    for (ASTConcept c : getASTGrammar().getConcepts()) {
      if (c.getName().equals(name)) {
        ret.add(c);
      }
    }
    return ret;
  }

  // ------------- Handling of super grammars -----------------------

  public void setSuperGrammars(List<MCGrammarSymbol> superGrammars) {
    this.superGrammars = new ArrayList<>(superGrammars);
  }

  public List<MCGrammarSymbol> getSuperGrammars() {
    return Collections.unmodifiableList(superGrammars);
  }

  public void addSuperGrammar(MCGrammarSymbol superGrammar) {
    superGrammars.add(superGrammar);
  }

  /**
   * Returns a set of all super grammars (transitively)
   *
   * @return
   */
  public Set<MCGrammarSymbol> getAllSuperGrammars() {
    Set<MCGrammarSymbol> allSuperGrammars = new LinkedHashSet<>();
    Set<MCGrammarSymbol> tmpList = new LinkedHashSet<>();
    allSuperGrammars.addAll(this.getSuperGrammars());
    
    boolean modified = false;
    do {
      for (MCGrammarSymbol curGrammar: allSuperGrammars) {
        tmpList.addAll(curGrammar.getSuperGrammars());
      }
      modified = allSuperGrammars.addAll(tmpList);
      tmpList.clear();
    } while (modified);
    
    return ImmutableSet.copyOf(allSuperGrammars);
  }

  //------------- Handling of options -----------------------------

  /**
   * @return true, if the grammar is abstract
   */
  public boolean isComponent() {
    return isComponent;
  }

  public void setComponent(boolean isComponent) {
    this.isComponent = isComponent;
  }

  /**
   * Getter for top AST node
   */
  public ASTMCGrammar getASTGrammar() {
    return (ASTMCGrammar)getAstNode().get();
  }

  //------------- Handling of types -----------------------------

  public void addType(MCTypeSymbol type) {
    if (type instanceof SymbolReference) {
      Log.debug("Type '" + type.getName() + "' is a reference, and thus, not stored in the "
          + "grammar symbol.", MCGrammarSymbol.class.getSimpleName());
      return;
    }

    if (getType(type.getName()) != null) {
      Log.debug("Type '" + type.getName() + "' already exists. The kind is '" + type
          .getKindOfType() + "'", MCGrammarSymbol.class.getSimpleName());
      return;
    }

    type.setGrammarSymbol(this);
    this.getMutableSpannedScope().add(type);
    // Since we do not create the type in the usual symbol tabel creation mechanism, the resolving
    // filters have to be added manually
    ((MutableScope)type.getSpannedScope()).setResolvingFilters(getSpannedScope().getResolvingFilters());
  }

  public Collection<MCTypeSymbol> getTypes() {
    return this.getSpannedScope().resolveLocally(MCTypeSymbol.KIND);
  }

  public Collection<String> getTypeNames() {
    final Set<String> typeNames = new LinkedHashSet<>();

    for (final MCTypeSymbol type : getTypes()) {
      typeNames.add(type.getName());
    }

    return ImmutableSet.copyOf(typeNames);
  }

  // TODO PN return Optional value
  public MCTypeSymbol getType(String typeName) {
    return this.getSpannedScope().<MCTypeSymbol>resolveLocally(typeName, MCTypeSymbol.KIND).orElse(null);
  }

  /**
   * Returns the STType associated with this name Use "super." as a prefix to
   * explicitly access the type of supergrammars this grammar overriddes.
   * Native/external types are types that are not defined in the grammar but are
   * refered from it. These types are indicated by the suffix "/" in the grammar
   * and refer to regular Java types. To access these type use the prefix "/"
   * e.g. "/String" or "/int"
   *
   * @param name Name of the type
   * @return Symboltable entry for this type
   */
  public MCTypeSymbol getTypeWithInherited(String name) {
    MCTypeSymbol ret = null;

    if (name.startsWith("super.")) {
      name = name.substring(6);
    }
    else {
      ret = getType(name);
    }

    final Iterator<MCGrammarSymbol> itSuperGramms = superGrammars.iterator();
    while (ret == null && itSuperGramms.hasNext()) {
      ret = itSuperGramms.next().getTypeWithInherited(name);
    }

    return ret;
  }

  public Set<MCTypeSymbol> getAllMCTypesWithGivenName(String name) {
    final Set<MCTypeSymbol> ret = Sets.newLinkedHashSet();
    MCTypeSymbol type;

    if (name.startsWith("super.")) {
      name = name.substring(6);
    }
    else {
      type = getType(name);
      if (type != null) {
        ret.add(type);
      }
    }

    for (MCGrammarSymbol superGrammars : this.superGrammars) {
      type = superGrammars.getTypeWithInherited(name);
      if (type != null) {
        ret.add(type);
      }
    }

    return ret;
  }

  public Map<String, MCTypeSymbol> getTypesWithInherited() {
    final Map<String, MCTypeSymbol> all = new HashMap<>();
    final List<MCGrammarSymbol> superGrammarsReversed = new ArrayList<>(superGrammars);
    Collections.reverse(superGrammarsReversed);

    for (MCGrammarSymbol s : superGrammarsReversed) {
      all.putAll(s.getTypesWithInherited());
    }

    for (final MCTypeSymbol type : getTypes()) {
      all.put(type.getName(), type);
    }

    return all;
  }


  //------------- Handling of rules -----------------------------

  public void addRule(MCRuleSymbol ruleSymbol) {
    if (getRule(ruleSymbol.getName()) != null) {
      Log.debug("Rule '" + ruleSymbol.getName() + "' already defined in grammar symbol '" +
          getFullName() + "'", MCGrammarSymbol.class.getSimpleName());
    }

    this.getMutableSpannedScope().add(ruleSymbol);
  }

  public Collection<MCRuleSymbol> getRules() {
    return this.getSpannedScope().resolveLocally(MCRuleSymbol.KIND);
  }

  public Collection<String> getRuleNames() {
    final Set<String> ruleNames = new LinkedHashSet<>();

    for (final MCRuleSymbol ruleSymbol : getRules()) {
      ruleNames.add(ruleSymbol.getName());
    }

    return ImmutableSet.copyOf(ruleNames);
  }

  // TODO PN return Optional value
  public MCRuleSymbol getRule(String ruleName) {
    return this.getSpannedScope().<MCRuleSymbol>resolveLocally(ruleName, MCRuleSymbol.KIND).orElse(null);
  }

  /**
   * Get MCProd with a given name
   *
   * @param ruleName
   * @return
   */
  public MCRuleSymbol getRuleWithInherited(String ruleName) {
    MCRuleSymbol mcProd = getRule(ruleName);
    Iterator<MCGrammarSymbol> itSuperGrammars = superGrammars.iterator();
    while (mcProd == null && itSuperGrammars.hasNext()) {
      mcProd = itSuperGrammars.next().getRuleWithInherited(ruleName);
    }

    return mcProd;
  }

  public MCClassRuleSymbol getRuleWithInherited(ASTClassProd a) {
    return (MCClassRuleSymbol) getRuleWithInherited(HelperGrammar.getRuleName(a));
  }

  public Map<String, MCRuleSymbol> getRulesWithInherited() {
    final Map<String, MCRuleSymbol> ret = new LinkedHashMap<>();

    for (int i = superGrammars.size() - 1; i >= 0; i--) {
      ret.putAll(superGrammars.get(i).getRulesWithInherited());
    }

    for (final MCRuleSymbol ruleSymbol : getRules()) {
      ret.put(ruleSymbol.getName(), ruleSymbol);
    }

    return ret;
  }

  //------------- Handling of predicates -----------------------------

  public Optional<ASTRuleComponent> getPredicateInInterface(String ruleName, String a) {
    for (PredicatePair p : predicats.get(ruleName)) {
      if (p.getClassname().equals(a)) {
        return p.getComponent();
      }
    }
    return Optional.empty();
  }

  public List<PredicatePair> getSubRulesForParsing(String ruleName) {
    // Consider superclass
    MCRuleSymbol ruleByName = getRuleWithInherited(ruleName);

    if (ruleByName == null) {
      return null;
    }
    else {
      List<PredicatePair> predicateList = predicats.get(ruleName);

      if (predicateList == null) {
        predicateList = Lists.newArrayList();
      }

      for (MCGrammarSymbol superGrammar : superGrammars) {

        List<PredicatePair> subRulesForParsing = superGrammar.getSubRulesForParsing(ruleName);
        if (subRulesForParsing != null) {
          for (PredicatePair predicate : subRulesForParsing) {

            if (!predicateList.contains(predicate)) {
              predicateList.add(predicate);
            }
          }
        }
      }

      if (predicateList.isEmpty()) {
        return null;
      }
      else {
        return predicateList;
      }
    }
  }

  public void addPredicate(String rule, PredicatePair predicate) {
    predicats.put(rule, predicate);
  }

  //------------- Getter and setter -----------------------------

  /**
   * @return the name for a lexsymbol that should be used in an Antlr-File
   *
   */
  public String getLexSymbolName(String x) {
    Log.errorIfNull(x);
    
    return lexNamer.getLexName(getRuleNames(), x);
  }

  /**
   * Get all used LexSymbols, different form information in AST, as inherited
   * ones are integrated as well
   *
   * @return Keys of all lex symbols
   */
  public Set<String> getLexSymbolsWithInherited() {
    return lexNamer.getLexnames();
  }

  public String getConstantNameForConstant(ASTConstant x) {
    String name;
    if (x.getHumanName().isPresent()) {
      name = x.getHumanName().get();
    }
    else {
      name = lexNamer.getConstantName(x.getName());
    }

    return name.toUpperCase();
  }

  /**
   * Transforms a rule name to the AST-class name e.g. testName -> ASTTestName
   * a.b.c -> a.b.ASTC
   *
   * @param a String to be transformed
   * @return
   */
  public String getClassNameFromRuleOrInterfaceName(String a) {
    MCRuleSymbol ruleByName = getRuleWithInherited(a);

    // String classnameFromRulenameorInterfacename =
    // getClassnameFromRulenameorInterfacename(a, MCConstants.AST);
    String classNameFromRuleNameOrInterfaceNamee = "";
    if (ruleByName == null) {
      Log.error("0xA2351 Asking for rule or interface '" + a + "' in grammar '" + getFullName() + "' "
          + "which is non-existing!");
    }
    else {
      classNameFromRuleNameOrInterfaceNamee = ruleByName.getType().getQualifiedName();
    }
    return classNameFromRuleNameOrInterfaceNamee;
  }

  /**
   * Transforms a rule name to the AST-class name e.g. testName -> ASTTestName
   * a.b.c -> a.b.ASTC
   *
   * @param a String to be transformed
   * @return
   */
  public String getDefinedClassNameFromRuleOrInterfaceName(String a) {
    String classNameFromRuleNameOrInterfaceName = getDefinedClassNameFromRuleNameOrInterfaceName
        (a, GeneratorHelper.AST_PREFIX);

    if (classNameFromRuleNameOrInterfaceName == null) {
      Log.error("0xA2350 Asking for rule or interface '" + a + "' in grammar '" + getFullName() + "' "
          + "which is non-existing!");
    }
    return classNameFromRuleNameOrInterfaceName;
  }

  /**
   * Transforms a rule name to the AST-class name e.g. testName
   * -><<suffix>>TestName a.b.c -> a.b.<<suffix>>C
   *
   * @param a String to be transformed
   * @param suffix suffix to be used
   * @return
   */
  public String getClassNameFromRuleNameOrInterfaceName(String a, String suffix) {
    MCRuleSymbol rule = getRuleWithInherited(a);
    if (rule != null) {
      return rule.getType().getQualifiedName(suffix, "");
    }
    return null;
  }

  /**
   * Transforms a rule name to the AST-class name e.g. testName
   * -><<suffix>>TestName a.b.c -> a.b.<<suffix>>C that ist defines (differs
   * from getClassnameFromRulenameorInterfacename if returns construct is used)
   *
   * @param a String to be transformed
   * @param suffix suffix to be used
   * @return
   */
  public String getDefinedClassNameFromRuleNameOrInterfaceName(String a, String suffix) {
    MCRuleSymbol rule = getRuleWithInherited(a);
    if (rule != null) {
      return rule.getDefinedType().getQualifiedName(suffix, "");
    }
    return null;
  }

  // TODO PN Move to a class, such as CodeGenInfoForGrammar?
  public boolean canParserBeGenerated() {
    boolean found = false;

    for (MCRuleSymbol r : getRulesWithInherited().values()) {
      if (r.getKindSymbolRule().equals(MCRuleSymbol.KindSymbolRule.PARSERRULE)) {
        found = true;
      }
    }
    return found;
  }

  // ------------ Help methods ---------------------------------------
  /**
   * Returns the simple name of the file for constants
   *
   * @return simple name of the file for constants
   */
  // TODO PN Move to a class, such as CodeGenInfoForGrammar?
  public String getConstantClassSimpleName() {
    return "ASTConstants" + getASTGrammar().getName();
  }

  /**
   * Returns the full qualified name of the file for constants for a certain
   * ASTConstantGroup
   *
   * @return full qualified name of the file for constants
   */
  // TODO PN Move to a class, such as CodeGenInfoForGrammar?
  public String getConstantClassName() {
    return getFullName().toLowerCase() +
        GeneratorHelper.AST_PACKAGE_SUFFIX_DOT + "."
        + getConstantClassSimpleName();

  }

  /**
   * Returns the simple name of the file for constants
   *
   * @return simple name of the file for constants
   */
  // TODO PN Move to a class, such as CodeGenInfoForGrammar?
  public String getAssCreatorSimplename() {
    return StringTransformations.capitalize(getASTGrammar().getName()) + "AssociationCreator";
  }

  // TODO PN why does a symbol has a create method??
  public void createEnum(MCAttributeInfo value, String enumName) {
    // Create private enum
    MCTypeSymbol typeEntry = MCGrammarSymbolsFactory.createMCTypeSymbol(enumName, this, null);
        //new MCTypeSymbolReference(enumName, this, getSpannedScope());
    typeEntry.setKindOfType(MCTypeSymbol.KindType.CONST);
    for (String constantValue : value.getConstantValues()) {
      typeEntry.addEnum(lexNamer.getConstantName(constantValue), constantValue);
    }
    addType(typeEntry);
  }

  // TODO PN why does a symbol has a create method??
  public MCTypeSymbol createType(ASTGenericType a, Scope definingScopeOfReference) {
    MCTypeSymbol typeByName = getTypeWithInherited(a.getTypeName());
    if (typeByName == null) {

      if (a.isExternal()) {
        typeByName = new MCExternalTypeSymbolReference(a.getTypeName(), this, definingScopeOfReference);
      }
    }

    return typeByName;
  }

  /**
   * Checks if ruleName is a valid rule in this grammar. If not an error message
   * is triggered using source position x
   *
   * @param ruleName Rulename of rule to check
   * @param x SourcePosition for error message (may be null)
   * @return true iff it is a valid rule
   */
  public boolean checkIfIsRule(String ruleName, SourcePosition x) {
    MCRuleSymbol ruleByName = getRuleWithInherited(ruleName);

    if (ruleByName == null) {
      Log.error("0xA0247 " + ruleName + " does not exist.", x);
      return false;
    }
    return true;
  }

  /**
   * Checks if rulename is a valid rule in this grammar and if attrributename is
   * a valid attribute of that rule. If not an error message is triggered using
   * source position x
   *
   * @param rulename Rulename of rule to check
   * @param position SourcePosition for error message (may be null)
   * @return true iff it is a valid rule
   */
  public boolean checkIfIsAttribute(String rulename, String attributename, SourcePosition position) {
    if (checkIfIsRule(rulename, position)) {
      MCRuleSymbol ruleByName = getRuleWithInherited(rulename);

      MCAttributeSymbol attribute = ruleByName.getDefinedType().getAttribute(attributename);
      if (attribute == null) {
        Log.error("0xA0248 " + attributename + " is a non-existing attribute of rule " + rulename, position);
        return false;
      }
      return true;
    }
    // TODO GV: changed in compared to head. Check!
    return false;
  }

  public String getSimpleName() {
    return getName();
  }

  public void setStartRule(MCRuleSymbol startRule) {
    this.startRule = startRule;
  }

  /**
   * The start rule typically is the first defined rule in the grammar.
   *
   * @return the start rule of the grammar, if not a component grammar
   */
  public Optional<MCRuleSymbol> getStartRule() {
    return Optional.ofNullable(startRule);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof MCGrammarSymbol)) {
      return false;
    }

    MCGrammarSymbol other = (MCGrammarSymbol) obj;
    // TODO PN needed? => Override this method in grammar reference symbol
    //    if (getBestKnownVersion() != this || other.getBestKnownVersion() != other) {
    //      return getBestKnownVersion().equals(other.getBestKnownVersion());
    //    }

    if ((getFullName() == null) && (other.getFullName() != null)) {
      return false;
    }
    else if (!getFullName().equals(other.getFullName())) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getFullName() == null) ? 0 : getFullName().hashCode());
    return result;
  }

  public static final class GrammarKind implements SymbolKind {
    private static final String NAME = GrammarKind.class.getName();

    private GrammarKind() {
    }

    @Override
    public String getName() {
      return NAME;
    }

    @Override
    public boolean isKindOf(SymbolKind kind) {
      return NAME.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
    }
  }
}
