/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;
import de.monticore.symboltable.resolving.SymbolAdapter;

public class Entity2ScAdapter extends StateChartSymbol implements SymbolAdapter<EntitySymbol> {

  private final EntitySymbol adaptee;

  public Entity2ScAdapter(EntitySymbol adaptee) {
    super(adaptee.getName());
    this.adaptee = adaptee;
  }

  public EntitySymbol getAdaptee() {
    return adaptee;
  }
}
