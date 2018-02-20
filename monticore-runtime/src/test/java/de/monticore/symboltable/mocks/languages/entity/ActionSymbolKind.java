/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.types.JMethodSymbolKind;

public class ActionSymbolKind extends JMethodSymbolKind {

  private static final String NAME = "de.monticore.symboltable.mocks.languages.entity.ActionSymbolKind";

  protected ActionSymbolKind(){}

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || super.isKindOf(kind);
  }

}
