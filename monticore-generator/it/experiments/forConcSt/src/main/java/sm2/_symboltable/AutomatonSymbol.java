/* (c) Monticore license: https://github.com/MontiCore/monticore */

package sm2._symboltable;

import java.util.Optional;

public class AutomatonSymbol extends AutomatonSymbolTOP {

  public AutomatonSymbol(final String name) {
    super(name);
  }

  @Override
  protected SM2Scope createSpannedScope() {
    return new SM2Scope(Optional.empty());
  }

  public Optional<StateSymbol> getState(final String name) {
    return getSpannedScope().resolveLocally(name, StateSymbol.KIND);
  }

}
