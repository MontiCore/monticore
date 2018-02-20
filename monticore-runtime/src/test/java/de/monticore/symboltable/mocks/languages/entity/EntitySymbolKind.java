/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.types.JTypeSymbolKind;

public class EntitySymbolKind extends JTypeSymbolKind {

  private static final String NAME = "de.monticore.symboltable.mocks.languages.entity.EntitySymbolKind";

  public static final EntitySymbolKind KIND = new EntitySymbolKind();

  protected EntitySymbolKind() {
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
