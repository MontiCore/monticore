/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;

import static com.google.common.base.Preconditions.checkArgument;

public class Sc2EntityTransitiveResolvingFilter extends TransitiveAdaptedResolvingFilter<EntitySymbol> {

  /**
   * Constructor for de.monticore.symboltable.mocks.languages.scandentity.Sc2EntityResolver
   */
  public Sc2EntityTransitiveResolvingFilter() {
    super(StateChartSymbol.KIND, EntitySymbol.class, EntitySymbol.KIND);
  }

  @Override
  public EntitySymbol translate(Symbol s) {
    checkArgument(s instanceof StateChartSymbol);

    return new Sc2EntityAdapter((StateChartSymbol) s);
  }

}
