/* (c) Monticore license: https://github.com/MontiCore/monticore */
package automaton._symboltable;

import java.util.Collection;
import java.util.Optional;
import static de.monticore.symboltable.Symbols.sortSymbolsByPosition;

public class AutomatonSymbol extends AutomatonSymbolTOP {

  public AutomatonSymbol(final String name) {
    super(name);
  }

  @Override
  protected AutomatonScope createSpannedScope() {
    return new AutomatonScope();
  }

  public Optional<StateSymbol> getState(final String name) {
    return getSpannedScope().resolveLocally(name, StateSymbol.KIND);
  }

  public Collection<StateSymbol> getStates() {
    return sortSymbolsByPosition(getSpannedScope().resolveLocally(StateSymbol.KIND));
  }
}
