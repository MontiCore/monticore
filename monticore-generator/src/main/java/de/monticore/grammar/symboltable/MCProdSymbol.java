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
import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class MCProdSymbol extends CommonScopeSpanningSymbol {

  public static final MCProdKind KIND = new MCProdKind();

  private boolean isStartProd = false;

  private boolean isInterface;
  private boolean isAbstract;
  private boolean isExternal;
  private boolean isEnum;

  private boolean isLexerProd;

  /**
   * the producution that defines the symbol kind of the current prod symbol (only if isSymbolDefinition is true)
   */
  private MCProdSymbolReference prodDefiningSymbolKind = null;

  /**
   * A extends B, C = ...
   */
  private final List<MCProdSymbolReference> superProds = new ArrayList<>();

  /**
   * A implements B, C = ...
   */
  private final List<MCProdSymbolReference> superInterfaceProds = new ArrayList<>();

  /**
   * A astextends B, C, external.java.Type
   */
  private List<MCProdOrTypeReference> astSuperClasses = new ArrayList<>();

  /**
   * A implements B, C, external.java.Type
   */
  private List<MCProdOrTypeReference> astSuperInterfaces = new ArrayList<>();


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

  public void addProdComponent(MCProdComponentSymbol prodComponent) {
    Log.errorIfNull(prodComponent);

    MCProdComponentSymbol prodComp = getProdComponent(prodComponent.getName()).orElse(null);

    if (prodComp != null) {
      // TODO NN <- PN handle the case: (a:A a:B), i.e. two different non-terminals have the same usage name
      // a prod component is a list (*), if at list one of the prod components is a list
      // TODO NN <- PN what about (a:A) | (a:A)? | (a:A)+ is the prod component a:A optional? a list? etc.
      prodComp.setList(prodComp.isList() || prodComponent.isList());
    }
    else {
      getMutableSpannedScope().add(prodComponent);
    }
  }

  public Collection<MCProdComponentSymbol> getProdComponents() {
    return getSpannedScope().resolveLocally(MCProdComponentSymbol.KIND);
  }

  public Optional<MCProdComponentSymbol> getProdComponent(String componentName) {
    return getSpannedScope().resolveLocally(componentName, MCProdComponentSymbol.KIND);
  }

  public void addSuperProd(MCProdSymbolReference superProdRef) {
    this.superProds.add(Log.errorIfNull(superProdRef));
  }

  public List<MCProdSymbolReference> getSuperProds() {
    return ImmutableList.copyOf(superProds);
  }

  public void addSuperInterfaceProd(MCProdSymbolReference superInterfaceProdRef) {
    this.superInterfaceProds.add(Log.errorIfNull(superInterfaceProdRef));
  }

  public List<MCProdSymbolReference> getSuperInterfaceProds() {
    return ImmutableList.copyOf(superInterfaceProds);
  }

  public void addAstSuperClass(MCProdOrTypeReference ref) {
    astSuperClasses.add(Log.errorIfNull(ref));
  }

  public List<MCProdOrTypeReference> getAstSuperClasses() {
    return ImmutableList.copyOf(astSuperClasses);
  }

  public void addAstSuperInterface(MCProdOrTypeReference ref) {
    astSuperInterfaces.add(Log.errorIfNull(ref));
  }

  public List<MCProdOrTypeReference> getAstSuperInterfaces() {
    return ImmutableList.copyOf(astSuperInterfaces);
  }

  /**
   * @return true, if production is a class production (which is the default)
   */
  public boolean isClass() {
    return !isInterface() && !isAbstract() && !isExternal() && !isEnum();
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

  public boolean isParserProd() {
    return !isLexerProd();
  }

  public void setLexerProd(boolean lexerProd) {
    isLexerProd = lexerProd;
  }

  public boolean isLexerProd() {
    return isLexerProd;
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
