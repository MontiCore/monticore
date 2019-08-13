/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import java.util.Optional;

public class AdditionalAttributeSymbol extends AdditionalAttributeSymbolTOP {

  private MCProdOrTypeReference typeReference;

  public AdditionalAttributeSymbol(String name) {
    super(name);
  }

  public void setTypeReference(MCProdOrTypeReference referencedProd) {
    this.typeReference = referencedProd;
  }
  
  /**
   * @return A reference to the defining production of this component, e.g., the
   * defining prod for the nonterminal <code>... = s:A</code> is the production
   * <code>A = ...</code>.
   */
  public Optional<MCProdOrTypeReference> getTypeReference() {
    return Optional.ofNullable(typeReference);
  }
  

}
