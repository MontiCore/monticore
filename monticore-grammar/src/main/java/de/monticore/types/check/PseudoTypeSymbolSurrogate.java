/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.check;

import de.monticore.types.typesymbols._symboltable.OOTypeSymbol;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbolSurrogate;

/**
 * This class does not load actual symbols from a scope via a TypSymbolLoader. Instead, it always
 * returns (the same instance of a ) pseudo symbol. This can be used to provide TypSymbols for built
 * in types such as "null" and "void".
 */
public class PseudoTypeSymbolSurrogate extends OOTypeSymbolSurrogate {

  protected OOTypeSymbol pseudoSymbol;

  public PseudoTypeSymbolSurrogate(OOTypeSymbol pseudoSymbol) {
    super(pseudoSymbol.getName());
    setEnclosingScope(pseudoSymbol.getEnclosingScope());
    this.pseudoSymbol = pseudoSymbol;
  }

  @Override
  public OOTypeSymbol lazyLoadDelegate() {
    return pseudoSymbol;
  }
}
