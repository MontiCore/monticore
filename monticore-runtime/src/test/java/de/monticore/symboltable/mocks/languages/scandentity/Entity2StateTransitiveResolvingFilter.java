/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateSymbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;

import static com.google.common.base.Preconditions.checkArgument;

public class Entity2StateTransitiveResolvingFilter extends TransitiveAdaptedResolvingFilter<StateSymbol> {

  public Entity2StateTransitiveResolvingFilter() {
    super(EntitySymbol.KIND, StateSymbol.class, StateSymbol.KIND);
  }

  @Override
  public StateSymbol translate(Symbol adaptee) {
    checkArgument(adaptee instanceof EntitySymbol);

    return new Entity2StateAdapter((EntitySymbol) adaptee);
  }

}
