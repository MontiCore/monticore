/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package mc.feature.scopes.superautomaton._symboltable;

import java.util.Collection;
import java.util.Optional;

import static de.monticore.symboltable.Symbols.sortSymbolsByPosition;

public class AutomatonSymbol extends AutomatonSymbolTOP {

  public AutomatonSymbol(final String name) {
    super(name);
  }

  @Override
  protected SuperAutomatonScope createSpannedScope() {
    SuperAutomatonScope a = new SuperAutomatonScope();
    a.setExportsSymbols(true);
    return a;
  }

  public Optional<StateSymbol> getState(final String name) {
    return getSpannedScope().resolveLocally(name, StateSymbol.KIND);
  }

  public Collection<StateSymbol> getStates() {
    return sortSymbolsByPosition(getSpannedScope().resolveLocally(StateSymbol.KIND));
  }
}
