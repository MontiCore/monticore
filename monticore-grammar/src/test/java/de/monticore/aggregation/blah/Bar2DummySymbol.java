/* (c) https://github.com/MontiCore/monticore */
package de.monticore.aggregation.blah;

import de.monticore.aggregation.blah._symboltable.DummySymbol;
import de.monticore.aggregation.foo._symboltable.BarSymbol;
import de.monticore.symboltable.resolving.ISymbolAdapter;

public class Bar2DummySymbol extends BarSymbol implements ISymbolAdapter<DummySymbol> {

  private DummySymbol adaptee;

  @Override
  public DummySymbol getAdaptee() {
    return adaptee;
  }

  public Bar2DummySymbol(DummySymbol symbol){
    super(symbol.getName());
    this.adaptee=symbol;
  }
}
