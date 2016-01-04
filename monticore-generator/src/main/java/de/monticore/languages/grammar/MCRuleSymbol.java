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

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author Pedram Mir Seyed Nazari
 * @version $Revision$, $Date$
 */
public abstract class MCRuleSymbol extends CommonScopeSpanningSymbol {
  
  public static final RuleKind KIND = new RuleKind();
  
  private MCGrammarSymbol grammarSymbol;

  // TODO NN <- PN What is the difference between type and definedType? Almost all sub classes
  // seem to delegate from definedType to type. Can one be removed?
  private MCTypeSymbol type;
  private MCTypeSymbol definedType;
  
  private ASTAlt follow = null;
  
  private int tmp_counter = 0;

  private boolean isStartRule = false;

  /**
   * the rule that defines the symbol kind of the current rule symbol (only if isSymbolDefinition is true)
   */
  // TODO PN find better names
  private MCRuleSymbol ruleDefiningSymbolKind = null;

  // TODO NN <- PN what is a tempName?? Template Name?
  private Map<ASTNode, String> tempNameForTerminalOrNonterminalOrMcStatement = new HashMap<>();
  
  public MCRuleSymbol(String name) {
    super(name, KIND);
  }
  
  public MCRuleSymbol() {
    this("");
  }
  
  public abstract KindSymbolRule getKindSymbolRule();
  
  /**
   * Returns the type this rule returns when called from another rule
   * 
   * @return return type of this rule
   */
  // TODO NN <- PN seems to be nullable. Return Optional value.
  public MCTypeSymbol getType() {
    return type;
  }

  /**
   * Sets the typed defined by this rule
   *
   * @param type the typed defined by this rule
   */
  public void setType(MCTypeSymbol type) {
    this.type = type;
  }
  
  /**
   * Returns the type this rule defines
   * 
   * @return type this rule defines
   */
  public MCTypeSymbol getDefinedType() {
    return this.definedType;
  }
  
  public void setDefinedType(MCTypeSymbol definedType) {
    this.definedType = definedType;
  }
  
  public Collection<MCRuleComponentSymbol> getRuleComponents() {
    return spannedScope.resolveLocally(MCRuleComponentSymbol.KIND);
  }

  public Optional<MCRuleComponentSymbol> getRuleComponent(String componentName) {
    return spannedScope.resolveLocally(componentName, MCRuleComponentSymbol.KIND);
  }
  
  public void addRuleComponent(MCRuleComponentSymbol ruleComponent) {
    Log.errorIfNull(ruleComponent);

    MCRuleComponentSymbol r = getRuleComponent(ruleComponent.getName()).orElse(null);

    if (r != null) {
      // TODO NN <- PN handle the case: (a:A a:B), i.e. two different non-terminals have the same usage name
      // a rule component is a list (*), if at list one of the rule componets is a list
      // TODO NN <- PN what about (a:A) | (a:A)? | (a:A)+ is the rule component a:A optional? a list? etc.
      r.setList(r.isList() || ruleComponent.isList());
    }
    else {
      spannedScope.add(ruleComponent);
    }
  }
  
  public void setGrammarSymbol(MCGrammarSymbol grammarSymbol) {
    this.grammarSymbol = grammarSymbol;
  }
  
  public MCGrammarSymbol getGrammarSymbol() {
    return this.grammarSymbol;
  }
  
  /**
   * @return the number of of parameters this rule has
   */
  public int getNoParam() {
    return 0;
  }
  
  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(getKindSymbolRule())
        .append(" ").append(getName())
        .append(" type ");

    if (getType() != null) {
      b.append(getType().getName());
    }
    
    return b.toString();
  }
  
  // ---
  // Add methods to build up data strcuture
  public String getTmpVarName(ASTNode a) {
    if (!tempNameForTerminalOrNonterminalOrMcStatement.containsKey(a)) {
      tempNameForTerminalOrNonterminalOrMcStatement.put(a, getnewtemp());
    }
    return tempNameForTerminalOrNonterminalOrMcStatement.get(a);
  }

  // TODO NN <- PN this method seems to be never called. Remove it?
  public boolean isOverridestandard() {
    return false;
  }

  // TODO NN <- PN write doc
  public ASTAlt getFollow() {
    return follow;
  }

  // TODO NN <- PN write doc
  public void setFollow(ASTAlt follow) {
    this.follow = follow;
  }

  // TODO NN <- PN what is this method for? Please write doc
  private String getnewtemp() {
    return "tmp" + (Integer.valueOf(tmp_counter++)).toString();
  }

  public boolean isStartRule() {
    return isStartRule;
  }

  public void setStartRule(boolean isStartRule) {
    this.isStartRule = isStartRule;
  }

  public boolean isSymbolDefinition() {
    return ruleDefiningSymbolKind != null;
  }

  public Optional<String> getSymbolDefinitionKind() {
    if (isSymbolDefinition()) {
      if (ruleDefiningSymbolKind == this) {
        return of(getName());
      }
      return ruleDefiningSymbolKind.getSymbolDefinitionKind();
    }
    return empty();
  }

  public Optional<MCRuleSymbol> getRuleDefiningSymbolKind() {
    return ofNullable(ruleDefiningSymbolKind);
  }

  public void setRuleDefiningSymbolKind(MCRuleSymbol ruleDefiningSymbolKind) {
    this.ruleDefiningSymbolKind = ruleDefiningSymbolKind;
  }

  /**
   * Returns the type this rule defines not the type used with the return construct
   *
   * TODO NN <- PN  please write doc more clearly, e.g., with an example.
   * 
   * @param suffix
   * @return
   */
  public String getDefinedType(final String suffix) {
    return grammarSymbol.getFullName().toLowerCase() +
        GeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT
        + suffix + StringTransformations.capitalize(getDefinedType().getName());
  }
  
  public enum KindSymbolRule {
    PARSERRULE("PARSERRULE"),
    LEXERRULE("LEXERRULE"),
    INTERFACEORABSTRACTRULE("INTERFACEORABSTRACTRULE"),
    HOLERULE("HOLERULE"),
    ENUMRULE("ENUMRULE"),
    REFERENCERULE("REFERENCERULE");
    
    private String kind;
    
    KindSymbolRule(String kind) {
      this.kind = kind;
    }
    
    /**
     * @see Enum#toString()
     */
    @Override
    public String toString() {
      return kind;
    }
  }
  
  public static final class RuleKind implements SymbolKind {
    private RuleKind() {
    }
  }
  
}
