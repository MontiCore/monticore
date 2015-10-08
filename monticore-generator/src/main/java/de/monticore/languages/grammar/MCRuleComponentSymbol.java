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

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.Optional;

import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.SymbolKind;

/**
 * Symbol for components of a grammar rule. For example, in <code>Foo = Bar "blub"</code> the
 * non-terminal <code>Bar</code> and the terminal <code>blub</code> are both rule componets.
 *
 */
public class MCRuleComponentSymbol extends CommonSymbol {
  
  public static final RuleComponentKind KIND = new RuleComponentKind();
  
  private MCGrammarSymbol grammarEntry;
  
  /**
   * E.g. usageName:QualifiedName
   */
  private String usageName = "";

  /**
   * Only for nonterminals. E.g., in u:R R is the name of the referenced rule.
   */
  private String referencedRuleName = "";

  /**
   * E.g., in from:Name@State State is the referenced symbol name
   */
  private Optional<String> referencedSymbolName = Optional.empty();

  /**
   * e.g., A* or A+
   */
  private boolean isList = false;

  /**
   * e.g., A?
   */
  private boolean isOptional = false;
  
  /**
   * Parent rule
   */
  protected MCRuleSymbol enclosingRule;
  
  /**
   * Kind of rule component
   */
  protected KindRuleComponent kindOfRuleComponent;
  
  protected MCRuleComponentSymbol(String name) {
    super(name, KIND);
  }
  
  /**
   * Returns the type this rule defines
   * 
   * @return type this rule defines
   */
  public MCRuleSymbol getEnclosingRule() {
    return enclosingRule;
  }
  
  public void setEnclosingRule(MCRuleSymbol enclosingRule) {
    this.enclosingRule = enclosingRule;
  }

  /**
   * @return true, if rule is used as a list, i.e. '+' or '*'.
   */
  public boolean isList() {
    return isList;
  }
  
  /**
   * @param isList true, if rule is used as a list, i.e. '+' or '*'.
   */
  public void setList(boolean isList) {
    this.isList = isList;
  }
  
  /**
   * @return true, if rule is optional, i.e. '?'.
   */
  public boolean isOptional() {
    return isOptional;
  }
  
  /**
   * @param isOptional true, if rule is optional, i.e. '?'.
   */
  public void setOptional(boolean isOptional) {
    this.isOptional = isOptional;
  }
  
  /**
   * E.g. usageName:QualifiedName
   * 
   * @param usageName the usageName to set
   */
  public void setUsageName(String usageName) {
    this.usageName = nullToEmpty(usageName);
  }
  
  /**
   * @return usageName
   */
  public String getUsageName() {
    return this.usageName;
  }

  public void setReferencedRuleName(String referencedRuleName) {
    this.referencedRuleName = nullToEmpty(referencedRuleName);
  }

  public String getReferencedRuleName() {
    return referencedRuleName;
  }

  public Optional<String> getReferencedSymbolName() {
    return referencedSymbolName;
  }

  public void setReferencedSymbolName(String referencedSymbolName) {
    this.referencedSymbolName = Optional.ofNullable(emptyToNull(referencedSymbolName));
  }

  public boolean isSymbolReference() {
    return referencedSymbolName.isPresent();
  }

  public void setGrammarSymbol(MCGrammarSymbol grammarEntry) {
    this.grammarEntry = grammarEntry;
  }
  
  public MCGrammarSymbol getGrammarSymbol() {
    return grammarEntry;
  }
  
  public void setKindOfRuleComponent(KindRuleComponent kindOfRuleComponent) {
    this.kindOfRuleComponent = kindOfRuleComponent;
  }
  
  public KindRuleComponent getKindOfRuleComponent() {
    return kindOfRuleComponent;
  }
  
  public enum KindRuleComponent {
    TERMINAL, NONTERMINAL, CONSTRUCTOR, CONSTANTGROUP, LEXNONTERMINAL, CONSTANT
  }
  
  public static final class RuleComponentKind implements SymbolKind {
    
    private RuleComponentKind() {
    }
  }
  
}
