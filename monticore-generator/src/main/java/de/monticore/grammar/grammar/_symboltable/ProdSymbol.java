/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import de.monticore.symboltable.SymbolKind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.copyOf;
import static de.se_rwth.commons.logging.Log.errorIfNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class ProdSymbol extends ProdSymbolTOP {


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
  private final List<ProdSymbolReference> superProds = new ArrayList<>();

  /**
   * A implements B, C = ...
   */
  private final List<ProdSymbolReference> superInterfaceProds = new ArrayList<>();

  /**
   * A astextends B, C, external.java.Type
   */
  private List<MCProdOrTypeReference> astSuperClasses = new ArrayList<>();

  /**
   * A implements B, C, external.java.Type
   */
  private List<MCProdOrTypeReference> astSuperInterfaces = new ArrayList<>();

  public ProdSymbol(String name) {
    super(name);
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

  public RuleComponentSymbol addProdComponent(RuleComponentSymbol prodComp) {
    errorIfNull(prodComp);

    RuleComponentSymbol prevProdComp = getProdComponent(prodComp.getName()).orElse(null);

    if (prevProdComp != null) {
      // a prod component is a list (*), if at list one of the prod components
      // is a list
      prevProdComp.setList(prevProdComp.isList() || prodComp.isList());
      return prevProdComp;
    } else {
      getSpannedScope().add(prodComp);
    }
    return prodComp;
  }

  public Collection<RuleComponentSymbol> getProdComponents() {
    return getSpannedScope().getLocalRuleComponentSymbols();
  }

  public Optional<RuleComponentSymbol> getProdComponent(String componentName) {
    return getSpannedScope().resolveRuleComponentLocally(componentName);
  }

  public void addProdAttribute(AdditionalAttributeSymbol attributeSymbol) {
    errorIfNull(attributeSymbol);
    getSpannedScope().add(attributeSymbol);
  }

  public Collection<AdditionalAttributeSymbol> getProdAttributes() {
    return getSpannedScope().getLocalAdditionalAttributeSymbols();
  }

  public Optional<AdditionalAttributeSymbol> getProdAttribute(String attributeName) {
    return getSpannedScope().resolveAdditionalAttributeLocally(attributeName);
  }

  public void addSuperProd(ProdSymbolReference superProdRef) {
    this.superProds.add(errorIfNull(superProdRef));
  }

  public List<ProdSymbolReference> getSuperProds() {
    return copyOf(superProds);
  }

  public void addSuperInterfaceProd(ProdSymbolReference superInterfaceProdRef) {
    this.superInterfaceProds.add(errorIfNull(superInterfaceProdRef));
  }

  public List<ProdSymbolReference> getSuperInterfaceProds() {
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
