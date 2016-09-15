/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2016, MontiCore, All rights reserved.
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

package de.monticore.grammar.symboltable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class MontiCoreGrammarSymbol extends CommonScopeSpanningSymbol {

  public static final MontiCoreGrammarKind KIND = new MontiCoreGrammarKind();

  private final List<MontiCoreGrammarSymbolReference> superGrammars = new ArrayList<>();

  /**
   * Is the grammar abstract?
   */
  private boolean isComponent = false;

  // the start production of the grammar
  private MCProdSymbol startRule;


  public MontiCoreGrammarSymbol(String name) {
    super(name, KIND);
  }


  public void setStartProd(MCProdSymbol startRule) {
    this.startRule = startRule;
  }

  /**
   * The start production typically is the first defined production in the grammar.
   *
   * @return the start production of the grammar, if not a component grammar
   */
  public Optional<MCProdSymbol> getStartRule() {
    return Optional.ofNullable(startRule);
  }

  /**
   * @return true, if the grammar is abstract
   */
  public boolean isComponent() {
    return isComponent;
  }

  public void setComponent(boolean isComponent) {
    this.isComponent = isComponent;
  }

  public List<MontiCoreGrammarSymbolReference> getSuperGrammars() {
    return ImmutableList.copyOf(superGrammars);
  }

  public void addSuperGrammar(MontiCoreGrammarSymbolReference superGrammarRef) {
    this.superGrammars.add(Log.errorIfNull(superGrammarRef));
  }

  public Collection<MCProdSymbol> getProds() {
    return this.getSpannedScope().resolveLocally(MCProdSymbol.KIND);
  }

  public Collection<String> getProdNames() {
    final Set<String> prodNames = new LinkedHashSet<>();

    for (final MCProdSymbol prodSymbol : getProds()) {
      prodNames.add(prodSymbol.getName());
    }

    return ImmutableSet.copyOf(prodNames);
  }

  public Optional<MCProdSymbol> getProd(String prodName) {
    return this.getSpannedScope().resolveLocally(prodName, MCProdSymbol.KIND);
  }


  public Optional<MCProdSymbol> getProdWithInherited(String ruleName) {
    Optional<MCProdSymbol> mcProd = getProd(ruleName);
    Iterator<MontiCoreGrammarSymbolReference> itSuperGrammars = superGrammars.iterator();

    while (!mcProd.isPresent() && itSuperGrammars.hasNext()) {
      mcProd = itSuperGrammars.next().getReferencedSymbol().getProdWithInherited(ruleName);
    }

    return mcProd;
  }


  public static class MontiCoreGrammarKind implements  SymbolKind {

    private static final String NAME = MontiCoreGrammarKind.class.getName();

    protected MontiCoreGrammarKind() {
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
