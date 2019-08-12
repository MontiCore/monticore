/* (c) https://github.com/MontiCore/monticore */

package sm2._symboltable;

import java.util.Optional;

public class AutomatonSymbol extends AutomatonSymbolTOP {

  public AutomatonSymbol(final String name) {
    super(name);
  }

  public Optional<StateSymbol> getState(final String name) {
    return getSpannedScope().resolveStateLocally(name);
  }

}
