/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.check;

import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;

import java.util.Optional;

/**
 * This class does not load actual symbols from a scope via a TypSymbolLoader. Instead, it always
 * returns (the same instance of a ) pseudo symbol. This can be used to provide TypSymbols for built
 * in types such as "null" and "void".
 */
public class PseudoTypeSymbolLoader extends TypeSymbolLoader {

  protected TypeSymbol pseudoSymbol;

  public PseudoTypeSymbolLoader(TypeSymbol pseudoSymbol) {
    super(pseudoSymbol.getName(), pseudoSymbol.getEnclosingScope());
    this.pseudoSymbol = pseudoSymbol;
  }

  @Override
  public boolean isSymbolLoaded() {
    return true;
  }

  @Override
  public TypeSymbol getLoadedSymbol() {
    return pseudoSymbol;
  }
}
