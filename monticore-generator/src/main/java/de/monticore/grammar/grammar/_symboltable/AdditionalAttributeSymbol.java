/* (c)  https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import de.monticore.symboltable.SymbolKind;

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
  
  public static class MCProdAttributeKind implements SymbolKind {
    
    private static final String NAME = MCProdAttributeKind.class.getName();
    
    protected MCProdAttributeKind() {
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
