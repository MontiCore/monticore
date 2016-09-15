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

import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.SymbolKind;

import java.util.Optional;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class MCProdComponentSymbol extends CommonSymbol {

  public static final MCProdComponentKind KIND = new MCProdComponentKind();

  private boolean isTerminal;

  private boolean isNonterminal;
  private boolean isConstantGroup;
  private boolean isConstant;
  private boolean isLexerNonterminal;

  /**
   * E.g. usageName:QualifiedName
   */
  private String usageName = "";

  /**
   * Only for nonterminals. E.g., in u:R R is the name of the referenced prod.
   */
  private MCProdSymbolReference referencedProd;

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


  public MCProdComponentSymbol(String name) {
    super(name, KIND);
  }

  public void setNonterminal(boolean nonterminal) {
    isNonterminal = nonterminal;
  }

  public boolean isNonterminal() {
    return isNonterminal;
  }

  public boolean isTerminal() {
    return isTerminal;
  }

  public void setTerminal(boolean terminal) {
    isTerminal = terminal;
  }

  public boolean isConstantGroup() {
    return isConstantGroup;
  }

  public void setConstantGroup(boolean constantGroup) {
    isConstantGroup = constantGroup;
  }

  public boolean isConstant() {
    return isConstant;
  }

  public void setConstant(boolean constant) {
    isConstant = constant;
  }

  public boolean isLexerNonterminal() {
    return isLexerNonterminal;
  }

  public void setLexerNonterminal(boolean lexerNonterminal) {
    isLexerNonterminal = lexerNonterminal;
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

  public void setReferencedProd(MCProdSymbolReference referencedProd) {
    this.referencedProd = referencedProd;
  }

  public Optional<MCProdSymbolReference> getReferencedProd() {
    return Optional.ofNullable(referencedProd);
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





  public static class MCProdComponentKind implements SymbolKind {

    private static final String NAME = MCProdComponentKind.class.getName();

    protected MCProdComponentKind() {
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
