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
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCRuleComponentSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.languages.grammar.MCTypeSymbol;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.references.CommonSymbolReference;
import de.monticore.symboltable.references.SymbolReference;

public class MCRuleSymbolReference extends MCRuleSymbol implements SymbolReference<MCRuleSymbol> {

  private final SymbolReference<MCRuleSymbol> symbolReference;

  public MCRuleSymbolReference(String name, Scope definingScopeOfReference) {
    super(name);

    this.symbolReference = new CommonSymbolReference<>(name, MCRuleSymbol.KIND, definingScopeOfReference);
  }


  @Override
  public MCRuleSymbol getReferencedSymbol() {
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

  @Override public KindSymbolRule getKindSymbolRule() {
    return getReferencedSymbol().getKindSymbolRule();
  }

  @Override public MCTypeSymbol getType() {
    return super.getType();
  }

  @Override public void setType(MCTypeSymbol type) {
    getReferencedSymbol().setType(type);
  }

  @Override public MCTypeSymbol getDefinedType() {
    return getReferencedSymbol().getDefinedType();
  }

  @Override public void setDefinedType(MCTypeSymbol definedType) {
    getReferencedSymbol().setDefinedType(definedType);
  }

  @Override public Collection<MCRuleComponentSymbol> getRuleComponents() {
    return getReferencedSymbol().getRuleComponents();
  }

  @Override public Optional<MCRuleComponentSymbol> getRuleComponent(String componentName) {
    return getReferencedSymbol().getRuleComponent(componentName);
  }

  @Override public void addRuleComponent(MCRuleComponentSymbol ruleComponent) {
    getReferencedSymbol().addRuleComponent(ruleComponent);
  }

  @Override public void setGrammarSymbol(MCGrammarSymbol grammarSymbol) {
    getReferencedSymbol().setGrammarSymbol(grammarSymbol);
  }

  @Override public MCGrammarSymbol getGrammarSymbol() {
    return getReferencedSymbol().getGrammarSymbol();
  }

  @Override public int getNoParam() {
    return getReferencedSymbol().getNoParam();
  }

  @Override public String toString() {
    return super.toString();
  }

  @Override public String getTmpVarName(ASTNode a) {
    return getReferencedSymbol().getTmpVarName(a);
  }

  @Override public boolean isOverridestandard() {
    return getReferencedSymbol().isOverridestandard();
  }

  @Override public ASTAlt getFollow() {
    return getReferencedSymbol().getFollow();
  }

  @Override public void setFollow(ASTAlt follow) {
    getReferencedSymbol().setFollow(follow);
  }

  @Override public boolean isStartRule() {
    return getReferencedSymbol().isStartRule();
  }

  @Override public void setStartRule(boolean isStartRule) {
    getReferencedSymbol().setStartRule(isStartRule);
  }

  @Override public boolean isSymbolDefinition() {
    return getReferencedSymbol().isSymbolDefinition();
  }

  @Override public Optional<String> getSymbolDefinitionKind() {
    return getReferencedSymbol().getSymbolDefinitionKind();
  }

  @Override public Optional<MCRuleSymbol> getRuleDefiningSymbolKind() {
    return getReferencedSymbol().getRuleDefiningSymbolKind();
  }

  @Override public void setRuleDefiningSymbolKind(MCRuleSymbol ruleDefiningSymbolKind) {
    getReferencedSymbol().setRuleDefiningSymbolKind(ruleDefiningSymbolKind);
  }

  @Override public String getDefinedType(String suffix) {
    return getReferencedSymbol().getDefinedType(suffix);
  }

  @Override protected MutableScope createSpannedScope() {
    return super.createSpannedScope();
  }

  @Override public Scope getSpannedScope() {
    return getReferencedSymbol().getSpannedScope();
  }

  @Override public void setEnclosingScope(MutableScope scope) {
    super.setEnclosingScope(scope);
  }

  @Override public void setPackageName(String packageName) {
    getReferencedSymbol().setPackageName(packageName);
  }

  @Override public String getPackageName() {
    return getReferencedSymbol().getPackageName();
  }

  @Override public String getFullName() {
    return getReferencedSymbol().getFullName();
  }

  @Override public SymbolKind getKind() {
    if (isReferencedSymbolLoaded()) {
      return getReferencedSymbol().getKind();
    }
    else {
      return super.getKind();
    }
  }

  @Override protected void setKind(SymbolKind kind) {
    super.setKind(kind);
  }

  @Override public void setAstNode(ASTNode node) {
    super.setAstNode(node);
  }

  @Override public Optional<ASTNode> getAstNode() {
    return super.getAstNode();
  }

  @Override public Scope getEnclosingScope() {
    return super.getEnclosingScope();
  }

  @Override public AccessModifier getAccessModifier() {
    return getReferencedSymbol().getAccessModifier();
  }

  @Override public void setAccessModifier(AccessModifier accessModifier) {
    getReferencedSymbol().setAccessModifier(accessModifier);
  }
}
