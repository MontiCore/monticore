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

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.SymbolKind;

import java.util.Collection;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class MCProdSymbol extends CommonScopeSpanningSymbol {

  public static final MCProdKind KIND = new MCProdKind();

  private boolean isStartProd = false;

  private boolean isParserProd;
  private boolean isLexerProd;

  private boolean isInterface;
  private boolean isAbstract;
  private boolean isExternal;
  private boolean isEnum;

  /**
   * the producution that defines the symbol kind of the current prod symbol (only if isSymbolDefinition is true)
   */
  private MCProdSymbolReference prodDefiningSymbolKind = null;


  public MCProdSymbol(String name) {
    super(name, KIND);
  }


  public boolean isStartProd() {
    return isStartProd;
  }

  public void setStartProd(boolean isStartProd) {
    this.isStartProd = isStartProd;
  }

  public boolean isSymbolDefinition() {
    return prodDefiningSymbolKind != null;
  }

  public Optional<MCProdSymbolReference> getProdDefiningSymbolKind() {
    return ofNullable(prodDefiningSymbolKind);
  }

  public void setProdDefiningSymbolKind(MCProdSymbolReference prodDefiningSymbolKind) {
    this.prodDefiningSymbolKind = prodDefiningSymbolKind;
  }

  public Collection<MCProdComponentSymbol> getProdComponents() {
    return getSpannedScope().resolveLocally(MCProdComponentSymbol.KIND);
  }

  public Optional<MCProdComponentSymbol> getProdComponent(String componentName) {
    return getSpannedScope().resolveLocally(componentName, MCProdComponentSymbol.KIND);
  }

  public void setInterface(boolean anInterface) {
    isInterface = anInterface;
  }

  public boolean isInterface() {
    return isInterface;
  }

  public void setAbstract(boolean anAbstract) {
    isAbstract = anAbstract;
  }

  public boolean isAbstract() {
    return isAbstract;
  }

  public void setExternal(boolean external) {
    isExternal = external;
  }

  public boolean isExternal() {
    return isExternal;
  }

  public void setEnum(boolean anEnum) {
    isEnum = anEnum;
  }

  public boolean isEnum() {
    return isEnum;
  }

  public static class MCProdKind implements SymbolKind {

    private static final String NAME = MCProdKind.class.getName();

    protected MCProdKind() {
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
