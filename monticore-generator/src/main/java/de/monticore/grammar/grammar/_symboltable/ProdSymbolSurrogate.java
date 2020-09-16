/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar._symboltable;

import de.se_rwth.commons.logging.Log;

import java.util.Optional;

@Deprecated
public   class ProdSymbolSurrogate extends ProdSymbolSurrogateTOP  {

  public ProdSymbolSurrogate(String name) {
    super(name);
  }

  public boolean isSymbolPresent() {
    if(!delegate.isPresent()){

      Optional<ProdSymbol> resolvedSymbol = enclosingScope.resolveProd(name);

      if (resolvedSymbol.isPresent()) {
         delegate = Optional.of(resolvedSymbol.get());
       }
    }
    return delegate.isPresent();
  }

}
