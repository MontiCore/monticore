/* (c) https://github.com/MontiCore/monticore */
package automata._symboltable;

import de.monticore.symboltable.ISymbol;

import java.util.Collection;
import java.util.Optional;

public class AutomatonSymbol extends AutomatonSymbolTOP {

  public AutomatonSymbol(final String name) {
    super(name);
  }

  public Optional<StateSymbol> getState(final String name) {
    return getSpannedScope().resolveStateLocally(name);
  }

  public Collection<StateSymbol> getStates() {
    return ISymbol.sortSymbolsByPosition(getSpannedScope().getLocalStateSymbols());
  }
}
