/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.statechart;

import java.util.Optional;

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.MutableScope;

public class StateChartSymbol extends CommonScopeSpanningSymbol {

  public static final StateChartKind KIND = new StateChartKind();
  
  /**
   * @param name
   */
  public StateChartSymbol(String name) {
    super(name, StateChartSymbol.KIND);
  }

  @Override
  protected MutableScope createSpannedScope() {
    return new StateChartScope(this);
  }

  public Optional<StateSymbol> getState(String simpleName) {
    return getSpannedScope().<StateSymbol>resolveLocally(simpleName, StateSymbol.KIND);
  }
  
  public void addState(StateSymbol state) {
    getMutableSpannedScope().add(state);
  }

}
