/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;
import de.monticore.symboltable.resolving.SymbolAdapter;
import de.se_rwth.commons.logging.Log;

public class Sc2ActionAdapter extends ActionSymbol implements SymbolAdapter<StateChartSymbol> {

  private StateChartSymbol adaptee;
  
  /**
   * Constructor for de.monticore.symboltable.mocks.languages.scandentity.Sc2ActionAdapter
   */
  public Sc2ActionAdapter(StateChartSymbol sc) {
    super(sc.getName());
    
    this.adaptee = Log.errorIfNull(sc);
  }
  
  /**
   * @return adaptee
   */
  public StateChartSymbol getAdaptee() {
    return this.adaptee;
  }
}
