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

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.logging.Log;

/**
 * @author Pedram Mir Seyed Nazari
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
   * the producution that defines the symbol kind of the current prod symbol
   * (only if isSymbolDefinition is true)
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
  
  /**
   * ast A = b:B c:external.java.Type;
   */
  private List<MCProdAttributeSymbol> astAttributes = new ArrayList<>();
  
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
  
  public Optional<String> getSymbolDefinitionKind() {
    if (isSymbolDefinition()) {
      if (prodDefiningSymbolKind.getReferencedSymbol() == this) {
        return of(getName());
      }
      return prodDefiningSymbolKind.getReferencedSymbol().getSymbolDefinitionKind();
    }
    return empty();
  }
  
  public Optional<MCProdSymbolReference> getProdDefiningSymbolKind() {
    return ofNullable(prodDefiningSymbolKind);
  }
  
  public void setProdDefiningSymbolKind(MCProdSymbolReference prodDefiningSymbolKind) {
    this.prodDefiningSymbolKind = prodDefiningSymbolKind;
  }
  
  public MCProdComponentSymbol addProdComponent(MCProdComponentSymbol prodComp) {
    Log.errorIfNull(prodComp);
    
    MCProdComponentSymbol prevProdComp = getProdComponent(prodComp.getName()).orElse(null);
    
    if (prevProdComp != null) {
      // a prod component is a list (*), if at list one of the prod components
      // is a list
      prevProdComp.setList(prevProdComp.isList() || prodComp.isList());
      return prevProdComp;
    }
    else {
      getMutableSpannedScope().add(prodComp);
    }
    return prodComp;
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
  
  public void addAstAttribute(MCProdAttributeSymbol ref) {
    astAttributes.add(Log.errorIfNull(ref));
  }
  
  public List<MCProdAttributeSymbol> getAstAttributes() {
    return ImmutableList.copyOf(astAttributes);
  }
  
  /**
   * @return true, if production is a class production (which is the default)
   */
  public boolean isClass() {
    return !isInterface() && !isAbstract() && !isExternal() && !isEnum() && !isLexerProd();
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
