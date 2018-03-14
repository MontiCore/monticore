/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.extendedstatechart;

import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;

public class XStateChartSymbol extends StateChartSymbol {

  public static final XStateChartKind KIND = new XStateChartKind();

  /**
   * Constructor for XStateChartSymbol
   * @param name
   */
  public XStateChartSymbol(String name) {
    super(name);
    setKind(XStateChartSymbol.KIND);
  }
  
}
