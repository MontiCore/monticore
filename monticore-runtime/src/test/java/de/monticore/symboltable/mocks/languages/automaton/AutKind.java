/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.automaton;

import de.monticore.symboltable.SymbolKind;

public class AutKind implements SymbolKind {


  private static final String NAME = "de.monticore.symboltable.mocks.languages.statechart.StateKind";

  protected AutKind(){}

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
  }
}
