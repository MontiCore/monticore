/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.extendedstatechart;

import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.mocks.languages.statechart.StateKind;

public class XStateKind extends StateKind {

  private static final String NAME = "de.monticore.symboltable.mocks.languages.extendedstatechart.XStateKind";

  protected XStateKind(){}

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || super.isKindOf(kind);
  }

}
