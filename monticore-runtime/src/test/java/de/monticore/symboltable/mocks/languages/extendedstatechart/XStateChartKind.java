/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.extendedstatechart;

import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.mocks.languages.statechart.StateChartKind;

public class XStateChartKind extends StateChartKind {

  private static final String NAME = XStateChartKind.class.getName();

  protected XStateChartKind(){}

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || super.isKindOf(kind);
  }
  
}
