/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;
import de.monticore.symboltable.resolving.SymbolAdapter;

public class Sc2EntityAdapter extends EntitySymbol implements SymbolAdapter<StateChartSymbol> {

  private final StateChartSymbol adaptee;
  
  /**
   * Constructor for de.monticore.symboltable.mocks.languages.scandentity.Sc2EntityAdapter
   */
  public Sc2EntityAdapter(StateChartSymbol sc) {
    super(sc.getName());
    this.adaptee = sc;
  }
  
  /**
   * @return adaptee
   */
  public StateChartSymbol getAdaptee() {
    return this.adaptee;
  }
  
}
