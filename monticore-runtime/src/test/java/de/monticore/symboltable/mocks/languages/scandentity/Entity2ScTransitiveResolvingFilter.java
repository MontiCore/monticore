/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;

import static com.google.common.base.Preconditions.checkArgument;

public class Entity2ScTransitiveResolvingFilter extends TransitiveAdaptedResolvingFilter<StateChartSymbol> {

  public Entity2ScTransitiveResolvingFilter() {
    super(EntitySymbol.KIND, StateChartSymbol.class, StateChartSymbol.KIND);
  }

  @Override
  public StateChartSymbol translate(Symbol adaptee) {
    checkArgument(adaptee instanceof EntitySymbol);

    return new Entity2ScAdapter((EntitySymbol) adaptee);
  }
}
