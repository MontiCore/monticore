/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateSymbol;
import de.monticore.symboltable.resolving.SymbolAdapter;

public class State2EntityAdapter extends EntitySymbol implements SymbolAdapter<StateSymbol> {

  private final StateSymbol adaptee;

  public State2EntityAdapter(StateSymbol sc) {
    super(sc.getName());
    this.adaptee = sc;
  }
  
  /**
   * @return adaptee
   */
  public StateSymbol getAdaptee() {
    return this.adaptee;
  }
  
}
