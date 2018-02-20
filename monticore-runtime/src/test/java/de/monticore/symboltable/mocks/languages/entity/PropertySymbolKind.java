/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.types.JAttributeSymbolKind;

public class PropertySymbolKind extends JAttributeSymbolKind {

  private static final String NAME = "de.monticore.symboltable.mocks.languages.entity.PropertySymbolKind";

  protected PropertySymbolKind() {
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || super.isKindOf(kind);
  }

}
