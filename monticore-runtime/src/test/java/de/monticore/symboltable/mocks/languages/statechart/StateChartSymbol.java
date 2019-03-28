/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.statechart;

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.Scope;

import java.util.Optional;

public class StateChartSymbol extends CommonScopeSpanningSymbol {

  public static final StateChartKind KIND = new StateChartKind();
  
  /**
   * @param name
   */
  public StateChartSymbol(String name) {
    super(name, StateChartSymbol.KIND);
  }

  @Override
  protected Scope createSpannedScope() {
    return new StateChartScope(this);
  }

  public Optional<StateSymbol> getState(String simpleName) {
    return getSpannedScope().<StateSymbol>resolveLocally(simpleName, StateSymbol.KIND);
  }
  
  public void addState(StateSymbol state) {
    getSpannedScope().add(state);
  }

}
