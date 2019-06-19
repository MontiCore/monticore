/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import de.monticore.symboltable.types.JTypeSymbol;

/**
 *
 * The <code>astextends</code> constructs allows both extending another rule
 * or an external type (see {@link JTypeSymbol]). This class helps resolve the respective type.
 *
 */
public class MCProdOrTypeReference {

  private final ProdSymbolReference prodRef;

  public MCProdOrTypeReference(String referencedSymbolName, IGrammarScope enclosingScopeOfReference) {
    prodRef = new ProdSymbolReference(referencedSymbolName, enclosingScopeOfReference);
  }

  public ProdSymbolReference getProdRef() {
    return prodRef;
  }

  public boolean isProdRef() {
    return prodRef.existsReferencedSymbol();
  }

}
