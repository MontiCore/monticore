/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.statechart;

import java.util.Optional;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.MutableScope;

public class StateChartScope extends CommonScope {

  /**
   * Constructor for StateChartScope
   * @param spanningSymbol
   * @param enclosingScope
   */
  public StateChartScope(StateChartSymbol spanningSymbol, Optional<MutableScope> enclosingScope) {
    super(enclosingScope, true);
    setSpanningSymbol(spanningSymbol);
  }
  
 /**
 * Constructor for StateChartScope
 */
public StateChartScope(StateChartSymbol spanningSymbol) {
  this(spanningSymbol, Optional.empty());
}


}
