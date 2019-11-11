/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.copyOf;
import static de.se_rwth.commons.logging.Log.errorIfNull;

public class ProdSymbol extends ProdSymbolTOP {


  /**
   * A extends B, C = ...
   */
  private final List<ProdSymbolLoader> superProds = new ArrayList<>();

  /**
   * A implements B, C = ...
   */
  private final List<ProdSymbolLoader> superInterfaceProds = new ArrayList<>();

  /**
   * A astextends B, C, external.java.Type
   */
  private List<ProdSymbolLoader> astSuperClasses = new ArrayList<>();

  /**
   * A implements B, C, external.java.Type
   */
  private List<ProdSymbolLoader> astSuperInterfaces = new ArrayList<>();

  public ProdSymbol(String name) {
    super(name);
  }

  public RuleComponentSymbol addProdComponent(RuleComponentSymbol prodComp) {
    errorIfNull(prodComp);

    RuleComponentSymbol prevProdComp = getProdComponent(prodComp.getName()).orElse(null);

    if (prevProdComp != null) {
      // a prod component is a list (*), if at list one of the prod components
      // is a list
      prevProdComp.setIsList(prevProdComp.isList() || prodComp.isList());
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

  public void addSuperProd(ProdSymbolLoader superProdRef) {
    this.superProds.add(errorIfNull(superProdRef));
  }

  public List<ProdSymbolLoader> getSuperProds() {
    return copyOf(superProds);
  }

  public void addSuperInterfaceProd(ProdSymbolLoader superInterfaceProdRef) {
    this.superInterfaceProds.add(errorIfNull(superInterfaceProdRef));
  }

  public List<ProdSymbolLoader> getSuperInterfaceProds() {
    return copyOf(superInterfaceProds);
  }

  public void addAstSuperClass(ProdSymbolLoader ref) {
    astSuperClasses.add(errorIfNull(ref));
  }

  public List<ProdSymbolLoader> getAstSuperClasses() {
    return copyOf(astSuperClasses);
  }

  public void addAstSuperInterface(ProdSymbolLoader ref) {
    astSuperInterfaces.add(errorIfNull(ref));
  }

  public List<ProdSymbolLoader> getAstSuperInterfaces() {
    return copyOf(astSuperInterfaces);
  }

  public boolean isParserProd() {
    return isClass() || isAbstract();
  }

  /**
   * @return true, if production is a class production (which is the default)
   */
  public boolean isClass() {
    return !isInterface() && !isAbstract() && !isExternal() && !isEnum() && !isLexerProd();
  }

  @Override
  public String toString() {
    return getName();
  }
}
