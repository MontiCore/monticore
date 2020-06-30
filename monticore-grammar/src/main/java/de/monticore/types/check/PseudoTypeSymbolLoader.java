/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.check;

import de.monticore.types.typesymbols._symboltable.OOTypeSymbol;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbolLoader;

/**
 * This class does not load actual symbols from a scope via a TypSymbolLoader. Instead, it always
 * returns (the same instance of a ) pseudo symbol. This can be used to provide TypSymbols for built
 * in types such as "null" and "void".
 */
public class PseudoTypeSymbolLoader extends OOTypeSymbolLoader {

  protected OOTypeSymbol pseudoSymbol;

  public PseudoTypeSymbolLoader(OOTypeSymbol pseudoSymbol) {
    super(pseudoSymbol.getName(), pseudoSymbol.getEnclosingScope());
    this.pseudoSymbol = pseudoSymbol;
  }

  @Override
  public boolean isSymbolLoaded() {
    return true;
  }

  @Override
  public OOTypeSymbol getLoadedSymbol() {
    return pseudoSymbol;
  }
}
