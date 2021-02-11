/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;


import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
import static de.se_rwth.commons.logging.Log.errorIfNull;

public class ProdSymbol extends ProdSymbolTOP {


  /**
   * A extends B, C = ...
   */
  private final List<ProdSymbolSurrogate> superProds = new ArrayList<>();

  /**
   * A implements B, C = ...
   */
  private final List<ProdSymbolSurrogate> superInterfaceProds = new ArrayList<>();

  /**
   * A astextends B, C, external.java.Type
   */
  private List<ProdSymbolSurrogate> astSuperClasses = new ArrayList<>();

  /**
   * A implements B, C, external.java.Type
   */
  private List<ProdSymbolSurrogate> astSuperInterfaces = new ArrayList<>();

  public ProdSymbol(String name) {
    super(name);
  }

  public List<RuleComponentSymbol> getProdComponents() {
    return getSpannedScope().getLocalRuleComponentSymbols();
  }

  public void addSuperProd(ProdSymbolSurrogate superProdRef) {
    this.superProds.add(errorIfNull(superProdRef));
  }

  public List<ProdSymbolSurrogate> getSuperProds() {
    return copyOf(superProds);
  }

  public void addSuperInterfaceProd(ProdSymbolSurrogate superInterfaceProdRef) {
    this.superInterfaceProds.add(errorIfNull(superInterfaceProdRef));
  }

  public List<ProdSymbolSurrogate> getSuperInterfaceProds() {
    return copyOf(superInterfaceProds);
  }

  public void addAstSuperClass(ProdSymbolSurrogate ref) {
    astSuperClasses.add(errorIfNull(ref));
  }

  public List<ProdSymbolSurrogate> getAstSuperClasses() {
    return copyOf(astSuperClasses);
  }

  public void addAstSuperInterface(ProdSymbolSurrogate ref) {
    astSuperInterfaces.add(errorIfNull(ref));
  }

  public List<ProdSymbolSurrogate> getAstSuperInterfaces() {
    return copyOf(astSuperInterfaces);
  }

  public boolean isParserProd() {
    return isClass() || isIsAbstract();
  }

  /**
   * @return true, if production is a class production (which is the default)
   */
  public boolean isClass() {
    return !isIsInterface() && !isIsAbstract() && !isIsExternal() && !isIsEnum() && !isIsLexerProd();
  }

  @Override
  public String toString() {
    return getName();
  }
}
