/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.extendedstatechart;

import de.monticore.symboltable.mocks.languages.statechart.StateSymbol;

public class XStateSymbol extends StateSymbol {
  
  public static final XStateKind KIND = new XStateKind();

  /**
   * @param name
   */
  public XStateSymbol(String name) {
    super(name);
    setKind(XStateSymbol.KIND);
  }
  
}
