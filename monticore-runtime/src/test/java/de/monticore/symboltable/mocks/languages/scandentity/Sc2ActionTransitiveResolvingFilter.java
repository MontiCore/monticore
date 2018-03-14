/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;
import de.monticore.symboltable.resolving.TransitiveAdaptedResolvingFilter;

import static com.google.common.base.Preconditions.checkArgument;

public class Sc2ActionTransitiveResolvingFilter extends TransitiveAdaptedResolvingFilter<ActionSymbol> {

  /**
   * Constructor for de.monticore.symboltable.mocks.languages.scandentity.Sc2ActionResolver
   */
  public Sc2ActionTransitiveResolvingFilter() {
    super(StateChartSymbol.KIND, ActionSymbol.class, ActionSymbol.KIND);
  }
  

  /**
   * @see de.monticore.symboltable.resolving.CommonAdaptedResolvingFilter#translate(de.monticore.symboltable.Symbol)
   */
  @Override
  public ActionSymbol translate(Symbol s) {
    checkArgument(s instanceof StateChartSymbol);

    return new Sc2ActionAdapter((StateChartSymbol) s);
  }
}
