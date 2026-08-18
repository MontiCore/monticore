/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar._symboltable;

import java.util.Optional;

@Deprecated
public   class ProdSymbolSurrogate extends ProdSymbolSurrogateTOP  {

  public ProdSymbolSurrogate(String name) {
    super(name);
  }

  public boolean isSymbolPresent() {
    if(delegate.isEmpty()){

      Optional<ProdSymbol> resolvedSymbol = enclosingScope.resolveProd(name);
      
      resolvedSymbol.ifPresent(prodSymbol -> delegate = Optional.of(prodSymbol));
    }
    return delegate.isPresent();
  }

}
