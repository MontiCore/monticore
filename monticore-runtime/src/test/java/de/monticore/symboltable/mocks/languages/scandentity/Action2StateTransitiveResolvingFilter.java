/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateSymbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;

import static com.google.common.base.Preconditions.checkArgument;

public class Action2StateTransitiveResolvingFilter extends TransitiveAdaptedResolvingFilter<StateSymbol> {

  public Action2StateTransitiveResolvingFilter() {
    super(ActionSymbol.KIND, StateSymbol.class, StateSymbol.KIND);
  }

  @Override
  public StateSymbol translate(Symbol adaptee) {
    checkArgument(adaptee instanceof ActionSymbol);

    return new Action2StateAdapter((ActionSymbol) adaptee);
  }

}
