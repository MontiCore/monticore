/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.symboltable;

import static com.google.common.collect.ImmutableList.copyOf;
import static de.se_rwth.commons.logging.Log.errorIfNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.logging.Log;

public class MCProdSymbol extends CommonScopeSpanningSymbol {

  public static final MCProdKind KIND = new MCProdKind();

  private boolean isStartProd = false;

  private boolean isInterface;

  private boolean isAbstract;

  private boolean isExternal;

  private boolean isEnum;

  private boolean isLexerProd;

  private boolean isScope = false;

  /**
   * the producution that defines the symbol kind of the current prod symbol
   * (only if isSymbolDefinition is true)
   */
  private String prodDefiningSymbolKind = "";

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
    return !prodDefiningSymbolKind.isEmpty();
  }

  public boolean isScopeDefinition() {
    return isScope;
  }

  public void setScopeDefinition(boolean isScope) {
    this.isScope = isScope;
  }

  public Optional<String> getSymbolDefinitionKind() {
    if (isSymbolDefinition()) {
      return of(prodDefiningSymbolKind);
    }
    return empty();
  }

  public void setProdDefiningSymbolKind(String prodDefiningSymbolKind) {
    this.prodDefiningSymbolKind = prodDefiningSymbolKind;
  }

  public MCProdComponentSymbol addProdComponent(MCProdComponentSymbol prodComp) {
    errorIfNull(prodComp);

    MCProdComponentSymbol prevProdComp = getProdComponent(prodComp.getName()).orElse(null);

    if (prevProdComp != null) {
      // a prod component is a list (*), if at list one of the prod components
      // is a list
      prevProdComp.setList(prevProdComp.isList() || prodComp.isList());
      return prevProdComp;
    } else {
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

  public void addProdAttribute(MCProdAttributeSymbol attributeSymbol) {
    errorIfNull(attributeSymbol);
    getMutableSpannedScope().add(attributeSymbol);
  }

  public Collection<MCProdAttributeSymbol> getProdAttributes() {
    return getSpannedScope().resolveLocally(MCProdAttributeSymbol.KIND);
  }

  public Optional<MCProdAttributeSymbol> getProdAttribute(String attributeName) {
    return getSpannedScope().resolveLocally(attributeName, MCProdAttributeSymbol.KIND);
  }

  public void addSuperProd(MCProdSymbolReference superProdRef) {
    this.superProds.add(errorIfNull(superProdRef));
  }

  public List<MCProdSymbolReference> getSuperProds() {
    return copyOf(superProds);
  }

  public void addSuperInterfaceProd(MCProdSymbolReference superInterfaceProdRef) {
    this.superInterfaceProds.add(errorIfNull(superInterfaceProdRef));
  }

  public List<MCProdSymbolReference> getSuperInterfaceProds() {
    return copyOf(superInterfaceProds);
  }

  public void addAstSuperClass(MCProdOrTypeReference ref) {
    astSuperClasses.add(errorIfNull(ref));
  }

  public List<MCProdOrTypeReference> getAstSuperClasses() {
    return copyOf(astSuperClasses);
  }

  public void addAstSuperInterface(MCProdOrTypeReference ref) {
    astSuperInterfaces.add(errorIfNull(ref));
  }

  public List<MCProdOrTypeReference> getAstSuperInterfaces() {
    return copyOf(astSuperInterfaces);
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
    return isClass() || isAbstract();
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
