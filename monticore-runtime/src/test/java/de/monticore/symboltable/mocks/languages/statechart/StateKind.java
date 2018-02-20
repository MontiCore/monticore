/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.statechart;

import de.monticore.symboltable.SymbolKind;

public class StateKind implements SymbolKind {


  private static final String NAME = "de.monticore.symboltable.mocks.languages.statechart.StateKind";

  protected StateKind(){}

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
  }
}
