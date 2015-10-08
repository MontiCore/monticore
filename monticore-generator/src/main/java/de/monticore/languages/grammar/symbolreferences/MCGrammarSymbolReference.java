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

package de.monticore.languages.grammar.symbolreferences;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConcept;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTGenericType;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTRuleComponent;
import de.monticore.languages.grammar.MCClassRuleSymbol;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.languages.grammar.MCTypeSymbol;
import de.monticore.languages.grammar.PredicatePair;
import de.monticore.languages.grammar.attributeinfos.MCAttributeInfo;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.references.CommonSymbolReference;
import de.monticore.symboltable.references.SymbolReference;
import de.se_rwth.commons.SourcePosition;

/**
 * Reference for {@link MCGrammarSymbol}.
 *
 * @author Pedram Mir Seyed Nazari
 */
public class MCGrammarSymbolReference extends MCGrammarSymbol
    implements SymbolReference<MCGrammarSymbol> {

  private final SymbolReference<MCGrammarSymbol> symbolReference;

  public MCGrammarSymbolReference(String name, Scope definingScopeOfReference) {
    super(name);

    this.symbolReference = new CommonSymbolReference<>(name, MCGrammarSymbol.KIND,
        definingScopeOfReference);
  }

  @Override
  public MCGrammarSymbol getReferencedSymbol() {
    return symbolReference.getReferencedSymbol();
  }

  @Override
  public boolean existsReferencedSymbol() {
    return symbolReference.existsReferencedSymbol();
  }

  @Override
  public boolean isReferencedSymbolLoaded() {
    return symbolReference.isReferencedSymbolLoaded();
  }

  @Override
  public String getFullName() {
    return getReferencedSymbol().getFullName();
  }

  @Override
  public String getPackageName() {
    return getReferencedSymbol().getPackageName();
  }

  @Override
  public boolean isComponent() {
    return getReferencedSymbol().isComponent();
  }

  @Override
  public Collection<MCRuleSymbol> getRules() {
    return getReferencedSymbol().getRules();
  }

  @Override
  public MCRuleSymbol getRule(String ruleName) {
    return getReferencedSymbol().getRule(ruleName);
  }

  @Override
  public Collection<String> getRuleNames() {
    return getReferencedSymbol().getRuleNames();
  }

  @Override
  public MCRuleSymbol getRuleWithInherited(String ruleName) {
    return getReferencedSymbol().getRuleWithInherited(ruleName);
  }

  @Override
  public Map<String, MCRuleSymbol> getRulesWithInherited() {
    return getReferencedSymbol().getRulesWithInherited();
  }

  @Override
  public MCClassRuleSymbol getRuleWithInherited(ASTClassProd a) {
    return getReferencedSymbol().getRuleWithInherited(a);
  }

  @Override
  public List<Symbol> getChildren() {
    return getReferencedSymbol().getChildren();
  }

  @Override
  public boolean equals(Object obj) {
    return getReferencedSymbol().equals(obj);
  }

  @Override
  public int hashCode() {
    return getReferencedSymbol().hashCode();
  }

  @Override
  public List<ASTConcept> getConcept(String name) {
    return getReferencedSymbol().getConcept(name);
  }

  @Override
  public AccessModifier getAccessModifier() {
    return getReferencedSymbol().getAccessModifier();
  }

  @Override
  public Scope getEnclosingScope() {
    return getReferencedSymbol().getEnclosingScope();
  }

  @Override
  public void setEnclosingScope(MutableScope scope) {
    getReferencedSymbol().setEnclosingScope(scope);
  }

  @Override
  public Optional<ASTNode> getAstNode() {
    return getReferencedSymbol().getAstNode();
  }

  @Override
  public void setAstNode(ASTNode node) {
    getReferencedSymbol().setAstNode(node);
  }


  @Override
  public SymbolKind getKind() {
    return getReferencedSymbol().getKind();
  }

  @Override
  public void setSuperGrammars(List<MCGrammarSymbol> superGrammars) {
    getReferencedSymbol().setSuperGrammars(superGrammars);
  }

  @Override
  public List<MCGrammarSymbol> getSuperGrammars() {
    return getReferencedSymbol().getSuperGrammars();
  }

  @Override
  public void addSuperGrammar(MCGrammarSymbol superGrammar) {
    getReferencedSymbol().addSuperGrammar(superGrammar);
  }

  @Override
  public Set<MCGrammarSymbol> getAllSuperGrammars() {
    return getReferencedSymbol().getAllSuperGrammars();
  }

  @Override
  public void setComponent(boolean isComponent) {
    getReferencedSymbol().setComponent(isComponent);
  }

  @Override
  public ASTMCGrammar getASTGrammar() {
    return getReferencedSymbol().getASTGrammar();
  }

  @Override
  public String getSimpleName() {
    return getReferencedSymbol().getSimpleName();
  }

  @Override
  public void addType(MCTypeSymbol type) {
    getReferencedSymbol().addType(type);
  }

  @Override
  public Collection<MCTypeSymbol> getTypes() {
    return getReferencedSymbol().getTypes();
  }

  @Override
  public Collection<String> getTypeNames() {
    return getReferencedSymbol().getTypeNames();
  }

  @Override
  public MCTypeSymbol getType(String typeName) {
    return getReferencedSymbol().getType(typeName);
  }

  @Override
  public MCTypeSymbol getTypeWithInherited(String name) {
    return getReferencedSymbol().getTypeWithInherited(name);
  }

  @Override
  public Set<MCTypeSymbol> getAllMCTypesWithGivenName(String name) {
    return getReferencedSymbol().getAllMCTypesWithGivenName(name);
  }

  @Override
  public Map<String, MCTypeSymbol> getTypesWithInherited() {
    return getReferencedSymbol().getTypesWithInherited();
  }

  @Override
  public void addRule(MCRuleSymbol ruleSymbol) {
    getReferencedSymbol().addRule(ruleSymbol);
  }

  @Override
  public Optional<ASTRuleComponent> getPredicateInInterface(String ruleName, String a) {
    return getReferencedSymbol().getPredicateInInterface(ruleName, a);
  }

  @Override
  public List<PredicatePair> getSubRulesForParsing(String ruleName) {
    return getReferencedSymbol().getSubRulesForParsing(ruleName);
  }

  @Override
  public void addPredicate(String rule, PredicatePair predicate) {
    getReferencedSymbol().addPredicate(rule, predicate);
  }

  @Override
  public String getLexSymbolName(String x) {
    return getReferencedSymbol().getLexSymbolName(x);
  }

  @Override
  public Set<String> getLexSymbolsWithInherited() {
    return getReferencedSymbol().getLexSymbolsWithInherited();
  }

  @Override
  public String getConstantNameForConstant(ASTConstant x) {
    return getReferencedSymbol().getConstantNameForConstant(x);
  }

  @Override
  public String getClassNameFromRuleOrInterfaceName(String a) {
    return getReferencedSymbol().getClassNameFromRuleOrInterfaceName(a);
  }

  @Override
  public String getDefinedClassNameFromRuleOrInterfaceName(String a) {
    return getReferencedSymbol().getDefinedClassNameFromRuleOrInterfaceName(a);
  }

  @Override
  public String getClassNameFromRuleNameOrInterfaceName(String a, String suffix) {
    return getReferencedSymbol().getClassNameFromRuleNameOrInterfaceName(a, suffix);
  }

  @Override
  public String getDefinedClassNameFromRuleNameOrInterfaceName(String a, String suffix) {
    return getReferencedSymbol().getDefinedClassNameFromRuleNameOrInterfaceName(a, suffix);
  }

  @Override
  public boolean canParserBeGenerated() {
    return getReferencedSymbol().canParserBeGenerated();
  }

  @Override
  public String getConstantClassSimpleName() {
    return getReferencedSymbol().getConstantClassSimpleName();
  }

  @Override
  public String getConstantClassName() {
    return getReferencedSymbol().getConstantClassName();
  }

  @Override
  public String getAssCreatorSimplename() {
    return getReferencedSymbol().getAssCreatorSimplename();
  }

  @Override
  public void createEnum(MCAttributeInfo value, String enumName) {
    getReferencedSymbol().createEnum(value, enumName);
  }

  @Override
  public MCTypeSymbol createType(ASTGenericType a, Scope definingScopeOfReference) {
    return getReferencedSymbol().createType(a, definingScopeOfReference);
  }

  @Override
  public boolean checkIfIsRule(String ruleName, SourcePosition x) {
    return getReferencedSymbol().checkIfIsRule(ruleName, x);
  }

  @Override
  public boolean checkIfIsAttribute(String rulename, String attributename, SourcePosition
      position) {
    return getReferencedSymbol().checkIfIsAttribute(rulename, attributename, position);
  }

  @Override
  public Optional<MCRuleSymbol> getStartRule() {
    return getReferencedSymbol().getStartRule();
  }

  @Override
  public void setStartRule(MCRuleSymbol startRule) {
    getReferencedSymbol().setStartRule(startRule);
  }

  @Override
  public String toString() {
    return getReferencedSymbol().toString();
  }

}
