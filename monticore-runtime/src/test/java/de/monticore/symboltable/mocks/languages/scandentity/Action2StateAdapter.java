/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateSymbol;
import de.monticore.symboltable.resolving.SymbolAdapter;

public class Action2StateAdapter extends StateSymbol implements SymbolAdapter<ActionSymbol> {

  private final ActionSymbol adaptee;

  public Action2StateAdapter(ActionSymbol adaptee) {
    super(adaptee.getName());
    this.adaptee = adaptee;
  }
  
  public ActionSymbol getAdaptee() {
    return this.adaptee;
  }
  
}
