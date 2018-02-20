/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbolKind;
import de.monticore.symboltable.mocks.languages.statechart.StateSymbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;

import static com.google.common.base.Preconditions.checkArgument;

public class State2EntityTransitiveResolvingFilter extends TransitiveAdaptedResolvingFilter<EntitySymbol> {

  public State2EntityTransitiveResolvingFilter() {
    super(StateSymbol.KIND, EntitySymbol.class, EntitySymbolKind.KIND);
  }

  @Override
  public EntitySymbol translate(Symbol s) {
    checkArgument(s instanceof StateSymbol);

    return new State2EntityAdapter((StateSymbol) s);
  }

}
