/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateSymbol;
import de.monticore.symboltable.resolving.SymbolAdapter;

public class Entity2StateAdapter extends StateSymbol implements SymbolAdapter<EntitySymbol> {

  private final EntitySymbol adaptee;

  public Entity2StateAdapter(EntitySymbol adaptee) {
    super(adaptee.getName());
    this.adaptee = adaptee;
  }
  
  public EntitySymbol getAdaptee() {
    return this.adaptee;
  }
  
}
